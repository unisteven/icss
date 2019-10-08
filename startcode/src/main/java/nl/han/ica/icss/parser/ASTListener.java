package nl.han.ica.icss.parser;

import java.util.Stack;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {

    //Accumulator attributes:
    private AST ast;

    //Use this to keep track of the parent nodes when recursively traversing the ast
    private Stack<ASTNode> currentContainer;

    public ASTListener() {
        ast = new AST();
        currentContainer = new Stack<>();
    }

    public AST getAST() {

        return ast;
    }

    @Override
    public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
        Stylesheet styleSheet = new Stylesheet();
        this.ast.root = styleSheet;
        this.currentContainer.push(styleSheet);
    }

    @Override
    public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
//        this.ast.root = (Stylesheet) this.currentContainer.pop();
    }

    @Override
    public void enterStyleRule(ICSSParser.StyleRuleContext ctx) {
        Stylerule stylerule = new Stylerule();
        this.currentContainer.peek().addChild(stylerule);
        this.currentContainer.push(stylerule);
    }
    @Override
    public void exitStyleRule(ICSSParser.StyleRuleContext ctx) {
        this.currentContainer.pop();
    }


    @Override
    public void enterTagSelector(ICSSParser.TagSelectorContext ctx) {
        TagSelector tagSelector = new TagSelector(ctx.getText());
        this.currentContainer.peek().addChild(tagSelector);
        this.currentContainer.push(tagSelector);
    }

    @Override
    public void exitTagSelector(ICSSParser.TagSelectorContext ctx) {
        this.currentContainer.pop();
    }

    @Override
    public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
        Declaration declaration = new Declaration(ctx.propertyName().getText());
        this.currentContainer.peek().addChild(declaration);
        this.currentContainer.push(declaration);
    }

    @Override
    public void enterColorLiteral(ICSSParser.ColorLiteralContext ctx) {
        ColorLiteral colorLiteral = new ColorLiteral(ctx.getText());
        this.currentContainer.peek().addChild(colorLiteral);
    }

    @Override
    public void enterPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
        PixelLiteral pixelLiteral = new PixelLiteral(ctx.getText());
        this.currentContainer.peek().addChild(pixelLiteral);
    }

    @Override
    public void exitPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
        this.currentContainer.pop();
    }

    @Override
    public void exitColorLiteral(ICSSParser.ColorLiteralContext ctx) {
        this.currentContainer.pop();
    }

    @Override
    public void enterIdSelector(ICSSParser.IdSelectorContext ctx) {
        IdSelector idSelector = new IdSelector(ctx.getText());
        this.currentContainer.peek().addChild(idSelector);
        this.currentContainer.push(idSelector);
    }

    @Override
    public void exitIdSelector(ICSSParser.IdSelectorContext ctx) {
        this.currentContainer.pop();
    }

    @Override
    public void enterClassSelector(ICSSParser.ClassSelectorContext ctx) {
        ClassSelector classSelector = new ClassSelector(ctx.getText());
        this.currentContainer.peek().addChild(classSelector);
        this.currentContainer.push(classSelector);
    }

    @Override
    public void exitClassSelector(ICSSParser.ClassSelectorContext ctx) {
        this.currentContainer.pop();
    }
    
}
