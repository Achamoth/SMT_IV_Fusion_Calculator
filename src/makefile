#	A makefile for compilation of SMT IV fusion calculator
# Author: Ammar Abu Shamleh

PROJECT = FusionCalculator
SOURCES = $(wildcard *.java)
CLASS = $(wildcard *.class)

JC = javac
JFLAGS = -Xlint:unchecked

$(PROJECT) : $(CLASS)
	$(JC) $(SOURCES)

clean:
	rm -f *.class
