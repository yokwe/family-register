package yokwe.family.register.type;

import java.util.List;

import yokwe.util.JapaneseDate;
import yokwe.util.StringUtil;

public class Person implements Comparable<Person> {
	private static final org.slf4j.Logger logger = yokwe.util.LoggerUtil.getLogger();
	
	public static class Item {
		public enum Type {
			BIRTH("出生"),
			DEATH("死亡"),
			MARRIAGE("結婚"),
			MARRIAGE_JOIN("結婚入籍"),
			DIVORCE("離婚"),
			DIVORCE_REJOIN("離婚復籍"),
			BRANCH("分家"),
			RETIREMENT("隠居"),
			HEAD_OF_HOUSE_BRANCH("戸主_分家"),
			HEAD_OF_HOUSE_DEATH("戸主_前戸主死亡"),
			HEAD_OF_HOUSE_RETIREMENT("戸主_前戸主隠居"),
			INHERITANCE("嗣子"),
			DISINHERITANCE("廃嫡");
			
			public final String value;
			private Type(String value) {
				this.value = value;
			}
			
			@Override
			public String toString() {
				return value;
			}
		}
		
		public final JapaneseDate date;
		public final Type         type;
		public final String       value;
		
		private Item(JapaneseDate date, Type type, String value) {
			this.date  = date;
			this.type  = type;
			this.value = value;
		}
		private Item(JapaneseDate date, Type type) {
			this(date, type, null);
		}
		public static Item birth(JapaneseDate date) {
			return new Item(date, Type.BIRTH);
		}
		public static Item death(JapaneseDate date) {
			return new Item(date, Type.DEATH);
		}
		public static Item marriage(JapaneseDate date, String value) {
			return new Item(date, Type.MARRIAGE, value);
		}
		public static Item marriageJoin(JapaneseDate date, String value) {
			return new Item(date, Type.MARRIAGE_JOIN, value);
		}
		public static Item divorce(JapaneseDate date) {
			return new Item(date, Type.DIVORCE);
		}
		public static Item divorceRejoin(JapaneseDate date) {
			return new Item(date, Type.DIVORCE_REJOIN);
		}
		public static Item branch(JapaneseDate date, String value) {
			return new Item(date, Type.BRANCH, value);
		}
		public static Item retirement(JapaneseDate date, String value) {
			return new Item(date, Type.RETIREMENT, value);
		}
		public static Item headOfHouseBranch(JapaneseDate date) {
			return new Item(date, Type.HEAD_OF_HOUSE_BRANCH);
		}
		public static Item headOfHouseDeath(JapaneseDate date, String value) {
			return new Item(date, Type.HEAD_OF_HOUSE_DEATH, value);
		}
		public static Item headOfHouseRetirement(JapaneseDate date, String value) {
			return new Item(date, Type.HEAD_OF_HOUSE_RETIREMENT, value);
		}
		public static Item inheritance(JapaneseDate date) {
			return new Item(date, Type.INHERITANCE);
		}
		public static Item disinheritance(JapaneseDate date) {
			return new Item(date, Type.DISINHERITANCE);
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
	
	public final String   address;
	public final String   father;
	public final Relation relation;
	public final String   familyName;
	public final String   name;
	
	public final List<Item> itemList;
	
	public Person(String address, String father, Relation relation, String familyName, String name, List<Item> itemList) {
		this.address    = address;
		this.father     = father;
		this.relation   = relation;
		this.familyName = familyName;
		this.name       = name;
		this.itemList   = itemList;
	}
	
	public Item getBirth() {
		for(var e: itemList) {
			if (e.type == Item.Type.BIRTH) return e;
		}
		return null;
	}
	public Item getMarriage() {
		for(var e: itemList) {
			if (e.type == Item.Type.MARRIAGE) return e;
			if (e.type == Item.Type.MARRIAGE_JOIN) return e;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return StringUtil.toString(this);
	}

	@Override
	public int compareTo(Person that) {
		int result = this.father.compareTo(that.father);
		if (result == 0) {
			var thisBirth = this.getBirth();
			var thatBirth = that.getBirth();
			if (thisBirth != null && thatBirth != null) {
				result = thisBirth.date.compareTo(thatBirth.date);
			}
		}
		return result;
	}
}
