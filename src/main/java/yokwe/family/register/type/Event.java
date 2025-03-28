package yokwe.family.register.type;

import java.util.List;

import yokwe.util.JapaneseDate;

public class Event {
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

	
	public final JapaneseDate date;
	public final Type         type;
	public final String       value;
	
	private Event(JapaneseDate date, Type type, String value) {
		this.date  = date;
		this.type  = type;
		this.value = value;
	}
	private Event(JapaneseDate date, Type type) {
		this(date, type, null);
	}
	public static Event birth(JapaneseDate date) {
		return new Event(date, Type.BIRTH);
	}
	public static Event death(JapaneseDate date) {
		return new Event(date, Type.DEATH);
	}
	public static Event headOfHouse(JapaneseDate date) {
		return new Event(date, Type.HEAD_OF_HOUSE);
	}
	public static Event branch(JapaneseDate date, String value) {
		return new Event(date, Type.BRANCH, value);
	}
	public static Event retire(JapaneseDate date, String value) {
		return new Event(date, Type.RETIRE, value);
	}
	public static Event inheritDeath(JapaneseDate date, String value) {
		return new Event(date, Type.INHERIT_DEATH, value);
	}
	public static Event inheritRetire(JapaneseDate date, String value) {
		return new Event(date, Type.INHERIT_RETIRE, value);
	}
	public static Event marriage(JapaneseDate date, String value) {
		return new Event(date, Type.MARRIAGE, value);
	}
	public static Event marriageJoin(JapaneseDate date, String value) {
		return new Event(date, Type.MARRIAGE_JOIN, value);
	}
	public static Event divorce(JapaneseDate date) {
		return new Event(date, Type.DIVORCE);
	}
	public static Event divorceRejoin(JapaneseDate date) {
		return new Event(date, Type.DIVORCE_REJOIN);
	}
	public static Event adoptJoin(JapaneseDate date, String value) {
		return new Event(date, Type.ADOPT_JOIN, value);
	}
	public static Event successor(JapaneseDate date) {
		return new Event(date, Type.SUCCESSOR);
	}
	public static Event disinherit(JapaneseDate date) {
		return new Event(date, Type.DISINHERIT);
	}
	
	public boolean isMarriage() {
		return type == Type.MARRIAGE || type == Type.MARRIAGE_JOIN;
	}
	
	@Override
	public String toString() {
		if (value == null) {
			return String.format("{%s %s}", date.toString(), type.toString());
		} else {
			return String.format("{%s %s %s}", date.toString(), type.toString(), value);
		}
	}
}