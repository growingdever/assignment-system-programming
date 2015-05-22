import re
from SourceToken import *

instruction_table = dict()
input_source_lines = []
source_tokens = []


def read_instruction_table():
    with open('inst.data', 'r') as f:
        for line in f:
            tokens = line.split(' ')
            tokens[3] = tokens[3][:len(tokens[3]) - 1]
            instruction_table[tokens[0]] = (tokens[0], tokens[1], int(tokens[2], 0), tokens[3])


def get_opcode_by_mnemonic(mnemonic):
    return instruction_table[mnemonic][2]


def read_input_program():
    with open('program_in.txt', 'r') as f:
        for line in f:
            input_source_lines.append(line[:len(line) - 1])


def initialize():
    read_instruction_table()
    read_input_program()


def parsing_token(line):
    if line[0] == '.':
        # comment line
        return

    tokens = line.split()

    def operand_tokenizing(operand_line):
        operands = []
        split_string = re.compile('[,\\+\\-]').split(operand_line)
        for token in split_string:
            index = operand_line.find(token)
            if index > 0:
                if operand_line[index - 1] == '-':
                    token = '-%s' % token
            operands.append(token)
        return operands

    if len(tokens) == 1:
        source_tokens.append(SourceToken('', tokens[0]))
    elif len(tokens) == 2:
        if tokens[0] == 'CSECT':
            source_tokens.append(SourceToken(tokens[0], tokens[1]))
        else:
            source_tokens.append(
                SourceToken('',
                            tokens[0],
                            operand_tokenizing(tokens[1])))
    else:
        source_tokens.append(
            SourceToken(tokens[0],
                        tokens[1],
                        operand_tokenizing(tokens[2])))


def parsing_all_tokens():
    for line in input_source_lines:
        parsing_token(line)
    pass


def generate_literals():
    pass


def add_all_symbols():
    pass


def pass1():
    parsing_all_tokens()
    generate_literals()
    add_all_symbols()

    for token in source_tokens:
        print '%7s %-7s ' % (token.label, token.operator),
        print ",".join(token.operands)
    pass


def pass2():
    pass


def print_object_codes():
    pass


initialize()
pass1()
pass2()
print_object_codes()