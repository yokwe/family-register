package yokwe.family.register.type;

import yokwe.util.StringUtil;

public class Person implements Comparable<Person> {
	public final String   father;
	public final Relation relation;
	public final String   lastName;
	public final String   firstName;
	
	public final String   mother;
	public final String   address;
	
	public Person(String address, String lastName, String mother, String father, Relation relation, String firstName) {
		this.address   = address;
		this.lastName  = lastName;
		this.mother    = mother;
		this.father    = father;
		this.relation  = relation;
		this.firstName = firstName;
	}
	
	public String getKey() {
		return lastName + firstName;
	}
	
	@Override
	public String toString() {
		return StringUtil.toString(this);
	}
	
	@Override
	public int compareTo(Person that) {
		int ret = this.father.compareTo(that.father);
		if (ret == 0) ret = this.relation.compareTo(that.relation);
		if (ret == 0) ret = this.lastName.compareTo(that.lastName);
		if (ret == 0) ret = this.firstName.compareTo(that.firstName);
		return ret;
	}
}
