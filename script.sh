java -jar ./antlr-4.11.1-complete.jar -visitor Calculator.g4
javac -cp ./antlr-4.11.1-complete.jar Calculator*.java
#java -cp .:antlr-4.11.1-complete.jar org.antlr.v4.gui.TestRig Calculator expression -tree -gui example.txt