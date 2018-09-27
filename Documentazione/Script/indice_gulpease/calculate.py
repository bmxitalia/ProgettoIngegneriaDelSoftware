# Installation: pip install -r requirements.txt --user
# Usage: python calculate.py /path/to/document.pdf

import os.path
import re
import sys
import textract

if len(sys.argv) < 2:
    print "Usage: python calculate.py /path/to/document.pdf"
    sys.exit(1)

path = sys.argv[1]

if not os.path.isfile(path):
    print "Document not found"
    sys.exit(1)

text = textract.process(path)

#salta frontespizio e indice (non funziona per Glossario e verbali!)
if "Introduzione" in text:
    while "Introduzione" in text:
        introduzione = text.index("Introduzione") + len("Introduzione")
        text = text[introduzione : ]
else: #soluzione schifo per glossario e verbali:(
    if "Glossario" in path:
        introduzione = text.index(".tex") + len(".tex")
        if introduzione is not None:
            text = text[introduzione : ]
    else:
        if "Informazioni incontro" in text:
            while "Informazioni incontro" in text:
                introduzione = text.index("Informazioni incontro") + len("Informazioni incontro")
                if introduzione is not None:
                    text = text[introduzione : ]

words = len(re.findall(r'\w+', text))
print "Words: " + str(words)

letters = len(re.findall(r'\w{1}', text))
print "Letters: " + str(letters)

sentences = len(re.findall(r'([A-Z][^\.!?]*[\.!?])', text)) #[^.;]+
print "Sentences: " + str(sentences)

#per capire se sono valori fattibili
print "Letters/Words: " + str(letters/words)

print "Words/Sentences: " + str(words/sentences)

index = 89 + (((300 * sentences) - (10 * letters)) / words)
print "Index: " + str(index)
