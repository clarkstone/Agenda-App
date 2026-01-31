@echo off
echo Compiling without JavaFX modules...
if not exist "target\classes" mkdir "target\classes"

echo Compiling Java files...
javac -d "target\classes" ^
    src\main\java\com\agenda\model\Task.java ^
    src\main\java\com\agenda\service\AgendaService.java ^
    src\main\java\com\agenda\controller\AgendaController.java ^
    src\main\java\com\agenda\AgendaApp.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo Note: This version requires JavaFX to be installed separately.
    echo Please install JavaFX SDK and update the classpath accordingly.
    echo.
    echo Attempting to run with basic classpath...
    java -cp "target\classes" com.agenda.AgendaApp
) else (
    echo Compilation failed!
)
pause
