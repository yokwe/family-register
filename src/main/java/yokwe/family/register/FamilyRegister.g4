grammar FamilyRegister;

JAPANESE_STRING:	[\p{Script=Hiragana}\p{Script=Katakana}\p{Script=Han}]+;

fragment
JAPANESE_ERA:		[\p{Script=Han}][\p{Script=Han}];
fragment
JAPANESE_YEAR:		('元'|[1-9])|([1-9][0-9]);
fragment
JAPANESE_MONTH:		([1-9])|('1'[012]);
fragment
JAPANESE_DAY:		([1-9])|([12][0-9])|('3'[01]);
JAPANESE_DATE:		JAPANESE_ERA JAPANESE_YEAR '年' JAPANESE_MONTH '月' JAPANESE_DAY '日';

JAPANESE_GENDER:	'男'|'女';
SPACE:				[ \t\r\n]+ -> skip;


body
	: 'family' '{' blockList '}'
	;

blockList
	: block+
	;

block
	: marriageBlock
	| personBlock
	;

marriageBlock
	: 'marriage' '{' (marriageValue|childBlock)+ '}'
	;
marriageValue
	: addressValue
	| familyNameValue
	| dateValue
	| husbandValue
	| wifeValue
	;

personBlock
	:  'person' '{' personValue+ '}'
	;
personValue
	: addressValue
	| familyNameValue
	| fatherValue
	| motherValue
	| relationValue
	| nameValue
	| genderValue
	| birthValue
	| deathValue
	;

childBlock
	: 'child' '{' childValue+ '}'
	;
childValue
	: nameValue
	| genderValue
	| birthValue
	| deathValue
	;

addressValue
	: 'address' value=JAPANESE_STRING
	;
familyNameValue
	: 'family-name' value=JAPANESE_STRING
	;
fatherValue
	: 'father' value=JAPANESE_STRING
	;
motherValue
	: 'mother' value=JAPANESE_STRING
	;
dateValue
	: 'date' value=JAPANESE_DATE
	;
relationValue
	: 'relation' value=JAPANESE_STRING
	;
nameValue
	: 'name' value=JAPANESE_STRING
	;
genderValue
	: 'gender' value=JAPANESE_GENDER
	;
birthValue
	: 'birth' value=JAPANESE_DATE
	;
deathValue
	: 'death' value=JAPANESE_DATE
	;
husbandValue
	: 'husband' value=JAPANESE_STRING
	;
wifeValue
	: 'wife' value=JAPANESE_STRING
	;
