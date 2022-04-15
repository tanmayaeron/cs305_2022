from abc import ABC, abstractmethod

class Parser(ABC):

    @abstractmethod
    def __init__(self):
        pass

    @abstractmethod
    def getText(self, file_path,ifPath=False):
        pass