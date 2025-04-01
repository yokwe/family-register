package yokwe.family.register.type;

import yokwe.util.StringUtil;

public class Person implements Comparable<Person> {
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
