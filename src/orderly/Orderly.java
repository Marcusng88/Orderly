/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package orderly;

import java.util.ArrayList;
import java.util.Scanner;

// import java.util.Arrays;

public class Orderly {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static Scanner input = new Scanner(System.in);
    static ArrayList<Task> tasks = new ArrayList<>();

    // Create database object to set connection
    public static Database todolist = new Database();

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
                    mgmtMenu:
                    while (true) { 
                        action = mgmtMenu();
                        input.nextLine();
                        switch (action) {
                            case 1 -> manager.markTaskComplete();
                            case 2 -> manager.setTitle(tasks, todolist);
                            case 3 -> manager.setDescription(tasks, todolist);
                            case 4 -> manager.setDueDate(tasks, todolist);
                            case 5 -> manager.setCategory(tasks, todolist);
                            case 6 -> manager.setPriority(tasks, todolist);
                            case 7 -> manager.setPriority(tasks, todolist);
                            default -> System.out.println(ANSI_RED + "\nInvalid choice. Please Try again.\n" + ANSI_RESET);
                        }
                    }
                }
                case 4 -> {
                    editMenu:
                    while (true) { 
                        action = editMenu();
                        input.nextLine();
                        switch (action) {
                            case 1 -> dependencyManager.setTitle(tasks, todolist);
                            case 2 -> dependencyManager.setDescription(tasks, todolist);
                            case 3 -> dependencyManager.setDueDate(tasks, todolist);
                            case 4 -> dependencyManager.setCategory(tasks, todolist);
                            case 5 -> dependencyManager.setPriority(tasks, todolist);
                            case 6 -> dependencyManager.setDependencies();
                            case 7 -> {break editMenu;}
                            default -> System.out.println(ANSI_RED + "\nInvalid choice. Please Try again.\n" + ANSI_RESET);
                        }
                    }
                }
                case 5 -> manager.deleteTask(tasks, todolist);
                case 6 -> {
                    System.out.println(ANSI_RED + "Exiting Orderly..." + ANSI_RESET);
                    break mainMenu;
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

    private static int mgmtMenu(){
        System.out.println(ANSI_YELLOW + "\n=== Task Management ===" + ANSI_RESET);
        System.out.println("1. Mark Task Completion");
        System.out.println("2. Change Task Title");
        System.out.println("3. Change Task Description");
        System.out.println("4. Change Task Due Date");
        System.out.println("5. Change Task Category");
        System.out.println("6. Change Task Priority");
        System.out.println("7. Back to Main Menu");
        System.out.print(ANSI_PURPLE + "Choose an action >> " + ANSI_YELLOW);

        return input.nextInt();
    }

    private static int editMenu(){
        System.out.println(ANSI_YELLOW + "\n=== Edit Task ===" + ANSI_RESET);
        System.out.println("1. Change Task Title");
        System.out.println("2. Change Task Description");
        System.out.println("3. Change Task Due Date");
        System.out.println("4. Change Task Category");
        System.out.println("5. Change Task Priority");
        System.out.println("6. Set Dependency");
        System.out.println("7. Back to Main Menu");
        System.out.print(ANSI_PURPLE + "Choose an action >> " + ANSI_YELLOW);

        return input.nextInt();
    }

}
