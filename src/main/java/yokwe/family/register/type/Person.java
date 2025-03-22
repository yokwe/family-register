package yokwe.family.register.type;

import java.util.List;

import yokwe.util.JapaneseDate;
import yokwe.util.StringUtil;

public class Person implements Comparable<Person> {
	public static class Item {
		enum Type {
			BIRTH, DEATH, MARRIAGE,
			JOIN, SEPARATE,
			BRANCH, RETIREMENT,
			HEAD_OF_HOUSE_BRANCH,
			HEAD_OF_HOUSE_DEATH,
			HEAD_OF_HOUSE_RETIREMENT,
			INHERITANCE,
			DISINHERITANCE,
		}
		
		public final JapaneseDate date;
		public final Type   type;
		public final String value;
		
		private Item(String dateString, Type type, String value) {
			this.date  = JapaneseDate.getInstance(dateString);
			this.type  = type;
			this.value = value;
		}
		private Item(String dateString, Type type) {
			this(dateString, type, null);
		}
		public static Item birth(String dateString) {
			return new Item(dateString, Type.BIRTH);
		}
		public static Item death(String dateString) {
			return new Item(dateString, Type.DEATH);
		}
		public static Item marriage(String dateString, String value) {
			return new Item(dateString, Type.MARRIAGE, value);
		}
		public static Item join(String dateString, String value) {
			return new Item(dateString, Type.JOIN, value);
		}
		public static Item separate(String dateString, String value) {
			return new Item(dateString, Type.SEPARATE, value);
		}
		public static Item branch(String dateString, String value) {
			return new Item(dateString, Type.BRANCH, value);
		}
		public static Item retirement(String dateString, String value) {
			return new Item(dateString, Type.RETIREMENT, value);
		}
		public static Item headOfHouseBranch(String dateString) {
			return new Item(dateString, Type.HEAD_OF_HOUSE_BRANCH);
		}
		public static Item headOfHouseDeath(String dateString, String value) {
			return new Item(dateString, Type.HEAD_OF_HOUSE_DEATH, value);
		}
		public static Item headOfHouseRetirement(String dateString, String value) {
			return new Item(dateString, Type.HEAD_OF_HOUSE_RETIREMENT, value);
		}
		public static Item inheritance(String dateString) {
			return new Item(dateString, Type.INHERITANCE);
		}
		public static Item disinheritance(String dateString) {
			return new Item(dateString, Type.DISINHERITANCE);
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
	
	
	public final String address;
	public final String father;
	public final String relation;
	public final String familyName;
	public final String name;
	
	public final List<Item> itemList;
	
	public Person(String address, String father, String relation, String familyName, String name, List<Item> itemList) {
		this.address    = address;
		this.father     = father;
		this.relation   = relation;
		this.familyName = familyName;
		this.name       = name;
		this.itemList   = itemList;
	}
	
	public String getReference() {
		return father + relation + name;
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
