package yokwe.family.register;

import yokwe.family.register.type.*;

public class StorageRegister {
	public static final Storage storage = Storage.register;
	
	// address
	public static final Storage.LoadSave<Address, String> ADDRESS =
		new Storage.LoadSave.Impl<>(Address.class, Address::getKey, storage, "address.csv");
	// family
	public static final Storage.LoadSave<Family, String> FAMILY =
		new Storage.LoadSave.Impl<>(Family.class, Family::getKey, storage, "family.csv");
	// person
	public static final Storage.LoadSave<Person, String> PERSON =
		new Storage.LoadSave.Impl<>(Person.class, Person::getKey, storage, "person.csv");
	// event
	public static final Storage.LoadSave<Event, String> EVENT =
		new Storage.LoadSave.Impl<>(Event.class, Event::getKey, storage, "event.csv");
}
