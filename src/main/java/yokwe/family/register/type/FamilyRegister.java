package yokwe.family.register.type;

public class FamilyRegister {
	private static final String UNKNOWN = "不明";
	
	public static boolean isUnknown(String string) {
		return string.equals(UNKNOWN);
	}
}
