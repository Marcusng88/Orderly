/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package orderly;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// import java.util.Arrays;

public class Orderly {
private static List<Task> tasks = new ArrayList<>();
    private static Connection connection;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            connectToDatabase();
            loadTasksFromDatabase();
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database: " + e.getMessage());
            return;
        }

        while (true) {
            System.out.println("\n=== View All Tasks ===");
            viewTasks();

            System.out.println("\n=== Edit Task ===");
            System.out.print("Enter the task number you want to edit: ");
            int taskNumber = scanner.nextInt();
            scanner.nextLine(); // Consume the newline

            if (taskNumber > 0 && taskNumber <= tasks.size()) {
                editTask(tasks.get(taskNumber - 1), scanner);
            } else {
                System.out.println("Invalid task number.");
            }
        }
    }

    private static void connectToDatabase() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/todolist";
        String user = "root";
        String password = "[YGnexte18211]"; // Replace with your database password

        connection = DriverManager.getConnection(url, user, password);
        System.out.println("Connected to the database.");
    }

    private static void loadTasksFromDatabase() throws SQLException {
        String query = "SELECT * FROM tasks";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            Task task = new Task(
                resultSet.getInt("id"),
                resultSet.getString("title"),
                resultSet.getString("description"),
                resultSet.getString("due_date"),
                resultSet.getString("category"),
                resultSet.getString("priority"),
                resultSet.getString("status")
            );
            tasks.add(task);
        }
        System.out.println("Tasks loaded from the database.");
    }

    private static void updateTaskInDatabase(Task task) throws SQLException {
        String query = "UPDATE tasks SET title = ?, description = ?, due_date = ?, category = ?, priority = ? WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, task.getTitle());
        preparedStatement.setString(2, task.getDescription());
        preparedStatement.setString(3, task.getDueDate());
        preparedStatement.setString(4, task.getCategory());
        preparedStatement.setString(5, task.getPriority());
        preparedStatement.setInt(6, task.getId());

        preparedStatement.executeUpdate();
        System.out.println("Task updated in the database.");
    }

    private static void markTaskAsComplete(Task task) {
        String updateSql = "UPDATE tasks SET status = 'Complete' WHERE id = ?";
        try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
            updateStmt.setInt(1, task.getId());
            updateStmt.executeUpdate();
            System.out.println("Task '" + task.getTitle() + "' marked as complete!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewTasks() {
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
    }

    private static void editTask(Task task, Scanner scanner) {
        System.out.println("What would you like to edit?");
        System.out.println("1. Title");
        System.out.println("2. Description");
        System.out.println("3. Due Date");
        System.out.println("4. Category");
        System.out.println("5. Priority");
        System.out.println("6. Mark Complete");
        System.out.println("7. Cancel");
        System.out.print("> ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline

        try {
            switch (choice) {
                case 1 -> {
                    System.out.print("Enter the new title: ");
                    task.setTitle(scanner.nextLine());
                    updateTaskInDatabase(task);
                    System.out.println("Task updated successfully!");
                }
                case 2 -> {
                    System.out.print("Enter the new description: ");
                    task.setDescription(scanner.nextLine());
                    updateTaskInDatabase(task);
                    System.out.println("Task updated successfully!");
                }
                case 3 -> {
                    System.out.print("Enter the new due date (YYYY-MM-DD): ");
                    task.setDueDate(scanner.nextLine());
                    updateTaskInDatabase(task);
                    System.out.println("Task updated successfully!");
                }
                case 4 -> {
                    System.out.print("Enter the new category: ");
                    task.setCategory(scanner.nextLine());
                    updateTaskInDatabase(task);
                    System.out.println("Task updated successfully!");
                }
                case 5 -> {
                    System.out.print("Enter the new priority (Low, Medium, High): ");
                    task.setPriority(scanner.nextLine());
                    updateTaskInDatabase(task);
                    System.out.println("Task updated successfully!");
                }
                case 6 -> {
                    markTaskAsComplete(task);
                    task.setStatus("Complete");
                }
                case 7 -> System.out.println("Edit canceled.");
                default -> System.out.println("Invalid choice.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating task: " + e.getMessage());
        }
    }
}

class Task {
    private int id;
    private String title;
    private String description;
    private String dueDate;
    private String category;
    private String priority;
    private String status;

    public Task(int id, String title, String description, String dueDate, String category, String priority, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.category = category;
        this.priority = priority;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "[" + status + "] " + title + " - Due: " + dueDate + " - Category: " + category + " - Priority: " + priority;
    }
}