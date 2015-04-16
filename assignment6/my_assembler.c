/*
 * 화일명 : my_assembler.c 
 * 설  명 : 이 프로그램은 SIC/XE 머신을 위한 간단한 Assembler 프로그램의 메인루틴으로,
 * 입력된 파일의 코드 중, 명령어에 해당하는 OPCODE를 찾아 출력한다.
 *
 */

/*
 *
 * 프로그램의 헤더를 정의한다. 
 *
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/*
 * my_assembler 프로그램의 의존적인 데이터들이 정의된 곳이다. 
 */

#include "my_assembler.h"

/* -----------------------------------------------------------------------------------
 * 설명 : 사용자로 부터 어셈블리 파일을 받아서 명령어의 OPCODE를 찾아 출력한다.
 * 매계 : 실행 파일, 어셈블리 파일 
 * 반환 : 성공 = 0, 실패 = < 0 
 * 주의 : 현재 어셈블리 프로그램의 리스트 파일을 생성하는 루틴은 만들지 않았다. 
 *		   또한 중간파일을 생성하지 않는다. 
 * -----------------------------------------------------------------------------------
 */


int main(int args, char *arg[]) 
{
	if(init_my_assembler() < 0) {
		printf("init_my_assembler: 프로그램 초기화에 실패 했습니다.\n"); 
		return -1 ; 
	}

	if(assem_pass1() < 0 ) {
		printf("assem_pass1: 패스1 과정에서 실패하였습니다.  \n") ; 
		return -1 ; 
	}
	if(assem_pass2() < 0 ) {
		printf(" assem_pass2: 패스2 과정에서 실패하였습니다.  \n") ; 
		return -1 ; 
	}

	make_objectcode("output") ; 
}
/* -----------------------------------------------------------------------------------
 * 설명 : 프로그램 초기화를 위한 자료구조 생성 및 파일을 읽는 함수이다. 
 * 매계 : 없음
 * 반환 : 정상종료 = 0 , 에러 발생 = -1
 * 주의 : 각각의 명령어 테이블을 내부에 선언하지 않고 관리를 용이하게 하기 
 *		   위해서 파일 단위로 관리하여 프로그램 초기화를 통해 정보를 읽어 올 수 있도록
 *		   구현하였다. 
 * -----------------------------------------------------------------------------------
 */

int init_my_assembler(void)
{
	int result;

	if((result = init_inst_file(INSTRUCTION_TABLE_FILE_PATH)) < 0 )
		return -1;
	if((result = init_input_file(INPUT_FILE_PATH)) < 0 )
		return -1; 

	return result; 
}

/* -----------------------------------------------------------------------------------
 * 설명 : 어셈블리 코드를 위한 패스1과정을 수행하는 함수이다. 
 *		   패스1에서는..
 *		   1. 프로그램 소스를 스캔하여 해당하는 토큰단위로 분리하여 프로그램 라인별 토큰
 *		   테이블을 생성한다. 
 * 
 * 매계 : 없음
 * 반환 : 정상 종료 = 0 , 에러 = < 0 
 * 주의 : 현재 초기 버전에서는 에러에 대한 검사를 하지 않고 넘어간 상태이다.
 *	  따라서 에러에 대한 검사 루틴을 추가해야 한다. 
 *		
 * -----------------------------------------------------------------------------------
 */

static int assem_pass1(void)
{
	for( int i = 0; i < line_num; i++ ) {
		if(token_parsing(i) < 0) {
			return -1;
		}
	}

	// LTORG를 만나지 못해서 아직 추가되지 않은 것들 추가해줌.
	generate_literals();

	printf("parsing complete\n");

	for( int i = 0; i < symbol_num; i ++ ) {
		symbol sym = sym_table[i];
		printf("%s %04X\n", sym.symbol, sym.addr);
	}

	return 0;
}

/* -----------------------------------------------------------------------------------
 * 설명 : 어셈블리 코드를 기계어 코드로 바꾸기 위한 패스2 과정을 수행하는 함수이다. 
 *		   패스 2에서는 프로그램을 기계어로 바꾸는 작업은 라인 단위로 수행된다. 
 *		   다음과 같은 작업이 수행되어 진다. 
 *		   1. 실제로 해당 어셈블리 명령어를 기계어로 바꾸는 작업을 수행한다. 
 * 매계 : 없음
 * 반환 : 정상종료 = 0, 에러발생 = < 0 
 * 주의 : 
 * -----------------------------------------------------------------------------------
 */

