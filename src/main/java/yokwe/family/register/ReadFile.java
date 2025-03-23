package yokwe.family.register;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import yokwe.family.register.antlr.FamilyRegisterBaseVisitor;
import yokwe.family.register.antlr.FamilyRegisterLexer;
import yokwe.family.register.antlr.FamilyRegisterParser;
import yokwe.family.register.antlr.FamilyRegisterParser.*;
import yokwe.family.register.type.*;
import yokwe.util.FileUtil;
import yokwe.util.JapaneseDate;
import yokwe.util.UnexpectedException;

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
		
		var context = new Context();
		
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
				logger.info("==================");
				var visitor = new BuildContext(context);
				visitor.visitBody(tree);
				logger.info("==================");
			}
		}
	}
	
	public static class Context {
		public final List<List<String>> addressList  = new ArrayList<>();
		public final List<Person>       personList   = new ArrayList<>();
		public final List<Marriage>     marriageList = new ArrayList<>();
	}
	
	public static class BuildContext extends FamilyRegisterBaseVisitor<Void> {
		public final Context context;
		
		public BuildContext(Context context) {
			this.context = context;
		}
		//
		// AddressBlock
		//
		@Override
		public Void visitAddressBlock(FamilyRegisterParser.AddressBlockContext ctx) {
			var addressList = new ArrayList<String>();
			for(var e: ctx.addressBlockItem()) {
				var value = e.value.getText();
//				logger.info("  value  {}", value);
				addressList.add(value);
			}
			logger.info("address  {}", addressList);
			context.addressList.add(addressList);
			
			return visitChildren(ctx);
		}
		
		
		//
		// PersonBlock
		//
		@Override
		public Void visitPersonBlock(FamilyRegisterParser.PersonBlockContext ctx) {
			// addressValue fatherValue relationValue familyNameValue nameValue
			var address    = ctx.addressValue().value.getText();
			var father     = ctx.fatherValue().value.getText();
			var relation   = Relation.fromString(ctx.relationValue().value.getText());
			var familyName = ctx.familyNameValue().value.getText();
			var name       = ctx.nameValue().value.getText();
						
			// build list
			var list = new ArrayList<Person.Item>();
			for(var e: ctx.personItemBlock().personItemValue()) {
				if (e instanceof PersonItemBirthContext) {
					var item = (PersonItemBirthContext)e;
					var date = JapaneseDate.getInstance(item.date.getText());
					list.add(Person.Item.birth(date));
				} else if (e instanceof PersonItemDeathContext) {
					var item = (PersonItemDeathContext)e;
					var date = JapaneseDate.getInstance(item.date.getText());
					list.add(Person.Item.death(date));
				} else if (e instanceof PersonItemMarriageContext) {
					var item   = (PersonItemMarriageContext)e;
					var date   = JapaneseDate.getInstance(item.date.getText());
					var spouse = item.spouse.getText();
					list.add(Person.Item.marriage(date, spouse));
				} else if (e instanceof PersonItemJoinContext) {
					var item = (PersonItemJoinContext)e;
					var date = JapaneseDate.getInstance(item.date.getText());
					var addr = item.address.getText();
					list.add(Person.Item.join(date, addr));
				} else if (e instanceof PersonItemSeparateContext) {
					var item = (PersonItemSeparateContext)e;
					var date = JapaneseDate.getInstance(item.date.getText());
					var addr = item.address.getText();
					list.add(Person.Item.separate(date, addr));
				} else if (e instanceof PersonItemBranchContext) {
					var item = (PersonItemBranchContext)e;
					var date = JapaneseDate.getInstance(item.date.getText());
					var addr = item.address.getText();
					list.add(Person.Item.branch(date, addr));
				} else if (e instanceof PersonItemRetirementContext) {
					var item = (PersonItemRetirementContext)e;
					var date = JapaneseDate.getInstance(item.date.getText());
					var newHead = item.newHead.getText();
					list.add(Person.Item.retirement(date, newHead));
				} else if (e instanceof PersonItemHeadOfHouseBranchContext) {
					var item = (PersonItemHeadOfHouseBranchContext)e;
					var date = JapaneseDate.getInstance(item.date.getText());
					list.add(Person.Item.headOfHouseBranch(date));
				} else if (e instanceof PersonItemHeadOfHouseDeathContext) {
					var item = (PersonItemHeadOfHouseDeathContext)e;
					var date = JapaneseDate.getInstance(item.date.getText());
					var prevHead = item.prevHead.getText();
					list.add(Person.Item.headOfHouseDeath(date, prevHead));
				} else if (e instanceof PersonItemHeadOfHouseRetirementContext) {
					var item = (PersonItemHeadOfHouseRetirementContext)e;
					var date = JapaneseDate.getInstance(item.date.getText());
					var prevHead = item.prevHead.getText();
					list.add(Person.Item.headOfHouseRetirement(date, prevHead));
				} else if (e instanceof PersonItemInheritanceContext) {
					var item = (PersonItemInheritanceContext)e;
					var date = JapaneseDate.getInstance(item.date.getText());
					list.add(Person.Item.inheritance(date));
				} else if (e instanceof PersonItemDisinheritanceContext) {
					var item = (PersonItemDisinheritanceContext)e;
					var date = JapaneseDate.getInstance(item.date.getText());
					list.add(Person.Item.disinheritance(date));
				} else {
					logger.error("Unexpected class");
					logger.error("  class  {}", e.getClass().getName());
					throw new UnexpectedException("Unexpected class");
				}
			}
			
			var person = new Person(address, father, relation, familyName, name, list);
			logger.info("person  {}", person);
			context.personList.add(person);
			
			return visitChildren(ctx);
		}
		
		
		//
		// Marriage
		//
		@Override
		public Void visitMarriageBlock(FamilyRegisterParser.MarriageBlockContext ctx) {
			// familyNameValue husbandValue wifeValue
			var familyName = ctx.familyNameValue().value.getText();
			var husband    = ctx.husbandValue().value.getText();
			var wife       = ctx.wifeValue().value.getText();
			
			// build list
			var list = new ArrayList<Marriage.Item>();
			for(var e: ctx.marriageItemBlock().marriageItemValue()) {
				if (e instanceof MarriageItemMarriageContext) {
					var item = (MarriageItemMarriageContext)e;
					var date = JapaneseDate.getInstance(item.date.getText());
					list.add(Marriage.Item.marriage(date));
				} else if (e instanceof MarriageItemDivorceContext) {
					var item = (MarriageItemDivorceContext)e;
					var date = JapaneseDate.getInstance(item.date.getText());
					list.add(Marriage.Item.divorce(date));
				} else if (e instanceof MarriageItemRelationContext) {
					var item     = (MarriageItemRelationContext)e;
					var date     = JapaneseDate.getInstance(item.date.getText());
					var relation = Relation.fromString(item.type.getText());
					var name     = item.name.getText();
					list.add(Marriage.Item.relation(date, relation, name));
				} else {
					logger.error("Unexpected class");
					logger.error("  class  {}", e.getClass().getName());
					throw new UnexpectedException("Unexpected class");
				}
			}
			
			var marriage = new Marriage(familyName, husband, wife, list);
			logger.info("marriage  {}", marriage);
			context.marriageList.add(marriage);

			return visitChildren(ctx);
		}
	}
}
