package yokwe.family.register;

import java.io.File;
import java.util.Arrays;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import yokwe.family.register.antlr.FamilyRegisterBaseVisitor;
import yokwe.family.register.antlr.FamilyRegisterLexer;
import yokwe.family.register.antlr.FamilyRegisterParser;
import yokwe.util.FileUtil;

public class ReadFile {
	private static final org.slf4j.Logger logger = yokwe.util.LoggerUtil.getLogger();

	public static void main(String[] args) {
		logger.info("START");
		
		process();
		
		logger.info("STOP");
	}

	private static void process() {
		File[] files = new File("data/family-register").listFiles(o -> o.getName().endsWith(".txt"));
		Arrays.sort(files);
		
		for(var file: files) {
			logger.info("file  {}", file.getPath());
			
			final FamilyRegisterLexer lexer;
			{
				var string = FileUtil.read().file(file);
				var charStream = CharStreams.fromString(string);
				lexer = new FamilyRegisterLexer(charStream);
			}
			{
				lexer.reset();
				var parser = new FamilyRegisterParser(new CommonTokenStream(lexer));
				var tree = parser.body();
				logger.info("==== visitor ====");
				var visitor = new Visitor();
				visitor.visitBody(tree);
				logger.info("==== ======= ====");
			}
		}
	}
	
	public static class Context {
		
	}
	
	public static class Visitor extends FamilyRegisterBaseVisitor<Context> {
		Context context = new Context();
		
		//
		// AddressBlock
		//
		@Override
		public Context visitAddressBlock(FamilyRegisterParser.AddressBlockContext ctx) {
			var address = ctx.value.getText();
			logger.info("address  {}", address);
			for (var e: ctx.changeValue()) {
				var alias = e.value.getText();
				logger.info("  alias  {}", alias);
			}
			return visitChildren(ctx);
		}
		
		
		//
		// PersonBlock
		//
		@Override
		public Context visitPersonBlock(FamilyRegisterParser.PersonBlockContext ctx) {
			// addressValue fatherValue relationValue familyNameValue nameValue
			var address = ctx.addressValue().value.getText();
			var father = ctx.fatherValue().value.getText();
			var relation = ctx.relationValue().value.getText();
			var familyName = ctx.familyNameValue().value.getText();
			var name = ctx.nameValue().value.getText();
			
			logger.info("person  {}  {}  {}  {}  {}", address, father, relation, familyName, name);
			return visitChildren(ctx);
		}
		@Override
		public Context visitPersonItemBirth(FamilyRegisterParser.PersonItemBirthContext ctx) {
			var date = ctx.date.getText();
			logger.info("  {}  BIRTH", date);
			return visitChildren(ctx);
		}
		@Override
		public Context visitPersonItemDeath(FamilyRegisterParser.PersonItemDeathContext ctx) {
			var date = ctx.date.getText();
			logger.info("  {}  DEATH", date);
			return visitChildren(ctx);
		}
		@Override
		public Context visitPersonItemMarriage(FamilyRegisterParser.PersonItemMarriageContext ctx) {
			var date   = ctx.date.getText();
			var spouse = ctx.spouse.getText();
			logger.info("  {}  MARRIAGE  {}", date, spouse);
			return visitChildren(ctx);
		}
		@Override
		public Context visitPersonItemJoin(FamilyRegisterParser.PersonItemJoinContext ctx) {
			var date    = ctx.date.getText();
			var address = ctx.address.getText();
			logger.info("  {}  JOIN  {}", date, address);
			return visitChildren(ctx);
		}
		@Override
		public Context visitPersonItemSeparate(FamilyRegisterParser.PersonItemSeparateContext ctx) {
			var date    = ctx.date.getText();
			var address = ctx.address.getText();
			logger.info("  {}  SEPARATE  {}", date, address);
			return visitChildren(ctx);
		}
		@Override
		public Context visitPersonItemBranch(FamilyRegisterParser.PersonItemBranchContext ctx) {
			var date    = ctx.date.getText();
			var address = ctx.address.getText();
			logger.info("  {}  BRANCH  {}", date, address);
			return visitChildren(ctx);
		}
		@Override
		public Context visitPersonItemRetirement(FamilyRegisterParser.PersonItemRetirementContext ctx) {
			var date    = ctx.date.getText();
			var newHead = ctx.newHead.getText();
			logger.info("  {}  RETIREMENT  {}", date, newHead);
			return visitChildren(ctx);
		}
		@Override
		public Context visitPersonItemHeadOfHouseBranch(
				FamilyRegisterParser.PersonItemHeadOfHouseBranchContext ctx) {
			var date    = ctx.date.getText();
			logger.info("  {}  HEAD_OF_HOUSE BRANCH", date);
			return visitChildren(ctx);
		}
		@Override
		public Context visitPersonItemHeadOfHouseDeath(
				FamilyRegisterParser.PersonItemHeadOfHouseDeathContext ctx) {
			var date = ctx.date.getText();
			var name = ctx.prevHead.getText();
			logger.info("  {}  HEAD_OF_HOUSE DEATH  {}", date, name);
			return visitChildren(ctx);
		}
		@Override
		public Context visitPersonItemHeadOfHouseRetirement(
				FamilyRegisterParser.PersonItemHeadOfHouseRetirementContext ctx) {
			var date = ctx.date.getText();
			var name = ctx.prevHead.getText();
			logger.info("  {}  HEAD_OF_HOUSE RETIREMENT  {}", date, name);
			return visitChildren(ctx);
		}
		@Override
		public Context visitPersonItemInheritance(FamilyRegisterParser.PersonItemInheritanceContext ctx) {
			var date = ctx.date.getText();
			logger.info("  {}  INHERIT", date);
			return visitChildren(ctx);
		}
		@Override
		public Context visitPersonItemDisinheritance(FamilyRegisterParser.PersonItemDisinheritanceContext ctx) {
			var date = ctx.date.getText();
			logger.info("  {}  DISINHERIT", date);
			return visitChildren(ctx);
		}
		
		
		//
		// Marriage
		//
		@Override
		public Context visitMarriageBlock(FamilyRegisterParser.MarriageBlockContext ctx) {
			// familyNameValue husbandValue wifeValue
			var familyName = ctx.familyNameValue().value.getText();
			var husband    = ctx.husbandValue().value.getText();
			var wife       = ctx.wifeValue().value.getText();
			
			logger.info("marriage  {}  {}  {}", familyName, husband, wife);
			return visitChildren(ctx);
		}

		@Override
		public Context visitMarriageItemMarriage(FamilyRegisterParser.MarriageItemMarriageContext ctx) {
			var date = ctx.date.getText();
			var type = ctx.type.getText();
			logger.info("  {}  MARRIAGE  {}", date, type);
			return visitChildren(ctx);
		}

		@Override
		public Context visitMarriageItemDivorce(FamilyRegisterParser.MarriageItemDivorceContext ctx) {
			var date = ctx.date.getText();
			var type = ctx.type.getText();
			logger.info("  {}  DIVORCE  {}", date, type);
			return visitChildren(ctx);
		}

		@Override
		public Context visitMarriageItemRelation(FamilyRegisterParser.MarriageItemRelationContext ctx) {
			var date = ctx.date.getText();
			var type = ctx.type.getText();
			var name = ctx.name.getText();
			logger.info("  {}  RELATION  {}  {}", date, type, name);
			return visitChildren(ctx);
		}
	}
}
