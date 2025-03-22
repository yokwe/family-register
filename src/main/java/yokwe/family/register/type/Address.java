package yokwe.family.register.type;

import java.util.Map;
import java.util.TreeMap;

public class Address {
	private static Map<String, String> map = new TreeMap<>();
	//                alias   address
	public static void add(String address, String alias) {
		map.put(alias, address);
	}
	
	public static String getAddress(String value) {
		if (map.containsKey(value)) {
			return map.get(value);
		} else {
			return value;
		}
	}
}
