package nl.han.ica.icss.parser.maartenTests;

import nl.han.ica.icss.Pipeline;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.checker.Checker;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Deze testen zijn geschreven door maarten en worden gebruikt om de basis werking van alles te testen:
 * https://gist.github.com/MaartenGDev/9cd3caf7d1921449b0f4232461c8bc27
 * De code van de parser, checker, transform en generator is wel geschreven door mij: Steven Krol
 */
public class CheckingTest {
    @Test
    void CH01_should_4_variable_should_be_defined() {
        Pipeline pipeline = new Pipeline();

        String inputText = "h1 { color: red; width: Test; height: SecondTest; }";

        pipeline.parseString(inputText);

        AST ast = pipeline.getAST();
        new Checker().check(ast);

        List<VariableReference> results = TestingHelper.findInstancesOf(ast.root, VariableReference.class, new LinkedList<>());
        List<VariableReference> withErrors = results.stream().filter(ASTNode::hasError).collect(Collectors.toList());

        assertEquals(2, withErrors.size());
    }

    @Test
    void CH02_should_2_subtract_and_add_operation_with_same_literal_on_both_sides() {
        Pipeline pipeline = new Pipeline();

        String inputText = "h1 { width: 10px + 10px; width: 10px + 10%; width: 10px + 10px; width: 10px - 10px; width: 10px - 10%; }";

        pipeline.parseString(inputText);

        AST ast = pipeline.getAST();
        new Checker().check(ast);

        List<Operation> results = TestingHelper.findInstancesOf(ast.root, Operation.class, new LinkedList<>());
        List<ASTNode> withErrors = results.stream().filter(ASTNode::hasError).collect(Collectors.toList());

        assertEquals(2, withErrors.size());
    }

    @Test
    void CH02_should_2_multiply_with_literal_and_scalar() {
        Pipeline pipeline = new Pipeline();

        String inputText = "h1 { width: 10px * 2; width: 10px * 10px; width: 10px * 10%; }";

        pipeline.parseString(inputText);

        AST ast = pipeline.getAST();
        new Checker().check(ast);

        List<Operation> results = TestingHelper.findInstancesOf(ast.root, Operation.class, new LinkedList<>());
        List<ASTNode> withErrors = results.stream().filter(ASTNode::hasError).collect(Collectors.toList());

        assertEquals(2, withErrors.size());
    }

    @Test
    void CH03_should_3_colors_should_not_be_used_in_operations() {
        Pipeline pipeline = new Pipeline();

        String inputText = "MyVar := #ffffff; h1 { width: 10px * 2; width: 10px + MyVar; width: 10px * #ffffff; }";

        pipeline.parseString(inputText);

        AST ast = pipeline.getAST();
        new Checker().check(ast);

        List<Operation> results = TestingHelper.findInstancesOf(ast.root, Operation.class, new LinkedList<>());
        List<ASTNode> withErrors = results.stream().filter(ASTNode::hasError).collect(Collectors.toList());

        assertEquals(2, withErrors.size());
    }



    @Test
    void CH04_should_4_used_values_should_match_up_with_properties() {
        Pipeline pipeline = new Pipeline();

        String inputText = "h1 { color: #ffffff; height: #ffffff; width: 10px; width: #ffffff; }";

        pipeline.parseString(inputText);

        AST ast = pipeline.getAST();
        new Checker().check(ast);

        List<Declaration> results = TestingHelper.findInstancesOf(ast.root, Declaration.class, new LinkedList<>());
        List<ASTNode> withErrors = results.stream().filter(ASTNode::hasError).collect(Collectors.toList());

        assertEquals(2, withErrors.size());
    }

    @Test
    void CH05_should_4_value_in_if_clause_should_be_boolean() {
        Pipeline pipeline = new Pipeline();

        String inputText = "Result := TRUE; MyColor := #ffffff; h1 { if[TRUE] { width: 10px; }  if[Result]{ height: 20px; }  if[MyColor]{ height: 20px; } }";

        pipeline.parseString(inputText);

        AST ast = pipeline.getAST();
        new Checker().check(ast);

        List<IfClause> results = TestingHelper.findInstancesOf(ast.root, IfClause.class, new LinkedList<>());

        int ifClausesWithInvalidExpressions = 0;

        for (IfClause node : results) {
            if (node.hasError() || node.conditionalExpression.hasError()) {
                ifClausesWithInvalidExpressions++;
            }
        }

        assertEquals(1, ifClausesWithInvalidExpressions);
    }
}
