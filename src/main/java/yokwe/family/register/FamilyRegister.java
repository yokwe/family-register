package yokwe.family.register;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import yokwe.family.register.type.Event;
import yokwe.family.register.type.Family;
import yokwe.family.register.type.Person;
import yokwe.util.UnexpectedException;

public class FamilyRegister {
	private static final org.slf4j.Logger logger = yokwe.util.LoggerUtil.getLogger();
	
	public static final String UNKNOWN = "不明";
	
	public static boolean isUnknown(String string) {
		return string.equals(UNKNOWN);
	}
	
	public record Parent(String father, String mother) implements Comparable<Parent> {
		public Parent(Family family) {
			this(family.father, family.mother);
		}
		public Parent(Person person) {
			this(person.father, person.mother);
		}
		@Override
		public int compareTo(Parent that) {
			int ret = this.father.compareTo(that.father);
			if (ret == 0) ret = this.mother.compareTo(that.mother);
			return ret;
		}
	};
	
	private final Map<String, String>       addressMap;
	//                oldAddess
	//                        newAddress
	public final Map<String, Person>       personMap;
	//                 name
	public final Map<Parent, List<Family>> familyMap;
	public final List<Event>               eventList;
	
	private String newAddress(String address) {
		return addressMap.getOrDefault(address, address);
	}
	
	public Person getPerson(String name) {
		if (personMap.containsKey(name)) {
			return personMap.get(name);
		}
		logger.error("Unexpected name");
		logger.error("  name  {}", name);
		throw new UnexpectedException("Unexpected name");
	}
	
	public Collection<Person> getPersonList() {
		return personMap.values();
	}
	
	public FamilyRegister() {
		// build addressMap
		addressMap = StorageRegister.ADDRESS.getList().stream().collect(Collectors.toMap(o -> o.oldAddress, o -> o.newAddress));
		// build personMap
		{
			var map = StorageRegister.PERSON.getList().stream().collect(Collectors.toMap(o -> o.getName(), o -> new Person(o, newAddress(o.address))));
			personMap = Collections.unmodifiableMap(map);
		}
		// build eventList
		{
			var list = new ArrayList<Event>();
			for(var e: StorageRegister.EVENT.getList()) {
				Event event = (e.eventType == Event.EventType.BRANCH) ? Event.branch(e.name, e.date, newAddress(e.value)) : e;
				list.add(event);
			}
			eventList = Collections.unmodifiableList(list);
		}
		
		// build family
		{
			// build map
			var map = new TreeMap<Parent, List<Family>>();
			for(var e: StorageRegister.FAMILY.getList()) {
				var key = new Parent(e);
				if (map.containsKey(key)) {
					map.get(key).add(e);
				} else {
					map.put(key, new ArrayList<>(Arrays.asList(e)));
				}
			}
			// add empty child family from event
			int countAdd = 0;
			for(var e: eventList) {
				if (e.isMarriage()) {
					final String father;
					final String mother;
					if (personMap.get(e.name).relation.male) {
						father = e.name;
						mother = e.value;
					} else {
						father = e.value;
						mother = e.name;
					}
					var key = new Parent(father, mother);
					if (!map.containsKey(key)) {
						var value = new ArrayList<Family>();
						map.put(key, value);
						countAdd++;
					}
				}
			}
			logger.info("familyMap  add {} entries from eventList", countAdd);
			familyMap = Collections.unmodifiableMap(map);
		}
	}
	
	public static void main(String[] args) {
		logger.info("START");
		
		var familyRegister = new FamilyRegister();
		logger.info("addresMap  {}", familyRegister.addressMap.size());
		logger.info("personMap  {}", familyRegister.personMap.size());
		logger.info("familyMap  {}", familyRegister.familyMap.size());
		logger.info("eventList  {}", familyRegister.eventList.size());
		
		{
			var set = new TreeSet<String>();
			for(var e: familyRegister.personMap.values()) {
				set.add(e.address);
			}
			for(var e: familyRegister.eventList) {
				if (e.eventType == Event.EventType.BRANCH) set.add(e.value);
			}
			logger.info("address    {}", set.size());
		}
		
		logger.info("STOP");
	}
}
