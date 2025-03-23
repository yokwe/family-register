package yokwe.family.register.type;

import java.util.List;

import yokwe.util.JapaneseDate;
import yokwe.util.StringUtil;

public class Person implements Comparable<Person> {
	public static class Item {
		enum Type {
			BIRTH, DEATH, MARRIAGE,
			MARRIAGE_JOIN, MARRIAGE_SEPARATE,
			JOIN, SEPARATE,
			BRANCH, RETIREMENT,
			HEAD_OF_HOUSE_BRANCH,
			HEAD_OF_HOUSE_DEATH,
			HEAD_OF_HOUSE_RETIREMENT,
			INHERITANCE,
			DISINHERITANCE,
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
		public static Item marriageSeparate(JapaneseDate date, String value) {
			return new Item(date, Type.MARRIAGE_SEPARATE, value);
		}
		public static Item join(JapaneseDate date, String value) {
			return new Item(date, Type.JOIN, value);
		}
		public static Item separate(JapaneseDate date, String value) {
			return new Item(date, Type.SEPARATE, value);
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
		
		@Override
		public String toString() {
			if (value == null) {
				return String.format("{%s  %s}", date.toString(), type.toString());
			} else {
				return String.format("{%s  %s  %s}", date.toString(), type.toString(), value);
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
	
	public String getReference() {
		if (father.equals("不明")) {
			return name;
		} else {
			return father + relation + name;
		}
	}
	
	@Override
	public String toString() {
		return StringUtil.toString(this);
	}

	@Override
	public int compareTo(Person that) {
		return this.getReference().compareTo(that.getReference());
	}
}
