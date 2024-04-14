package org.example;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.io.PrintStream;

public class SimpleLangParser {
    private final Map<String, Integer> variables = new ConcurrentHashMap<>();
    private final Map<String, String> functions = new ConcurrentHashMap<>();

    public void execute(String code, PrintStream out) {
        String[] lines = code.split("\n");
        StringBuilder currentBlock = new StringBuilder();
        boolean inFunction = false;
        int openBraces = 0;

        for (String line : lines) {
            line = line.trim();

            if (line.startsWith("func")) {
                inFunction = true;
            }

            if (inFunction) {
                currentBlock.append(line).append("\n");

                if (line.contains("{")) {
                    openBraces++;
                }
                if (line.contains("}")) {
                    openBraces--;
                }

                if (openBraces == 0) {
                    handleFunctionDefinition(currentBlock.toString(), out);
                    inFunction = false;
                    currentBlock = new StringBuilder();
                }
            } else if (!line.isEmpty()) {
                processStatement(line, out);
            }
        }

        if (inFunction) {
            throw new IllegalArgumentException("Unclosed function definition");
        }
    }

    private synchronized void processStatement(String statement, PrintStream out) {
        statement = statement.trim();
        if (statement.contains("=")) {
            handleAssignment(statement);
        } else if (statement.startsWith("print")) {
            handlePrint(statement, out);
        } else if (statement.endsWith("();")) {
            handleFunctionCall(statement, out);
        }
    }

    private synchronized void handleAssignment(String statement) {
        String[] parts = statement.split("=");
        String variableName = parts[0].trim();
        int value = evaluateExpression(parts[1].trim());
        variables.put(variableName, value);
    }

    private synchronized void handlePrint(String statement, PrintStream out) {
        String expression = statement.substring(6).trim();
        int result = evaluateExpression(expression);
        out.println(result);
    }

    private synchronized void handleFunctionDefinition(String statement, PrintStream out) {
        int funcNameStartIndex = statement.indexOf("func") + "func".length();
        int funcNameEndIndex = statement.indexOf("(", funcNameStartIndex);
        if (funcNameEndIndex == -1) {
            throw new IllegalArgumentException("Invalid function declaration");
        }

        String funcName = statement.substring(funcNameStartIndex, funcNameEndIndex).trim();
        int bodyStartIndex = statement.indexOf("{", funcNameEndIndex);
        int bodyEndIndex = statement.lastIndexOf("}");
        if (bodyStartIndex == -1 || bodyEndIndex == -1) {
            throw new IllegalArgumentException("Invalid function body");
        }

        String body = statement.substring(bodyStartIndex + 1, bodyEndIndex).trim();
        functions.put(funcName, body);
    }

    private synchronized void handleFunctionCall(String statement, PrintStream out) {
        String funcName = statement.contains("(") ? statement.substring(0, statement.indexOf("(")).trim() : statement.trim();

        if (functions.containsKey(funcName)) {
            String functionBody = functions.get(funcName);
            execute(functionBody, out);
        } else {
            out.println("Function " + funcName + " is not defined");
        }
    }

    private int evaluateExpression(String expression) {
        ExpressionEvaluator evaluator = new ExpressionEvaluator(variables);
        return evaluator.evaluate(expression);
    }
}
