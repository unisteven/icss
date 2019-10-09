package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.*;

public class Generator {

	public String generate(AST ast) {
		StringBuilder sb = new StringBuilder();
		generateRecursive(ast.root, sb, 0);
        return sb.toString();
	}


	private void generateRecursive(ASTNode node, StringBuilder sb, int level){
		for (int i = 0; i < level; i++) {
			sb.append("  ");
		}
		sb.append(node.getNodeLabel());
		sb.append("\n");
		for(ASTNode nodes : node.getChildren()){
			this.generateRecursive(nodes, sb, level + 1);
		}

	}
}
