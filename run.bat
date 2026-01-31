@echo off
echo Compiling Java files...
if not exist "target\classes" mkdir "target\classes"

javac --module-path "%JAVA_HOME%\javafx\lib" --add-modules javafx.controls,javafx.fxml -d "target\classes" ^
    src\main\java\com\agenda\model\Task.java ^
    src\main\java\com\agenda\service\AgendaService.java ^
    src\main\java\com\agenda\controller\AgendaController.java ^
    src\main\java\com\agenda\AgendaApp.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo Running the application...
    java --module-path "%JAVA_HOME%\javafx\lib" --add-modules javafx.controls,javafx.fxml -cp "target\classes" com.agenda.AgendaApp
) else (
    echo Compilation failed!
)
pause
