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
        if tokens[1] == 'CSECT':
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
    literals = []
    generate_target_position = []

    control_section_num = 0
    num_of_ltorg = 0
    for i in xrange(0, len(source_tokens)):
        token = source_tokens[i]
        if token.operator == 'START' or token.operator == 'CSECT':
            control_section_num += 1
            generate_target_position.append((i, False))
            continue

        if token.operator == 'LTORG':
            generate_target_position.append((i - num_of_ltorg, True))
            continue

        if len(token.operands) == 0:
            continue

        operand1 = token.operands[0]
        if operand1[0] == '=':
            is_found = False
            for tp in literals:
                if tp[0] == operand1:
                    is_found = True
            if not is_found:
                literals.append((operand1, i))

    generate_target_position.append((len(source_tokens) - 1, False))

    generated = 0
    last = 0
    for target_index in generate_target_position:
        new_tokens = []
        for i in xrange(last, len(literals)):
            pair = literals[i]
            if pair[1] < target_index[0]:
                literal = pair[0]

                if literal[1] == 'X' or literal[1] == 'C':
                    new_token = SourceToken('', 'BYTE')
                else:
                    new_token = SourceToken('', 'WORD')

                new_token.label = literal
                new_token.generated_by_ltorg = target_index[1]
                new_token.operands.append(literal[1:])
                new_tokens.append(new_token)

                generated += 1
                last = i + 1

        index = target_index[0] + generated - len(new_tokens)
        source_tokens[index:index] = new_tokens

    for token in source_tokens:
        if token.operator == 'LTORG':
            source_tokens.remove(token)


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