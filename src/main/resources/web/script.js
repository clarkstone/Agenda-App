class AgendaApp {
    constructor() {
        this.currentDate = new Date().toISOString().split('T')[0];
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.updateDateDisplay();
        this.loadTasks();
    }

    setupEventListeners() {
        // Date navigation
        document.getElementById('prevDate').addEventListener('click', () => this.navigateDate(-1));
        document.getElementById('nextDate').addEventListener('click', () => this.navigateDate(1));
        document.getElementById('todayBtn').addEventListener('click', () => this.goToToday());
        document.getElementById('currentDateInput').addEventListener('change', (e) => this.setDate(e.target.value));

        // Task form
        document.getElementById('taskForm').addEventListener('submit', (e) => this.handleAddTask(e));

        // Set initial date input value
        document.getElementById('currentDateInput').value = this.currentDate;
        
        // Set default due date to current time
        const now = new Date();
        now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
        document.getElementById('taskDueDate').value = now.toISOString().slice(0, 16);
    }

    navigateDate(days) {
        const date = new Date(this.currentDate);
        date.setDate(date.getDate() + days);
        this.currentDate = date.toISOString().split('T')[0];
        this.updateDateDisplay();
        this.loadTasks();
    }

    goToToday() {
        this.currentDate = new Date().toISOString().split('T')[0];
        this.updateDateDisplay();
        this.loadTasks();
    }

    setDate(dateString) {
        this.currentDate = dateString;
        this.updateDateDisplay();
        this.loadTasks();
    }

    updateDateDisplay() {
        document.getElementById('currentDateInput').value = this.currentDate;
        
        const date = new Date(this.currentDate);
        const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        const formattedDate = date.toLocaleDateString('en-US', options);
        
        const today = new Date().toISOString().split('T')[0];
        const displayText = this.currentDate === today ? 'Today' : formattedDate;
        
        document.getElementById('selectedDate').textContent = displayText;
    }

    async loadTasks() {
        try {
            const response = await fetch(`/api/tasks?date=${this.currentDate}`);
            const tasks = await response.json();
            this.renderTasks(tasks);
            this.updateTaskCount(tasks);
        } catch (error) {
            console.error('Error loading tasks:', error);
            this.showNotification('Error loading tasks', 'error');
        }
    }

    renderTasks(tasks) {
        const tasksList = document.getElementById('tasksList');
        
        if (tasks.length === 0) {
            tasksList.innerHTML = `
                <div class="empty-state">
                    <div class="empty-icon">📝</div>
                    <p>No tasks for this date. Add your first task above!</p>
                </div>
            `;
            return;
        }

        tasksList.innerHTML = tasks.map(task => this.createTaskHTML(task)).join('');
        
        // Add event listeners to task buttons
        tasks.forEach(task => {
            document.getElementById(`toggle-${task.id}`).addEventListener('click', () => this.toggleTask(task.id));
            document.getElementById(`delete-${task.id}`).addEventListener('click', () => this.deleteTask(task.id));
        });
    }

    createTaskHTML(task) {
        const dueDate = task.dueDate ? new Date(task.dueDate) : null;
        const formattedDueDate = dueDate ? dueDate.toLocaleString() : 'No due date';
        
        return `
            <div class="task-item ${task.completed ? 'completed' : ''}">
                <div class="task-header">
                    <div class="task-description">${this.escapeHtml(task.description)}</div>
                    <span class="task-priority priority-${task.priority.toLowerCase()}">${task.priority}</span>
                </div>
                <div class="task-meta">
                    <span>Due: ${formattedDueDate}</span>
                    <div class="task-actions">
                        <button id="toggle-${task.id}" class="btn btn-sm ${task.completed ? 'btn-secondary' : 'btn-success'}">
                            ${task.completed ? '↩️ Undo' : '✓ Complete'}
                        </button>
                        <button id="delete-${task.id}" class="btn btn-sm btn-danger">🗑️ Delete</button>
                    </div>
                </div>
            </div>
        `;
    }

    updateTaskCount(tasks) {
        const count = tasks.length;
        const completedCount = tasks.filter(task => task.completed).length;
        const pendingCount = count - completedCount;
        
        let countText = `${count} task${count !== 1 ? 's' : ''}`;
        if (pendingCount > 0) {
            countText += ` (${pendingCount} pending)`;
        }
        
        document.getElementById('taskCount').textContent = countText;
    }

    async handleAddTask(event) {
        event.preventDefault();
        
        const description = document.getElementById('taskDescription').value.trim();
        const dueDate = document.getElementById('taskDueDate').value;
        const priority = document.getElementById('taskPriority').value;
        
        if (!description) {
            this.showNotification('Please enter a task description', 'error');
            return;
        }

        const task = {
            description: description,
            dueDate: dueDate ? new Date(dueDate).toISOString() : new Date().toISOString(),
            priority: priority
        };

        try {
            const response = await fetch('/api/tasks/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(task)
            });

            if (response.ok) {
                this.showNotification('Task added successfully!', 'success');
                document.getElementById('taskForm').reset();
                
                // Reset due date to current time
                const now = new Date();
                now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
                document.getElementById('taskDueDate').value = now.toISOString().slice(0, 16);
                
                this.loadTasks();
            } else {
                throw new Error('Failed to add task');
            }
        } catch (error) {
            console.error('Error adding task:', error);
            this.showNotification('Error adding task', 'error');
        }
    }

    async toggleTask(taskId) {
        try {
            const response = await fetch('/api/tasks/toggle', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `taskId=${taskId}`
            });

            if (response.ok) {
                this.showNotification('Task status updated', 'success');
                this.loadTasks();
            } else {
                throw new Error('Failed to toggle task');
            }
        } catch (error) {
            console.error('Error toggling task:', error);
            this.showNotification('Error updating task', 'error');
        }
    }

    async deleteTask(taskId) {
        if (!confirm('Are you sure you want to delete this task?')) {
            return;
        }

        try {
            const response = await fetch('/api/tasks/delete', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `taskId=${taskId}`
            });

            if (response.ok) {
                this.showNotification('Task deleted successfully', 'success');
                this.loadTasks();
            } else {
                throw new Error('Failed to delete task');
            }
        } catch (error) {
            console.error('Error deleting task:', error);
            this.showNotification('Error deleting task', 'error');
        }
    }

    showNotification(message, type = 'info') {
        const notification = document.getElementById('notification');
        notification.textContent = message;
        notification.className = `notification ${type}`;
        notification.classList.add('show');

        setTimeout(() => {
            notification.classList.remove('show');
        }, 3000);
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}

// Initialize the app when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new AgendaApp();
});
