@ECHO OFF

SET cc=javac
SET cflags=
SET src=src
SET lib=lib
SET res=resources
SET out=bin
SET dist=Infinity.jar

CALL :clean 2>NUL
CALL "%res%\FindJDK.bat"

SET lstf=temp.txt
SET imgdir=%res%\images
SET manifest=%res%\Manifest.txt
SET versionfile=%res%\version.txt
FOR /F %%G IN (%versionfile%) DO SET version=%%G
SET scripts=scripts

ECHO Compiling bot
IF EXIST "%lstf%" DEL /F /Q "%lstf%"
FOR /F "usebackq tokens=*" %%G IN (`DIR /B /S "%src%\*.java"`) DO CALL :append "%%G"
IF EXIST "%out%" RMDIR /S /Q "%out%" > NUL
MKDIR "%out%"
"%cc%" %cflags% -d "%out%" "@%lstf%" 2>NUL
DEL /F /Q "%lstf%"

:scripts
ECHO Compiling scripts
ECHO. > "%scripts%\.class"
DEL /F /Q "%scripts%\*.class" > NUL
"%cc%" %cflags% -cp "%out%" %scripts%\*.java

ECHO Packing JAR

IF EXIST "%dist%" DEL /F /Q "%dist%"
IF EXIST "%lstf%" DEL /F /Q "%lstf%"
COPY "%manifest%" "%lstf%"
ECHO Specification-Version: "%version%" >> "%lstf%"
ECHO Implementation-Version: "%version%" >> "%lstf%"
jar cfm "%dist%" "%lstf%" -C "%out%" . %scripts%\*.class %res%\*.dat %imgdir%\*.png %res%\*.bat %res%\*.sh com/jtattoo/plaf/icons/*.gif com/jtattoo/plaf/hifi/icons/*.gif com/jtattoo/plaf/aluminium/icons/*.gif com/jtattoo/plaf/aluminium/icons/*.jpg *.theme com/nilo/plaf/nimrod/icons/*.png de/muntjak/tinylookandfeel/icons/*.png de/muntjak/tinylookandfeel/*.properties
DEL /F /Q "%lstf%"

:end
CALL :clean 2>NUL
ECHO Compilation successful.
GOTO :eof

:append
SET gx=%1
SET gx=%gx:\=\\%
ECHO %gx% >> %lstf%
GOTO :eof

:clean
RMDIR /S /Q "%out%"
DEL /F /Q %scripts%\*.class
GOTO :eof
