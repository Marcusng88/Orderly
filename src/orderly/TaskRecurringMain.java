package orderly;

import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

class Task {
    private int id; // New field for database ID
    private String title;
    private String description;
    private String recurrence_interval;
    private LocalDate nextOccurrence;

    public Task(int id, String title, String description, String recurrenceInterval, LocalDate nextOccurrence) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.recurrence_interval = (recurrenceInterval != null) ? recurrenceInterval.toLowerCase() : "daily"; // Default to "daily"
        this.nextOccurrence = nextOccurrence != null ? nextOccurrence : LocalDate.now(); // Default to today
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getRecurrenceInterval() {
        return recurrence_interval;
    }

    public LocalDate getNextOccurrence() {
        return nextOccurrence;
    }

    public void markAsCompleted() {
        switch (recurrence_interval) {
            case "daily":
                nextOccurrence = nextOccurrence.plusDays(1);
                break;
            case "weekly":
                nextOccurrence = nextOccurrence.plusWeeks(1);
                break;
            case "monthly":
                nextOccurrence = nextOccurrence.plusMonths(1);
                break;
            default:
                throw new IllegalArgumentException("Invalid recurrence interval.");
        }
    }

    @Override
    public String toString() {
        return "Task ID: " + id + "\nTask: " + title + "\nDescription: " + description +
                "\nNext Occurrence: " + nextOccurrence + "\nRecurrence: " + recurrence_interval;
    }
}

public class TaskRecurringMain {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/todolist";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Othman@05";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("Connected to the database.");
            while (true) {
                System.out.println("=== Recurring Task Manager ===");
                System.out.println("1. Add Recurring Task");
                System.out.println("2. View Tasks");
                System.out.println("3. Mark Task as Completed");
                System.out.println("4. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        addRecurringTask(scanner, conn);
                        break;
                    case 2:
                        viewTasks(conn);
                        break;
                    case 3:
                        markTaskAsCompleted(scanner, conn);
                        break;
                    case 4:
                        System.out.println("Exiting the program. Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addRecurringTask(Scanner scanner, Connection conn) throws SQLException {
        System.out.println("=== Add a Recurring Task ===");
        System.out.print("Enter task title: ");
        String title = scanner.nextLine();
        System.out.print("Enter task description: ");
        String description = scanner.nextLine();
        System.out.print("Enter recurrence interval (daily, weekly, monthly): ");
        String recurrence_interval = scanner.nextLine();

        String sql = "INSERT INTO tasks (title, description, recurrence_interval, due_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setString(3, recurrence_interval);
            pstmt.setDate(4, Date.valueOf(LocalDate.now()));
            pstmt.executeUpdate();
            System.out.println("Recurring Task \"" + title + "\" created successfully!");
        }
    }

    private static void viewTasks(Connection conn) throws SQLException {
        System.out.println("=== Current Tasks ===");
        String sql = "SELECT * FROM tasks";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (!rs.isBeforeFirst()) {
                System.out.println("No tasks available.");
                return;
            }
            while (rs.next()) {
                LocalDate nextOccurrence = null;
                if (rs.getDate("due_date") != null) {
                    nextOccurrence = rs.getDate("due_date").toLocalDate();
                }
                Task task = new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("recurrence_interval"),
                        nextOccurrence
                );
                System.out.println(task);
            }
        }
    }

    private static void markTaskAsCompleted(Scanner scanner, Connection conn) throws SQLException {
        System.out.println("=== Mark Task as Completed ===");
        viewTasks(conn);

        System.out.print("Enter the ID of the task to mark as completed: ");
        int taskId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String selectSql = "SELECT * FROM tasks WHERE id = ?";
        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setInt(1, taskId);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    LocalDate nextOccurrence = null;
                    if (rs.getDate("due_date") != null) {
                        nextOccurrence = rs.getDate("due_date").toLocalDate();
                    }
                    Task task = new Task(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getString("recurrence_interval"),
                            nextOccurrence
                    );

                    task.markAsCompleted();
                    String updateSql = "UPDATE tasks SET due_date = ? WHERE id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setDate(1, Date.valueOf(task.getNextOccurrence()));
                        updateStmt.setInt(2, taskId);
                        updateStmt.executeUpdate();
                        System.out.println("Task \"" + task.getTitle() + "\" marked as completed.");
                    }
                } else {
                    System.out.println("Task ID not found.");
                }
            }
        }
    }
}
