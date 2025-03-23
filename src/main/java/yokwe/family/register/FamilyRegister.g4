grammar FamilyRegister;

//
// concrete lexer rule first
//
BLOCK_BEGIN:			'{';
BLOCK_END:				'}';

FAMILY_REGISTER:		'戸籍';

ADDRESS:				'本籍';
PERSON:					'人物';
MARRIAGE:				'結婚';

CHANGE:					'変更';
ITEM:					'事項';


FATHER:					'父親';
RELATION:				'続柄';
FAMILY_NAME:			'名字';
NAME:					'名前';

BIRTH:					'出生';
DEATH:					'死亡';
INHERITANCE:			'嗣子';
DISINHERITANCE:			'廃嫡';
HEAD_OF_HOUSE:			'戸主';
BRANCH:					'分家';
JOIN:					'入籍';
SEPARATE:				'除籍';
RETIREMENT:				'隠居';
DEATH_OF_PREVIOUS:		'前戸主死亡';
RETIREMENT_OF_PREVIOUS:	'前戸主隠居';

DIVORCE:				'離婚';

HUSBAND:				'夫';
WIFE:					'妻';


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
	: FAMILY_REGISTER BLOCK_BEGIN block+ BLOCK_END
	;

block
	: addressBlock
	| personBlock
	| marriageBlock
	;

addressBlock
	: ADDRESS BLOCK_BEGIN addressBlockItem+ BLOCK_END
	;
addressBlockItem
	: value=JAPANESE_STRING
	;
	
personBlock
	: PERSON BLOCK_BEGIN addressValue fatherValue relationValue familyNameValue nameValue personItemBlock BLOCK_END
	;
addressValue
	: ADDRESS value=JAPANESE_STRING
	;
fatherValue
	: FATHER value=JAPANESE_STRING
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
personItemBlock
	: ITEM BLOCK_BEGIN personItemValue+ BLOCK_END
	;
personItemValue
	: date=JAPANESE_DATE BIRTH															#  PersonItemBirth
	| date=JAPANESE_DATE DEATH															#  PersonItemDeath
	| date=JAPANESE_DATE MARRIAGE spouse=JAPANESE_STRING								#  PersonItemMarriage
	| date=JAPANESE_DATE JOIN address=JAPANESE_STRING									#  PersonItemJoin
	| date=JAPANESE_DATE SEPARATE address=JAPANESE_STRING								#  PersonItemSeparate
	| date=JAPANESE_DATE BRANCH address=JAPANESE_STRING									#  PersonItemBranch
	| date=JAPANESE_DATE RETIREMENT newHead=JAPANESE_STRING								#  PersonItemRetirement
	| date=JAPANESE_DATE HEAD_OF_HOUSE BRANCH											#  PersonItemHeadOfHouseBranch
	| date=JAPANESE_DATE HEAD_OF_HOUSE DEATH_OF_PREVIOUS prevHead=JAPANESE_STRING		#  PersonItemHeadOfHouseDeath
	| date=JAPANESE_DATE HEAD_OF_HOUSE RETIREMENT_OF_PREVIOUS prevHead=JAPANESE_STRING	#  PersonItemHeadOfHouseRetirement
	| date=JAPANESE_DATE INHERITANCE													#  PersonItemInheritance
	| date=JAPANESE_DATE DISINHERITANCE													#  PersonItemDisinheritance
	;


marriageBlock
	: MARRIAGE BLOCK_BEGIN familyNameValue husbandValue wifeValue marriageItemBlock BLOCK_END
	;
marriageItemBlock
	: ITEM BLOCK_BEGIN marriageItemValue+ BLOCK_END
	;
marriageItemValue
	: date=JAPANESE_DATE type=MARRIAGE									#  MarriageItemMarriage
	| date=JAPANESE_DATE type=DIVORCE									#  MarriageItemDivorce
	| date=JAPANESE_DATE type=JAPANESE_RELATION name=JAPANESE_STRING	#  MarriageItemRelation
	;
husbandValue
	: HUSBAND value=JAPANESE_STRING
	;
wifeValue
	: WIFE value=JAPANESE_STRING
	;
