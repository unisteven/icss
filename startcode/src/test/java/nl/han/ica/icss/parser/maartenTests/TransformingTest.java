package nl.han.ica.icss.parser.maartenTests;

import nl.han.ica.icss.Pipeline;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.checker.Checker;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransformingTest {
    @Test
    void TR01_should_5_replace_variable_expressions() {
        Pipeline pipeline = new Pipeline();

        String inputText = "Test := 10px; SecondTest := 300px; h1 { width: Test; height: SecondTest; }";

        pipeline.parseString(inputText);

        AST ast = pipeline.getAST();
        new Checker().check(ast);

        pipeline.transform();

        List<Declaration> results = TestingHelper.findInstancesOf(ast.root, Declaration.class, new LinkedList<>());

        assertEquals(new PixelLiteral(10), results.get(0).expression);
        assertEquals(new PixelLiteral(300), results.get(1).expression);
    }

    @Test
    void TR01_should_5_should_evaluate_operations() {
        Pipeline pipeline = new Pipeline();

        String inputText = "Test := 300px; h1 { width: 10px + 10px; height: Test - 20px; width: 10px * 2 + Test * 2; }";

        pipeline.parseString(inputText);

        AST ast = pipeline.getAST();
        new Checker().check(ast);

        pipeline.transform();

        List<Declaration> results = TestingHelper.findInstancesOf(ast.root, Declaration.class, new LinkedList<>());

        assertEquals(new PixelLiteral(20), results.get(0).expression);
        assertEquals(new PixelLiteral(280), results.get(1).expression);
        assertEquals(new PixelLiteral(620), results.get(2).expression);
    }


    @Test
    void TR02_should_5_should_replace_if_clauses() {
        Pipeline pipeline = new Pipeline();

        String inputText = "Test := 300px; h1 { if[TRUE]{ width: 10px; if[TRUE]{ height: 40px; } } if[FALSE]{ color: #ffffff;} }";

        pipeline.parseString(inputText);

        AST ast = pipeline.getAST();
        new Checker().check(ast);

        pipeline.transform();

        List<Declaration> results = TestingHelper.findInstancesOf(ast.root, Declaration.class, new LinkedList<>());

        assertEquals(2, results.size());

        assertEquals(new Declaration("width").addChild(new PixelLiteral(10)), results.get(0));
        assertEquals(new Declaration("height").addChild(new PixelLiteral(40)), results.get(1));

    }
}
