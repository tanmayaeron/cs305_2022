import argparse

from src import app 

argParser = argparse.ArgumentParser(description='Extract metadata from files')
argParser.add_argument('-d', '--dir', help='input path should be a directory',action='store_true')
argParser.add_argument('path',type=str,help='path to file or directory')

args = argParser.parse_args()

if __name__=='__main__':
    app = app.App(args.path,args.dir)
    app.run()
    