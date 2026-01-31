@echo off
echo Setting up JavaFX paths...
set JAVA_FX_PATH="C:\Program Files\Java\javafx-sdk-17.0.2\lib"

echo Creating target directory...
if not exist "target\classes" mkdir "target\classes"

echo Compiling Java files...
javac --module-path %JAVA_FX_PATH% --add-modules javafx.controls,javafx.fxml -d "target\classes" ^
    -cp "lib\jackson-databind-2.15.2.jar;lib\jackson-core-2.15.2.jar;lib\jackson-annotations-2.15.2.jar" ^
    src\main\java\com\agenda\model\Task.java ^
    src\main\java\com\agenda\service\AgendaService.java ^
    src\main\java\com\agenda\controller\AgendaController.java ^
    src\main\java\com\agenda\AgendaApp.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo Running the application...
    java --module-path %JAVA_FX_PATH% --add-modules javafx.controls,javafx.fxml ^
        -cp "target\classes;lib\jackson-databind-2.15.2.jar;lib\jackson-core-2.15.2.jar;lib\jackson-annotations-2.15.2.jar" ^
        com.agenda.AgendaApp
) else (
    echo Compilation failed!
)
pause
