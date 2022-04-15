import spacy


class Classifier:

    def __init__(self):
        self.margin = 0.05
        self.nlp = spacy.load('en_core_web_sm')

    def getTitle(self,textList):
        maxHeight = 0
        for text,height in textList:
            if height > maxHeight:
                maxHeight = height
        title = ''
        i = 0
        while i < len(textList):
            if textList[i][1] >= maxHeight * (1 - self.margin):
                title = title+textList[i][0]+' '
                textList.pop(i)
            else:
                i+=1
        if title.strip():
            return title.strip()
        else:
            return ''


    def getISBN(self,textList):
        for text,height in textList:
            numDigits = 0
            flag  = True
            for s in text:
                if s.isnumeric():
                    numDigits += 1
                elif s!='-':
                    flag = False
                    break
            if flag and (numDigits == 10 or numDigits==13):
                textList.remove((text,height))
                return text

        return ''

    def getAuthorPublisher(self,textList):
        textExtracted = ''
        for text,height in textList:
            textExtracted = textExtracted+text.lower()+' '
        
        author = set()
        publisher = set()
        
        
        doc = self.nlp(textExtracted.title())
        if doc.ents:
            for ent in doc.ents:
                if ent.label_ == 'PERSON':
                    author.add(ent.text.lower())
                elif ent.label_ == 'ORG':
                    publisher.add(ent.text.lower())


        return " ".join(author)," ".join(publisher)
        
    def getMetadata(self,textList):
        metadata = {}
        metadata['title'] = self.getTitle(textList)
        metadata['ISBN'] = self.getISBN(textList)
        metadata['author'],metadata['publisher'] = self.getAuthorPublisher(textList)
        return metadata