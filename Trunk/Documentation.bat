@ECHO OFF
CALL resources/FindJDK.bat
SET lstf=_list
FOR /F "usebackq tokens=*" %%G IN (`DIR /B /S src\*.java`) DO CALL :append "%%G"
IF EXIST doc RMDIR /S /Q doc >NUL
javadoc -d docs -windowtitle "Infinity" -linkoffline http://java.sun.com/javase/7/docs/api/ http://java.sun.com/javase/7/docs/api/ @%lstf%
DEL /F /Q %lstf%
START docs\index.html
GOTO :eof

:append
SET gx=%1
SET gx=%gx:\=\\%
ECHO %gx% >> "%lstf%"
GOTO :eof