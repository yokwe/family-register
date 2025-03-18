grammar FamilyRegister;

#
# concrete lexer rule first
#
BLOCK_BEGIN:		'{';
BLOCK_END:			'}';

FAMILY:				'家族';
MARRIAGE:			'結婚';
PERSON:				'人物';

ADDRESS:			'本籍';
FAMILY_NAME:		'姓';
FATHER:				'父';
MOTHER:				'母';
RELATION:			'関係';
NAME:				'名';
GENDER:				'性別';
BIRTH:				'出生';
DEATH:				'死亡';
DATE:				'日付';
HUSBAND:			'夫';
WIFE:				'妻';
CHILD:				'子';

JAPANESE_GENDER:	[男女];

#
# abstract lexer rule second
#
JAPANESE_STRING:	[\p{Script=Hiragana}\p{Script=Katakana}\p{Script=Han}]+;
JAPANESE_DATE:		[\p{Script=Han}0-9]+;

# ignore comment
COMMENT:			'#' ~( '\r' | '\n' )* -> skip;
# ignore white space
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
