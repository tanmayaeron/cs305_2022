from src.writer import Writer
import xlsxwriter
import sys

class XlsxWriter(Writer):

    def __init__(self):
        self.row = 0

    def writeRow(self, rowTuple):
        print(rowTuple)
        try:
            for i in range(len(rowTuple)):
                self.worksheet.write(self.row, i, rowTuple[i])
            self.row += 1
        except:
            print('Error: Cannot write row.')
            sys.exit()

    def open(self, filePath):
        try:
            self.workbook = xlsxwriter.Workbook(filePath)
            self.worksheet = self.workbook.add_worksheet()
            self.row = 0
        except:
            print('Error: Cannot open file.')
            sys.exit()
        
    def close(self):
        try:
            self.workbook.close()
            self.row = 0
        except:
            print('Error: Cannot close file.')
            sys.exit()