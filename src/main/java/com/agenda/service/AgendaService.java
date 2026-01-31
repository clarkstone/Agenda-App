package com.agenda.service;

import com.agenda.model.Task;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgendaService {
    private static final String DATA_FILE = "agenda_data.txt";
    private Map<LocalDate, List<Task>> tasksByDate;

    public AgendaService() {
        this.tasksByDate = new HashMap<>();
        loadData();
    }

    public List<Task> getTasksForDate(LocalDate date) {
        return tasksByDate.getOrDefault(date, new ArrayList<>());
    }

    public void addTask(Task task) {
        LocalDate taskDate = task.getDueDate() != null ? 
            task.getDueDate().toLocalDate() : LocalDate.now();
        
        tasksByDate.computeIfAbsent(taskDate, k -> new ArrayList<>()).add(task);
        saveData();
    }

    public void updateTask(Task task) {
        LocalDate oldDate = null;
        LocalDate newDate = task.getDueDate() != null ? 
            task.getDueDate().toLocalDate() : LocalDate.now();

        for (Map.Entry<LocalDate, List<Task>> entry : tasksByDate.entrySet()) {
            if (entry.getValue().removeIf(t -> t.getId().equals(task.getId()))) {
                oldDate = entry.getKey();
                break;
            }
        }

        if (oldDate != null && !oldDate.equals(newDate)) {
            tasksByDate.computeIfAbsent(newDate, k -> new ArrayList<>()).add(task);
        } else {
            tasksByDate.computeIfAbsent(newDate, k -> new ArrayList<>()).add(task);
        }
        
        saveData();
    }

    public void deleteTask(String taskId) {
        for (List<Task> tasks : tasksByDate.values()) {
            if (tasks.removeIf(task -> task.getId().equals(taskId))) {
                saveData();
                break;
            }
        }
    }

    public void toggleTaskCompletion(String taskId) {
        for (List<Task> tasks : tasksByDate.values()) {
            for (Task task : tasks) {
                if (task.getId().equals(taskId)) {
                    task.setCompleted(!task.isCompleted());
                    saveData();
                    return;
                }
            }
        }
    }

    public List<LocalDate> getDatesWithTasks() {
        return new ArrayList<>(tasksByDate.keySet()).stream()
            .sorted()
            .toList();
    }

    public Map<LocalDate, Integer> getTaskCounts() {
        Map<LocalDate, Integer> counts = new HashMap<>();
        for (Map.Entry<LocalDate, List<Task>> entry : tasksByDate.entrySet()) {
            counts.put(entry.getKey(), entry.getValue().size());
        }
        return counts;
    }

    private void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (Map.Entry<LocalDate, List<Task>> entry : tasksByDate.entrySet()) {
                for (Task task : entry.getValue()) {
                    writer.println(entry.getKey() + "|" + 
                        task.getId() + "|" + 
                        task.getDescription() + "|" + 
                        task.isCompleted() + "|" + 
                        task.getCreatedAt() + "|" + 
                        task.getDueDate() + "|" + 
                        task.getPriority());
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 7) {
                    LocalDate date = LocalDate.parse(parts[0]);
                    Task task = new Task();
                    task.setId(parts[1]);
                    task.setDescription(parts[2]);
                    task.setCompleted(Boolean.parseBoolean(parts[3]));
                    task.setCreatedAt(LocalDateTime.parse(parts[4]));
                    task.setDueDate(LocalDateTime.parse(parts[5]));
                    task.setPriority(parts[6]);
                    
                    tasksByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(task);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
            tasksByDate = new HashMap<>();
        }
    }
}
