import pickle


class Decoder:

    def __init__(self):
        pass

    #convert pickle to numpy array
    def decode(self,encoding):
        return pickle.loads(encoding)

