__author__ = 'loki'


class Symbol:
    def __init__(self, symbol, address, control_section_number):
        self.symbol = symbol
        self.address = address
        self.control_section_number = control_section_number

    def is_same(self, str_symbol, control_section_number):
        return self.symbol == str_symbol and self.control_section_number == control_section_number
