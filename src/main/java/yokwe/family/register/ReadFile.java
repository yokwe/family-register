package yokwe.family.register;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

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
			var father     = ctx.fatherValue().value.getText();
			var mother     = ctx.motherValue().value.getText();
			
			// build list
			var list = new ArrayList<Family.Item>();
			for(var e: ctx.familyItemBlock().familyItemValue()) {
				var date     = JapaneseDate.getInstance(e.date.getText());
				var relation = Relation.fromString(e.type.getText());
				var name     = e.name.getText();
				list.add(Family.Item.relation(date, relation, name));
			}
			
			var family = new Family(familyName, father, mother, list);
//			logger.info("family  {}", family);
			{
				var key = family.familyName + family.father;
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

			context.familyMap.put(family.father, family);

			return visitChildren(ctx);
		}
	}
	
	
	public static void checkContext(Context context) {
		final var personMap = context.personMap;
		final var familyMap = context.familyMap;
		
		logger.info("checkConsistency");
		int countWarn = 0;
		
		logger.info("====================================");		
		{
			var addressSet = new TreeSet<String>();
			for(var e: personMap.values()) {
				addressSet.add(e.address);
			}
			for(var e: addressSet) {
				logger.info("address  {}", e);
			}
		}
		
		logger.info("====================================");
		{
			var personList = new ArrayList<Person>();
			for(var e: personMap.values()) {
				personList.add(e);
			}
			Collections.sort(personList);
			for(var e: personList) {
				logger.info("person  {}  {}  {}{}", e.father, e.relation, e.familyName, e.name);
//				var birthDate = e.getBirthDate();
//				logger.info("person  {}  {}  {}{}  {}  {}  {}  {}", e.father, e.relation, e.familyName, e.name, birthDate, birthDate.year, birthDate.month, birthDate.day);
			}
		}
		
		logger.info("====================================");

		// check person
		//   check person.father exists in personMap
		{
			var set = new HashSet<String>();
			
			for(var person: personMap.values()) {
				var fullNameInPerson  = person.familyName + person.name;
				var fatherInPerson    = person.father;
				
				if (FamilyRegister.isUnknown(fatherInPerson)) continue;
				if (set.contains(fatherInPerson)) continue;
				set.add(fatherInPerson);
				
				if (personMap.containsKey(fatherInPerson)) {
					// expected
				} else {
					// not expected
					logger.warn("PERSON  father doesn't exist in personMap  {}  {}",  fullNameInPerson, fatherInPerson);
					countWarn++;
				}
			}
		}
		//   check person.father match father in family
		{
			for(var person: personMap.values()) {
				var fullNameInPerson = person.familyName + person.name;
				var fatherInPerson   = person.father;
				
				if (FamilyRegister.isUnknown(fatherInPerson)) continue;

				for(var family: familyMap.values()) {
					var fatherInFamily = family.father;
					for(var item: family.itemList) {
						var fullNameInFamily = family.familyName + item.name;
						if (fullNameInFamily.equals(fullNameInPerson)) {
							if (fatherInFamily.equals(fatherInPerson)) {
								// expect
							} else {
								logger.warn("PERSON  father mismatch  {}  {}  {}", fullNameInPerson, fatherInPerson, fatherInFamily);
								countWarn++;
							}
						}
					}
				}
			}
		}
		//   check person marriage spouse exists in personMap
		{
			for(var person: personMap.values()) {
				var fullName = person.familyName + person.name;
				var marriage = person.getMarriage();
				if (marriage != null) {
					var spouse = marriage.value;
					if (personMap.containsKey(spouse)) {
						// expect
					} else {
						logger.warn("PERSON  spouse doesn't exist in personMap  {}  {}",  fullName, spouse);
						countWarn++;
					}
				}
			}
		}
		//   check person birth date match birth date in family
		{
			for(var person: personMap.values()) {
				var fullName = person.familyName + person.name;
				var birth = person.getBirth();
				if (birth != null) {
					var date = birth.date;
					
					for(var family: familyMap.values()) {
						for(var item: family.itemList) {
							var fullNameInFamily = family.familyName + item.name;
							if (fullNameInFamily.equals(fullName)) {
								var dateInFamily = item.date;
								if (dateInFamily.equals(date)) {
									// expect
								} else {
									logger.warn("PERSON  birth date mismatch  {}  {}  {}", fullName, date, dateInFamily);
								}
							}
						}
					}
				}
			}
		}
		//   check person relation match relation in family
		{
			for(var person: personMap.values()) {
				var fullName = person.familyName + person.name;
				var relation = person.relation;
				for(var family: familyMap.values()) {
					for(var item: family.itemList) {
						var fullNameInFamily = family.familyName + item.name;
						if (fullNameInFamily.equals(fullName)) {
							var relationInFamily = item.relation;
							if (relationInFamily.equals(relation)) {
								// expect
							} else {
								logger.warn("PERSON  relation mismatch  {}  {}  {}", fullName, relation, relationInFamily);
							}
						}
					}
				}
			}
		}

		// check family
		//   check family.father exists in personMap
		{
			var set = new HashSet<>();
			for(var family: familyMap.values()) {
				var fatherInFamily = family.father;
				if (set.contains(fatherInFamily)) continue;
				if (personMap.containsKey(fatherInFamily)) {
					// expected
				} else {
					logger.warn("FAMILY  father doesn't exist in personMap  {}",  fatherInFamily);
					countWarn++;
					set.add(fatherInFamily);
				}
			}
		}
		//   check family.mother exists in personMap
		{
			var set = new HashSet<>();
			for(var family: familyMap.values()) {
				var motherInFamily = family.mother;
				if (set.contains(motherInFamily)) continue;
				if (personMap.containsKey(motherInFamily)) {
					// expected
				} else {
					logger.warn("FAMILY  mother doesn't exist in personMap  {}",  motherInFamily);
					countWarn++;
					set.add(motherInFamily);
				}
			}
		}
		//   check family child exists in personMap
		{
			for(var family: familyMap.values()) {
				var familyName = family.familyName;
				for(var item: family.itemList) {
					var fullName = familyName + item.name;
					if (personMap.containsKey(fullName)) {
						// expected
					} else {
						logger.warn("FAMILY  child doesn't exist in personMap  {}",  fullName);
						countWarn++;
					}
				}
			}
		}
		//   check child birth date match in ersonMap
		{
			for(var family: familyMap.values()) {
				var familyName = family.familyName;
				for(var item: family.itemList) {
					var date = item.date;
					var fullName = familyName + item.name;
					if (personMap.containsKey(fullName)) {
						var person = personMap.get(fullName);
						var personItem = person.getBirth();
						if (personItem != null) {
							var personDate = personItem.date;
							if (personDate.equals(date)) {
								// exptected
							} else {
								logger.warn("FAMILY  child birth date mismatch  {}  {}  {}", fullName, date, personDate);
								countWarn++;
							}
						}
					}
				}
			}
		}
		//   check child relation match in personMap
		{
			for(var family: familyMap.values()) {
				var familyName = family.familyName;
				for(var item: family.itemList) {
					var relation = item.relation;
					var fullName = familyName + item.name;
					if (personMap.containsKey(fullName)) {
						var person = personMap.get(fullName);
						var personRelation = person.relation;
						if (personRelation != relation) {
							logger.warn("FAMILY  child relation mismatch  {}  {}  {}", fullName, relation, personRelation);
							countWarn++;
						}
					}
				}
			}
		}
		
		logger.info("countWarn  {}", countWarn);
	}
	
}
