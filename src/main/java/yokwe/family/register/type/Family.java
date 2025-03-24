package yokwe.family.register.type;

import java.util.List;

import yokwe.util.JapaneseDate;
import yokwe.util.StringUtil;

public class Family implements Comparable<Family> {
	static final org.slf4j.Logger logger = yokwe.util.LoggerUtil.getLogger();
	
	public static class Item {
		public final JapaneseDate date;
		public final Relation     relation;
		public final String       name;
		
		private Item(JapaneseDate date, Relation relation, String name) {
			this.date     = date;
			this.relation = relation;
			this.name     = name;
		}
		
		public static Item relation(JapaneseDate date, Relation relation, String name) {
			return new Item(date, relation, name);
		}
		
		@Override
		public String toString() {
			return String.format("{%s  %s  %s}", date.toString(), relation.toString(), name);
		}
	}
	
	public final String     familyName;
	public final String     father;
	public final String     mother;
	public final List<Item> itemList;
	
	public Family(String familyName, String father, String mother, List<Item> itemList) {
		this.familyName = familyName;
		this.father     = father;
		this.mother     = mother;
		this.itemList   = itemList;	
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
