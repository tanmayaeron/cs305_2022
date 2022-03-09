from numpy import ndarray
import face_recognition
from PIL import Image
from PIL.ExifTags import TAGS
  
class ImageInfo:

    def __init__(self,metaDataList=['Name','Version','Date','Place']):
        self.metaDataList = metaDataList
        self.nameTag  = 'Name'
        #['Name','Version','Date','Place']

    #returns encoding for every face
    #returns None if a directory object/non-image file is provided
    def getImageEncoding(self,image):
        try:
            #converts image to numpy array
            image = face_recognition.load_image_file(image)
            #get 128 sized array encoding for face
            img_encoding = face_recognition.face_encodings(image)[0]
            #converts encoding to a string  
            encode_str = ndarray.dumps(img_encoding)
            return encode_str
        except:
            return None

    #return list containing 
    #return emppty list if there is no face
    #or file provided is not an image
    def getFaces(self,image):
        try:
            image = face_recognition.load_image_file(image)
            img_encoding = face_recognition.face_encodings(image)
            return img_encoding
        except:
            return []
        
    #return 'Name','Version','Date','Place' metadata 
    def getImageMetadata(self,image):
        metadata = {}
        image = Image.open(image)
        exifdata = image.getexif()
  
        #check every tag, if it is reqired metadata tag
        for tagid in exifdata:
            tagname = TAGS.get(tagid, tagid)

            if tagname in self.metaDataList:
                value = exifdata.get(tagid)
                metadata[tagname]=value

        return metadata

    #return a dictionary containing pickle-encoding 
    #as well as metadata
    def getImgInfo(self,image,filename):
        #get encoding
        encoding = self.getImageEncoding(image)
        if encoding is None:
            return None
        
        #get metadata
        info = self.getImageMetadata(image)
        info['encoding'] = encoding
        info['filename'] = filename

        #if name is not there,name is set to filename without extension
        if self.nameTag not in info.keys():
            info[self.nameTag] = filename.split('.')[0]
        
        for tag in self.metaDataList:
            if tag not in info.keys():
                info[tag]='NA'

        return info