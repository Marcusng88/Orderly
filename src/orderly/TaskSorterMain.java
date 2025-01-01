package orderly;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

class Task {
    private String nameTask;
    private Date dueDate;
    private int priorityTask;

    public Task(String nameTask, Date dueDate, int priorityTask) {
        this.nameTask = nameTask;
        this.dueDate = dueDate;
        this.priorityTask = priorityTask;
    }

    public String getNameTask() {
        return nameTask;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public int getPriorityTask() {
        return priorityTask;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return "\n===Task Details===\n" +
                "Name: " + nameTask + "\n" +
                "Due Date: " + sdf.format(dueDate) + "\n" +
                "Priority: " + priorityTask;
    }
}

class TaskSorting {
    public static void DateSortingAlgorithm(List<Task> tasks, boolean ascending) {
        int N = tasks.size();

        for (int i = 0; i < N - 1; i++) {
            for (int j = 0; j < N - i - 1; j++) {
                if (ascending
                        ? tasks.get(j).getDueDate().after(tasks.get(j + 1).getDueDate())
                        : tasks.get(j).getDueDate().before(tasks.get(j + 1).getDueDate())) {
                    Task temp = tasks.get(j);
                    tasks.set(j, tasks.get(j + 1));
                    tasks.set(j + 1, temp);
                   }
                 }
               }
            }

    public static void prioritySortingAlgorithm(List<Task> tasks, boolean highToLow) {
        int N = tasks.size();
        for (int i = 0; i < N - 1; i++) {
            for (int j = 0; j < N - i - 1; j++) {
                if (highToLow
                        ? tasks.get(j).getPriorityTask() < tasks.get(j + 1).getPriorityTask()
                        : tasks.get(j).getPriorityTask() > tasks.get(j + 1).getPriorityTask()) {
                    Task temp = tasks.get(j);
                    tasks.set(j, tasks.get(j + 1));
                    tasks.set(j + 1, temp);
                             }   
                          }
                       }
                    } 
                 }
 //Main method to test the functionality of program
/*
public class TaskSorterMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<Task> tasks = new ArrayList<>();

        System.out.print("Enter the number of tasks: ");
        int NumofTasks = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < NumofTasks; i++) {
            System.out.print("Enter task name: ");
            String nameTask = scanner.nextLine();

            System.out.print("Enter due date (yyyy-MM-dd): ");
            String dateInput = scanner.nextLine();
            Date dueDate;
            try {
                dueDate = sdf.parse(dateInput);
                 } catch (ParseException e) {
                System.out.println("Invalid date format. Please try again.");
                i--;
                  continue;
             }

            System.out.print("Enter priority (1 to 10, where 10 is the highest priority): ");
            int priorityTask = scanner.nextInt();
            scanner.nextLine();

            tasks.add(new Task(nameTask, dueDate, priorityTask));
        }

        while (true) {
            System.out.println("\n===Sort Tasks===");
            System.out.println("1. Ascending Order (Due Date)");
            System.out.println("2. Descending Order (Due Date)");
            System.out.println("3. Priority (High to Low)");
            System.out.println("4. Priority (Low to High)");
            System.out.println("5. Exit");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    TaskSorting.DateSortingAlgorithm(tasks, true);
                    System.out.println("\nAfter Sorting (Ascending by Due Date):");
                    for (Task task : tasks) {
                        System.out.println(task);
                    }
                    break;
                case 2:
                    TaskSorting.DateSortingAlgorithm(tasks, false);
                    System.out.println("\nAfter Sorting (Descending by Due Date):");
                    for (Task task : tasks) {
                        System.out.println(task);
                    }
                    break;
                case 3:
                    TaskSorting.prioritySortingAlgorithm(tasks, true);
                    System.out.println("\nAfter Sorting (High to Low by Priority):");
                    for (Task task : tasks) {
                        System.out.println(task);
                    }
                    break;
                case 4:
                    TaskSorting.prioritySortingAlgorithm(tasks, false);
                    System.out.println("\nAfter Sorting (Low to High by Priority):");
                    for (Task task : tasks) {
                        System.out.println(task);
                    }
                    break;
                case 5:
                    System.out.println("Exiting the program!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 5.");
                }
          }
    } 
 } */
