@echo off
echo Compiling Web Agenda App...
if not exist "target\classes" mkdir "target\classes"

javac -d "target\classes" ^
    src\main\java\com\agenda\model\Task.java ^
    src\main\java\com\agenda\service\AgendaService.java ^
    src\main\java\com\agenda\api\AgendaApiServer.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo Starting Web Server...
    echo.
    echo The server will start on http://localhost:8080
    echo Open your browser and navigate to that address
    echo Press Ctrl+C to stop the server
    echo.
    java -cp "target\classes" com.agenda.api.AgendaApiServer
) else (
    echo Compilation failed!
)
pause