static int assem_pass2(void)
{
	int location_counter = 0;

	for( int i = 0; i < token_line; i ++ ) {
		token *curr_token = token_table[i];
		if( curr_token->operator == NULL ) {
			continue;
		}

		int instruction_number = -1;
		if( curr_token->operator[0] == '+' ) {
			instruction_number = search_opcode( curr_token->operator + 1 );
		} else {
			instruction_number = search_opcode( curr_token->operator );
		}

		if( instruction_number < 0 ) {
			if( is_assembly_directive(curr_token->operator) ) {
				if( strcmp(curr_token->operator, ASSEMBLY_DIRECTIVE_CSECT_STRING) == 0 ) {
					location_counter = 0;
				}

				// printf("%s\t", curr_token->label);
				// printf("%s\t", curr_token->operator);
				// printf("%8s\t\n", curr_token->operand[0]);
			} else {
				// error!
			}
			continue;
		}

		const char* operator = curr_token->operator;
		int n = 1, i = 1, x = 0, b = 0, p = 0, e = 0, disp = 0;
		int code = 0;
		int opcode = get_opcode_of_instruction(instruction_number);
		if( operator[0] == '+' ) {
			// format 4
			location_counter += 4;

			e = 1;
		} else {
			// format 2 or 3
			int format = get_format_of_instruction(instruction_number);
			if( format == 2 ) {
				location_counter += 2;

				// opcode 쓰고
				code += opcode << 8;

				const char* operand1 = curr_token->operand[0];
				if( operand1 != NULL ) {
					int addr = get_address_of_register(operand1);
					code += addr << 4;
				}

				const char* operand2 = curr_token->operand[1];
				if( operand2 != NULL ) {
					int addr = get_address_of_register(operand2);
					code += addr;
				}

			} else if( format == 3 ) {
				location_counter += 3;

				p = 1;

				const char* operand1 = curr_token->operand[0];
				const char* operand2 = curr_token->operand[1];

				if( operand1[0] == 0 ) {
					n = 1;
					i = 1;
					x = b = p = e = 0;
				} else {
					if( operand1[0] == '#' ) {
						n = 0;
						i = 1;
						p = 0;

						disp = atoi(operand1 + 1);
					} else if( operand1[0] == '@' ) {
						n = 1;
						i = 0;

						int target_address = get_symbol_address(operand1 + 1);
						// printf("target:%06X loc:%06X\n", target_address, location_counter);
						disp = target_address - location_counter;
					} else {
						n = 1;
						i = 1;

						int target_address = get_symbol_address(operand1);
						// printf("target:%06X loc:%06X\n", target_address, location_counter);
						disp = target_address - location_counter;
					}
				}

				if( curr_token->operand[1] != NULL ) {
					x = 1;
				}

				code += (opcode + n * 2 + i) << 16;
				code += x << 15;
				code += b << 14;
				code += p << 13;
				code += e << 12;
				code += (0x00000FFF & disp);
			}
		}

		if( curr_token->label ) {
			printf("%s\t", curr_token->label);
		} else {
			printf("%6s\t", "");
		}

		printf("%s\t", curr_token->operator);
		printf("%8s\t", curr_token->operand[0]);
		printf("0x%02X\t", opcode);

		printf("0x%08X\n", code);
	}

	return -1;
}
/* -----------------------------------------------------------------------------------
 * 설명 : 머신을 위한 기계 코드목록 파일을 읽어 기계어 목록 테이블(inst_table)을 
 *        생성하는 함수이다. 
 * 매계 : 기계어 목록 파일
 * 반환 : 정상종료 = 0 , 에러 < 0 
 * 주의 : 기계어 목록파일 형식은 다음과 같다. 
 *	
 *	===============================================================================
 *		   | 이름 | 형식 | 기계어 코드 | 오퍼랜드의 갯수 | NULL|
 *	===============================================================================	   
 *		
 * -----------------------------------------------------------------------------------
 */

int init_inst_file(char *inst_file)
{
	FILE *fp = fopen(inst_file, "r");
	if( ! fp ) {
		return -1;
	}

	char line[MAX_LENGTH_OPCODE_TABLE];
	while( fgets( line, MAX_LENGTH_OPCODE_TABLE, fp ) ) {
		char name[MAX_LENGTH_OPCODE_NAME_NULL] = {0, };
		char format[8] = {0, };
		int code;
		int num_of_operand;
		sscanf(line, "%s %s %x %d", name, format, &code, &num_of_operand);

		char **curr = inst[inst_num];
		
		// 이름 넣고
		curr[OPCODE_COLUMN_NAME] = strdup(name);
		// format 정보 넣고
		curr[OPCODE_COLUMN_FORMAT] = strdup(format);

		// opcode 값 넣고
		curr[OPCODE_COLUMN_CODE] = (char*)malloc( sizeof(char) );
		*curr[OPCODE_COLUMN_CODE] = (char)code;

		// operand 갯수 넣고
		curr[OPCODE_COLUMN_NUM_OF_OPERAND] = (char*)malloc( sizeof(char) );
		*curr[OPCODE_COLUMN_NUM_OF_OPERAND] = (char)num_of_operand;

		memset(line, 0, strlen(line));
		inst_num++;
	}

	return 0;
}

