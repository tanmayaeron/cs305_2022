
from fastapi import FastAPI, File, UploadFile, Form
import uvicorn
from api.kmatch import MatchImage
from api.zipInfo import ZipInfo
from api.imageInfo import ImageInfo
from api.database import Database
import io
import time
import json


configFile = open('config.json')
data = json.load(configFile)
server  = data['server']
config = data['database']


db = Database(config)
db.createTable()
app = FastAPI()



@app.post("/search_faces/")
async def search_faces(file: UploadFile = File(...),k: str=Form(...),tolerance: str=Form(...)):
    fileContent = await file.read()
    fileContentBinary = io.BytesIO(fileContent)
    result = MatchImage(db).kmatchImage(fileContentBinary,int(k),float(tolerance))
    return {"status":"OK",
    "body":{
        "matches":result
    }}


@app.post("/add_face/")
async def add_face(file: UploadFile = File(...)):
    fileContent = await file.read()
    begin = time.time()
    fileContentBinary = io.BytesIO(fileContent)
    infoObj = ImageInfo()
    info = infoObj.getImgInfo(fileContentBinary,file.filename)
    rowsInserted = db.insertOne(info)
    end = time.time()
    print(end-begin)
    return {"status":"OK","rowsInserted":rowsInserted}



@app.post("/add_faces_in_bulk/")
async def add_faces_in_bulk(file: UploadFile = File(...)):
    zipFileContent = await file.read()
    begin = time.time()
    zipBinary = io.BytesIO(zipFileContent)
    dataList = ZipInfo().getZipInfo(zipBinary)
    rowsInserted = db.insertMany(dataList)
    end = time.time()
    print(end-begin)
    return {"status":"OK","rowsInserted":rowsInserted}



@app.post("/get_face_info/")
async def get_face_info(api_key: str = Form(...),face_id: str=Form(...)):
    info = db.getInfo(int(face_id))
    return {"status":"OK","info":info}

if __name__=='__main__':
    uvicorn.run('main:app',host=server['host'],port=server['port'],debug=True)