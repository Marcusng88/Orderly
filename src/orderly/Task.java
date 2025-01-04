package orderly;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class Task {
    int taskID;
    String title,desc,status,dueDate,category,priority;
    public Scanner input = new Scanner(System.in);
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public Task(){}

    public Task(int taskID, String title, String desc, String status, String dueDate, String category, String priority) {
        this.taskID = taskID;
        this.title = title;
        this.desc = desc;
        this.status = status;
        this.dueDate = dueDate;
        this.category = category;
        this.priority = priority;
    }

    public void viewAll(ArrayList<Task> tasks){
        System.out.println(ANSI_CYAN + "----------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("| Task ID | Title                | Description                                          | Status       | Due Date     | Category      | Priority   |");
        System.out.println("|---------|----------------------|------------------------------------------------------|--------------|--------------|---------------|------------|");
        for(Task task : tasks){
            String[] descLines = splitDescription(task.desc, 52);
            for (int i = 0; i < descLines.length; i++) {
                if (i == 0) {
                    System.out.printf("| %-7d | %-20s | %-52s | %-12s | %-12s | %-13s | %-10s |\n",
                            task.taskID, task.title, descLines[i], task.status, task.dueDate, task.category, task.priority);
                } else {
                    System.out.printf("| %-7s | %-20s | %-52s | %-12s | %-12s | %-13s | %-10s |\n",
                            "", "", descLines[i], "", "", "", "");
                }
            }
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------\n");
    }

    private String[] splitDescription(String desc, int maxLength) {
        ArrayList<String> lines = new ArrayList<>();
        while (desc.length() > maxLength) {
            int splitIndex = desc.lastIndexOf(' ', maxLength);
            if (splitIndex == -1) {
                splitIndex = maxLength;
            }
            lines.add(desc.substring(0, splitIndex));
            desc = desc.substring(splitIndex).trim();
        }
        lines.add(desc);
        return lines.toArray(new String[0]);
    }

    public void newTask(Database db){
        System.out.println(ANSI_CYAN + "\n=== Add a New Task ===" + ANSI_RESET);
        System.out.print("Enter Task Title                              : ");
        title = input.nextLine();
        System.out.print("Enter Task Description                        : ");
        desc = input.nextLine();

        while (true) { 
            System.out.print("Enter Due Date (YYYY-MM-DD)                   : ");
            dueDate = input.nextLine();
            if(isValidDate(dueDate)){
                break;
            }else{
                System.out.println(ANSI_RED + "Invalid date. Please try again." + ANSI_RESET);
            }
        }
        
        System.out.print("Enter task category (Homework, Personal, Work): ");
        category = input.nextLine();
        System.out.print("Priority level (Low, Medium, High)            : ");
        priority = input.nextLine();

        int update = db.insertTask(title,desc,dueDate,category,priority);
        if(update >= 1){
            System.out.println(ANSI_GREEN + "Your task has been added!" + ANSI_RESET);
        }else{
            System.out.println(ANSI_RED + "Your task could not be added. Please try again." + ANSI_RESET);
        }
    }

    private static boolean isValidDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate dueDate = LocalDate.parse(dateStr, formatter);
            LocalDate today = LocalDate.now();
            return dueDate.isAfter(today);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public void setCompletion(ArrayList<Task> tasks,Database db){
        System.out.println(ANSI_YELLOW + "\n=== Mark Task Completion ===" + ANSI_RESET);
        System.out.print("Enter the task number you want to mark as complete: ");
        int target = input.nextInt();
        input.nextLine();
        
        for(Task task : tasks){
            if(task.taskID == target){
                int update = db.updateTask(target, "status", "Complete");
                if(update >= 1){
                    System.out.println(ANSI_GREEN + "Task \"" + task.title + "\" marked as complete!\n" + ANSI_RESET);
                }else{
                    System.out.println(ANSI_RED + "Failed to update task completion\n" + ANSI_RESET);
                }
            }
        }
    }

    public void setTitle(ArrayList<Task> tasks,Database db){
        System.out.println(ANSI_YELLOW + "\n=== Change Task Title ===" + ANSI_RESET);
        System.out.print("Enter the task number you want to modify: ");
        int target = input.nextInt();
        input.nextLine();
        System.out.print("Enter a new title for the task: ");
        String newData = input.nextLine();
        
        for(Task task : tasks){
            if(task.taskID == target){
                int update = db.updateTask(target, "title", newData);
                if(update >= 1){
                    System.out.println(ANSI_GREEN + "Task title successfully changed to \"" + newData + "\"!\n" + ANSI_RESET);
                }else{
                    System.out.println(ANSI_RED + "Failed to update task title\n" + ANSI_RESET);
                }
            }
        }
    }

    public void setDescription(ArrayList<Task> tasks,Database db){
        System.out.println(ANSI_YELLOW + "\n=== Change Task Description ===" + ANSI_RESET);
        System.out.print("Enter the task number you want to modify: ");
        int target = input.nextInt();
        input.nextLine();
        System.out.print("Enter a new description for the task: ");
        String newData = input.nextLine();
        
        for(Task task : tasks){
            if(task.taskID == target){
                int update = db.updateTask(target, "description", newData);
                if(update >= 1){
                    System.out.println(ANSI_GREEN + "Task description successfully changed for \"" + task.title + "\"!\n" + ANSI_RESET);
                }else{
                    System.out.println(ANSI_RED + "Failed to update task description\n" + ANSI_RESET);
                }
            }
        }
    }

    public void setDueDate(ArrayList<Task> tasks,Database db){
        System.out.println(ANSI_YELLOW + "\n=== Change Task Due Date ===" + ANSI_RESET);
        System.out.print("Enter the task number you want to modify: ");
        int target = input.nextInt();
        input.nextLine();
        
        while (true) { 
            System.out.print("Enter a new due date for the task: ");
            String newData = input.nextLine();
            if(isValidDate(newData)){
                for(Task task : tasks){
                    if(task.taskID == target){
                        int update = db.updateTask(target, "due_date", newData);
                        if(update >= 1){
                            System.out.println(ANSI_GREEN + "Task description successfully changed for \"" + task.title + "\"!\n" + ANSI_RESET);
                        }else{
                            System.out.println(ANSI_RED + "Failed to update task description\n" + ANSI_RESET);
                        }
                    }
                }
                break;
            }else{
                System.out.println(ANSI_RED + "Invalid date. Please try again." + ANSI_RESET);
            }
        }
    }

    public void setCategory(ArrayList<Task> tasks,Database db){
        System.out.println(ANSI_YELLOW + "\n=== Change Task Category ===" + ANSI_RESET);
        System.out.print("Enter the task number you want to modify: ");
        int target = input.nextInt();
        input.nextLine();
        System.out.print("Enter a new category for the task: ");
        String newData = input.nextLine();
        
        for(Task task : tasks){
            if(task.taskID == target){
                int update = db.updateTask(target, "category", newData);
                if(update >= 1){
                    System.out.println(ANSI_GREEN + "Task category successfully changed for \"" + task.title + "\"!\n" + ANSI_RESET);
                }else{
                    System.out.println(ANSI_RED + "Failed to update task category\n" + ANSI_RESET);
                }
            }
        }
    }

    public void setPriority(ArrayList<Task> tasks,Database db){
        System.out.println(ANSI_YELLOW + "\n=== Change Task Priority ===" + ANSI_RESET);
        System.out.print("Enter the task number you want to modify: ");
        int target = input.nextInt();
        input.nextLine();
        System.out.print("Enter a new priority for the task: ");
        String newData = input.nextLine();
        
        for(Task task : tasks){
            if(task.taskID == target){
                int update = db.updateTask(target, "priority", newData);
                if(update >= 1){
                    System.out.println(ANSI_GREEN + "Task priority successfully changed for \"" + task.title + "\"!\n" + ANSI_RESET);
                }else{
                    System.out.println(ANSI_RED + "Failed to update task priority\n" + ANSI_RESET);
                }
            }
        }
    }

    public void deleteTask(ArrayList<Task> tasks,Database db){
        System.out.println(ANSI_YELLOW + "\n=== Delete Task ===" + ANSI_RESET);
        System.out.print("Enter the task number you want to delete: ");
        int target = input.nextInt();
        input.nextLine();

        for(Task task : tasks){
            if(task.taskID == target){
                int update = db.deleteTask(target);
                if(update >= 1){
                    System.out.println(ANSI_GREEN + "Task \"" + task.title + "\" was deleted successfully!\n" + ANSI_RESET);
                }else{
                    System.out.println(ANSI_RED + "Failed to delete task\n" + ANSI_RESET);
                }
            }
        }
    }
}