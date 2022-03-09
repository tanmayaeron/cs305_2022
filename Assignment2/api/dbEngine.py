from abc import ABC, abstractmethod

#abstract class for database engine
class DBEngine(ABC):

    @abstractmethod
    def __init__(self,config):
        pass

    @abstractmethod
    def create(self,command):
        pass

    @abstractmethod
    def insert(self,command,param=None):
        pass

    @abstractmethod
    def insertMany(self,command,paramList=None):
        pass

    @abstractmethod
    def selectOne(self,command,param=None):
        pass

    @abstractmethod
    def selectMany(self,command,param=None):
        pass

    @abstractmethod
    def update(self,command,param=None):
        pass

    @abstractmethod
    def delete(self,command,param=None):
        pass