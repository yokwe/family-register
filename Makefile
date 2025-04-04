#
#
#

ANTLR_FILE := src/main/java/yokwe/family/register/FamilyRegister.g4
TOKEN_FILE := src/main/java/yokwe/family/register/visitor/FamilyRegister.tokens


main:
	@echo "ANTLR_FILE  ${ANTLR_FILE}"
	@echo "TOKEN_FILE  ${TOKEN_FILE}"
	#echo main

build: ${TOKEN_FILE}
	mvn ant:ant install

full-build:
	ant run-antlr
	mvn clean ant:ant install

${TOKEN_FILE}: ${ANTLR_FILE}
	ant run-antlr

run-T001:
	ant run-T001

run-antlr:
	ant run-antlr

tmp/dot/a.png: tmp/dot/a.dot
	dot -Tpng tmp/dot/a.dot >tmp/dot/a.png
