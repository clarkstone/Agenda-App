# Setup Instructions for Agenda App

## Option 1: Using Maven (Recommended)
1. Install Maven from https://maven.apache.org/download.cgi
2. Add Maven to your system PATH
3. Run: `mvn clean javafx:run`

## Option 2: Using Gradle
1. Install Gradle from https://gradle.org/install/
2. Run: `gradle run`

## Option 3: Manual Compilation
1. Download JavaFX SDK from https://gluonhq.com/products/javafx/
2. Download Jackson libraries:
   - jackson-databind-2.15.2.jar
   - jackson-core-2.15.2.jar  
   - jackson-annotations-2.15.2.jar
3. Create a `lib` folder and place the JAR files inside
4. Update the JavaFX path in `compile_and_run.bat`
5. Run: `compile_and_run.bat`

## Option 4: Using IDE
1. Open the project in IntelliJ IDEA or Eclipse
2. Import as Maven project
3. Add JavaFX library to project dependencies
4. Run the `AgendaApp.java` main class

## Required Libraries
- JavaFX 17+ (controls, fxml modules)
- Jackson Databind 2.15.2+
- Java 17+

## Troubleshooting
- If JavaFX is not found, ensure the path in the batch file matches your JavaFX installation
- Make sure all required JAR files are in the lib folder
- Verify Java 17+ is installed and in PATH
