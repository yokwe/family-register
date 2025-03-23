package yokwe.family.register.type;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Address {
	private static final org.slf4j.Logger logger = yokwe.util.LoggerUtil.getLogger();
	
	private Map<String, String> map = new TreeMap<>();
	//          alias   address
	public void add(List<String> addressList) {
		logger.info("add {}", addressList);
		
		if (addressList.size() <= 1) return;
		// treat last element as address
		var address = addressList.get(addressList.size() - 1);
		for(var alias: addressList) {
			if (alias.equals(address)) continue;
			map.put(address, alias);
		}
	}
	
	public String getAddress(String value) {
		if (map.containsKey(value)) {
			return map.get(value);
		} else {
			return value;
		}
	}
}
