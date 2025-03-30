package yokwe.family.register.type;

import java.util.List;

import yokwe.util.StringUtil;

public class Family implements Comparable<Family> {
	public final String       address;
	public final String       familyName;
	public final String       mother;
	public final String       father;
	public final List<String> childList; // list of person full name
	
	public Family(String address, String familyName, String mother, String father, List<String> childList) {
		this.address    = address;
		this.familyName = familyName;
		this.mother     = mother;
		this.father     = father;
		this.childList  = childList;
	}
	
	public String getKey() {
		return father + "-" + mother;
	}

	@Override
	public String toString() {
		return StringUtil.toString(this);
	}
	
	@Override
	public int compareTo(Family that) {
		return this.father.compareTo(that.father);
	}
}
