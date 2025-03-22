package yokwe.family.register;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ErrorNode;

import yokwe.family.register.antlr.FamilyRegisterBaseVisitor;
import yokwe.family.register.antlr.FamilyRegisterLexer;
import yokwe.family.register.antlr.FamilyRegisterParser;
import yokwe.family.register.antlr.FamilyRegisterVisitor;
import yokwe.util.FileUtil;

public class FileVisitor {
	private static final org.slf4j.Logger logger = yokwe.util.LoggerUtil.getLogger();

	public static void main(String[] args) {
		logger.info("START");
		
		process();
		
		logger.info("STOP");
	}
	
	public static class MyVisitor extends FamilyRegisterBaseVisitor<Object> implements FamilyRegisterVisitor<Object> {
		@Override public Object visitBody(FamilyRegisterParser.BodyContext ctx) {
			logger.info("body  {}", ctx.getText());
			return visitChildren(ctx);
		}
		
		@Override
		public Object visitPersonBlock(FamilyRegisterParser.PersonBlockContext ctx) {
			logger.info("person  {}", ctx.getText());
			return visitChildren(ctx);
		}
		@Override
		public Object visitMarriageBlock(FamilyRegisterParser.MarriageBlockContext ctx) {
			logger.info("marriage  {}", ctx.getText());
			return visitChildren(ctx);
		}
		
		@Override
		public Object visitAddressValue(FamilyRegisterParser.AddressValueContext ctx) {
				logger.info("address  {}", ctx.value.getText());
				return visitChildren(ctx);
		 }
		 public Object visitNameValue(FamilyRegisterParser.NameValueContext ctx) {
				logger.info("name  {}", ctx.value.getText());
				return visitChildren(ctx);
		 }
		 
		 public Object visitErrorNode(ErrorNode node) {
			logger.info("error node  {}", node.getText());
			return null;
		 }
	}
	
	private static void process() {
		var string = FileUtil.read().file("data/family-register.txt");
		var charStream = CharStreams.fromString(string);
		
		var lexer = new FamilyRegisterLexer(charStream);
		
		{
			lexer.reset();
			var parser = new FamilyRegisterParser(new CommonTokenStream(lexer));
			var tree = parser.body();
			logger.info("==== visitor ====");
			var visitor = new MyVisitor();
			visitor.visitBody(tree);
			logger.info("==== ======= ====");
		}
		
		{
			lexer.reset();
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			tokens.fill();
			logger.info("==== dump token ====");
			for (var token : tokens.getTokens()) {
				logger.info("token  {}  {}  {}  {}", token.getLine(), token.getCharPositionInLine(), token.getType(), token.getText());
			}
			logger.info("==== ==== ===== ====");
		}
		
	}
}
