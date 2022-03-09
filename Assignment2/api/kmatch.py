from api.imageInfo import ImageInfo
from api.decoder import Decoder
import face_recognition

class MatchImage:

    def __init__(self,db):
        self.db = db
        #get all rows from database
        self.rows = self.db.getAllRows()
        #get array for every image
        self.encodings = [Decoder().decode(row[2]) for row in self.rows]     
    
    def kmatchFace(self,encoding,k,strictness):
        #get distance for given encoding for every face in database
        distance = face_recognition.face_distance(self.encodings,encoding)
        match = []
        
        #get those encodings which have distance less than tolerance/strictness
        for count,dist in enumerate(distance):
            if dist<=strictness:
                match.append((count,dist))

        print(match)

        #get top-k matches if more than k present
        if len(match)!=0:
            match = sorted(match,key =lambda x: x[1])
        match = match[:k]

        match = [{"id":self.rows[tup[0]][0],"name": self.rows[tup[0]][1]} for tup in match]
        return match

    def kmatchImage(self,image,k,strictness):
        #get encoding for every face in image
        faceEncodings = ImageInfo().getFaces(image)
        print(faceEncodings)
        matches = {}
        #get top-k matches for every encoding(every face)
        for count,encoding in enumerate(faceEncodings,1):
            matches['face'+str(count)] = self.kmatchFace(encoding,k,strictness)

        return matches
