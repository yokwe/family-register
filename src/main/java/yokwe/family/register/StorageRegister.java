package yokwe.family.register;

import yokwe.family.register.type.*;

public class StorageRegister {
	public static final Storage storage = Storage.register;
	
	// family
	public static final Storage.LoadSaveList<Family> Family =
		new Storage.LoadSaveList.Impl<>(Family.class,  storage, "family.csv");
	// person
	public static final Storage.LoadSaveList<Person> Person =
		new Storage.LoadSaveList.Impl<>(Person.class,  storage, "person.csv");
	// event
	public static final Storage.LoadSaveList<Event> Event =
		new Storage.LoadSaveList.Impl<>(Event.class,  storage, "event.csv");
}
