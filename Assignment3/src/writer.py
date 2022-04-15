from abc import ABC, abstractmethod

class Writer(ABC):

    @abstractmethod
    def __init__(self):
        pass

    @abstractmethod
    def open(self,filePath):
        pass

    @abstractmethod
    def writeRow(self, rowTuple):
        pass

    @abstractmethod
    def close(self):
        pass