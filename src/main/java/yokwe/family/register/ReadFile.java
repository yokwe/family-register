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
import yokwe.family.register.type.Address;
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
			}
		}
		// check context
		context.checkContext();
		
		{
			var addressSet = new TreeSet<String>();
			context.personMap.values().stream().forEach(o -> addressSet.add(o.address));
			logger.info("address               {}", addressSet.size());
		}
		logger.info("countPerson           {}", context.countPerson);
		logger.info("countBiologicalChild  {}", context.countBiologicalChild);
		logger.info("countAdoptedChild     {}", context.countAdoptedChild);
		
		logger.info("address  {}  {}", context.addressMap.size(), StorageRegister.ADDRESS.getPath());
		StorageRegister.ADDRESS.save(context.addressMap.values());
		
		logger.info("family   {}  {}", context.familyMap.size(), StorageRegister.FAMILY.getPath());
		StorageRegister.FAMILY.save(context.familyMap.values());
		
		logger.info("person   {}  {}", context.personMap.size(), StorageRegister.PERSON.getPath());
		StorageRegister.PERSON.save(context.personMap.values());
		
		logger.info("event    {}  {}", context.eventMap.size(), StorageRegister.EVENT.getPath());
		StorageRegister.EVENT.save(context.eventMap.values());
	}

	public static class Context {
		public final Map<String, Address> addressMap = new TreeMap<>();
		public final Map<String, Person>  personMap  = new TreeMap<>();
		public final Map<String, Family>  familyMap  = new TreeMap<>();
		public final Map<String, Event>   eventMap   = new TreeMap<>();
		// father
		
		public int countPerson          = 0;
		public int countBiologicalChild = 0;
		public int countAdoptedChild    = 0;
		
		public void addAddress(List<String> list) {
			for(var e: Address.getList(list)) addAddress(e);
		}
		public void addAddress(Address address) {
			var key = address.getKey();
			if (addressMap.containsKey(key)) {
				// duplicate key
				logger.error("Duplicate address");
				logger.error("  key  {}", key);
				logger.error("  new  {}", address);
				logger.error("  old  {}", addressMap.get(key));
				throw new UnexpectedException("Duplicate address");
			} else {
				addressMap.put(key, address);
			}
		}
		public void addPerson(Person person) {
			var key = person.getKey();
			if (personMap.containsKey(key)) {
				// duplicate key
				logger.error("Duplicate person");
				logger.error("  key  {}", key);
				logger.error("  new  {}", person);
				logger.error("  old  {}", personMap.get(key));
				throw new UnexpectedException("Duplicate person");
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
				throw new UnexpectedException("Duplicate family");
			} else {
				familyMap.put(key, family);
			}
		}
		public void addEvent(Person person, ItemBlockContext ctx) {
			if (ctx == null) return;
			
			var name = person.lastName + person.firstName;
			
			var eventList = new ArrayList<Event>();
			for (var e : ctx.itemValue()) {
				if (e instanceof ItemBirthContext) {
					var item = (ItemBirthContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					eventList.add(Event.birth(name, date));
				} else if (e instanceof ItemDeathContext) {
					var item = (ItemDeathContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					eventList.add(Event.death(name, date));
				} else if (e instanceof ItemHeadOfHouseContext) {
					var item = (ItemHeadOfHouseContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					eventList.add(Event.headOfHouse(name, date));
				} else if (e instanceof ItemBranchContext) {
					var item = (ItemBranchContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					var addr = item.address.getText();
					eventList.add(Event.branch(name, date, addr));
				} else if (e instanceof ItemRetireContext) {
					var item = (ItemRetireContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					var newHead = item.newHead.getText();
					eventList.add(Event.retire(name, date, newHead));
				} else if (e instanceof ItemInheritDeathContext) {
					var item = (ItemInheritDeathContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					var prevHead = item.oldHead.getText();
					eventList.add(Event.inheritDeath(name, date, prevHead));
				} else if (e instanceof ItemInheritRetireContext) {
					var item = (ItemInheritRetireContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					var prevHead = item.oldHead.getText();
					eventList.add(Event.inheritRetire(name, date, prevHead));
				} else if (e instanceof ItemMarriageContext) {
					var item = (ItemMarriageContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					var spouse = item.spouse.getText();
					eventList.add(Event.marriage(name, date, spouse));
				} else if (e instanceof ItemMarriageJoinContext) {
					var item = (ItemMarriageJoinContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					var spouse = item.spouse.getText();
					eventList.add(Event.marriageJoin(name, date, spouse));
				} else if (e instanceof ItemDivorceContext) {
					var item = (ItemDivorceContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					eventList.add(Event.divorce(name, date));
				} else if (e instanceof ItemDivorceRejoinContext) {
					var item = (ItemDivorceRejoinContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					eventList.add(Event.divorceRejoin(name, date));
				} else if (e instanceof ItemAdoptJoinContext) {
					var item = (ItemAdoptJoinContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					var father = item.father.getText();
					eventList.add(Event.adoptJoin(name, date, father));
				} else if (e instanceof ItemSuccessorContext) {
					var item = (ItemSuccessorContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					eventList.add(Event.successor(name, date));
				} else if (e instanceof ItemDisinheritContext) {
					var item = (ItemDisinheritContext) e;
					var date = JapaneseDate.getInstance(item.date.getText());
					eventList.add(Event.disinherit(name, date));
				} else {
					logger.error("Unexpected class");
					logger.error("  class  {}", e.getClass().getName());
					throw new UnexpectedException("Unexpected class");
				}
			}
			
			for(var event: eventList) {
				var key = event.getKey();
				if (eventMap.containsKey(key)) {
					// duplicate key
					logger.error("Duplicate event");
					logger.error("  key  {}", key);
					logger.error("  new  {}", event);
					logger.error("  old  {}", eventMap.get(key));
					throw new UnexpectedException("Duplicate event");
				} else {
					eventMap.put(key, event);
				}
			}
		}
		
		public void checkContext() {
			int countWarn = 0;

			logger.info("checkContext");
			logger.info("====================================");
			// check person
			// check person.father exists in personMap
			{
				var set = new HashSet<String>();

				for (var person : personMap.values()) {
					var name = person.lastName + person.firstName;
					var father = person.father;

					if (FamilyRegister.isUnknown(father)) continue;
					if (set.contains(father)) continue;
					set.add(father);

					if (personMap.containsKey(father)) {
						// expected
					} else {
						// not expected
						logger.warn("PERSON  father doesn't exist in personMap  {}  {}", name, father);
						countWarn++;
					}
				}
			}
			// check person.mother exists in personMap
			{
				var set = new HashSet<String>();

				for (var person : personMap.values()) {
					var name = person.lastName + person.firstName;
					var mother = person.mother;

					if (mother.isEmpty()) continue;
					if (set.contains(mother)) continue;
					set.add(mother);

					if (personMap.containsKey(mother)) {
						// expected
					} else {
						// not expected
						logger.warn("PERSON  mother doesn't exist in personMap  {}  {}", name, mother);
						countWarn++;
					}
				}
			}
			
			
			// check family
			// check family.father exists in personMap
			{
				var set = new HashSet<>();
				for (var family : familyMap.values()) {
					var father = family.father;
					if (set.contains(father)) continue;
					if (personMap.containsKey(father)) {
						// expected
					} else {
						logger.warn("FAMILY  father doesn't exist in personMap  {}", father);
						countWarn++;
						set.add(father);
					}
				}
			}
			// check family.mother exists in personMap
			{
				var set = new HashSet<>();
				for (var family : familyMap.values()) {
					var mother = family.mother;
					if (set.contains(mother)) continue;
					if (personMap.containsKey(mother)) {
						// expected
					} else {
						logger.warn("FAMILY  mother doesn't exist in personMap  {}", mother);
						countWarn++;
						set.add(mother);
					}
				}
			}
			
			
			// check event
			// check marriage spouse exists in personMap
			{
				for(var event: eventMap.values()) {
					if (event.isMarriage()) {
						var spouse = event.value;
						if (personMap.containsKey(spouse)) {
							// expected
						} else {
							logger.warn("EVENT   spouse doesn't exist in personMap  {}", spouse);
							countWarn++;
						}
					}
				}
			}
			// check marriage of spouse exists in eventMap
			{
				for(var event: eventMap.values()) {
					if (event.type == Event.Type.MARRIAGE) {
						var name   = event.name;
						var spouse = event.value;
						var list = eventMap.values().stream().filter(o -> o.name.equals(spouse)).toList();
						boolean found = false;
						for(var e: list) {
							if (e.type == Event.Type.MARRIAGE_JOIN) {
								found = true;
								if (e.value.equals(name)) {
									// expect
									if (e.date.equals(event.date)) {
										// expect
									} else {
										logger.warn("EVENT   spouse marriage date is not same");
										logger.warn("        self   {}", event);
										logger.warn("        spouse {}", e);
										countWarn++;
									}
								} else {
									logger.warn("EVENT   spouse name is not same");
									logger.warn("        self   {}", event);
									logger.warn("        spouse {}", e);
									countWarn++;
								}
							}
						}
						if (!found) {
							logger.warn("EVENT   spouse marriage event is not found");
							logger.warn("        self   {}", event);
							countWarn++;
						}
					}
					if (event.type == Event.Type.MARRIAGE_JOIN) {
						var name   = event.name;
						var spouse = event.value;
						var list = eventMap.values().stream().filter(o -> o.name.equals(spouse)).toList();
						boolean found = false;
						for(var e: list) {
							if (e.type == Event.Type.MARRIAGE) {
								found = true;
								if (e.value.equals(name)) {
									// expect
									if (e.date.equals(event.date)) {
									} else {
										logger.warn("EVENT   spouse marriage date is not same");
										logger.warn("        self   {}", event);
										logger.warn("        spouse {}", e);
										countWarn++;
									}
								} else {
									logger.warn("EVENT   spouse name is not same");
									logger.warn("        self   {}", event);
									logger.warn("        spouse {}", e);
									countWarn++;
								}
							}
						}
						if (!found) {
							logger.warn("EVENT   spouse marriage event is not found");
							logger.warn("        self   {}", event);
							countWarn++;
						}
					}
				}
			}
			// check old head exists in personMap
			{
				for(var event: eventMap.values()) {
					if (event.type == Event.Type.INHERIT_DEATH) {
						var oldHead = event.value;
						if (FamilyRegister.isUnknown(oldHead)) continue;
						if (personMap.containsKey(oldHead)) {
							// expected
						} else {
							logger.warn("EVENT   old head doesn't exist in personMap  {}", oldHead);
							countWarn++;
						}
					}
				}
			}
			// check retired head exists in personMap
			{
				for(var event: eventMap.values()) {
					if (event.type == Event.Type.INHERIT_RETIRE) {
						var oldHead = event.value;
						if (FamilyRegister.isUnknown(oldHead)) continue;
						if (personMap.containsKey(oldHead)) {
							// expected
						} else {
							logger.warn("EVENT   retired head doesn't exist in personMap  {}", oldHead);
							countWarn++;
						}
					}
				}
			}
			// check new head exists in personMap
			{
				for(var event: eventMap.values()) {
					if (event.type == Event.Type.RETIRE) {
						var newHead = event.value;
						if (FamilyRegister.isUnknown(newHead)) continue;
						if (personMap.containsKey(newHead)) {
							// expected
						} else {
							logger.warn("EVENT   new head doesn't exist in personMap  {}", newHead);
							countWarn++;
						}
					}
				}
			}
			// check ADOPT_JOIN spouse
			// FIXME
			
			logger.info("countWarn  {}", countWarn);
			logger.info("====================================");
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

			// build personMap
			var person = new Person(address, lastName, "", father, relation, firstName);
			context.addPerson(person);
			context.countPerson++;
			
			// build eventList
			context.addEvent(person, ctx.itemBlock());
			return visitChildren(ctx);
		}

		//
		// FamilyBlock
		//
		@Override
		public Void visitFamilyBlock(FamilyRegisterParser.FamilyBlockContext ctx) {
			var familyList = new ArrayList<Family>();
			{
				var familyAddress   = ctx.addressValue().value.getText();
				var familyLastName = ctx.lastNameValue().value.getText();
				var familyMother   = ctx.motherValue().value.getText();
				var familyFather   = ctx.fatherValue().value.getText();

				if (ctx.childBlock() != null) {
					// addressValue? relationValue nameValue itemBlock?
					for (var e : ctx.childBlock()) {
						if (e instanceof BiolgicalChildContext) {
							var item = (BiolgicalChildContext) e;
							var block = item.biologicalChildBlock();

							var address   = block.addressValue() == null ? familyAddress : block.addressValue().value.getText();
							var relation  = Relation.fromString(block.relationValue().value.getText());
							var firstName = block.firstNameValue().value.getText();

							// build person
							var person = new Person(address, familyLastName, familyMother, familyFather, relation, firstName);
							context.addPerson(person);
							context.countBiologicalChild++;
							
							// build event
							context.addEvent(person, block.itemBlock());
							
							// build family
							var childName = person.lastName + person.firstName;
							var family = Family.biological(familyFather, familyMother, familyLastName, childName);
							context.addFamily(family);
							familyList.add(family);
						} else if (e instanceof AdoptedChildContext) {
							var item = (AdoptedChildContext) e;
							var childName = item.adoptedChildValue().value.getText();
							
							// build family
							var family = Family.adopted(familyFather, familyMother, familyLastName, childName);
							context.addFamily(family);
							familyList.add(family);

							context.countAdoptedChild++;
						} else {
							logger.error("Unexpected class");
							logger.error("  class  {}", e.getClass().getName());
							throw new UnexpectedException("Unexpected class");
						}
					}
				}
			}
			
			// sanity check
			{
				var set = new HashSet<String>();
				for (var family : familyList) {
					var childName = family.childName;
					
					if (set.contains(childName)) {
						logger.error("Duplicate childName");
						logger.error("  childName  {}", childName);
						throw new UnexpectedException("Duplicate childName");
					}
					set.add(childName);
				}
			}
			
			return visitChildren(ctx);
		}
	}
}
