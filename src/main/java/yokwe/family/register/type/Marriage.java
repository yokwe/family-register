package yokwe.family.register.type;

import java.util.List;

import yokwe.util.JapaneseDate;
import yokwe.util.StringUtil;

public class Marriage implements Comparable<Marriage> {
	static final org.slf4j.Logger logger = yokwe.util.LoggerUtil.getLogger();
	
	public static class Item {
		enum Type {
			MARRIAGE,
			DIVORCE,
			RELATION,
		}
		
		public final JapaneseDate date;
		public final Type         type;
		public final Relation     relation;
		public final String       name;
		
		private Item(JapaneseDate date, Type type, Relation relation, String name) {
			this.date     = date;
			this.type     = type;
			this.relation = relation;
			this.name     = name;
		}
		private Item(JapaneseDate date, Type type) {
			this(date, type, null, null);
		}
		
		public static Item marriage(JapaneseDate date) {
			return new Item(date, Type.MARRIAGE);
		}
		public static Item divorce(JapaneseDate date) {
			return new Item(date, Type.DIVORCE);
		}
		public static Item relation(JapaneseDate date, Relation relation, String name) {
			return new Item(date, Type.RELATION, relation, name);
		}
		
		@Override
		public String toString() {
			if (relation == null) {
				return String.format("{%s  %s}", date.toString(), type.toString());
			} else {
				return String.format("{%s  %s  %s  %s}", date.toString(), type.toString(), relation.toString(), name);
			}
		}
	}
	
	public final String     familyName;
	public final String     husband;
	public final String     wife;
	public final List<Item> itemList;
	
	public Marriage(String familyName, String husband, String wife, List<Item> itemList) {
		this.familyName = familyName;
		this.husband    = husband;
		this.wife       = wife;
		this.itemList   = itemList;	
	}
	
	@Override
	public String toString() {
		return StringUtil.toString(this);
	}
	
	@Override
	public int compareTo(Marriage that) {
		return this.husband.compareTo(that.husband);
	}
}
