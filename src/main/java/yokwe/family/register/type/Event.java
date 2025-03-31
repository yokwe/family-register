package yokwe.family.register.type;

import java.util.List;

import yokwe.util.JapaneseDate;

public class Event implements Comparable<Event> {
	public enum Type {
		BIRTH         ("出生"),
		DEATH         ("死亡"),
		HEAD_OF_HOUSE ("戸主"),
		BRANCH        ("分家"),
		RETIRE        ("隠居"),
		INHERIT_DEATH ("相続_戸主死亡"),
		INHERIT_RETIRE("相続_戸主隠居"),
		MARRIAGE      ("結婚"),
		MARRIAGE_JOIN ("結婚入籍"),
		DIVORCE       ("離婚"),
		DIVORCE_REJOIN("離婚復籍"),
		ADOPT_JOIN    ("養子入籍"),
		SUCCESSOR     ("嗣子"),
		DISINHERIT    ("廃嫡");
		
		public final String value;
		private Type(String value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return value;
		}
	}
	
	public static Event getBirth(List<Event> list) {
		if (list == null) return null;
		for(var e: list) {
			if (e.type == Event.Type.BIRTH) return e;
		}
		return null;
	}
	public static JapaneseDate getBirthDate(List<Event> list) {
		var event = getBirth(list);
		return event == null ? null : event.date;
	}
	public static Event getMarriage(List<Event> list) {
		if (list == null) return null;
		for(var e: list) {
			if (e.type == Event.Type.MARRIAGE) return e;
			if (e.type == Event.Type.MARRIAGE_JOIN) return e;
		}
		return null;
	}
	public static JapaneseDate getMarriageDate(List<Event> list) {
		var event = getMarriage(list);
		return event == null ? null : event.date;
	}

	public final String       name;
	public final JapaneseDate date;
	public final Type         type;
	public final String       value;
	
	private Event(String name, JapaneseDate date, Type type, String value) {
		this.name  = name;
		this.date  = date;
		this.type  = type;
		this.value = value;
	}
	private Event(String name, JapaneseDate date, Type type) {
		this(name, date, type, "");
	}
	public static Event birth(String name, JapaneseDate date) {
		return new Event(name, date, Type.BIRTH);
	}
	public static Event death(String name, JapaneseDate date) {
		return new Event(name, date, Type.DEATH);
	}
	public static Event headOfHouse(String name, JapaneseDate date) {
		return new Event(name, date, Type.HEAD_OF_HOUSE);
	}
	public static Event branch(String name, JapaneseDate date, String value) {
		return new Event(name, date, Type.BRANCH, value);
	}
	public static Event retire(String name, JapaneseDate date, String value) {
		return new Event(name, date, Type.RETIRE, value);
	}
	public static Event inheritDeath(String name, JapaneseDate date, String value) {
		return new Event(name, date, Type.INHERIT_DEATH, value);
	}
	public static Event inheritRetire(String name, JapaneseDate date, String value) {
		return new Event(name, date, Type.INHERIT_RETIRE, value);
	}
	public static Event marriage(String name, JapaneseDate date, String value) {
		return new Event(name, date, Type.MARRIAGE, value);
	}
	public static Event marriageJoin(String name, JapaneseDate date, String value) {
		return new Event(name, date, Type.MARRIAGE_JOIN, value);
	}
	public static Event divorce(String name, JapaneseDate date) {
		return new Event(name, date, Type.DIVORCE);
	}
	public static Event divorceRejoin(String name, JapaneseDate date) {
		return new Event(name, date, Type.DIVORCE_REJOIN);
	}
	public static Event adoptJoin(String name, JapaneseDate date, String value) {
		return new Event(name, date, Type.ADOPT_JOIN, value);
	}
	public static Event successor(String name, JapaneseDate date) {
		return new Event(name, date, Type.SUCCESSOR);
	}
	public static Event disinherit(String name, JapaneseDate date) {
		return new Event(name, date, Type.DISINHERIT);
	}
	
	public boolean isMarriage() {
		return type == Type.MARRIAGE || type == Type.MARRIAGE_JOIN;
	}
	
	public String getKey() {
		return name + date.toString() + type;
	}
	
	@Override
	public String toString() {
		if (value.isEmpty()) {
			return String.format("{%s %s %s}", name, date, type);
		} else {
			return String.format("{%s %s %s}", name, date, type, value);
		}
	}
	
	@Override
	public int compareTo(Event that) {
		int ret = this.name.compareTo(that.name);
		if (ret == 0) ret = this.date.compareTo(that.date);
		if (ret == 0) ret = this.type.compareTo(that.type);
		return ret;
	}
}