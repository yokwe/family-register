package yokwe.family.register.type;

import java.util.List;

import yokwe.util.JapaneseDate;
import yokwe.util.StringUtil;

public class Person implements Comparable<Person> {
	private static final org.slf4j.Logger logger = yokwe.util.LoggerUtil.getLogger();
	
	//addressValue familyNameValue fatherValue relationValue nameValue itemBlock
	public final String   address;
	public final String   familyName;
	public final String   father;
	public final Relation relation;
	public final String   name;
	
	public final List<Event> eventList;
	
	public Person(String address, String familyName, String father, Relation relation, String name, List<Event> itemList) {
		this.address    = address;
		this.familyName = familyName;
		this.father     = father;
		this.relation   = relation;
		this.name       = name;
		this.eventList  = itemList;
	}
	
	public Event getBirth() {
		return Event.getBirth(eventList);
	}
	public JapaneseDate getBirthDate() {
		return Event.getBirthDate(eventList);
	}
	public Event getMarriage() {
		return Event.getMarriage(eventList);
	}
	public JapaneseDate getMarriageDate() {
		return Event.getMarriageDate(eventList);
	}
	
	@Override
	public String toString() {
		return StringUtil.toString(this);
	}

	@Override
	public int compareTo(Person that) {
		int result = this.father.compareTo(that.father);
		if (result == 0) {
			var thisBirth = this.getBirthDate();
			var thatBirth = that.getBirthDate();
			if (thisBirth != null && thatBirth != null) {
				result = thisBirth.compareTo(thatBirth);
			}
		}
		return result;
	}
}
