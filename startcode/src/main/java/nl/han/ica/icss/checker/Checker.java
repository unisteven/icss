package nl.han.ica.icss.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.MultiplyOperation;

public class Checker {

    private Map<String, Expression> variables;
    private Map<String, List<Class>> validCombinations = new HashMap<>();
    private ASTNode currentVariableAssignment;
    private ASTNode currentIfClauseAssignment;

    public void check(AST ast) {
        variables = new HashMap<>();
        ast.variables = variables;
        validCombinations.put("color", new ArrayList<>());
        validCombinations.get("color").add(ColorLiteral.class);

        validCombinations.put("background-color", new ArrayList<>());
        validCombinations.get("background-color").add(ColorLiteral.class);

        validCombinations.put("width", new ArrayList<>());
        validCombinations.get("width").add(PixelLiteral.class);
        validCombinations.get("width").add(PercentageLiteral.class);

        validCombinations.put("height", new ArrayList<>());
        validCombinations.get("height").add(PixelLiteral.class);
        validCombinations.get("height").add(PercentageLiteral.class);
        this.checkRecursively(ast.root);
    }


    public void checkRecursively(ASTNode node) {
        // do the checks
        checkvariable(node);
        checkOperations(node);
        checkIfClauses(node);
        checkValidCombination(node, node);
        if (node.getChildren().size() > 0) {
            for (ASTNode astNode : node.getChildren()) {
                this.checkRecursively(astNode);
            }
        }

    }

    private void checkValidCombination(ASTNode node, ASTNode parent) {
        if(node instanceof Literal){
            if(parent instanceof Declaration) {
                Declaration declaration = (Declaration) parent;
                List<Class> list = this.validCombinations.get(declaration.property.name);
                if (list == null) {
                    return;
                }
                boolean invalid = true; // invalid until proven
                for (Class c : list) {
                    if (c.equals(node.getClass())) {
                        invalid = false;
                    }
                }
                if (invalid) {
                    declaration.setError("Invalid combination of types: " + declaration.property.name + " = " + declaration.expression.getNodeLabel());
                }
            }
        }
        if (node instanceof Declaration) {
            Declaration declaration = (Declaration) node;
            List<Class> list = this.validCombinations.get(declaration.property.name);
            if (list == null) {
                return;
            }
            Expression expression = declaration.expression;
            if (expression instanceof VariableReference) {
                expression = variables.get(((VariableReference) expression).name);
                this.checkValidCombination(expression, node);
                return;
            }
            if (expression instanceof Operation) {
                Operation operation = (Operation) expression;
                this.checkValidCombination(operation.lhs, node);
                this.checkValidCombination(operation.rhs, node);
            }
        }
    }

    private Literal checkOperations(ASTNode node) {
        if (node == null) {
            return null; // end condition
        }

        if (!(node instanceof Expression)) {
            return null;
        }

        Expression expression = (Expression) node;

        if (expression instanceof Literal) {
            return (Literal) expression;
        }
        if (expression instanceof VariableReference) {
            Expression var = this.variables.get(((VariableReference) expression).name);
            return this.checkOperations(var);
        }
        if (expression instanceof Operation) {
            Literal literalR = this.checkOperations(((Operation) expression).rhs);
            Literal literalL = this.checkOperations(((Operation) expression).lhs);
            if(literalL == null || literalR == null){
                return null;
            }
            // check if it matches the requirements
            if (!(expression instanceof MultiplyOperation)) {
                // only check this if it is not a multiply operation because that should accept scalar values too
                if (!(literalL.getClass().equals(literalR.getClass()))) {
                    // they are not of the same type on the left and right side so error.
                    expression.setError("There is an operation with two different types on line: ");
                }
                return null;
            }
            // check if the literal is a colour
            if (literalL instanceof ColorLiteral || literalR instanceof ColorLiteral) {
                expression.setError("You can't use an operation on the Color type on line: ");
                return null;
            }

            if (!((literalL instanceof ScalarLiteral) || (literalR instanceof ScalarLiteral))) {
                expression.setError("You can't multiply two non scalar values on line: ");
                return null;
            }
            if(literalL instanceof ScalarLiteral){
                return literalR; // return the correct type.
            }else{
                return literalL;
            }
        }
        return null;
    }

    // check if ifclause has a boolean value
    public void checkIfClauses(ASTNode node) {
        if(node instanceof IfClause){
            this.currentIfClauseAssignment = node;
        }
        if(this.currentIfClauseAssignment == null){
            return; // if there is no current if clause no need to check.
        }
        if(node instanceof VariableReference){
            this.checkIfClauses(this.variables.get(((VariableReference) node).name));
        }
        if(node instanceof Literal){
            if(!(node instanceof BoolLiteral)){
                this.currentIfClauseAssignment.setError("The ifclause is not of the type boolean");
            }
            this.currentIfClauseAssignment = null; // reset current ifclause
        }

    }

    // check if variables are defined
    public void checkvariable(ASTNode node) {
        if (node instanceof VariableAssignment) {
            this.variables.put(((VariableAssignment) node).name.name, null);
            this.currentVariableAssignment = node;
        }
        if (node instanceof VariableReference) {
            if (node instanceof BoolLiteral) {
                // booleans are special cases
                return;
            }
            ASTNode var = this.variables.get(((VariableReference) node).name);
            if (var != null) {
                this.checkvariable(var);
                return;
            }
            if (!(this.variables.containsKey(((VariableReference) node).name))) {
                node.setError("Referencing undefined variable");
            }
        }
        if ((node instanceof Expression) && this.currentVariableAssignment != null) {
            this.addVariable((Expression) node);
        }
    }

    private void addVariable(Expression expression) {
        if (expression instanceof VariableReference) {
            String varName = ((VariableAssignment) this.currentVariableAssignment).name.name;
            this.variables.remove(varName);
            this.variables.put(varName, this.variables.get(((VariableReference) expression).name));
        } else {
            String varName = ((VariableAssignment) this.currentVariableAssignment).name.name;
            this.variables.remove(varName);
            this.variables.put(varName, expression);
            this.currentVariableAssignment = null;
        }
    }

}
