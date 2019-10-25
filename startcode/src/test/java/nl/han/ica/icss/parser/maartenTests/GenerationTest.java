package nl.han.ica.icss.parser.maartenTests;

import nl.han.ica.icss.Pipeline;
import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.checker.Checker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GenerationTest {
    @Test
    void GE01_must_5_contains_property_names_and_values() {
        Pipeline pipeline = new Pipeline();

        String inputText = "Test := 10px; SecondTest := 300px; h1 { width: Test; height: SecondTest; } h3 {}";

        pipeline.parseString(inputText);

        AST ast = pipeline.getAST();
        new Checker().check(ast);

        pipeline.transform();
        String generatedCss = pipeline.generate();

        // Contains selectors
        assertTrue(generatedCss.contains("h1"));

        assertTrue(generatedCss.contains("width"));
        assertTrue(generatedCss.contains("10px"));

        assertTrue(generatedCss.contains("height"));
        assertTrue(generatedCss.contains("300px"));

        assertTrue(generatedCss.contains("h3"));

        // Does not contain variables
        assertFalse(generatedCss.contains("Test"));
        assertFalse(generatedCss.contains("SecondTest"));
    }

    @Test
    void GE02_should_5_is_formatted() {
        Pipeline pipeline = new Pipeline();

        String inputText = "Test := 10px; SecondTest := 300px; h1 { width: Test; height: SecondTest; } h3 {}";

        pipeline.parseString(inputText);

        AST ast = pipeline.getAST();
        new Checker().check(ast);

        pipeline.transform();
        String generatedCss = pipeline.generate();

        assertTrue(generatedCss.contains(System.lineSeparator()));
    }
}