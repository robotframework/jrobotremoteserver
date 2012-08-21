from types import *


class TypeLibrary(object):

    def should_be_int(self, value):
        if type(value) is not IntType:
            raise AssertionError('Type is %s, not int' % type(value))

    def should_be_float(self, value):
        if type(value) is not FloatType:
            raise AssertionError('Type is %s, not float' % type(value))

    def should_be_string(self, value):
        if type(value) is not StringType:
            raise AssertionError('Type is %s, not string' % type(value))