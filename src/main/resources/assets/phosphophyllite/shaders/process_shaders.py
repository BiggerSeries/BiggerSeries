import os
import sys


def processDir(dir: str):
    inputDir = os.path.abspath("./" + dir + "/")
    outputDir = os.path.abspath("../processed/" + dir + "/")
    inputs = os.listdir(inputDir)
    if not os.path.exists(outputDir):
        os.mkdir(outputDir)
    for file in inputs:
        # if os.path.isdir(inputDir + file):
        #     processDir(dir + "/" + file)
        #     pass
        inputFile = inputDir + "/" + file
        outputFile = outputDir + "/" + file
        print(inputFile, file=sys.stderr)
        os.system("gcc -E -P -o " + outputFile + " - < " + inputFile)
    pass


if __name__ == '__main__':
    try:
        os.chdir("./src")
        for dir in os.listdir():
            if dir != "util":
                processDir(dir)
            pass
    except Exception as e:
        print(e)
        sys.exit(-1)
        pass
    pass
