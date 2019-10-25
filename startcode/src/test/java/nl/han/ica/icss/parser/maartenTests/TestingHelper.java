package nl.han.ica.icss.parser.maartenTests;

import nl.han.ica.icss.ast.ASTNode;

import java.util.List;

public class TestingHelper {
    public static <T> List<T> findInstancesOf(ASTNode ast, Class<T> clazz, List<T> instances) {
        for (ASTNode astNode : ast.getChildren()) {

            if (clazz.isInstance(astNode)) {
                instances.add((T) astNode);
            }

            findInstancesOf(astNode, clazz, instances);
        }

        return instances;
    }
}