/* -----------------------------------------------------------------------------------
 * 설명 : 어셈블리 할 소스코드를 읽어오는 함수이다. 
 * 매계 : 어셈블리할 소스파일명
 * 반환 : 정상종료 = 0 , 에러 < 0  
 * 주의 : 
 *		
 * -----------------------------------------------------------------------------------
 */
int init_input_file(char *path)
{
	FILE *fp = fopen(path, "r");
	if( ! fp ) {
		return -1;
	}
	input_file = path;

	char line[MAX_LENGTH_INSTRUCTION_LINE];
	line_num = 0;
	while( fgets( line, MAX_LENGTH_INSTRUCTION_LINE, fp ) ) {
		char *str = (char*)malloc(sizeof(char) * (strlen(line) + 1));
		strncpy(str, line, MAX_LENGTH_INSTRUCTION_LINE);
		input_data[line_num++] = str;

		// 개행문자 없앰
		int last = strlen(str) - 1;
		str[last] = '\0';

		memset(line, 0, MAX_LENGTH_INSTRUCTION_LINE);
	}

	return 0;
}

/* -----------------------------------------------------------------------------------
 * 설명 : 소스 코드를 읽어와 토큰단위로 분석하고 토큰 테이블을 작성하는 함수이다. 
 *        패스 1로 부터 호출된다. 
 * 매계 : 소스코드의 라인번호  
 * 반환 : 정상종료 = 0 , 에러 < 0 
 * 주의 : my_assembler 프로그램에서는 라인단위로 토큰 및 오브젝트 관리를 하고 있다. 
 * -----------------------------------------------------------------------------------
 */

int token_parsing(int index)
{
	const char *line = input_data[index];
	if( line == NULL ) {
		return -1;
	}

	// comment 라인은 comment만 복사해서 넣고 끝냄
	if( line[0] == '.' ) {
		token* curr_token = malloc_token();
		curr_token->comment = strdup(line);
		return 0;
	}

	char label[MAX_LENGTH_LABEL_NULL] = { 0, };
	char operator[MAX_LENGTH_OPCODE_NAME_NULL] = { 0, };
	char operand[MAX_LENGTH_INSTRUCTION_LINE] = { 0, };
	char comment[MAX_LENGTH_INSTRUCTION_LINE] = { 0, };

	// 첫 번째 문자가 공백 == 레이블 없음
	if( line[0] == ' ' ) {
		sscanf(line, "%s %s %s", operator, operand, comment);
	} else {
		sscanf(line, "%s %s %s %s", label, operator, operand, comment);
	}

	// printf("%s %s %s %s\n", label, operator, operand, comment);
	if( is_assembly_directive(operator) ) {
		token_parsing_assembly_directive(line);
	} else {
		make_token(label, operator, operand, comment);
	}

	return 0;
}

token* malloc_token() {
	token_table[token_line] = (token*)malloc( sizeof(token) );
	token *curr_token = token_table[token_line];
	token_line++;

	curr_token->label = NULL;
	curr_token->operator = NULL;
	for( int i = 0; i < MAX_OPERAND; i ++ ) {
		curr_token->operand[i] = NULL;
	}
	curr_token->comment = NULL;

	return curr_token;
}

void make_token(const char* label, 
	const char* operator, 
	const char* operand, 
	const char* comment) {
	token* curr_token = malloc_token();

	// 레이블이 있으면 복사
	if( strlen(label) > 0 ) {
		curr_token->label = strdup(label);
	}
	// opcode name 복사
	curr_token->operator = strdup(operator);
	// operand 부분 토크나이징 해서 넣음
	tokenizing_operand(operand, curr_token->operand);
	// // comment 복사(아직 한 단어 밖에 인식 안되지만 그래도....)
	curr_token->comment = strdup(comment);

	if( strcmp(operator, ASSEMBLY_DIRECTIVE_CSECT_STRING) == 0 ) {
		locctr = 0;
	}

	// 레이블 있으면 심볼 테이블에 추가
	if( strlen(label) > 0 ) {
		add_symbol(label, locctr);
	}

	locctr += increase_locctr_by_opcode(curr_token);
}

