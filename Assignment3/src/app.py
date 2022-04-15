import os, sys


from src import xlsxWriter,imageParser, classifier

class App:
    def __init__(self,path,ifDir=False):
        self.outputFile = 'output.xlsx'
        self.path = path
        self.ifDir = ifDir
        self.files = []
        if ifDir:
            if os.path.isdir(path):
                self.files = os.listdir(path)
                self.files = [os.path.join(path,f) for f in self.files]
                
            else:
                print('The path is not a directory.')
                sys.exit()
        else:
            if os.path.isfile(path):
                self.files.append(path)
            else:
                print('The path is not a file.') 
                sys.exit()
        self.imgParser = imageParser.ImageParser()
        self.classifier = classifier.Classifier()
        
    
    def run(self):
        writer = xlsxWriter.XlsxWriter()
        writer.open(self.outputFile)
        writer.writeRow(('Path','Title','Author','Publisher','ISBN'))
        for file in self.files:
            if os.path.isfile(file):
                textList = self.imgParser.getText(file,True)
                print(textList)
                metadata = self.classifier.getMetadata(textList)
                writer.writeRow((file,metadata['title'],metadata['author'],metadata['publisher'],metadata['ISBN']))
        writer.close()

        