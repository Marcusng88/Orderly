package orderly;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class TaskSorter {
    private String nameTask;
    private Date dueDate;
    private int priorityTask;

    public TaskSorter(String nameTask, Date dueDate, int priorityTask) {
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
    public static void dateSortingAlgorithm(List<TaskSorter> tasks, boolean ascending) {
        tasks.sort((t1, t2) -> ascending ? t1.getDueDate().compareTo(t2.getDueDate())
                                         : t2.getDueDate().compareTo(t1.getDueDate()));
    }

    public static void prioritySortingAlgorithm(List<TaskSorter> tasks, boolean highToLow) {
        tasks.sort((t1, t2) -> highToLow ? Integer.compare(t2.getPriorityTask(), t1.getPriorityTask())
                                         : Integer.compare(t1.getPriorityTask(), t2.getPriorityTask()));
    }
}

// Main method to test the functionality of the program
 public class TaskSorterMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<TaskSorter> tasks = new ArrayList<>();

        System.out.print("Enter the number of tasks: ");
        int numOfTasks = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < numOfTasks; i++) {
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

            tasks.add(new TaskSorter(nameTask, dueDate, priorityTask));
        }

        while (true) {
            System.out.println("\n=== Sort Tasks ===");
            System.out.println("1. Ascending Order (Due Date)");
            System.out.println("2. Descending Order (Due Date)");
            System.out.println("3. Priority (High to Low)");
            System.out.println("4. Priority (Low to High)");
            System.out.println("5. Exit");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    TaskSorting.dateSortingAlgorithm(tasks, true);
                    System.out.println("\nAfter Sorting (Ascending by Due Date):");
                    tasks.forEach(System.out::println);
                    break;
                case 2:
                    TaskSorting.dateSortingAlgorithm(tasks, false);
                    System.out.println("\nAfter Sorting (Descending by Due Date):");
                    tasks.forEach(System.out::println);
                    break;
                case 3:
                    TaskSorting.prioritySortingAlgorithm(tasks, true);
                    System.out.println("\nAfter Sorting (High to Low by Priority):");
                    tasks.forEach(System.out::println);
                    break;
                case 4:
                    TaskSorting.prioritySortingAlgorithm(tasks, false);
                    System.out.println("\nAfter Sorting (Low to High by Priority):");
                    tasks.forEach(System.out::println);
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
}
