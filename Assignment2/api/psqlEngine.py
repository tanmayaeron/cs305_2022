from api.dbEngine import DBEngine
import psycopg2


class PSQLEngine(DBEngine):

    #creates a psql connection
    def __init__(self,config):
        self.conn = psycopg2.connect(**config)
        self.conn.autocommit = False
        
    #method to create table
    def create(self,command):
        cur = self.conn.cursor()
        cur.execute(command)
        cur.close()
        self.conn.commit()

    #if param is provide, command populated with param is executed
    #otherwise command is executed directly
    def executeQuery(self,command,param=None):
        cur = self.conn.cursor()
        if param is not None:
            cur.execute(command,param)
        else:
            cur.execute(command)
        return cur        
    
    #return number of rows affected since initialisation of cursor
    def getRowCount(self,cur):
        rowCount = cur.rowcount 
        cur.close()
        self.conn.commit()
        return rowCount


    def insert(self,command,param=None):
        cur = self.executeQuery(command,param)
        return self.getRowCount(cur)

    def insertMany(self,command,paramList=None):
        cur = self.conn.cursor()

        if paramList is not None:
            cur.executemany(command,paramList)
        else:
            for c in command:
                cur.execute(command)
        return self.getRowCount(cur)
    
    def selectOne(self,command,param=None):
        cur = self.executeQuery(command,param)

        row = cur.fetchone()
        cur.close()
        return row

    def selectMany(self,command,param=None):
        cur = self.executeQuery(command,param)

        row = cur.fetchall()
        cur.close()
        return row

    
    def update(self,command,param):
        cur = self.executeQuery(command,param)
        return self.getRowCount(cur)

    def delete(self,command,param=None):
        cur = self.executeQuery(command,param)
        return self.getRowCount(cur)