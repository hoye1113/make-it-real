@echo off
title eufyMake (MakeItReal) Mobile App Simulation
set JAVA_HOME=C:\Users\38788\AppData\Local\mise\installs\java\21.0.2
set PATH=%JAVA_HOME%\bin;%PATH%
set GRADLE_BAT=C:\Users\38788\AppData\Local\mise\installs\gradle\8.10.2\gradle-8.10.2\bin\gradle.bat

echo ===================================================
echo   Launching eufyMake (MakeItReal) Mobile App (420x880)
echo ===================================================

cd /d "%~dp0"
call "%GRADLE_BAT%" :composeApp:run
pause
