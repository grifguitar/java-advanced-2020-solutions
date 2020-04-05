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
SET artifacts=%externalFolder%\java-advanced-2020\artifacts
SET lib=%externalFolder%\java-advanced-2020\lib
SET allLib=%lib%;%artifacts%

SET modPath=ru\ifmo\rain\khlytin\implementor
SET modName=ru.ifmo.rain.khlytin.implementor

mkdir "%outFolder%"
javac --module-path "%allLib%" "%src%"\module-info.java "%src%"\%modPath%\*.java -d "%outFolder%\%modName%"
cd "%outFolder%\%modName%"
jar --create --file="%pathToThisScript%"\_implementor.jar --main-class=%modName%.JarImplementor --module-path="%allLib%" module-info.class %modPath%\*.class

cd "%initDir%"