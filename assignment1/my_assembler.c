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

	//make_output("output") ; 
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

	if((result = init_inst_file("inst.data")) < 0 )
		return -1;
	if((result = init_input_file("program_in.txt")) < 0 )
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

	for( int i = 0; i < line_num; i ++ ) {
		token *curr_token = token_table[i];
		if( curr_token->operator == NULL ) {
			continue;
		}

		int instruction_number = search_opcode( curr_token->operator );
		if( instruction_number < 0 ) {
			if( is_assembly_directive(curr_token->operator) ) {
				printf("%s\t", curr_token->label);
				printf("%s\t", curr_token->operator);
				printf("%8s\t\n", curr_token->operand[0]);
			} else {
				// error!
			}
			continue;
		}

		int opcode = get_opcode_of_instruction(instruction_number);

		if( curr_token->label ) {
			printf("%s\t", curr_token->label);
		} else {
			printf("%6s\t", "");
		}

		printf("%s\t", curr_token->operator);
		printf("%8s\t", curr_token->operand[0]);
		printf("0x%02X\n", opcode);
	}

	return -1;
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
	/* add your code here */
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
int init_input_file(char *input_file)
{
	FILE *fp = fopen(input_file, "r");
	if( ! fp ) {
		return -1;
	}

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
	char *line = input_data[index];
	if( line == NULL ) {
		return -1;
	}

	token_table[index] = (token*)malloc( sizeof(token) );
	token *curr_token = token_table[index];
	curr_token->label = NULL;
	curr_token->operator = NULL;
	for( int i = 0; i < MAX_OPERAND; i ++ ) {
		curr_token->operand[i] = NULL;
	}
	curr_token->comment = NULL;

	// 먼저 comment 빼냄
	if( line[0] == '.' ) {
		curr_token->comment = (char*)malloc( sizeof(char) * strlen(line) );
		strcpy(curr_token->comment, line);
		return 0;
	}

	// Label 존재 판단 기준 : 첫 번째 문자가 \t
	char label[MAX_LENGTH_LABEL_NULL] = { 0, };
	char operator[MAX_LENGTH_OPCODE_NAME_NULL] = { 0, };
	char operand[MAX_LENGTH_OPERAND_NULL] = { 0, };
	char comment[MAX_LENGTH_INSTRUCTION_LINE] = { 0, };
	if( line[0] == ' ' ) {
		sscanf(line, "%s %s %s", operator, operand, comment);
	} else {
		sscanf(line, "%s %s %s %s", label, operator, operand, comment);
		curr_token->label = strdup(label);
	}

	// opcode name 복사
	curr_token->operator = strdup(operator);
	// // 일단 operand 통째로 0번째 인덱스에 복사해넣게 함
	curr_token->operand[0] = strdup(operand);
	// // comment 복사(아직 한 단어 밖에 인식 안되지만 그래도....)
	curr_token->comment = strdup(comment);

	return 0;
}
/* -----------------------------------------------------------------------------------
 * 설명 : 입력 문자열이 기계어 코드인지를 검사하는 함수이다. 
 * 매계 : 토큰 단위로 구분된 문자열 
 * 반환 : 정상종료 = 기계어 테이블 인덱스, 에러 < 0 
 * 주의 : 
 *		
 * -----------------------------------------------------------------------------------
 */

int search_opcode(char *str) 
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
int get_opcode_of_instruction(int i) {
	char **curr = inst[i];
	// 오른쪽에서 두 번째 16진수 수의 맨 앞 비트가 1이면 앞 부분이 모두 1로 채워지는 문제 해결
	return 0x000000FF & *curr[OPCODE_COLUMN_CODE];
}

int get_num_of_operand_of_instruction(int i) {
	char **curr = inst[i];
	// 오른쪽에서 두 번째 16진수 수의 맨 앞 비트가 1이면 앞 부분이 모두 1로 채워지는 문제 해결
	return 0x000000FF & *curr[OPCODE_COLUMN_NUM_OF_OPERAND];
}

int is_assembly_directive(const char* opcode) {
	return strcmp(opcode, ASSEMBLY_DIRECTIVE_START_STRING) == 0
		|| strcmp(opcode, ASSEMBLY_DIRECTIVE_END_STRING) == 0
		|| strcmp(opcode, ASSEMBLY_DIRECTIVE_BYTE_STRING) == 0
		|| strcmp(opcode, ASSEMBLY_DIRECTIVE_WORD_STRING) == 0
		|| strcmp(opcode, ASSEMBLY_DIRECTIVE_RESB_STRING) == 0
		|| strcmp(opcode, ASSEMBLY_DIRECTIVE_RESW_STRING) == 0;
}