package nl.han.ica.icss.checker;

import java.util.HashMap;
import java.util.Map;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.MultiplyOperation;

public class Checker {

    private Map<String, Expression> variables;
    private ASTNode currentVariableAssignment;
    private ASTNode currentIfClauseAssignment;

    public void check(AST ast) {
        variables = new HashMap<>();
        this.checkRecursively(ast.root);
    }


    public void checkRecursively(ASTNode node) {
        // do the checks
        checkvariable(node);
        checkOperations(node);
        this.checkIfClauses(node);
        if (node.getChildren().size() > 0) {
            for (ASTNode astNode : node.getChildren()) {
                this.checkRecursively(astNode);
            }
        }

    }

    private void checkOperations(ASTNode node) {
        if (!(node instanceof Operation)) {
            return;
        }
        Operation operation = (Operation) node;
        Expression literalL = operation.lhs;
        Expression literalR = operation.rhs;
        // check if operation is in the variables list.
        if (operation.lhs instanceof VariableReference) {
            VariableReference reference = (VariableReference) operation.lhs;
            if (this.variables.containsKey(reference.name)) {
                literalL = this.variables.get(reference.name);
            }
        }
        if (operation.rhs instanceof VariableReference) {
            VariableReference reference = (VariableReference) operation.rhs;
            if (this.variables.containsKey(reference.name)) {
                literalR = this.variables.get(reference.name);
            }
        }
        // check if both sides are of the same type
        if (!(literalL.getClass().equals(literalR.getClass()))) {
            // they are not of the same type on the left and right side so error.
            operation.setError("There is an operation with two different types on line: ");
        }
        // check if the literal is a colour
        if (literalL instanceof ColorLiteral || literalR instanceof ColorLiteral) {
            operation.setError("You can't use an operation on the Color type on line: ");
        }

        if (operation instanceof MultiplyOperation) {
            if (!((literalL instanceof ScalarLiteral) && (literalR instanceof ScalarLiteral))) {
                operation.setError("You can't multiply two non scalar values on line: ");
            }
        }
    }

    // check if ifclause has a boolean value
    public void checkIfClauses(ASTNode node){
        if(node instanceof IfClause){
            this.currentIfClauseAssignment = node;
        }
        if(this.currentIfClauseAssignment != null){
            if(((IfClause)this.currentIfClauseAssignment).conditionalExpression instanceof VariableReference){
                return;
            }
            if(((IfClause)this.currentIfClauseAssignment).conditionalExpression instanceof BoolLiteral){
                return;
            }
            this.currentIfClauseAssignment.setError("The ifclause is not of the type boolean");
            this.currentIfClauseAssignment = null;
        }

    }

    // check if variables are defined
    public void checkvariable(ASTNode node) {
        if (node instanceof VariableAssignment) {
            this.variables.put(((VariableAssignment) node).name.name, null);
            this.currentVariableAssignment = node;
        }
        if (node instanceof VariableReference) {
            if(node instanceof BoolLiteral){
                // booleans are special cases
                return;
            }
            if (!(this.variables.containsKey(((VariableReference) node).name))) {
                node.setError("Referencing undefined variable");
            }
        }
        if (node instanceof Literal && this.currentVariableAssignment != null) {
            this.addVariable((Literal) node);
        }
    }

    private void addVariable(Literal literal) {
        String varName = ((VariableAssignment) this.currentVariableAssignment).name.name;
        this.variables.remove(varName);
        this.variables.put(varName, literal);
        this.currentVariableAssignment = null;
    }

}
