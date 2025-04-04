package yokwe.family.register;

import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import yokwe.family.register.graphviz.Dot;
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
	
	private static void personNode(FamilyRegister familyRegister, Dot.GraphBase g, String id, Person person) {
		var name = person.getName();
		var birthDay = familyRegister.getBirthday(name);
		var color = person.relation.male ? "lightblue" : "pink";
		var year = birthDay.eraString + birthDay.yearString;
		var label = name + "\\n" + person.father + person.relation + "\\n" + year + "年";
		g.node(id).attr("fillcolor", color).attr("label", label);
	}
	private static void process() {
		var familyRegister = new FamilyRegister();
		
		var g = new Dot.Graph("G");
		{
			g.attr("ranksep", "1").attr("nodesep", "2");
			g.nodeAttr("shape", "box").attr("style", "filled");
			
			var parentSet = new TreeSet<String>();
			
			for(var entry: familyRegister.familyMap.entrySet()) {
				var parent = entry.getKey();
				var childList = entry.getValue();

				var father = parent.father();
				var mother = parent.mother();
				var name   = "G_" + father + "_" + mother;
				
				parentSet.add(father);
				parentSet.add(mother);
				
				var f = g.subgraph("cluster_" + name);
				
				var f1 = f.subgraph();
				var f2 = f.subgraph();
				
				var left   = "F_" + father;
				var middle = "M_" + father + "_" + mother;
				var right  = "F_" + mother;
				
				f1.attr("rank", "same");
				
				personNode(familyRegister, f1, left, familyRegister.getPerson(father));
				f1.node(middle).attr("shape", "point").attr("label", "").attr("width", "1").attr("height", "0.1");
				personNode(familyRegister, f1, right, familyRegister.getPerson(mother));
				
				f.edge(left, middle, right);
				
				if (!childList.isEmpty()) {
					Map<JapaneseDate, Person> childMap = new TreeMap<>();
					for(var child: childList) {
						var birthDay = familyRegister.getBirthday(child.childName);
						var person   = familyRegister.getPerson(child.childName);
						childMap.put(birthDay, person);
					}
					f2.attr("rank", "same");
					for(var e: childMap.values()) {
						var child = "P_" + e.getName();
						personNode(familyRegister, f2, child, e);
						
						f.edge(middle, child);
					}
				}
			}
			
//			parentSet.retainAll(childSet);
			for(var parent: parentSet) {
				var person = familyRegister.getPerson(parent);
				if (FamilyRegister.isUnknown(person.mother)) continue;
				g.edge("P_" + parent, "F_" + parent);
			}
		}
		logger.info("g  {}", g.toString().length());
		FileUtil.write().file("tmp/dot/a.dot", g.toString());
	}
}
