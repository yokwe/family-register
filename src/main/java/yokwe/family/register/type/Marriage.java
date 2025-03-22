package yokwe.family.register.type;

import java.util.List;

import yokwe.util.JapaneseDate;
import yokwe.util.StringUtil;
import yokwe.util.UnexpectedException;

public class Marriage implements Comparable<Marriage> {
	private static final org.slf4j.Logger logger = yokwe.util.LoggerUtil.getLogger();
	
	public static class Item {
		enum Type {
			MARRIAGE,
			DIVORCE,
			RELATION,
		}
		
		enum Relation {
			SON("男"),
			SON_1("長男"),
			SON_2("二男"),
			SON_3("三男"),
			SON_4("四男"),
			SON_5("五男"),
			DAUGHTER("女"),
			DAUGHTER_1("長女"),
			DAUGHTER_2("二女"),
			DAUGHTER_3("三女"),
			DAUGHTER_4("四女"),
			DAUGHTER_5("五女");
			
			public static Relation fromString(String string) {
				for(var e: values()) {
					if (e.value.equals(string)) return e;
				}
				logger.error("Unexpected string");
				logger.error("  string  {}!", string);
				throw new UnexpectedException("Unexpected string");
			}
			
			public final String value;
			private Relation(String value) {
				this.value = value;
			}
			
			@Override
			public String toString() {
				return value;
			}
		}
		
		public final JapaneseDate date;
		public final Type         type;
		public final Relation     relation;
		public final String       name;
		
		private Item(String dateString, Type type, String relationStrings, String name) {
			this.date     = JapaneseDate.getInstance(dateString);
			this.type     = type;
			this.relation = Relation.fromString(relationStrings);
			this.name     = name;
		}
		private Item(String dateString, Type type) {
			this(dateString, type, null, null);
		}
		
		public static Item marriage(String dateString) {
			return new Item(dateString, Type.MARRIAGE);
		}
		public static Item divorce(String dateString) {
			return new Item(dateString, Type.DIVORCE);
		}
		public static Item relation(String dateString, String relationString, String name) {
			return new Item(dateString, Type.RELATION, relationString, name);
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
	// familyNameValue husbandValue wifeValue
	
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
