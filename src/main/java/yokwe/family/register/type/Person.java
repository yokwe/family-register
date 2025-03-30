package yokwe.family.register.type;

import java.util.List;

import yokwe.util.JapaneseDate;
import yokwe.util.StringUtil;

public class Person implements Comparable<Person> {
	public final String   address;
	public final String   lastName;
	public final String   mother;
	public final String   father;
	public final Relation relation;
	public final String   firstName;
	
	public final List<Event> eventList;
	
	public Person(String address, String lastName, String mother, String father, Relation relation, String firstName, List<Event> itemList) {
		this.address   = address;
		this.lastName  = lastName;
		this.mother    = mother;
		this.father    = father;
		this.relation  = relation;
		this.firstName = firstName;
		this.eventList = itemList;
	}
	
	public String getKey() {
		return lastName + firstName;
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
