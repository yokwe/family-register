package yokwe.family.register;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
import yokwe.family.register.antlr.FamilyRegisterParser.AdoptedChildContext;
import yokwe.family.register.antlr.FamilyRegisterParser.BiolgicalChildContext;
import yokwe.family.register.antlr.FamilyRegisterParser.ItemAdoptJoinContext;
import yokwe.family.register.antlr.FamilyRegisterParser.ItemBirthContext;
import yokwe.family.register.antlr.FamilyRegisterParser.ItemBlockContext;
import yokwe.family.register.antlr.FamilyRegisterParser.ItemBranchContext;
import yokwe.family.register.antlr.FamilyRegisterParser.ItemDeathContext;
import yokwe.family.register.antlr.FamilyRegisterParser.ItemDisinheritContext;
import yokwe.family.register.antlr.FamilyRegisterParser.ItemDivorceContext;
import yokwe.family.register.antlr.FamilyRegisterParser.ItemDivorceRejoinContext;
import yokwe.family.register.antlr.FamilyRegisterParser.ItemHeadOfHouseContext;
import yokwe.family.register.antlr.FamilyRegisterParser.ItemInheritDeathContext;
import yokwe.family.register.antlr.FamilyRegisterParser.ItemInheritRetireContext;
import yokwe.family.register.antlr.FamilyRegisterParser.ItemMarriageContext;
import yokwe.family.register.antlr.FamilyRegisterParser.ItemMarriageJoinContext;
import yokwe.family.register.antlr.FamilyRegisterParser.ItemRetireContext;
import yokwe.family.register.antlr.FamilyRegisterParser.ItemSuccessorContext;
import yokwe.family.register.type.Event;
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

			for (var file : files) {
				logger.info("file  {}", file.getPath());

				final FamilyRegisterLexer lexer;
				{
					var string = FileUtil.read().file(file);
					var charStream = CharStreams.fromString(string);
					lexer = new FamilyRegisterLexer(charStream);
				}

				var parser = new FamilyRegisterParser(new CommonTokenStream(lexer));
				visitor.visitBody(parser.body());
				logger.info("context  address  {}  person  {}  family  {}", context.addressList.size(),
						context.personMap.size(), context.familyMap.size());
			}
		}
		// check context
		checkContext(context);
		
		logger.info("addressList           {}", context.addressList.size());
		logger.info("familyMap             {}", context.familyMap.size());
		logger.info("personMap             {}", context.personMap.size());
		
		{
			var addressSet = new TreeSet<String>();
			context.addressList.stream().forEach(o -> addressSet.addAll(o));
			context.familyMap.values().stream().forEach(o -> addressSet.add(o.address));
			context.personMap.values().stream().forEach(o -> addressSet.add(o.address));
			logger.info("address               {}", addressSet.size());
		}
		logger.info("countPerson           {}", context.countPerson);
		logger.info("countBiologicalChild  {}", context.countBiologicalChild);
		logger.info("countAdoptedChild     {}", context.countAdoptedChild);
		{
			int countFamilyChild = 0;
			for(var family: context.familyMap.values()) {
				countFamilyChild += family.childList.size();
			}
			logger.info("countFamilyChild      {}", countFamilyChild);
		}
	}

	public static class Context {
		public final List<List<String>> addressList = new ArrayList<>();
		public final Map<String, Person> personMap = new TreeMap<>();
		// familyName + name
		public final Map<String, Family> familyMap = new TreeMap<>();
		// father
		
		public int countPerson          = 0;
		public int countBiologicalChild = 0;
		public int countAdoptedChild    = 0;
		
		public void addAddress(List<String> list) {
			addressList.add(list);
		}
		public void addPerson(Person person) {
			var key = person.getKey();
			if (personMap.containsKey(key)) {
				// duplicate key
				logger.error("Duplicate person");
				logger.error("  key  {}", key);
				logger.error("  new  {}", person);
				logger.error("  old  {}", personMap.get(key));
			} else {
				personMap.put(key, person);
			}
		}
		public void addFamily(Family family) {
			var key = family.getKey();
			if (familyMap.containsKey(key)) {
				// duplicate key
				logger.error("Duplicate family");
				logger.error("  key  {}", key);
				logger.error("  new  {}", family);
				logger.error("  old  {}", familyMap.get(key));
			} else {
				familyMap.put(key, family);
			}
		}
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
			for (var e : ctx.addressBlockItem()) {
				var value = e.value.getText();
//				logger.info("  value  {}", value);
				addressList.add(value);
			}
//			logger.info("address  {}", addressList);
			context.addAddress(addressList);

			return visitChildren(ctx);
		}

		//
		// PersonBlock
		//
		@Override
		public Void visitPersonBlock(FamilyRegisterParser.PersonBlockContext ctx) {
			// addressValue familyNameValue fatherValue relationValue nameValue itemBlock?
			var address   = ctx.addressValue().value.getText();
			var lastName  = ctx.lastNameValue().value.getText();
			var father    = ctx.fatherValue().value.getText();
			var relation  = Relation.fromString(ctx.relationValue().value.getText());
			var firstName = ctx.firstNameValue().value.getText();

			// build list
			var eventList = new ArrayList<Event>();
			addEvent(eventList, ctx.itemBlock());

			// addressValue familyNameValue fatherValue relationValue nameValue itemBlock?
			var person = new Person(address, lastName, null, father, relation, firstName, eventList);
			context.addPerson(person);
			context.countPerson++;
			return visitChildren(ctx);
		}

		//
		// FamilyBlock
		//
		@Override
		public Void visitFamilyBlock(FamilyRegisterParser.FamilyBlockContext ctx) {
			// addressValue? familyNameValue motherValue fatherValue childBlock+
			var familyAddress   = ctx.addressValue().value.getText();
			var familyLastName = ctx.lastNameValue().value.getText();
			var familyMother   = ctx.motherValue().value.getText();
			var familyFather   = ctx.fatherValue().value.getText();

			// build list
			var childList = new ArrayList<String>();
			if (ctx.childBlock() != null) {
				// addressValue? relationValue nameValue itemBlock?
				for (var e : ctx.childBlock()) {
					if (e instanceof BiolgicalChildContext) {
						var item = (BiolgicalChildContext) e;
						var block = item.biologicalChildBlock();

						var address   = block.addressValue() == null ? familyAddress : block.addressValue().value.getText();
						var relation  = Relation.fromString(block.relationValue().value.getText());
						var firstName = block.firstNameValue().value.getText();

						List<Event> eventList = new ArrayList<>();
						addEvent(eventList, block.itemBlock());
						
						var person = new Person(address, familyLastName, familyMother, familyFather, relation, firstName, eventList);
						context.addPerson(person);
						context.countBiologicalChild++;
						
						childList.add(person.getKey());
					} else if (e instanceof AdoptedChildContext) {
						var item = (AdoptedChildContext) e;
						var name = item.adoptedChildValue().value.getText();
						childList.add(name);
						context.countAdoptedChild++;
					} else {
						logger.error("Unexpected class");
						logger.error("  class  {}", e.getClass().getName());
						throw new UnexpectedException("Unexpected class");
					}
				}
			}

			var family = new Family(familyAddress, familyLastName, familyMother, familyFather, childList);
//			logger.info("family  {}", family);
			context.addFamily(family);
			
			// sanity check
			{
				var set = new HashSet<String>();
				for (var child : childList) {
					if (set.contains(child)) {
						logger.error("Duplicate child");
						logger.error("  child  {}", child);
						throw new UnexpectedException("Duplicate child");
					}
					set.add(child);
				}
			}
			return visitChildren(ctx);
		}

		private static void addEvent(List<Event> list, ItemBlockContext ctx) {
			if (ctx == null)
				return;

			for (var e : ctx.itemValue()) {
				if (e instanceof ItemBirthContext) {
					var item = (ItemBirthContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					list.add(Event.birth(date));
				} else if (e instanceof ItemDeathContext) {
					var item = (ItemDeathContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					list.add(Event.death(date));
				} else if (e instanceof ItemHeadOfHouseContext) {
					var item = (ItemHeadOfHouseContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					list.add(Event.headOfHouse(date));
				} else if (e instanceof ItemBranchContext) {
					var item = (ItemBranchContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					var addr = item.address.getText();
					list.add(Event.branch(date, addr));
				} else if (e instanceof ItemRetireContext) {
					var item = (ItemRetireContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					var newHead = item.newHead.getText();
					list.add(Event.retire(date, newHead));
				} else if (e instanceof ItemInheritDeathContext) {
					var item = (ItemInheritDeathContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					var prevHead = item.oldHead.getText();
					list.add(Event.inheritDeath(date, prevHead));
				} else if (e instanceof ItemInheritRetireContext) {
					var item = (ItemInheritRetireContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					var prevHead = item.oldHead.getText();
					list.add(Event.inheritRetire(date, prevHead));
				} else if (e instanceof ItemMarriageContext) {
					var item = (ItemMarriageContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					var spouse = item.spouse.getText();
					list.add(Event.marriage(date, spouse));
				} else if (e instanceof ItemMarriageJoinContext) {
					var item = (ItemMarriageJoinContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					var spouse = item.spouse.getText();
					list.add(Event.marriageJoin(date, spouse));
				} else if (e instanceof ItemDivorceContext) {
					var item = (ItemDivorceContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					list.add(Event.divorce(date));
				} else if (e instanceof ItemDivorceRejoinContext) {
					var item = (ItemDivorceRejoinContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					list.add(Event.divorceRejoin(date));
				} else if (e instanceof ItemAdoptJoinContext) {
					var item = (ItemAdoptJoinContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					var father = item.father.getText();
					list.add(Event.adoptJoin(date, father));
				} else if (e instanceof ItemSuccessorContext) {
					var item = (ItemSuccessorContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					list.add(Event.successor(date));
				} else if (e instanceof ItemDisinheritContext) {
					var item = (ItemDisinheritContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					list.add(Event.disinherit(date));
				} else {
					logger.error("Unexpected class");
					logger.error("  class  {}", e.getClass().getName());
					throw new UnexpectedException("Unexpected class");
				}
			}
		}
	}
	
	public static void checkContext(Context context) {
		final var personMap = context.personMap;
		final var familyMap = context.familyMap;
		
		int countWarn = 0;

		logger.info("checkContext");
		logger.info("====================================");
		// check person
		// check person.father exists in personMap
		{
			var set = new HashSet<String>();

			for (var person : personMap.values()) {
				var fullNameInPerson = person.lastName + person.firstName;
				var fatherInPerson = person.father;

				if (FamilyRegister.isUnknown(fatherInPerson))
					continue;
				if (set.contains(fatherInPerson))
					continue;
				set.add(fatherInPerson);

				if (personMap.containsKey(fatherInPerson)) {
					// expected
				} else {
					// not expected
					logger.warn("PERSON  father doesn't exist in personMap  {}  {}", fullNameInPerson,
							fatherInPerson);
					countWarn++;
				}
			}
		}
		// check person marriage spouse exists in personMap
		{
			for (var person : personMap.values()) {
				var fullName = person.lastName + person.firstName;
				for (var event : person.eventList) {
					if (event.isMarriage()) {
						var spouse = event.value;
						if (personMap.containsKey(spouse)) {
							// expect
						} else {
							logger.warn("PERSON  spouse doesn't exist in personMap  {}  {}", fullName, spouse);
							countWarn++;
						}

					}
				}
			}
		}
		// check old head exists in personMap
		{
			for (var person : personMap.values()) {
				var fullName = person.lastName + person.firstName;
				for (var event : person.eventList) {
					if (event.type == Event.Type.INHERIT_DEATH) {
						var oldHead = event.value;
						
						if (personMap.containsKey(oldHead)) {
							// expect
						} else if (FamilyRegister.isUnknown(oldHead)) {
							// expect
						} else {
							logger.warn("PERSON  died head doesn't exist in personMap  {}  {}", fullName, oldHead);
							countWarn++;
						}
					}
				}
			}
		}
		// check retired head exists in personMap
		{
			for (var person : personMap.values()) {
				var fullName = person.lastName + person.firstName;
				for (var event : person.eventList) {
					if (event.type == Event.Type.INHERIT_RETIRE) {
						var oldHead = event.value;
						if (personMap.containsKey(oldHead)) {
							// expect
						} else if (FamilyRegister.isUnknown(oldHead)) {
							// expect
						} else {
							logger.warn("PERSON  retired head doesn't exist in personMap  {}  {}", fullName,
									oldHead);
							countWarn++;
						}
					}
				}
			}
		}
		// check new head exists in personMap
		{
			for (var person : personMap.values()) {
				var fullName = person.lastName + person.firstName;
				for (var event : person.eventList) {
					if (event.type == Event.Type.RETIRE) {
						var newHead = event.value;
						if (personMap.containsKey(newHead)) {
							// expect
						} else {
							logger.warn("PERSON  new head doesn't exist in personMap  {}  {}", fullName, newHead);
							countWarn++;
						}
					}
				}
			}
		}

		// check family
		// check family.father exists in personMap
		{
			var set = new HashSet<>();
			for (var family : familyMap.values()) {
				var fatherInFamily = family.father;
				if (set.contains(fatherInFamily))
					continue;
				if (personMap.containsKey(fatherInFamily)) {
					// expected
				} else {
					logger.warn("FAMILY  father doesn't exist in personMap  {}", fatherInFamily);
					countWarn++;
					set.add(fatherInFamily);
				}
			}
		}
		// check family.mother exists in personMap
		{
			var set = new HashSet<>();
			for (var family : familyMap.values()) {
				var motherInFamily = family.mother;
				if (set.contains(motherInFamily))
					continue;
				if (personMap.containsKey(motherInFamily)) {
					// expected
				} else {
					logger.warn("FAMILY  mother doesn't exist in personMap  {}", motherInFamily);
					countWarn++;
					set.add(motherInFamily);
				}
			}
		}
		// check family.childList exists in personMap
		{
			for (var family : familyMap.values()) {
				for(var child: family.childList) {
					if (personMap.containsKey(child)) {
						// expected
					} else {
						logger.warn("FAMILY  child doesn't exist in personMap  {}", child);
						countWarn++;
					}
				}
			}
		}

		logger.info("countWarn  {}", countWarn);
		logger.info("====================================");
	}

}
