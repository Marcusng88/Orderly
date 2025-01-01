/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
  Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package orderly;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Task {
    private String title;
    private String description;
    private String recurrenceInterval;
    private LocalDate nextOccurrence;

    public Task(String title, String description, String recurrenceInterval) {
        this.title = title;
        this.description = description;
        this.recurrenceInterval = recurrenceInterval;
        this.nextOccurrence = LocalDate.now();
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getRecurrenceInterval() {
        return recurrenceInterval;
    }

    public LocalDate getNextOccurrence() {
        return nextOccurrence;
    }

    public void markAsCompleted() {
        switch (recurrenceInterval.toLowerCase()) {
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
                System.out.println("Invalid recurrence interval.");
        }
    }

    @Override
    public String toString() {
        return "Task: " + title + "\nDescription: " + description + "\nNext Occurrence: " + nextOccurrence;
    }
}

public class TaskRecurringMain {
    private static List<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

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
                    addRecurringTask(scanner);
                    break;
                case 2:
                    viewTasks();
                    break;
                case 3:
                    markTaskAsCompleted(scanner);
                    break;
                case 4:
                    System.out.println("Exiting the program. Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void addRecurringTask(Scanner scanner) {
        System.out.println("=== Add a Recurring Task ===");
        System.out.print("Enter task title: ");
        String title = scanner.nextLine();
        System.out.print("Enter task description: ");
        String description = scanner.nextLine();
        System.out.print("Enter recurrence interval (daily, weekly, monthly): ");
        String recurrenceInterval = scanner.nextLine();

        Task newTask = new Task(title, description, recurrenceInterval);
        tasks.add(newTask);
        System.out.println("Recurring Task \"" + title + "\" created successfully!");
    }

    private static void viewTasks() {
        System.out.println("=== Current Tasks ===");
        if (tasks.isEmpty()) {
            System.out.println("No tasks available.");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + ". " + tasks.get(i));
            }
        }
    }

    private static void markTaskAsCompleted(Scanner scanner) {
    System.out.println("=== Mark Task as Completed ===");
    if (tasks.isEmpty()) {
        System.out.println("No tasks available to mark as completed.");
        return;
    }

    viewTasks();
    System.out.print("Enter the number of the task to mark as completed: ");
    int taskNumber = scanner.nextInt();
    scanner.nextLine(); // Consume newline

    if (taskNumber < 1 || taskNumber > tasks.size()) {
        System.out.println("Invalid task number.");
    } else {
        Task completedTask = tasks.get(taskNumber - 1);

     //create a new task for the next recurrence
        String title = completedTask.getTitle();
        String description = completedTask.getDescription();
        String recurrenceInterval = completedTask.getRecurrenceInterval();

       //update the next occurrence based on the recurrence interval
        completedTask.markAsCompleted();
        LocalDate nextOccurrence = completedTask.getNextOccurrence();

        // Add the new task to the list
        Task newTask = new Task(title, description, recurrenceInterval);
        tasks.add(newTask);

        System.out.println("Task \"" + title + "\" marked as completed. New task created for next occurrence: " + nextOccurrence);
    }
  }

}

