import io
import multiprocessing
from zipfile import ZipFile

from api.imageInfo import ImageInfo

class ZipInfo:

    def __init__(self):
        pass

    def getZipInfo(self,zipFileContent):
        #creates processor pool to do task quickly
        p = multiprocessing.Pool()
        with ZipFile(zipFileContent,'r') as zip:
            #get list of all files in zip
            fileList = zip.namelist()
            fileList = [(io.BytesIO(zip.read(file)),file.split('/')[-1]) for file in fileList]
            encodeList = p.starmap(ImageInfo().getImgInfo,fileList)
            encodeList = list(filter(None,encodeList))
            return encodeList