package yokwe.family.register;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReadCSV {
	private static final org.slf4j.Logger logger = yokwe.util.LoggerUtil.getLogger();

	public static void main(String[] args) {
		logger.info("START");
		
		process();

		logger.info("STOP");
	}
	
	private static void process() {
//		var addressList = StorageRegister.ADDRESS.getList();
		var familyList  = StorageRegister.FAMILY.getList();
		var personList  = StorageRegister.PERSON.getList();
		var eventList   = StorageRegister.EVENT.getList();

		var personMap  = personList.stream().collect(Collectors.toMap(o -> (o.lastName + o.firstName), Function.identity()));
		
		// fix address
//		{
//			var addressMap = addressList.stream().collect(Collectors.toMap(o -> o.oldAddress, o -> o.newAddress));
//			for(var e: personMap.values()) {
//				if (addressMap.containsKey(e.address)) {
//					e.address = addressMap.get(e.address);
//				}
//			}
//			for(var e: eventList) {
//				if (e.type == Type.BRANCH) {
//					if (addressMap.containsKey(e.value)) {
//						e.value = addressMap.get(e.value);
//					}
//				}
//			}
//		}
		
		// fix family
		{
			record FatherMother(String father, String mother) implements Comparable<FatherMother> {
				
				public String getKey() {
					return father + mother;
				}
				@Override
				public int compareTo(FatherMother that) {
					return this.getKey().compareTo(that.getKey());
				}
			};
			
			// build family from eventList
			var fatherMotherSet = new TreeSet<FatherMother>();
			for(var e: eventList) {
				if (e.isMarriage()) {
					final String father;
					final String mother;
					if (personMap.get(e.name).relation.male) {
						father = e.name;
						mother = e.value;
					} else {
						father = e.value;
						mother = e.name;
					}
					fatherMotherSet.add(new FatherMother(father, mother));
				}
			}
			logger.info("fatherMotherSet  {}", fatherMotherSet.size());
			var familyMap  = new TreeMap<FatherMother, List<String>>();
			for(var e: familyList) {
				var key = new FatherMother(e.father, e.mother);
				if (familyMap.containsKey(key)) {
					familyMap.get(key).add(e.childName);
				} else {
					familyMap.put(key, new ArrayList<String>(Arrays.asList(e.childName)));
				}
			}
			logger.info("familyMap  {}", familyMap.size());

			int countFound = 0;
			int countNotFound = 0;
			for(var e: fatherMotherSet) {
				if (familyMap.containsKey(e)) {
//					logger.info("__  {}  {}", e, familyMap.get(e));
					countFound++;
				} else {
					logger.info("YY  {}", e);
					countNotFound++;
				}
			}
			logger.info("countFound     {}", countFound);
			logger.info("countNotFound  {}", countNotFound);
		}
	}
}
