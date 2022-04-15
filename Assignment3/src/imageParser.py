from src.parser import Parser
from PIL import Image
import pytesseract
import sys

class ImageParser(Parser):

    def __init__(self):
        self.confThreshold= 50
        f = open('tesseractpath.txt','r')
        pytesseract.pytesseract.tesseract_cmd = f.read()

    def getText(self,file,ifPath=True):
        if ifPath:
            try:
                fileObject = Image.open(file)
            except:
                print('Couldn\'t open file.')
                sys.exit()
        else:
            fileObject  = file
        text_dict = pytesseract.image_to_data(fileObject,output_type=pytesseract.Output.DICT)
        text = []
        for i in range(len(text_dict['text'])):
            word = text_dict['text'][i].replace('\n', ' ')
            word = word.strip()
            if word and float(text_dict['conf'][i])>=self.confThreshold:
                text.append((text_dict['text'][i],text_dict['height'][i]))
        return text