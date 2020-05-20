@ECHO off
SET initPath=%~dp0
SET initPath=%initPath:~0,-1%
cd ..\..\..\..\..\..\..\
SET src=%CD%
SET lib=%src%\java-advanced-2020\lib
SET classpath=ru\ifmo\rain\khlytin\bank
SET classname=ru.ifmo.rain.khlytin.bank
SET outFolder=%initPath%\_build
cd %initPath%
mkdir "%outFolder%"
javac -cp "%lib%"\junit-4.11.jar "%src%"\java-advanced-2020-solutions\java-solutions\%classpath%\*.java -d "%outFolder%"
cd "%outFolder%"
java -cp "%lib%"\junit-4.11.jar;"%lib%"\hamcrest-core-1.3.jar;"%outFolder%" org.junit.runner.JUnitCore %classname%.Test
SET code=%ERRORLEVEL%
EXIT %code%