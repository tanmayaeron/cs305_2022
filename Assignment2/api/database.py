from api.psqlEngine import PSQLEngine
import psycopg2


class Database:

    def __init__(self,config):
        #initialises database connection
        self.engine = PSQLEngine(config)
        #initialises required commands
        self.tableName = 'dataset'
        self.tableCheckCommand = """select exists(select relname from pg_class where relname=%s);"""
        self.createCommand = """CREATE TABLE DATASET(id SERIAL PRIMARY KEY,encoding bytea,filename text,name text, version text, date text, location text);"""
        self.insertCommand = """INSERT into DATASET(encoding,filename,name,version,date,location) VALUES(%s,%s,%s,%s,%s,%s)"""
        self.selectCommand = """SELECT id, name, encoding from DATASET;"""
        self.selectInfoCommand = """SELECT name,version,date,location from dataset where id=%s;"""

    #checks if dataset relation exists
    #if dataset relation does not exist create this relation
    def createTable(self):
        row = self.engine.selectOne(self.tableCheckCommand,(self.tableName,))
        
        if not row[0]:
            self.engine.create(self.createCommand)
        
    #converts dictionary to tuple so that it can be inserted properly
    def getTuple(self,valueDict):
        return (valueDict['encoding'],valueDict['filename'],valueDict['Name'],valueDict['Version'],valueDict['Date'],valueDict['Place'])
        # 'Name','Version','Date','Place'

    #insert face encoding and metadate to database
    #returns number of rows inserted
    def insertOne(self,valueDict):
        valueTuple = self.getTuple(valueDict)
        rowsInserted = self.engine.insert(self.insertCommand,valueTuple)
        return rowsInserted

    #inserts various rows of
    #face encoding and metadata to database
    #returns number of rows inserted
    def insertMany(self,valList):
        valList = [self.getTuple(val) for val in valList]
        rowsInserted = self.engine.insertMany(self.insertCommand,valList)
        return rowsInserted

    #return id,name, encoding for every row
    def getAllRows(self):
        rowList = self.engine.selectMany(self.selectCommand)
        return rowList

    #return metadata for row for given id
    def getInfo(self,id):
        rowTuple = self.engine.selectOne(self.selectInfoCommand,(id,))
        return {'Name':rowTuple[0],'Version':rowTuple[1],'Date':rowTuple[2],'Location':rowTuple[3]}