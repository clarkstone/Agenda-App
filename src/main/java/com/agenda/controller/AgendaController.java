package com.agenda.controller;

import com.agenda.model.Task;
import com.agenda.service.AgendaService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class AgendaController implements Initializable {
    private final AgendaService agendaService = new AgendaService();
    private LocalDate currentDate;

    @FXML
    private Label currentDateLabel;

    @FXML
    private TableView<Task> taskTableView;

    @FXML
    private TableColumn<Task, String> descriptionColumn;

    @FXML
    private TableColumn<Task, String> dueDateColumn;

    @FXML
    private TableColumn<Task, String> statusColumn;

    @FXML
    private TableColumn<Task, Void> actionsColumn;

    @FXML
    private TextField descriptionField;

    @FXML
    private DatePicker dueDatePicker;

    @FXML
    private ComboBox<String> priorityComboBox;

    @FXML
    private Button previousDateButton;

    @FXML
    private Button nextDateButton;

    @FXML
    private Button todayButton;

    @FXML
    private Button addTaskButton;

    @FXML
    private VBox mainVBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentDate = LocalDate.now();
        
        priorityComboBox.setItems(FXCollections.observableArrayList("HIGH", "MEDIUM", "LOW"));
        priorityComboBox.setValue("MEDIUM");
        
        setupTableView();
        setupDateNavigation();
        updateDisplay();
    }

    private void setupTableView() {
        descriptionColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDescription()));
        
        dueDateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getFormattedDueDate()));
        
        statusColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().isCompleted() ? "Completed" : "Pending"));
        
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button toggleButton = new Button("Toggle");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttons = new HBox(5, toggleButton, deleteButton);

            {
                toggleButton.setOnAction(event -> {
                    Task task = getTableView().getItems().get(getIndex());
                    agendaService.toggleTaskCompletion(task.getId());
                    updateDisplay();
                });

                deleteButton.setOnAction(event -> {
                    Task task = getTableView().getItems().get(getIndex());
                    agendaService.deleteTask(task.getId());
                    updateDisplay();
                });

                toggleButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });
    }

    private void setupDateNavigation() {
        previousDateButton.setOnAction(event -> {
            currentDate = currentDate.minusDays(1);
            updateDisplay();
        });

        nextDateButton.setOnAction(event -> {
            currentDate = currentDate.plusDays(1);
            updateDisplay();
        });

        todayButton.setOnAction(event -> {
            currentDate = LocalDate.now();
            updateDisplay();
        });
    }

    @FXML
    private void addTask() {
        String description = descriptionField.getText().trim();
        if (description.isEmpty()) {
            showAlert("Error", "Please enter a task description.");
            return;
        }

        LocalDate dueDate = dueDatePicker.getValue();
        LocalDateTime dueDateTime = dueDate != null ? 
            dueDate.atTime(LocalTime.now()) : LocalDateTime.now();

        Task task = new Task(description, dueDateTime);
        task.setPriority(priorityComboBox.getValue());

        agendaService.addTask(task);
        
        descriptionField.clear();
        dueDatePicker.setValue(null);
        priorityComboBox.setValue("MEDIUM");
        
        updateDisplay();
    }

    private void updateDisplay() {
        currentDateLabel.setText(currentDate.toString());
        
        ObservableList<Task> tasks = FXCollections.observableArrayList(
            agendaService.getTasksForDate(currentDate)
        );
        taskTableView.setItems(tasks);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
