/* 
 * my_assembler 함수를 위한 변수 선언 및 매크로를 담고 있는 헤더 파일이다. 
 * 
 */
#define MAX_INST 256
#define MAX_LINES 5000

#define MAX_COLUMNS 4
#define MAX_OPERAND 3

#define MAX_LENGTH_INSTRUCTION_LINE 128
#define MAX_LENGTH_OPCODE_TABLE 64

#define SIZE_WORD_BY_BYTE 3
#define SIZE_WORD_BY_BYTE_NULL 4
#define MAX_LENGTH_LABEL 6
#define MAX_LENGTH_LABEL_NULL 7
#define MAX_LENGTH_OPCODE_NAME 6
#define MAX_LENGTH_OPCODE_NAME_NULL 7
#define MAX_LENGTH_OPERAND 13
#define MAX_LENGTH_OPERAND_NULL 14

#define LINE_SCANF_REGEX "%6[0-9a-zA-Z ] %6[0-9a-zA-Z] %13[0-9a-zA-Z,\']"

#define ASSEMBLY_DIRECTIVE_START_STRING "START"
#define ASSEMBLY_DIRECTIVE_END_STRING "END"
#define ASSEMBLY_DIRECTIVE_BYTE_STRING "BYTE"
#define ASSEMBLY_DIRECTIVE_WORD_STRING "WORD"
#define ASSEMBLY_DIRECTIVE_RESB_STRING "RESB"
#define ASSEMBLY_DIRECTIVE_RESW_STRING "RESW"
#define ASSEMBLY_DIRECTIVE_LTORG_STRING "LTORG"
#define ASSEMBLY_DIRECTIVE_EXTDEF_STRING "EXTDEF"
#define ASSEMBLY_DIRECTIVE_EXTREF_STRING "EXTREF"
#define ASSEMBLY_DIRECTIVE_CSECT_STRING "CSECT"

#define INSTRUCTION_TABLE_FILE_PATH "inst.data"
#define INPUT_FILE_PATH "program_in.txt"



/* 
 * 기계어 목록 파일로 부터 정보를 받아와 생성하는 기계어 변환 테이블이다.
 * 해당 기계어의 정보를 토큰으로 나눠 기록하고 있다. 
 */
#define OPCODE_COLUMN_NAME 0
#define OPCODE_COLUMN_FORMAT 1
#define OPCODE_COLUMN_CODE 2
#define OPCODE_COLUMN_NUM_OF_OPERAND 3
char *inst[MAX_INST][MAX_COLUMNS];
int inst_index;
static int inst_num;

char *input_data[MAX_LINES];
static int line_num;

int label_num;

/*
 * 어셈블리 할 소스코드를 토큰 단위로 관리하는 테이블이다. 
 * 관리 정보는 소스 라인 단위로 관리되어진다.
 */
struct token_unit {
	char *label;
	char *operator;
	char *operand[MAX_OPERAND];
	char *comment;
};

typedef struct token_unit token;
token *token_table[MAX_LINES];

static int token_line;
//--------------

/*
 * 심볼을 관리하는 구조체이다.
 * 심볼 테이블은 심볼 이름, 심볼의 위치로 구성된다.
 */
struct symbol_unit {
	char symbol[10];
	int addr;
};

typedef struct symbol_unit symbol;
symbol sym_table[MAX_LINES];

static int locctr;
static int symbol_num;
//--------------

char *literal_table[MAX_LINES];
static int literal_num;

static char *input_file;
static char *output_file;


int init_my_assembler(void);
static int assem_pass1(void);
static int assem_pass2(void);
int init_inst_file(char *inst_file);
int init_input_file(char *path);
int search_opcode(const char *str);
void make_objectcode(char *file_name);
int token_parsing(int index);


// my functions
void add_literal_if_not_exist(const char* operand);
void generate_literals();
int get_opcode_of_instruction(int i);
int get_num_of_operand_of_instruction(int i);
int get_instruction_size(const char* operator);
int is_assembly_directive(const char* opcode);
int is_assembly_directive_affect_locctr(const char* opcode);
token* malloc_token();
void make_token(const char* label, 
	const char* operator, 
	const char* operand, 
	const char* comment);
int increase_locctr_by_opcode(const token* tk);
void token_parsing_assembly_directive(const char* line);
void tokenizing_operand(const char* operand, char* target[MAX_OPERAND]);
int get_num_of_operand(const char* operand);