int increase_locctr_by_opcode(const token* tk) {
	const char *opcode = tk->operator;
	int size = 0;
	if( is_assembly_directive_affect_locctr(opcode) ) {
		if( strcmp(opcode, ASSEMBLY_DIRECTIVE_BYTE_STRING) == 0 ) {
			size = 1;
		} else if( strcmp(opcode, ASSEMBLY_DIRECTIVE_WORD_STRING) == 0 ) {
			size = 3;
		} else if( strcmp(opcode, ASSEMBLY_DIRECTIVE_RESB_STRING) == 0 ) {
			size = atoi(tk->operand[0]);
		} else if( strcmp(opcode, ASSEMBLY_DIRECTIVE_RESW_STRING) == 0 ) {
			size = atoi(tk->operand[0]) * SIZE_WORD_BY_BYTE;
		}
	} else {
		if( ! is_assembly_directive(opcode) ) {
			size = get_instruction_size(opcode);
		}
	}

	return size;
}

void token_parsing_assembly_directive(const char* line) {
	char label[MAX_LENGTH_LABEL_NULL] = { 0, };
	char operator[MAX_LENGTH_OPCODE_NAME_NULL] = { 0, };
	char operand[MAX_LENGTH_INSTRUCTION_LINE] = { 0, };
	char comment[MAX_LENGTH_INSTRUCTION_LINE] = { 0, };

	// 첫 번째 문자가 공백 == 레이블 없음
	if( line[0] == ' ' ) {
		sscanf(line, "%s %s %s", operator, operand, comment);
	} else {
		sscanf(line, "%s %s %s %s", label, operator, operand, comment);
	}

	make_token(label, operator, operand, comment);

	if( strcmp(operator, ASSEMBLY_DIRECTIVE_LTORG_STRING) == 0 ) {
		generate_literals();
	}
}

void tokenizing_operand(const char* operand, char* target[MAX_OPERAND]) {
	int num_of_operand = get_num_of_operand(operand);
	if( num_of_operand == 1 ) {
		target[0] = strdup(operand);

		// 리터럴 추가
		add_literal_if_not_exist(operand);

		return;
	}

	char *str = strdup(operand);
	int index = 0;

	char *ptr = strtok(str, ",");
	target[index++] = strdup(ptr);
	while(1) {
		ptr = strtok(NULL, ",");
		if( ptr == NULL ) {
			break;
		}

		target[index++] = strdup(ptr);
	}

	free(str);
}

int get_num_of_operand(const char* operand) {
	int count = 1;
	unsigned int length = strlen(operand);
	for( int i = 0; i < length; i ++ ) {
		if( operand[i] == ',' ) {
			count++;
		}
	}

	return count;
}
/* -----------------------------------------------------------------------------------
 * 설명 : 입력 문자열이 기계어 코드인지를 검사하는 함수이다. 
 * 매계 : 토큰 단위로 구분된 문자열 
 * 반환 : 정상종료 = 기계어 테이블 인덱스, 에러 < 0 
 * 주의 : 
 *		
 * -----------------------------------------------------------------------------------
 */

int search_opcode(const char *str) 
{
	for( int i = 0; i < inst_num; i ++ ) {
		// 일단 컬럼 가져오고
		char *name = inst[i][OPCODE_COLUMN_NAME];
		// 이름 같으면 인덱스 리턴
		if( strcmp(name, str) == 0 ) {
			return i;
		}
	}

	return -1;
}

/* -----------------------------------------------------------------------------------
 * 설명 : 입력된 문자열의 이름을 가진 파일에 프로그램의 결과를 저장하는 함수이다. 
 * 매계 : 생성할 오브젝트 파일명 
 * 반환 : 없음
 * 주의 : 만약 인자로 NULL값이 들어온다면 프로그램의 결과를 표준출력으로 보내어 
 *        화면에 출력해준다. 
 *		
 * -----------------------------------------------------------------------------------
 */

void make_objectcode(char *file_name) 
{
	FILE *fp = NULL;
	if( file_name ) {
		fp = fopen(file_name, "w");
	} else {
		fp = stdout;
	}

	if( ! fp ) {
		// error!
		return;
	}
}


//
// my functions
//
void add_literal_if_not_exist(const char* operand) {
	for( int i = 0; i < literal_num; i ++ ) {
		if( strcmp(literal_table[i], operand) == 0 ) {
			return;
		}
	}

	if( operand[0] == '=' ) {
		literal_table[literal_num] = strdup(operand);
		literal_num++;
	}
}

