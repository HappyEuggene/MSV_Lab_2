package org.example;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.testng.annotations.DataProvider;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class SimpleLangParserTest {
    private SimpleLangParser parser;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        parser = new SimpleLangParser();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        outputStream.reset();
    }

    @Test(groups = {"basic", "functionality"})
    public void testAssignmentAndPrint() {
        String code = "x = 10;\nprint x;";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        parser.execute(code, printStream);
        Assert.assertEquals("10\r\n", outputStream.toString());
    }

    @Test(groups = {"functionality"})
    public void testFunctionExecution() {
        String code = "func add() { result = 3 + 2; }\nadd();\nprint result;";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        parser.execute(code, printStream);
        Assert.assertEquals("5\r\n", outputStream.toString());
    }


    @Test(groups = {"errorHandling"}, expectedExceptions = IllegalArgumentException.class)
    public void testInvalidSyntaxThrowsException() {
        String code = "func add() {";
        parser.execute(code, System.out);
    }

    @Test(groups = {"functionality", "errorHandling"})
    public void testUndefinedFunctionCall() {
        String code = "callUndefinedFunction();";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        parser.execute(code, new PrintStream(outputStream));
        Assert.assertEquals("Function callUndefinedFunction is not defined\r\n", outputStream.toString());
    }

    @DataProvider(name = "arithmeticOperations")
    public Object[][] createArithmeticOperations() {
        return new Object[][] {
                { "5 + 5" },
                { "2 * 3" },
                { "10 - 5" },
                { "20 / 4" }
        };
    }

    @Test(dataProvider = "arithmeticOperations", groups = {"arithmetic"})
    public void testArithmeticOperations(String operation) {
        String code = String.format("result = %s;\nprint result;", operation);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        parser.execute(code, new PrintStream(outputStream));
        Assert.assertTrue(!outputStream.toString().isEmpty());
    }

    @Test(groups = {"errorHandling"}, expectedExceptions = IllegalArgumentException.class)
    public void testInvalidSyntax() {
        String code = "x = 5 + ;";
        parser.execute(code, System.out);
    }

    @DataProvider(name = "multipleArithmeticOperations")
    public Object[][] createMultipleArithmeticOperations() {
        return new Object[][] {
                { "15 + 5" },
                { "20 - 5" },
                { "4 * 5" },
                { "20 / 2" }
        };
    }

    @Test(dataProvider = "multipleArithmeticOperations", groups = {"arithmetic"})
    public void testMultipleArithmeticOperations(String operation) {
        String code = String.format("result = %s;\nprint result;", operation);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        parser.execute(code, new PrintStream(outputStream));
        Assert.assertTrue(!outputStream.toString().contains("Error") && !outputStream.toString().isEmpty());
    }

    @Test(groups = {"errorHandling"}, expectedExceptions = IllegalArgumentException.class)
    public void testUndefinedVariableThrowsException() {
        String code = "print a;";
        parser.execute(code, System.out);
    }

    @Test(groups = {"arithmetic"})
    public void testArithmeticOperationsBasic() {
        String code = "x = 10 + 5;\nprint x;";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        parser.execute(code, new PrintStream(outputStream));
        Assert.assertEquals("15\r\n", outputStream.toString());
    }

    @Test(groups = {"errorHandling"}, expectedExceptions = UnsupportedOperationException.class)
    public void testDivisionByZeroThrowsException() {
        String code = "x = 10 / 0;";
        parser.execute(code, System.out);
    }

    @Test(groups = {"arithmetic"})
    public void testMathOperationsWithParentheses() {
        String code = "result = (10 + 5) * 2;\nprint result;";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        parser.execute(code, new PrintStream(outputStream));
        Assert.assertEquals("30\r\n", outputStream.toString());
    }

    @Test(groups = {"basic"})
    public void testVariableReassignment() {
        String code = "x = 10;\nx = 20;\nprint x;";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        parser.execute(code, new PrintStream(outputStream));
        Assert.assertEquals("20\r\n", outputStream.toString());
    }

}