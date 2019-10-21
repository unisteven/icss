package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.HashMap;
import java.util.Iterator;
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
        this.evalExpression(ast.root, ast.root);
        // remove all variable assignments
        this.removeVariableAssignment(ast.root, ast.root, null);
    }

    public void removeVariableAssignment(ASTNode node, ASTNode parent, Iterator iterator) {
        if (node instanceof VariableAssignment) {
            iterator.remove();
            parent.removeChild(node);
        }
        Iterator<ASTNode> iter = node.getChildren().iterator();
        while (iter.hasNext()) {
            this.removeVariableAssignment(iter.next(), node, iter);
        }
    }

    public void evalExpression(ASTNode node, ASTNode parent){
        if(node instanceof Expression){
            // if it is an expression calculate the value and replace it of the root.
            Literal l = this.calculateExpression((Expression) node);
            parent.removeChild(node);
            parent.addChild(l);
        }
        for(ASTNode nodes : node.getChildren()){
            this.evalExpression(nodes, node);
        }
    }

    public Literal calculateExpression(Expression expression){
        if(expression instanceof Literal){
            return (Literal) expression;
        }
        if(expression instanceof VariableReference){
                Expression var = this.variables.get(((VariableReference) expression).name);
                return this.calculateExpression(var);
        }
        if(expression instanceof Operation){
            Literal lr = this.calculateExpression(((Operation) expression).rhs);
            Literal ll = this.calculateExpression(((Operation) expression).lhs);
            // do the operation
            return this.solveOperation((Operation) expression, ll, lr);
        }
        return null;
    }


    private Literal solveOperation(Operation operation, Expression expressionleft, Expression expressionright) {
        if (operation instanceof MultiplyOperation) {
            if (expressionleft instanceof ScalarLiteral) {
                ScalarLiteral literalL = (ScalarLiteral) expressionleft;
                // calculate this value times the right
                if (expressionright instanceof PixelLiteral) {
                    int value = literalL.value * ((PixelLiteral) expressionright).value;
                    return new PixelLiteral(value);
                }
                if (expressionright instanceof PercentageLiteral) {
                    int value = literalL.value * ((PercentageLiteral) expressionright).value;
                    return new PercentageLiteral(value);
                }
                if (expressionright instanceof ScalarLiteral) {
                    int value = literalL.value * ((ScalarLiteral) expressionright).value;
                    return new ScalarLiteral(value);
                }
            }
            if (expressionright instanceof ScalarLiteral) {
                ScalarLiteral literalR = (ScalarLiteral) expressionright;
                // calculate this value times the left
                if (expressionleft instanceof PixelLiteral) {
                    int value = literalR.value * ((PixelLiteral) expressionleft).value;
                    return new PixelLiteral(value);
                }
                if (expressionleft instanceof PercentageLiteral) {
                    int value = literalR.value * ((PercentageLiteral) expressionleft).value;
                    return new PercentageLiteral(value);
                }
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
