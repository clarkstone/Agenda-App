# Agenda App
A Java-based task management application with date navigation and persistence.

## Features
- Add, edit, and delete tasks
- Set due dates and priorities for tasks
- Navigate between dates (previous/next/today)
- Mark tasks as completed
- Automatic data persistence using JSON
- Clean and intuitive JavaFX interface

## Requirements
- Java 17 or higher
- Maven 3.6 or higher

## How to Run

### Using Maven
1. Navigate to the project directory
2. Run the application:
   ```bash
   mvn clean javafx:run
   ```

### Using IDE
1. Import the project as a Maven project
2. Run the `AgendaApp.java` main class

## Usage
1. **Add Tasks**: Enter a description, select due date and priority, then click "Add Task"
2. **Navigate Dates**: Use the Previous/Next buttons or click "Today" to jump to current date
3. **Manage Tasks**: Toggle completion status or delete tasks using the action buttons
4. **Data Persistence**: All tasks are automatically saved and loaded

## Project Structure
```
src/main/java/com/agenda/
├── AgendaApp.java              # Main application entry point
├── controller/
│   └── AgendaController.java   # UI controller
├── model/
│   └── Task.java              # Task data model
└── service/
    └── AgendaService.java     # Business logic and persistence

src/main/resources/com/agenda/
└── agenda.fxml                # UI layout definition
```

## Technologies Used
- Java 17
- JavaFX 17
- Jackson (JSON processing)
- Maven (build management)
