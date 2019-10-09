package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;

import java.util.HashMap;
import java.util.Map;

public class RemoveIf implements Transform {

    private Map<String, Expression> variables;

    public RemoveIf() {
        variables = new HashMap<>();
    }

    @Override
    public void apply(AST ast) {
        this.variables = ast.variables;
        this.evauluateStatements(ast.root, ast.root);
    }

    public void evauluateStatements(ASTNode node, ASTNode parent){
        if(node instanceof IfClause){
            IfClause ifClause = (IfClause) node;
            if(ifClause.conditionalExpression instanceof BoolLiteral){
                BoolLiteral boolLiteral = (BoolLiteral) ifClause.conditionalExpression;
                if(boolLiteral.value){
                    // set body
                    parent.removeChild(node);
                    for(ASTNode nodes : ifClause.body){
                        parent.addChild(nodes); // add all items of the body.
                        // evaluate the entire new body
                        this.evauluateStatements(nodes, parent);
                    }
                }else{
                    parent.removeChild(node);
                    // remove whole thing
                }
            }
            if(ifClause.conditionalExpression instanceof VariableReference){
                VariableReference variableReference = (VariableReference) ifClause.conditionalExpression;
                Expression expression = this.variables.get(variableReference.name);
                ifClause.conditionalExpression = expression;
                this.evauluateStatements(ifClause, parent);
                return;
            }
        }


        for(ASTNode nodes : node.getChildren()){
            this.evauluateStatements(nodes, node);
        }
    }
}
