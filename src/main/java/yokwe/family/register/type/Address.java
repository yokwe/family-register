package yokwe.family.register.type;

import java.util.ArrayList;
import java.util.List;

import yokwe.util.UnexpectedException;

public class Address implements Comparable<Address> {
	private static final org.slf4j.Logger logger = yokwe.util.LoggerUtil.getLogger();
	
	public static List<Address> getList(List<String> list) {
		if (list.size() == 0 || list.size() == 1) {
			logger.error("Unexpected list size");
			logger.error("  list  {}!", list);
			throw new UnexpectedException("Unexpected list size");
		}
		var ret = new ArrayList<Address>();
		{
			var address = list.get(list.size() - 1);
			for(var alias: list) {
				if (alias.equals(address)) continue;
				ret.add(new Address(address, alias));
			}
		}		
		return ret;
	}
	
	public final String newAddress;
	public final String oldAddress;
	
	private Address(String newAddress, String oldAddress) {
		this.newAddress = newAddress;
		this.oldAddress = oldAddress;
	}
	
	@Override
	public String toString() {
		return String.format("{%s  %s}", newAddress, oldAddress);
	}
	
	public String getKey() {
		return oldAddress;
	}
	@Override
	public int compareTo(Address that) {
		int ret = this.newAddress.compareTo(that.newAddress);
		if (ret == 0) ret = this.oldAddress.compareTo(that.oldAddress);
		return ret;
	}
}
