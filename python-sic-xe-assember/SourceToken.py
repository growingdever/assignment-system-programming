__author__ = 'loki'


class SourceToken:
    def __init__(self, label, operator, operands=None, generated_by_ltorg=False):
        self.label = label
        self.operator = operator
        if operands is None:
            operands = []
        self.operands = operands
        self.generated_by_ltorg = generated_by_ltorg