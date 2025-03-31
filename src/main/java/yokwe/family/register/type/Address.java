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
	
	public final String address;
	public final String alias;
	
	private Address(String address, String alias) {
		this.address = address;
		this.alias   = alias;
	}
	
	@Override
	public String toString() {
		return String.format("{%s  %s}", address, alias);
	}
	
	public String getKey() {
		return address;
	}
	@Override
	public int compareTo(Address that) {
		int ret = this.address.compareTo(that.address);
		if (ret == 0) ret = this.alias.compareTo(that.alias);
		return ret;
	}
}
