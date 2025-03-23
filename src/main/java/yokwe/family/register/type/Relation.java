package yokwe.family.register.type;

import yokwe.util.UnexpectedException;

public enum Relation {
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
	private Relation(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
}