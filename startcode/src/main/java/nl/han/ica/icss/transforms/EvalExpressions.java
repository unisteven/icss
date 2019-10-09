package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class EvalExpressions implements Transform {

    private Map<String, Expression> variables;

    public EvalExpressions() {
        variables = new HashMap<>();
    }

    @Override
    public void apply(AST ast) {
        variables = ast.variables;
        this.evaluateExpression(ast.root, ast.root);
    }

    public void evaluateExpression(ASTNode node, ASTNode parent) {

        if (node instanceof Expression) {
            System.out.println("expression");
            if (node instanceof Operation) {
                Operation operation = (Operation) node;
                if (operation.lhs instanceof Operation) {
                    this.evaluateExpression(operation.lhs, parent);
                    return;
                }
                if (operation.rhs instanceof Operation) {
                    this.evaluateExpression(operation.rhs, parent);
                    return;
                }
                if(operation.lhs instanceof VariableReference){
                    VariableReference reference = (VariableReference) operation.lhs;
                    operation.lhs = this.variables.get(reference.name);
                    this.evaluateExpression(operation, parent);
                    return;
                }
                if(operation.rhs instanceof VariableReference){
                    VariableReference reference = (VariableReference) operation.rhs;
                    operation.rhs = this.variables.get(reference.name);
                    this.evaluateExpression(operation, parent);
                    return;
                }
                Literal literal = solveOperation(operation, operation.lhs, operation.rhs);
                if (literal != null) {
                    parent.removeChild(node);
                    parent.addChild(literal);
//                    node = literal;
                    return;
                }
            }
        }
        for (ASTNode nodes : node.getChildren()) {
            this.evaluateExpression(nodes, node);
        }

    }

    private Literal solveOperation(Operation operation, Expression expressionleft, Expression expressionright) {
        if (operation instanceof MultiplyOperation) {
            if (expressionleft instanceof ScalarLiteral) {
                ScalarLiteral literalL = (ScalarLiteral) expressionleft;
                ScalarLiteral literalR = (ScalarLiteral) expressionright;
                int value = literalL.value * literalR.value;
                return new ScalarLiteral(value);
            }
        }
        if (operation instanceof SubtractOperation) {
            if (expressionleft instanceof ScalarLiteral) {
                ScalarLiteral literalL = (ScalarLiteral) expressionleft;
                ScalarLiteral literalR = (ScalarLiteral) expressionright;
                int value = literalL.value - literalR.value;
                return new ScalarLiteral(value);
            }
            if (expressionleft instanceof PixelLiteral) {
                PixelLiteral literalL = (PixelLiteral) expressionleft;
                PixelLiteral literalR = (PixelLiteral) expressionright;
                int value = literalL.value - literalR.value;
                return new PixelLiteral(value);
            }
            if (expressionleft instanceof PercentageLiteral) {
                PercentageLiteral literalL = (PercentageLiteral) expressionleft;
                PercentageLiteral literalR = (PercentageLiteral) expressionright;
                int value = literalL.value - literalR.value;
                return new PercentageLiteral(value);
            }
        }
        if (operation instanceof AddOperation) {
            if (expressionleft instanceof ScalarLiteral) {
                ScalarLiteral literalL = (ScalarLiteral) expressionleft;
                ScalarLiteral literalR = (ScalarLiteral) expressionright;
                int value = literalL.value + literalR.value;
                return new ScalarLiteral(value);
            }
            if (expressionleft instanceof PixelLiteral) {
                PixelLiteral literalL = (PixelLiteral) expressionleft;
                PixelLiteral literalR = (PixelLiteral) expressionright;
                int value = literalL.value + literalR.value;
                return new PixelLiteral(value);
            }
            if (expressionleft instanceof PercentageLiteral) {
                PercentageLiteral literalL = (PercentageLiteral) expressionleft;
                PercentageLiteral literalR = (PercentageLiteral) expressionright;
                int value = literalL.value + literalR.value;
                return new PercentageLiteral(value);
            }
        }
        return null;
    }
}
