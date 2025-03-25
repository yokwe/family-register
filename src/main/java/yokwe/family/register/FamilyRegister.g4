grammar FamilyRegister;

//
// concrete lexer rule first
//
BLOCK_BEGIN:		'{';
BLOCK_END:			'}';

FAMILY_REGISTER:	'戸籍';

// block
ADDRESS:			'本籍';
PERSON:				'人物';
FAMILY:				'家族';
ITEM:				'事項';
CHILD:				'子供';

FATHER:				'父親';
MOTHER:				'母親';
RELATION:			'続柄';
FAMILY_NAME:		'名字';
NAME:				'名前';

BIRTH:				'出生';
DEATH:				'死亡';
BRANCH:				'分家';
HEAD_OF_HOUSE:		'戸主';
RETIRE:				'隠居';
INHERIT:			'相続';
DEATH_OF_HEAD:		'戸主死亡';
RETIRE_OF_HEAD:		'戸主隠居';
MARRIAGE:			'結婚';
MARRIAGE_JOIN:		'結婚入籍';
DIVORCE:			'離婚';
DIVORCE_REJOIN:		'離婚復籍';
SUCCESSOR:			'嗣子';
DISINHERIT:			'廃嫡';


JAPANESE_RELATION:	[長二三四五]?[男女];
JAPANESE_GENDER:	[男女];

//
// abstract lexer rule second
//
fragment
JAPANESE_ERA:		'昭和'|'大正'|'明治'|'慶応'|'元治'|'文久'|'万延'|'安政'|'嘉永'|'弘化'|'天保'|'文政'|'文化'|'享和';
fragment
JAPANESE_YEAR:		'元'|[1-9]|[1-9][0-9];
fragment
JAPANESE_MONTH:		[1-9]|'1'[012];
fragment
JAPANESE_DAY:		[1-9]|[12][0-9]|[3][01];
JAPANESE_DATE:		JAPANESE_ERA JAPANESE_YEAR '年' JAPANESE_MONTH '月' JAPANESE_DAY '日';

JAPANESE_STRING:	[\p{Script=Hiragana}\p{Script=Katakana}\p{Script=Han}]+;

// ignore comment
COMMENT:			'#' ~( '\r' | '\n' )* -> skip;
// ignore white space
SPACE:				[ \t\r\n]+ -> skip;



body
	: FAMILY_REGISTER BLOCK_BEGIN block* BLOCK_END
	;

block
	: addressBlock
	| personBlock
	| familyBlock
	;

addressBlock
	: ADDRESS BLOCK_BEGIN addressBlockItem* BLOCK_END
	;
addressBlockItem
	: value=JAPANESE_STRING
	;
	
	
personBlock
	: PERSON BLOCK_BEGIN addressValue familyNameValue fatherValue relationValue nameValue itemBlock? BLOCK_END
	;
itemBlock
	: ITEM BLOCK_BEGIN itemValue* BLOCK_END
	;
itemValue
	: date=JAPANESE_DATE BIRTH													#  ItemBirth
	| date=JAPANESE_DATE DEATH													#  ItemDeath
	| date=JAPANESE_DATE HEAD_OF_HOUSE											#  ItemHeadOfHouse
	| date=JAPANESE_DATE BRANCH						address=JAPANESE_STRING		#  ItemBranch
	| date=JAPANESE_DATE RETIRE						newHead=JAPANESE_STRING		#  ItemRetire
	| date=JAPANESE_DATE INHERIT DEATH_OF_HEAD		oldHead=JAPANESE_STRING		#  ItemInheritDeath
	| date=JAPANESE_DATE INHERIT RETIRE_OF_HEAD		oldHead=JAPANESE_STRING		#  ItemInheritRetire
	| date=JAPANESE_DATE MARRIAGE					spouse=JAPANESE_STRING		#  ItemMarriage
	| date=JAPANESE_DATE MARRIAGE_JOIN				spouse=JAPANESE_STRING		#  ItemMarriageJoin
	| date=JAPANESE_DATE DIVORCE												#  ItemDivorce
	| date=JAPANESE_DATE DIVORCE_REJOIN				address=JAPANESE_STRING		#  ItemDivorceRejoin
	| date=JAPANESE_DATE SUCCESSOR												#  ItemSuccessor
	| date=JAPANESE_DATE DISINHERIT												#  ItemDisinherit
	;


familyBlock
	: FAMILY BLOCK_BEGIN addressValue? familyNameValue motherValue fatherValue childBlock+ BLOCK_END
	;
childBlock
	: CHILD BLOCK_BEGIN addressValue? relationValue nameValue itemBlock? BLOCK_END
	;
	

addressValue
	: ADDRESS value=JAPANESE_STRING
	;
fatherValue
	: FATHER value=JAPANESE_STRING
	;
motherValue
	: MOTHER value=JAPANESE_STRING
	;
relationValue
	: RELATION value=JAPANESE_RELATION
	;
familyNameValue
	: FAMILY_NAME value=JAPANESE_STRING
	;
nameValue
	: NAME value=JAPANESE_STRING
	;
