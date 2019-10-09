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
        checkValidCombination(node);
        if (node.getChildren().size() > 0) {
            for (ASTNode astNode : node.getChildren()) {
                this.checkRecursively(astNode);
            }
        }

    }

    private void checkValidCombination(ASTNode node) {
        if (node instanceof Declaration) {
            Declaration declaration = (Declaration) node;
            List<Class> list = this.validCombinations.get(declaration.property.name);
            if (list == null) {
                // TODO warning if a combination has not been set it means it is always valid.
                return;
            }
            Expression expression = declaration.expression;
            if (expression instanceof VariableReference) {
                expression = variables.get(((VariableReference) expression).name);
                this.checkValidCombination(expression);
                return;
            }
            if (expression instanceof Operation) {
                Operation operation = (Operation) expression;
                this.checkValidCombination(operation.lhs);
                this.checkValidCombination(operation.rhs);
                return;
            }
            boolean invalid = true; // invalid until proven
            for (Class c : list) {
                if (c.equals(expression.getClass())) {
                    invalid = false;
                }
            }
            if (invalid) {
                node.setError("Invalid combination of types: " + declaration.property.name + " = " + expression.getNodeLabel());
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
                operation.lhs = literalL;
                this.checkOperations(operation);
                return;
            }
        }
        if (operation.rhs instanceof VariableReference) {
            VariableReference reference = (VariableReference) operation.rhs;
            if (this.variables.containsKey(reference.name)) {
                literalR = this.variables.get(reference.name);
                operation.rhs = literalR;
                this.checkOperations(operation);
                return;
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
    public void checkIfClauses(ASTNode node) {
        if (node instanceof IfClause) {
            this.currentIfClauseAssignment = node;
        }
        if (this.currentIfClauseAssignment != null) {
            if (((IfClause) this.currentIfClauseAssignment).conditionalExpression instanceof VariableReference) {
                return;
            }
            if (((IfClause) this.currentIfClauseAssignment).conditionalExpression instanceof BoolLiteral) {
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
            if (node instanceof BoolLiteral) {
                // booleans are special cases
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

    private void addVariable(Expression literal) {
        if(literal instanceof VariableReference){
            String varName = ((VariableAssignment) this.currentVariableAssignment).name.name;
            this.variables.remove(varName);
            this.variables.put(varName, this.variables.get(((VariableReference) literal).name));
        }
        if(literal instanceof Literal) {
            String varName = ((VariableAssignment) this.currentVariableAssignment).name.name;
            this.variables.remove(varName);
            this.variables.put(varName, literal);
            this.currentVariableAssignment = null;
        }
    }

}
