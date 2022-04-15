from src import classifier,imageParser,xlsxWriter,app
import pandas as pd
import os

def test_classifier():
    textList = [('Prentice',7),('Hall',7),('the', 19), ('lazy', 20), ('dog', 20),('123-123-123-123-1',10)]
    clObject = classifier.Classifier()
    metadata = clObject.getMetadata(textList)
    assert metadata['title'] == 'the lazy dog'
    assert metadata['author'] == ''
    assert metadata['publisher'] == 'prentice hall'
    assert metadata['ISBN'] == '123-123-123-123-1'
    
def test_parser():
    expected = [('Computer', 56), ('Networking', 55), ('A', 22), ('Top-Down', 30),
     ('Approach', 32), ('KUROSE', 28), ('ROSS', 28)]
    parser = imageParser.ImageParser()
    path = os.path.join('test_images','d.png')
    assert parser.getText(path) == expected

def test_writer():
    writer = xlsxWriter.XlsxWriter()
    writer.open('output.xlsx')
    writer.writeRow(('Path','Title','Author','Publisher','ISBN'))
    writer.close()
    assert os.path.isfile('output.xlsx')
    
    os.remove('output.xlsx')



def test_no_dir():
    try:
        path = os.path.join('test_images','d.png')
        ap = app.App(path,True)
    except:
        assert True

def test_no_file():
    try:
        ap = app.App('test_images')
    except:
        assert True


def test_app():
    ap = app.App('test_images',True)
    ap.run()
    assert os.path.isfile('output.xlsx')
    
    os.remove('output.xlsx')

def test_parse_fail():
    try:
        parser = imageParser.ImageParser()
        parser.getText('file.xlsx')
    except:
        assert True

def test_app_file():
    path = os.path.join('test_images','d.png')
    ap = app.App(path,False)
    ap.run()
    assert os.path.isfile('output.xlsx')
    
    os.remove('output.xlsx')