@ECHO off
SET pathToThisScript=%~dp0
SET pathToThisScript=%pathToThisScript:~0,-1%
cd %pathToThisScript%
cd ..\..\..\..\..\..\..\
SET externalFolder=%CD%
cd %pathToThisScript%
cd ..\..\..\..\..\
SET src=%CD%
SET artifacts=%externalFolder%\java-advanced-2020\artifacts
SET lib=%externalFolder%\java-advanced-2020\lib
SET allLib=%lib%;%artifacts%
SET classpath=ru\ifmo\rain\khlytin\implementor
SET classname=ru.ifmo.rain.khlytin.implementor
SET outFolder=%pathToThisScript%\_build
cd %pathToThisScript%
mkdir "%outFolder%"
javac --module-path "%allLib%" "%src%"\module-info.java "%src%"\%classpath%\*.java -d "%outFolder%\%classname%"
cd "%outFolder%\%classname%"
jar --create --file="%pathToThisScript%"\_implementor.jar --main-class=%classname%.JarImplementor --module-path="%allLib%" module-info.class %classpath%\*.class