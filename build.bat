cd src

set CLASSPATH=.;
set CLASSPATH=%CLASSPATH%;./com.jcraft.jsch_0.1.27.jar;

javac *.java

pause

java sFTPClient

pause