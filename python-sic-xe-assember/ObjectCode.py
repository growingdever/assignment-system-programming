__author__ = 'loki'


class ObjectCode:
    def __init__(self, type, code, address):
        self.symbol = ''
        self.type = type
        self.code = code
        self.address = address
        self.size = 0

    def set_format(self, inst_format):
        self.size = inst_format
