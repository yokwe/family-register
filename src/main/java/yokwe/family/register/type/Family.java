package yokwe.family.register.type;

import yokwe.util.StringUtil;
import yokwe.util.UnexpectedException;

public class Family implements Comparable<Family> {
	private static final org.slf4j.Logger logger = yokwe.util.LoggerUtil.getLogger();
	
	public enum ChildType {
		BIOLOGICAL("実子"),
		ADOPTED   ("養子");
		
		public static ChildType fromString(String string) {
			for(var e: values()) {
				if (e.value.equals(string)) return e;
			}
			logger.error("Unexpected string");
			logger.error("  string  {}!", string);
			throw new UnexpectedException("Unexpected string");
		}
		
		public final String value;
		
		private ChildType(String value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return value;
		}
	}
	
	
	public static Family biological(String father, String mother, String childName, String lastName) {
		return new Family(father, mother, childName, lastName, ChildType.BIOLOGICAL);
	}
	public static Family adopted(String father, String mother, String childName, String lastName) {
		return new Family(father, mother, childName, lastName, ChildType.ADOPTED);
	}
	
	public final String    father;
	public final String    mother;
	public final String    childName;
	
	public final String    lastName;
	public final ChildType childType;
	
	public Family(String father, String mother, String childName, String lastName, ChildType childType) {
		this.father    = father;
		this.mother    = mother;
		this.childName = childName;
		
		this.lastName  = lastName;
		this.childType = childType;
	}
		
	@Override
	public String toString() {
		return StringUtil.toString(this);
	}
	
	public String getKey() {
		return father + mother + childName;
	}
	@Override
	public int compareTo(Family that) {
		int ret = this.father.compareTo(that.father);
		if (ret == 0) ret = this.mother.compareTo(that.mother);
		if (ret == 0) ret = this.childName.compareTo(that.childName);
		return ret;
	}
}
