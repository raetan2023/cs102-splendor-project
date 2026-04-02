@echo off
echo Creating classes directory...
if not exist classes mkdir classes

echo Compiling Java files...
dir /s /b src\*.java > sources.txt
javac --release 11 -d classes @sources.txt

if %errorlevel% equ 0 (
    echo Compilation successful!
) else (
    echo Compilation failed. Please check the errors above.
)
del sources.txt
