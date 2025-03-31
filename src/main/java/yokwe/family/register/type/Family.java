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
	
	
	public static Family biological(String father, String mother, String lastName, String childName) {
		return new Family(father, mother, lastName, ChildType.BIOLOGICAL, childName);
	}
	public static Family adopted(String father, String mother, String lastName, String childName) {
		return new Family(father, mother, lastName, ChildType.ADOPTED, childName);
	}
	
	public final String    father;
	public final String    mother;
	public final String    childName;
	
	public final String    lastName;
	public final ChildType childType;
	
	private Family(String father, String mother, String lastName, ChildType childType, String childName) {
		this.father    = father;
		this.mother    = mother;
		this.lastName  = lastName;
		this.childType = childType;
		this.childName = childName;
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
