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

JAPANESE_GENDER:	[MF];

BLOCK_BEGIN:		'{';
BLOCK_END:			'}';

FAMILY:				'family';
MARRIAGE:			'marriage';
PERSON:				'person';

ADDRESS:			'address';
FAMILY_NAME:		'family-name';
FATHER:				'father';
MOTHER:				'mother';
RELATION:			'relation';
NAME:				'name';
GENDER:				'gender';
BIRTH:				'birth';
DEATH:				'death';
DATE:				'date';
HUSBAND:			'husband';
WIFE:				'wife';
CHILD:				'child';

SPACE:				[ \t\r\n]+ -> skip;



body
	: FAMILY BLOCK_BEGIN blockList BLOCK_END
	;

blockList
	: block+
	;

block
	: marriageBlock
	| personBlock
	;

marriageBlock
	: MARRIAGE BLOCK_BEGIN (marriageValue|childBlock)+ BLOCK_END
	;
marriageValue
	: addressValue
	| familyNameValue
	| dateValue
	| husbandValue
	| wifeValue
	;

personBlock
	:  PERSON BLOCK_BEGIN personValue+ BLOCK_END
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
	: CHILD BLOCK_BEGIN childValue+ BLOCK_END
	;
childValue
	: nameValue
	| genderValue
	| birthValue
	| deathValue
	;

addressValue
	: ADDRESS value=JAPANESE_STRING
	;
familyNameValue
	: FAMILY_NAME value=JAPANESE_STRING
	;
fatherValue
	: FATHER value=JAPANESE_STRING
	;
motherValue
	: MOTHER value=JAPANESE_STRING
	;
dateValue
	: DATE value=JAPANESE_DATE
	;
relationValue
	: RELATION value=JAPANESE_STRING
	;
nameValue
	: NAME value=JAPANESE_STRING
	;
genderValue
	: GENDER value=JAPANESE_GENDER
	;
birthValue
	: BIRTH value=JAPANESE_DATE
	;
deathValue
	: DEATH value=JAPANESE_DATE
	;
husbandValue
	: HUSBAND value=JAPANESE_STRING
	;
wifeValue
	: WIFE value=JAPANESE_STRING
	;
