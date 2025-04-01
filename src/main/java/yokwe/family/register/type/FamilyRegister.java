package yokwe.family.register.type;

import java.util.List;
import java.util.stream.Collectors;

public class FamilyRegister {
	public static final String UNKNOWN = "不明";
	
	public static boolean isUnknown(String string) {
		return string.equals(UNKNOWN);
	}
	
	
	private List<Event> eventList;

	public List<Event> getEvent(Event.Type type) {
		return null;
	}
	public List<Event> getEvent(String name) {
		return eventList.stream().filter(o -> o.name.equals(name)).collect(Collectors.toList());
	}
	
	private List<Person> personList;

	public Person getPerson(String name) {
		return personList.stream().filter(o -> name.equals(o.lastName + o.firstName)).findFirst().orElse(null);
	}
}
