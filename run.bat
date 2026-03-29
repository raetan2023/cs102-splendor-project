@echo off
set MAIN_CLASS=%1
if "%MAIN_CLASS%"=="" set MAIN_CLASS=com.splendor.Main

echo Starting Splendor with: %MAIN_CLASS%
java -cp classes %MAIN_CLASS%
