@ECHO off
SET initPath=%~dp0
SET initPath=%initPath:~0,-1%
cd %initPath%
cd ..\..\..\..\..\
SET javaSolutions=%CD%
cd ..\..\
SET src=%CD%
SET lib=%src%\java-advanced-2020\lib
SET classpath=ru\ifmo\rain\khlytin\i18n
SET classname=ru.ifmo.rain.khlytin.i18n
SET allLib=%lib%\junit-4.11.jar;%lib%\hamcrest-core-1.3.jar
SET outFolder=%initPath%\_build
cd %initPath%
mkdir %outFolder%
javac -cp %allLib% %javaSolutions%\%classpath%\*.java -d %outFolder%
cd %outFolder%
java -cp %allLib%;%outFolder% %classname%.TextStatistics en_US ru_RU ..\input.txt ..\output.html
cd %initPath%
rmdir /S /Q %outFolder%