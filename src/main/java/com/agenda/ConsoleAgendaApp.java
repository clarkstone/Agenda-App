package com.agenda;

import com.agenda.model.Task;
import com.agenda.service.AgendaService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ConsoleAgendaApp {
    private static final AgendaService agendaService = new AgendaService();
    private static final Scanner scanner = new Scanner(System.in);
    private static LocalDate currentDate = LocalDate.now();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        System.out.println("=== Agenda App - Console Version ===");
        
        while (true) {
            displayMenu();
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    addTask();
                    break;
                case "2":
                    viewTasks();
                    break;
                case "3":
                    navigateDate();
                    break;
                case "4":
                    toggleTask();
                    break;
                case "5":
                    deleteTask();
                    break;
                case "6":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Current Date: " + currentDate.format(dateFormatter));
        System.out.println("=".repeat(50));
        System.out.println("1. Add Task");
        System.out.println("2. View Tasks");
        System.out.println("3. Navigate Date");
        System.out.println("4. Toggle Task Completion");
        System.out.println("5. Delete Task");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void addTask() {
        System.out.print("Enter task description: ");
        String description = scanner.nextLine().trim();
        
        if (description.isEmpty()) {
            System.out.println("Task description cannot be empty!");
            return;
        }

        System.out.print("Enter due date (YYYY-MM-DD) or press Enter for today: ");
        String dueDateStr = scanner.nextLine().trim();
        
        LocalDateTime dueDateTime;
        if (dueDateStr.isEmpty()) {
            dueDateTime = LocalDateTime.now();
        } else {
            try {
                LocalDate dueDate = LocalDate.parse(dueDateStr, dateFormatter);
                dueDateTime = dueDate.atTime(LocalTime.now());
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Using today's date.");
                dueDateTime = LocalDateTime.now();
            }
        }

        System.out.print("Enter priority (HIGH/MEDIUM/LOW) or press Enter for MEDIUM: ");
        String priority = scanner.nextLine().trim().toUpperCase();
        if (priority.isEmpty()) {
            priority = "MEDIUM";
        }
        if (!priority.equals("HIGH") && !priority.equals("MEDIUM") && !priority.equals("LOW")) {
            priority = "MEDIUM";
        }

        Task task = new Task(description, dueDateTime);
        task.setPriority(priority);
        agendaService.addTask(task);
        
        System.out.println("Task added successfully!");
    }

    private static void viewTasks() {
        List<Task> tasks = agendaService.getTasksForDate(currentDate);
        
        System.out.println("\nTasks for " + currentDate.format(dateFormatter) + ":");
        System.out.println("-".repeat(50));
        
        if (tasks.isEmpty()) {
            System.out.println("No tasks found for this date.");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                System.out.printf("%d. [%s] %s (Priority: %s)\n", 
                    i + 1,
                    task.isCompleted() ? "✓" : "○",
                    task.getDescription(),
                    task.getPriority());
                System.out.printf("   Due: %s\n", task.getFormattedDueDate());
                System.out.println();
            }
        }
    }

    private static void navigateDate() {
        System.out.println("\nDate Navigation:");
        System.out.println("1. Previous Day");
        System.out.println("2. Next Day");
        System.out.println("3. Go to Today");
        System.out.println("4. Go to Specific Date");
        System.out.print("Enter choice: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                currentDate = currentDate.minusDays(1);
                System.out.println("Moved to: " + currentDate.format(dateFormatter));
                break;
            case "2":
                currentDate = currentDate.plusDays(1);
                System.out.println("Moved to: " + currentDate.format(dateFormatter));
                break;
            case "3":
                currentDate = LocalDate.now();
                System.out.println("Moved to: " + currentDate.format(dateFormatter));
                break;
            case "4":
                System.out.print("Enter date (YYYY-MM-DD): ");
                String dateStr = scanner.nextLine().trim();
                try {
                    currentDate = LocalDate.parse(dateStr, dateFormatter);
                    System.out.println("Moved to: " + currentDate.format(dateFormatter));
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format.");
                }
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void toggleTask() {
        List<Task> tasks = agendaService.getTasksForDate(currentDate);
        
        if (tasks.isEmpty()) {
            System.out.println("No tasks found for this date.");
            return;
        }

        viewTasks();
        System.out.print("Enter task number to toggle: ");
        
        try {
            int taskNum = Integer.parseInt(scanner.nextLine().trim());
            if (taskNum >= 1 && taskNum <= tasks.size()) {
                Task task = tasks.get(taskNum - 1);
                agendaService.toggleTaskCompletion(task.getId());
                System.out.println("Task completion status toggled!");
            } else {
                System.out.println("Invalid task number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private static void deleteTask() {
        List<Task> tasks = agendaService.getTasksForDate(currentDate);
        
        if (tasks.isEmpty()) {
            System.out.println("No tasks found for this date.");
            return;
        }

        viewTasks();
        System.out.print("Enter task number to delete: ");
        
        try {
            int taskNum = Integer.parseInt(scanner.nextLine().trim());
            if (taskNum >= 1 && taskNum <= tasks.size()) {
                Task task = tasks.get(taskNum - 1);
                agendaService.deleteTask(task.getId());
                System.out.println("Task deleted successfully!");
            } else {
                System.out.println("Invalid task number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
}
