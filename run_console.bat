@echo off
echo Compiling Console Agenda App...
if not exist "target\classes" mkdir "target\classes"

javac -d "target\classes" ^
    src\main\java\com\agenda\model\Task.java ^
    src\main\java\com\agenda\service\AgendaService.java ^
    src\main\java\com\agenda\ConsoleAgendaApp.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo Running Console Agenda App...
    echo.
    java -cp "target\classes" com.agenda.ConsoleAgendaApp
) else (
    echo Compilation failed!
)
pause
