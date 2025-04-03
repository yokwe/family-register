package yokwe.family.register.type;

import yokwe.util.StringUtil;
import yokwe.util.UnexpectedException;

public class Person implements Comparable<Person> {
	public enum Relation {
		SON       ("男",   true),
		SON_1     ("長男", true),
		SON_2     ("二男", true),
		SON_3     ("三男", true),
		SON_4     ("四男", true),
		SON_5     ("五男", true),
		DAUGHTER  ("女",   false),
		DAUGHTER_1("長女", false),
		DAUGHTER_2("二女", false),
		DAUGHTER_3("三女", false),
		DAUGHTER_4("四女", false),
		DAUGHTER_5("五女", false);
		
		private static final org.slf4j.Logger logger = yokwe.util.LoggerUtil.getLogger();

		public static Relation fromString(String string) {
			for(var e: values()) {
				if (e.value.equals(string)) return e;
			}
			logger.error("Unexpected string");
			logger.error("  string  {}!", string);
			throw new UnexpectedException("Unexpected string");
		}
		
		public final String value;
		public final boolean male;
		private Relation(String value, boolean male) {
			this.value = value;
			this.male  = male;
		}
		
		@Override
		public String toString() {
			return value;
		}
	}
	
	public final String   lastName;
	public final String   firstName;
	
	public final String   father;
	public final Relation relation;
	public final String   mother;
	public final String   address;
	
	public Person(String lastName, String firstName, String father, Relation relation, String mother, String address) {
		this.lastName  = lastName;
		this.firstName = firstName;
		this.father    = father;
		this.relation  = relation;
		this.mother    = mother;
		this.address   = address;
	}
	public Person(Person that, String address) {
		this(that.lastName, that.firstName, that.father, that.relation, that.mother, address);
	}
	
	@Override
	public String toString() {
		return StringUtil.toString(this);
	}
	
	public String getName() {
		return lastName + firstName;
	}
		
	public String getKey() {
		return lastName + firstName;
	}
	@Override
	public int compareTo(Person that) {
		int ret = 0;
		if (ret == 0) ret = this.lastName.compareTo(that.lastName);
		if (ret == 0) ret = this.firstName.compareTo(that.firstName);
		return ret;
	}
}
