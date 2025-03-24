package yokwe.family.register;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import yokwe.family.register.antlr.FamilyRegisterBaseVisitor;
import yokwe.family.register.antlr.FamilyRegisterLexer;
import yokwe.family.register.antlr.FamilyRegisterParser;
import yokwe.family.register.antlr.FamilyRegisterParser.PersonItemBirthContext;
import yokwe.family.register.antlr.FamilyRegisterParser.PersonItemBranchContext;
import yokwe.family.register.antlr.FamilyRegisterParser.PersonItemDeathContext;
import yokwe.family.register.antlr.FamilyRegisterParser.PersonItemDisinheritanceContext;
import yokwe.family.register.antlr.FamilyRegisterParser.PersonItemDivorceContext;
import yokwe.family.register.antlr.FamilyRegisterParser.PersonItemDivorceRejoinContext;
import yokwe.family.register.antlr.FamilyRegisterParser.PersonItemHeadOfHouseBranchContext;
import yokwe.family.register.antlr.FamilyRegisterParser.PersonItemHeadOfHouseDeathContext;
import yokwe.family.register.antlr.FamilyRegisterParser.PersonItemHeadOfHouseRetirementContext;
import yokwe.family.register.antlr.FamilyRegisterParser.PersonItemInheritanceContext;
import yokwe.family.register.antlr.FamilyRegisterParser.PersonItemMarriageContext;
import yokwe.family.register.antlr.FamilyRegisterParser.PersonItemMarriageJoinContext;
import yokwe.family.register.antlr.FamilyRegisterParser.PersonItemRetirementContext;
import yokwe.family.register.type.Family;
import yokwe.family.register.type.FamilyRegister;
import yokwe.family.register.type.Person;
import yokwe.family.register.type.Relation;
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
		var context = new Context();
		{
			var visitor = new BuildContext(context);
			
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
				
				var parser = new FamilyRegisterParser(new CommonTokenStream(lexer));
				visitor.visitBody(parser.body());
				logger.info(
					"context  address  {}  person  {}  family  {}",
					context.addressList.size(), context.personMap.size(), context.familyMap.size());
			}
		}
		// check context
		checkContext(context);
	}
	
	public static class Context {
		public final List<List<String>>  addressList = new ArrayList<>();
		public final Map<String, Person> personMap  = new TreeMap<>();
		//               familyName + name
		public final Map<String, Family> familyMap   = new TreeMap<>();
		//               father
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
//			logger.info("address  {}", addressList);
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
				} else if (e instanceof PersonItemMarriageJoinContext) {
					var item   = (PersonItemMarriageJoinContext)e;
					var date   = JapaneseDate.getInstance(item.date.getText());
					var spouse = item.spouse.getText();
					list.add(Person.Item.marriageJoin(date, spouse));
				} else if (e instanceof PersonItemDivorceContext) {
					var item   = (PersonItemDivorceContext)e;
					var date   = JapaneseDate.getInstance(item.date.getText());
					list.add(Person.Item.divorce(date));
				} else if (e instanceof PersonItemDivorceRejoinContext) {
					var item   = (PersonItemDivorceRejoinContext)e;
					var date   = JapaneseDate.getInstance(item.date.getText());
					list.add(Person.Item.divorceRejoin(date));
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
//			logger.info("person  {}", person);
			{
				var key = person.familyName + person.name;
				if (context.personMap.containsKey(key)) {
					logger.error("Duplicate person");
					logger.error("  key  {}", key);
					logger.error("  new  {}", person);
					logger.error("  old  {}", context.personMap.get(key));
					throw new UnexpectedException("Duplicate person");
				} else {
					context.personMap.put(key, person);
					logger.info("person key  {}", key);
				}
			}
			
			return visitChildren(ctx);
		}
		
		
		//
		// FamilyBlock
		//
		@Override
		public Void visitFamilyBlock(FamilyRegisterParser.FamilyBlockContext ctx) {
			// familyNameValue husbandValue wifeValue
			var familyName = ctx.familyNameValue().value.getText();
			var husband    = ctx.husbandValue().value.getText();
			var wife       = ctx.wifeValue().value.getText();
			
			// build list
			var list = new ArrayList<Family.Item>();
			for(var e: ctx.familyItemBlock().familyItemValue()) {
				var date     = JapaneseDate.getInstance(e.date.getText());
				var relation = Relation.fromString(e.type.getText());
				var name     = e.name.getText();
				list.add(Family.Item.relation(date, relation, name));
			}
			
			var family = new Family(familyName, husband, wife, list);
//			logger.info("family  {}", family);
			{
				var key = family.familyName + family.husband;
				if (context.familyMap.containsKey(key)) {
					logger.error("Duplicate family");
					logger.error("  key  {}", key);
					logger.error("  new  {}", family);
					logger.error("  old  {}", context.familyMap.get(key));
					throw new UnexpectedException("Duplicate family");
				} else {
					context.familyMap.put(key, family);
				}
			}

			context.familyMap.put(family.husband, family);

			return visitChildren(ctx);
		}
	}
	
	public static void checkContext(Context context) {
		final var personMap = context.personMap;
		final var familyMap = context.familyMap;
		
		logger.info("checkConsistency");
		logger.info("====================================");
		int countUnknown = 0;
		
		{
			var keySet = new HashSet<String>();
			for(var e: personMap.values()) {
				if (FamilyRegister.isUnknown(e.father)) {
					// nothing to do
				} else {
					var key = e.father;
					if (keySet.contains(key)) continue;
					keySet.add(key);
					
					if (!personMap.containsKey(key)) {
						logger.info("Unknown person father  {}{}  {}", e.familyName, e.name, e.father);
						countUnknown++;
					}
				}
			}
		}
		
		{
			var keySet = new HashSet<String>();
			for(var e: personMap.values()) {
				for(var ee: e.itemList) {
					if (ee.isMarriage()) {
						var key = ee.value;
						if (keySet.contains(key)) continue;
						keySet.add(key);
						
						if (!personMap.containsKey(key)) {
							logger.info("Unknown person marriage  {}{}  {}", e.familyName, e.name, ee.value);
							countUnknown++;
						}
					}
				}
			}
		}
		
		{
			var keySet = new HashSet<String>();
			for(var e: familyMap.values()) {
				// husband
				{
					var key = e.husband;
					if (keySet.contains(key)) continue;
					keySet.add(key);
					
					if (!personMap.containsKey(key)) {
						logger.info("Unknown family husband  {}{}  {}", e.familyName, e.husband, e.husband);
						countUnknown++;
					}
				}
			}
		}
		
		{
			var keySet = new HashSet<String>();
			for(var e: familyMap.values()) {
				// wife
				{
					var key = e.wife;
					if (keySet.contains(key)) continue;
					keySet.add(key);
					
					if (!personMap.containsKey(key)) {
						logger.info("Unknown family wife  {}  {}", e.husband, e.wife);
						countUnknown++;
					}
				}
			}
		}
		{
			var keySet = new HashSet<String>();
			for(var e: familyMap.values()) {
				// child
				for(var ee: e.itemList) {
					var key = e.familyName + ee.name;
					if (keySet.contains(key)) continue;
					keySet.add(key);
					
					if (!personMap.containsKey(key)) {
						logger.info("Unknown family child  {}  {}  {}", e.husband, ee.relation, ee.name);
						countUnknown++;
					}
				}
			}
		}
		
		{
			for(var e: familyMap.values()) {
				var husband = e.husband;
				var wife    = e.wife;
				
				// should have marriage or marriageJoin item entry with sama date
				if (personMap.containsKey(husband) && personMap.containsKey(wife)) {
					var personHusband = personMap.get(husband);
					var personWife    = personMap.get(wife);
					
					var itemListHusband = personHusband.itemList.stream().
							filter(o -> o.isMarriage()).
							filter(o -> o.value.equals(wife)).
							toList();
					var itemListWife    = personWife.itemList.stream().
							filter(o -> o.isMarriage()).
							filter(o -> o.value.equals(husband)).
							toList();
					
					if (itemListHusband.size() == 1 && itemListWife.size() == 1) {
						var dateHusband = itemListHusband.get(0).date;
						var dateWife    = itemListWife.get(0).date;
						if (dateHusband.equals(dateWife)) {
							// OK
						} else {
							logger.info("marriage date is different.  {}  {}  --  {}  {}", husband, dateHusband, wife, dateWife);
						}
					} else if (itemListHusband.size() == 0) {
						logger.info("husband has no marriage.  {}  {}  --  {}  {}", husband, wife);
					} else if (itemListWife.size() == 0) {
						logger.info("wife has no marriage.  {}  {}  --  {}  {}", husband, wife);
					} else {
						logger.info("Unexpected marriage item");
						logger.info("  husband  {}", personHusband);
						logger.info("  wife     {}", personWife);
					}
				}
			}
		}

		
		logger.info("====================================");

		logger.info("countUnknown  {}", countUnknown);
	}
	
}
