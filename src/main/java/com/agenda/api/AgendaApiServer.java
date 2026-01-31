package com.agenda.api;

import com.agenda.model.Task;
import com.agenda.service.AgendaService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class AgendaApiServer {
    private static final AgendaService agendaService = new AgendaService();
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        server.createContext("/api/tasks", new TasksHandler());
        server.createContext("/api/tasks/add", new AddTaskHandler());
        server.createContext("/api/tasks/toggle", new ToggleTaskHandler());
        server.createContext("/api/tasks/delete", new DeleteTaskHandler());
        server.createContext("/", new StaticFileHandler());
        
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + PORT);
        System.out.println("Open http://localhost:" + PORT + " in your browser");
    }

    static class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                LocalDate date = LocalDate.now();
                
                if (query != null && query.startsWith("date=")) {
                    String dateStr = query.substring(5);
                    try {
                        date = LocalDate.parse(dateStr);
                    } catch (Exception e) {
                        date = LocalDate.now();
                    }
                }
                
                List<Task> tasks = agendaService.getTasksForDate(date);
                String response = tasksToJson(tasks);
                
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class AddTaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                Task task = jsonToTask(body);
                agendaService.addTask(task);
                
                String response = "{\"status\":\"success\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class ToggleTaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                String taskId = body.split("=")[1];
                agendaService.toggleTaskCompletion(taskId);
                
                String response = "{\"status\":\"success\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class DeleteTaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                String taskId = body.split("=")[1];
                agendaService.deleteTask(taskId);
                
                String response = "{\"status\":\"success\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if ("/".equals(path)) {
                path = "/index.html";
            }
            
            byte[] response;
            String contentType = "text/html";
            
            if (path.endsWith(".css")) {
                contentType = "text/css";
            } else if (path.endsWith(".js")) {
                contentType = "application/javascript";
            }
            
            try {
                response = readResourceFile("web" + path);
            } catch (Exception e) {
                response = "404 Not Found".getBytes();
                contentType = "text/plain";
                exchange.sendResponseHeaders(404, response.length);
            }
            
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }

    private static String tasksToJson(List<Task> tasks) {
        return tasks.stream()
            .map(task -> String.format(
                "{\"id\":\"%s\",\"description\":\"%s\",\"completed\":%b,\"dueDate\":\"%s\",\"priority\":\"%s\"}",
                task.getId(),
                task.getDescription().replace("\"", "\\\""),
                task.isCompleted(),
                task.getDueDate() != null ? task.getDueDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "",
                task.getPriority()
            ))
            .collect(Collectors.joining(",", "[", "]"));
    }

    private static Task jsonToTask(String json) {
        Task task = new Task();
        String[] pairs = json.replace("{", "").replace("}", "").split(",");
        
        for (String pair : pairs) {
            String[] kv = pair.split("\":\"", 2);
            if (kv.length == 2) {
                String key = kv[0].replace("\"", "").trim();
                String value = kv[1].replace("\"", "").trim();
                
                switch (key) {
                    case "description":
                        task.setDescription(value);
                        break;
                    case "priority":
                        task.setPriority(value);
                        break;
                    case "dueDate":
                        if (!value.isEmpty()) {
                            try {
                                task.setDueDate(LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                            } catch (Exception e) {
                                task.setDueDate(LocalDateTime.now());
                            }
                        }
                        break;
                }
            }
        }
        
        if (task.getDueDate() == null) {
            task.setDueDate(LocalDateTime.now());
        }
        
        return task;
    }

    private static byte[] readResourceFile(String path) throws IOException {
        try {
            // Try to read from resources first
            var is = AgendaApiServer.class.getClassLoader().getResourceAsStream(path);
            if (is != null) {
                return is.readAllBytes();
            }
            
            // If not found in resources, try to read from file system
            File file = new File("src/main/resources/" + path);
            if (file.exists()) {
                return java.nio.file.Files.readAllBytes(file.toPath());
            }
            
            throw new IOException("File not found: " + path);
        } catch (Exception e) {
            throw new IOException("File not found: " + path, e);
        }
    }
}
