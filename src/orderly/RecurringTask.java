package orderly;

import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class RecurringTask extends Task{
    String recurrence;
    LocalDate nextOccurrence;

    public RecurringTask(){}

    public RecurringTask(int taskID, String title, String desc, String status, String dueDate, String category, String priority, String recurrence, String vector){
        super(taskID,title,desc,status,dueDate,category,priority,vector);
        this.recurrence = recurrence;
    }

    public void addRecurringTask(Scanner scanner, Database todolist) throws SQLException {
        System.out.println("=== Add a Recurring Task ===");
        System.out.print("Enter task title: ");
        String taskTitle = scanner.nextLine();
        System.out.print("Enter task description: ");
        String description = scanner.nextLine();
        System.out.print("Enter recurrence interval (Daily, Weekly, Monthly): ");
        String recurrence_interval = scanner.nextLine();

        todolist.insertRecurring(taskTitle, description, LocalDate.now().toString(), "Recurring", "None", recurrence_interval);
        System.out.println("Recurring Task \"" + taskTitle + "\" created successfully!");        
    }

    public void markTaskAsCompleted(Scanner scanner, Database todolist) throws SQLException {
        System.out.println("=== Mark Task as Completed ===");
        System.out.print("Enter the ID of the task to mark as completed: ");
        int taskId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String selectSql = "SELECT * FROM tasks WHERE id = ?";
        try (PreparedStatement selectStmt = todolist.db.prepareStatement(selectSql)) {
            selectStmt.setInt(1, taskId);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    nextOccurrence = null;
                    if (rs.getDate("due_date") != null) {
                        nextOccurrence = rs.getDate("due_date").toLocalDate();
                    }
                    
                    if (rs.getString("recurrence_interval") != null && nextOccurrence != null) {
                        recurrence = rs.getString("recurrence_interval");
                        switch (recurrence.toLowerCase()) {
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

                        String updateSql = "UPDATE tasks SET due_date = ? WHERE id = ?";
                        try (PreparedStatement updateStmt = todolist.db.prepareStatement(updateSql)) {
                            updateStmt.setDate(1, Date.valueOf(nextOccurrence));
                            updateStmt.setInt(2, taskId);
                            updateStmt.executeUpdate();
                            System.out.println("Task marked as completed and due date updated to " + nextOccurrence);
                        }
                    } else {
                        System.out.println("Recurrence interval or due date is null. Cannot update the task.");
                    }
                } else {
                    System.out.println("Task with ID " + taskId + " not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}