package yokwe.family.register;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import yokwe.family.register.FamilyRegister.Parent;
import yokwe.family.register.graphviz.Dot;
import yokwe.family.register.type.Family;
import yokwe.family.register.type.Person;
import yokwe.util.FileUtil;
import yokwe.util.JapaneseDate;

public class WriteDotFile {
	private static final org.slf4j.Logger logger = yokwe.util.LoggerUtil.getLogger();

	public static void main(String[] args) {
		logger.info("START");

		process();

		logger.info("STOP");
	}
	
	private static final String UNKNOWN = FamilyRegister.UNKNOWN;
	private static final boolean isUnknown(String name) {
		return FamilyRegister.isUnknown(name);
	}
	
	private static final FamilyRegister            familyRegister;
	private static final Map<String, Person>       personMap;
	private static final Map<Parent, List<Family>> familyMap;
	static {
		familyRegister = new FamilyRegister();
		personMap      = familyRegister.personMap;
		familyMap      = familyRegister.familyMap;
	}
	private static JapaneseDate getBirthday(String name) {
		return familyRegister.getBirthday(name);
	}
	private static Person getPerson(String name) {
		return familyRegister.getPerson(name);
	}
	
	private static void personNode(Dot.GraphBase g, String id, Person person) {
		var name     = person.getName();
		var birthDay = getBirthday(name);
		var color    = person.relation.male ? "lightblue" : "pink";
		var year     = isUnknown(birthDay.eraString) ? UNKNOWN : (birthDay.eraString + birthDay.yearString + "年");
		var relation = isUnknown(person.father) ? UNKNOWN : (person.father + person.relation);
		var label = name + "\\n" + relation + "\\n" + year;
		g.node(id).attr("fillcolor", color).attr("label", label);
	}
	private static void process() {
		var g = new Dot.Graph("G");
		{
			g.attr("ranksep", "1").attr("nodesep", "0.5");
			g.nodeAttr("shape", "box").attr("style", "filled");
			
			var parentSet = new TreeSet<String>();
			var personSet = new TreeSet<String>();
			
			for(var entry: familyMap.entrySet()) {
				var parent    = entry.getKey();
				var childList = entry.getValue();
				var father    = parent.father();
				var mother    = parent.mother();
				var name      = "G_" + father + "_" + mother;
				
				parentSet.add(father);
				parentSet.add(mother);
				
				var f = g.subgraph("cluster_" + name);
				
				var f1 = f.subgraph();
				var f2 = f.subgraph();
				
				var left   = "F_" + father;
				var middle = "M_" + father + "_" + mother;
				var right  = "F_" + mother;
				
				f1.attr("rank", "same");
				
				personNode(f1, left, getPerson(father));
				f1.node(middle).attr("shape", "point").attr("label", "").attr("width", "1").attr("height", "0.1");
				personNode(f1, right, getPerson(mother));
				personSet.add(father);
				personSet.add(mother);
				
				f.edge(left, middle, right);
				
				if (!childList.isEmpty()) {
					Map<JapaneseDate, Person> childMap = new TreeMap<>();
					for(var child: childList) {
						var birthday = getBirthday(child.childName);
						var person   = getPerson(child.childName);
						childMap.put(birthday, person);
					}
					f2.attr("rank", "same");
					for(var e: childMap.values()) {
						var child = "P_" + e.getName();
						
						personSet.add(e.getName());
						personNode(f2, child, e);
						
						f.edge(middle, child);
					}
				}
			}
			
//			parentSet.retainAll(childSet);
			for(var parent: parentSet) {
				var person = getPerson(parent);
				if (isUnknown(person.mother)) continue;
				g.edge("P_" + parent, "F_" + parent);
			}
			
			logger.info("personMap  {}", personMap.size());
			logger.info("parentSet  {}", parentSet.size());
			logger.info("personSet  {}", personSet.size());
			
//			for(var e: personMap.values()) {
//				var name = e.getName();
//				if (personSet.contains(name)) continue;
//				logger.info("XX  {}  {}", e.getName(), e.father + e.relation);
//			}
		}
		
		logger.info("g  {}", g.toString().length());
		FileUtil.write().file("tmp/dot/a.dot", g.toString());
	}
}
