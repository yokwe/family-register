#
#
#

main:
	#echo main

build:
	mvn ant:ant install

full-build:
	mvn clean ant:ant install

run-T001:
	ant run-T001

run-antlr:
	ant run-antlr
