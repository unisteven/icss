package nl.han.ica.icss.checker;

import java.util.HashMap;
import java.util.Map;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ScalarLiteral;

public class Checker {

    private Map<String, Expression> variables;
    private ASTNode currentVariableAssignment;

    public void check(AST ast) {
        variables = new HashMap<>();
        this.checkRecursively(ast.root);
    }


    public void checkRecursively(ASTNode node){
        // do the checks
        checkvariable(node);
        if(node.getChildren().size() > 0) {
            for (ASTNode astNode : node.getChildren()) {
                this.checkRecursively(astNode);
            }
        }

    }

    // check if variables are defined
    public void checkvariable(ASTNode node){
        if(node instanceof VariableAssignment){
            this.currentVariableAssignment = node;
            this.variables.put(((VariableAssignment) node).name.name, null);
        }
        if(node instanceof VariableReference){
            if(!(this.variables.containsKey(((VariableReference) node).name))){
                node.setError("Referencing undefined variable");
            }
        }
        if(node instanceof Literal){
            this.addVariable((Literal) node);
        }
    }

    private void addVariable(Literal literal){
        String varName = ((VariableAssignment) this.currentVariableAssignment).name.name;
        this.variables.remove(varName);
        this.variables.put(varName, literal);
    }

}