void generate_literals() {
	for( int i = 0; i < literal_num; i ++ ) {
		token* curr_token = malloc_token();
		curr_token->operand[0] = literal_table[i];
		literal_table[i] = NULL;

		add_symbol(curr_token->operand[0], locctr);

		// =X'~', =C'~' 라서 4글자 빼줌
		int size = strlen(curr_token->operand[0]) - 4;
		locctr += size;
	}
	literal_num = 0;
}

void add_symbol(const char* symbol, int address) {
	strcpy(sym_table[symbol_num].symbol, symbol);
	sym_table[symbol_num].addr = address;
	symbol_num++;
}

int get_symbol_address(const char* symbol) {
	for( int i = 0; i < symbol_num; i ++ ) {
		if( strcmp(sym_table[i].symbol, symbol) == 0 ) {
			return sym_table[i].addr;
		}
	}
	return -1;
}

int get_opcode_of_instruction(int i) {
	char **curr = inst[i];
	// 오른쪽에서 두 번째 16진수 수의 맨 앞 비트가 1이면 앞 부분이 모두 1로 채워지는 문제 해결
	return 0x000000FF & *curr[OPCODE_COLUMN_CODE];
}

int get_format_of_instruction(int i) {
	char **curr = inst[i];
	char *format_str = curr[OPCODE_COLUMN_FORMAT];
	return format_str[0] - '0';
}

int get_address_of_register(const char* reg) {
	if( strcmp(reg, "A") == 0 ) {
		return 0;
	} else if( strcmp(reg, "X") == 0 ) {
		return 1;
	} else if( strcmp(reg, "L") == 0 ) {
		return 2;
	} else if( strcmp(reg, "PC") == 0 ) {
		return 8;
	} else if( strcmp(reg, "SW") == 0 ) {
		return 9;
	} else if( strcmp(reg, "B") == 0 ) {
		return 3;
	} else if( strcmp(reg, "S") == 0 ) {
		return 4;
	} else if( strcmp(reg, "T") == 0 ) {
		return 5;
	} else if( strcmp(reg, "F") == 0 ) {
		return 6;
	}

	return -1;
}

int get_num_of_operand_of_instruction(int i) {
	char **curr = inst[i];
	// 오른쪽에서 두 번째 16진수 수의 맨 앞 비트가 1이면 앞 부분이 모두 1로 채워지는 문제 해결
	return 0x000000FF & *curr[OPCODE_COLUMN_NUM_OF_OPERAND];
}

int get_instruction_size(const char* operator) {
	int instruction_size;
	if( operator[0] == '+' ) {
		instruction_size = 4;
	} else {
		int index = search_opcode(operator);
		if( index >= 0 ) {
			const char *format = inst[index][OPCODE_COLUMN_FORMAT];
			if( format[0] == '2' ) {
				instruction_size = 2;
			} else {
				instruction_size = 3;
			}
		} else {
			instruction_size = 3;
		}
	}

	return instruction_size;
}

int is_assembly_directive(const char* opcode) {
	return strcmp(opcode, ASSEMBLY_DIRECTIVE_START_STRING) == 0
		|| strcmp(opcode, ASSEMBLY_DIRECTIVE_END_STRING) == 0
		|| strcmp(opcode, ASSEMBLY_DIRECTIVE_BYTE_STRING) == 0
		|| strcmp(opcode, ASSEMBLY_DIRECTIVE_WORD_STRING) == 0
		|| strcmp(opcode, ASSEMBLY_DIRECTIVE_RESB_STRING) == 0
		|| strcmp(opcode, ASSEMBLY_DIRECTIVE_RESW_STRING) == 0
		|| strcmp(opcode, ASSEMBLY_DIRECTIVE_LTORG_STRING) == 0
		|| strcmp(opcode, ASSEMBLY_DIRECTIVE_EXTDEF_STRING) == 0
		|| strcmp(opcode, ASSEMBLY_DIRECTIVE_EXTREF_STRING) == 0
		|| strcmp(opcode, ASSEMBLY_DIRECTIVE_CSECT_STRING) == 0;
}

int is_assembly_directive_affect_locctr(const char* opcode) {
	return strcmp(opcode, ASSEMBLY_DIRECTIVE_BYTE_STRING) == 0
		|| strcmp(opcode, ASSEMBLY_DIRECTIVE_WORD_STRING) == 0
		|| strcmp(opcode, ASSEMBLY_DIRECTIVE_RESB_STRING) == 0
		|| strcmp(opcode, ASSEMBLY_DIRECTIVE_RESW_STRING) == 0;
}