instruction_table = dict()
input_lines = []


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
            input_lines.append(line)


def initialize():
    read_instruction_table()
    read_input_program()


def pass1():
    pass


def pass2():
    pass


def print_object_codes():
    pass


initialize()
pass1()
pass2()
print_object_codes()