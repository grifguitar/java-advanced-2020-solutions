@ECHO off
SET initPath=%~dp0
SET initPath=%initPath:~0,-1%
cd %initPath%
cd ..\..\..\..\..\
SET javaSolutions=%CD%
cd ..\..\
SET src=%CD%
SET lib=%src%\java-advanced-2020\lib
SET classpath=ru\ifmo\rain\khlytin\bank
SET classname=ru.ifmo.rain.khlytin.bank
SET allLib=%lib%\junit-4.11.jar;%lib%\hamcrest-core-1.3.jar
SET outFolder=%initPath%\_build
cd %initPath%
mkdir %outFolder%
javac -cp %allLib% %javaSolutions%\%classpath%\*.java -d %outFolder%
cd %outFolder%
java -cp %allLib%;%outFolder% org.junit.runner.JUnitCore %classname%.Test
SET code=%ERRORLEVEL%
cd %initPath%
rmdir /S /Q %outFolder%
EXIT %code%