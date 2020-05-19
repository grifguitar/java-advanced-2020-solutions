@ECHO off

SET initDir=%CD%

SET pathToThisScript=%~dp0
SET pathToThisScript=%pathToThisScript:~0,-1%
cd %pathToThisScript%
cd ..\..\..\..\..\..\..\
SET externalFolder=%CD%
cd %pathToThisScript%
cd ..\..\..\..\..\
SET src=%CD%
cd %pathToThisScript%

SET outFolder=%pathToThisScript%\_build
SET lib=%externalFolder%\java-advanced-2020\lib

SET modPath=ru\ifmo\rain\khlytin\bank
SET modName=ru.ifmo.rain.khlytin.bank

mkdir "%outFolder%"
javac -cp "%lib%"\junit-4.11.jar "%src%"\%modPath%\*.java -d "%outFolder%"

cd "%outFolder%"
java -cp "%lib%"\junit-4.11.jar;"%lib%"\hamcrest-core-1.3.jar;"%outFolder%" %modName%.BankTests

set exitcode=%ERRORLEVEL%

cd %initDir%

EXIT %exitcode%