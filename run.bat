@echo off
cd /d "%~dp0"
java -cp "out;lib\sqlite-jdbc-3.46.1.3.jar" com.expensetracker.main.Main
pause
