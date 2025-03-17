#
#
#


main:
	#echo main

build: src/main/java/yokwe/family/register/visitor/FamilyRegisterVisitor.java
	mvn ant:ant install

full-build:
	ant run-antlr
	mvn clean ant:ant install

src/main/java/yokwe/family/register/visitor/FamilyRegisterVisitor.java: src/main/java/yokwe/family/register/FamilyRegister.g4
	ant run-antlr

run-T001:
	ant run-T001

run-antlr:
	ant run-antlr
