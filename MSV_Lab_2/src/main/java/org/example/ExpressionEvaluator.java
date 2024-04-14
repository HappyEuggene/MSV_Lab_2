package org.example;

import java.util.Map;
import java.util.Stack;

public class ExpressionEvaluator {
    private final Map<String, Integer> variables;

    public ExpressionEvaluator(Map<String, Integer> variables) {
        this.variables = variables;
    }

    public int evaluate(String expression) {
        Stack<Integer> values = new Stack<>();
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == ' ' || c == ';') continue;

            if (Character.isDigit(c)) {
                int num = 0;
                while (i < expression.length() && Character.isDigit(expression.charAt(i))) {
                    num = num * 10 + (expression.charAt(i) - '0');
                    i++;
                }
                values.push(num);
                i--;
            } else if (Character.isLetter(c)) {
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() && Character.isLetter(expression.charAt(i))) {
                    sb.append(expression.charAt(i));
                    i++;
                }
                String varName = sb.toString();
                if (!variables.containsKey(varName)) {
                    throw new IllegalArgumentException("Variable " + varName + " is not defined");
                }
                values.push(variables.get(varName));
                i--;
            } else if (c == '(') {
                ops.push(c);
            } else if (c == ')') {
                while (!ops.empty() && ops.peek() != '(') {
                    if (values.size() < 2) throw new IllegalArgumentException("Invalid expression");
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                if (ops.empty()) throw new IllegalArgumentException("Unmatched parentheses");
                ops.pop();
            } else if ("+-*/".indexOf(c) >= 0) {
                if (values.isEmpty()) throw new IllegalArgumentException("Invalid expression");
                while (!ops.empty() && hasPrecedence(c, ops.peek())) {
                    if (values.size() < 2) throw new IllegalArgumentException("Invalid expression");
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.push(c);
            } else {
                throw new IllegalArgumentException("Invalid character: " + c);
            }
        }

        while (!ops.empty()) {
            if (values.size() < 2) throw new IllegalArgumentException("Invalid expression");
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        }

        if (values.size() != 1) throw new IllegalArgumentException("Invalid expression");

        return values.pop();
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false;
        return true;
    }

    private int applyOp(char op, int b, int a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) throw new UnsupportedOperationException("Cannot divide by zero");
                return a / b;
        }
        return 0;
    }
}
