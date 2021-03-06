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



/* 
 * 기계어 목록 파일로 부터 정보를 받아와 생성하는 기계어 변환 테이블이다.
 * 해당 기계어의 정보를 토큰으로 나눠 기록하고 있다. 
 */
#define OPCODE_COLUMN_NAME 0
#define OPCODE_COLUMN_FORMAT 1
#define OPCODE_COLUMN_CODE 2
#define OPCODE_COLUMN_NUM_OF_OPERAND 3
char *inst[MAX_INST][MAX_COLUMNS];
int inst_index; // 이거 어디에 쓰는거지?
static int inst_num;

/*
 * 어셈블리 할 소스코드를 토큰 단위로 관리하는 테이블이다. 
 * 관리 정보는 소스 라인 단위로 관리되어진다.
 */
char *input_data[MAX_LINES];
static int line_num;

int label_num ; 

struct token_unit {
	char *label;
	char *operator;
	char *operand[MAX_OPERAND];
	char *comment;
};

typedef struct token_unit token;
token *token_table[MAX_LINES];

int init_my_assembler(void);
static int assem_pass1(void);
static int assem_pass2(void);
int init_inst_file(char *inst_file);
int init_input_file(char *input_file);
int search_opcode(char *str);
void make_objectcode(char *file_name);
int token_parsing(int index);


// my functions
int get_opcode_of_instruction(int i);
int get_num_of_operand_of_instruction(int i);
int is_assembly_directive(const char* opcode);