@ECHO off

SET initDir=%CD%

SET pathToThisScript=%~dp0
SET pathToThisScript=%pathToThisScript:~0,-1%
cd %pathToThisScript%
cd ..\..\..\..\..\..\..\
SET externalFolder=%CD%
cd %pathToThisScript%

SET artifacts=%externalFolder%\java-advanced-2020\artifacts
SET lib=%externalFolder%\java-advanced-2020\lib
SET modules=%externalFolder%\java-advanced-2020\modules

SET modPath=ru\ifmo\rain\khlytin\implementor
SET modName=ru.ifmo.rain.khlytin.implementor
SET implerPath=info\kgeorgiy\java\advanced\implementor
SET implerName=info.kgeorgiy.java.advanced.implementor

javadoc ^
    -private ^
    -link https://docs.oracle.com/en/java/javase/11/docs/api/ ^
    -d _javadoc ^
    -cp "%artifacts%\*.jar";"%lib%\*.jar"; ^
     %pathToThisScript%\*.java ^
     "%modules%\%implerName%\%implerPath%\Impler.java" ^
     "%modules%\%implerName%\%implerPath%\JarImpler.java" ^
     "%modules%\%implerName%\%implerPath%\ImplerException.java" ^
     "%modules%\%implerName%\%implerPath%\package-info.java"

cd "%initDir%"