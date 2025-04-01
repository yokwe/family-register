package yokwe.family.register.type;

import yokwe.util.UnexpectedException;

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