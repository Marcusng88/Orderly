package orderly;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;

class Task {
    private String nameTask;
    private Date dueDate;

    public Task(String nameTask, Date dueDate) {
        this.nameTask = nameTask;
        this.dueDate = dueDate;
    }

    public String getNameTask() {
        return nameTask;
    }

    public Date getDueDate() {
        return dueDate;
    }

    @Override
    public String toString() {
        return "Task" + " name = '" + nameTask + '\'' + "\ndue Date = " +dueDate ;
    }
}

class TaskSorting {
    public static void DateSortingAlgorithm(List<Task> tasks, boolean ascending) {
        int N = tasks.size();

        for (int i = 0; i < N - 1; i++) { 
            for (int j = 0; j < N - i - 1; j++) { 
                if (ascending
                        ? tasks.get(j).getDueDate().after(tasks.get(j + 1).getDueDate()) // ascending order by due date
                        : tasks.get(j).getDueDate().before(tasks.get(j + 1).getDueDate())) { // descending order by due date 
                    Task temp = tasks.get(j);
                    tasks.set(j, tasks.get(j + 1));
                    tasks.set(j + 1, temp);
                }
            }
        }
    }
}

public class TaskSorterMain {
   public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<Task> tasks = new ArrayList<>();

        System.out.print("Enter the number of tasks: ");
        int NumofTasks = scanner.nextInt();
        scanner.nextLine(); // consume newline so that it doesnt take the empty string as the input when we press Enter

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
                i--; // retry the input date format
                continue;
            }

            tasks.add(new Task(nameTask, dueDate));
        }

        while (true) {
            System.out.println("\n===Sort Tasks===");
            System.out.println("\nSort tasks by:");
            System.out.println("1. Ascending Order (Due Date)");
            System.out.println("2. Descending Order (Due Date)");
            System.out.println("3. Exit");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    TaskSorting.DateSortingAlgorithm(tasks, true);
                    System.out.println("\nAfter Sorting (Ascending):");
                    for (Task task : tasks) {
                        System.out.println(task);
                    }
                    break;
                case 2:
                    TaskSorting.DateSortingAlgorithm(tasks, false);
                    System.out.println("\nAfter Sorting (Descending):");
                    for (Task task : tasks) {
                        System.out.println(task);
                    }
                    break;
                case 3:
                    System.out.println("Exiting the program !");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
    }
}