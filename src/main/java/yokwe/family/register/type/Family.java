package yokwe.family.register.type;

import java.util.List;

import yokwe.util.JapaneseDate;
import yokwe.util.StringUtil;

public class Family implements Comparable<Family> {
	// addressValue? relationValue nameValue itemBlock?
	public static class Child {
		public final String      address;
		public final Relation    relation;
		public final String      name;
		public final List<Event> eventList;
		
		public Child(String address, Relation relation, String name, List<Event> eventList) {
			this.address   = address;
			this.relation  = relation;
			this.name      = name;
			this.eventList = eventList;
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
	}
	
	// addressValue? familyNameValue motherValue fatherValue childBlock+
	public final String      address;
	public final String      familyName;
	public final String      mother;
	public final String      father;
	public final List<Child> childList;
	
	public Family(String address, String familyName, String mother, String father, List<Child> childList) {
		this.address    = address;
		this.familyName = familyName;
		this.mother     = mother;
		this.father     = father;
		this.childList  = childList;	
	}
	
	@Override
	public String toString() {
		return StringUtil.toString(this);
	}
	
	@Override
	public int compareTo(Family that) {
		return this.father.compareTo(that.father);
	}
}
