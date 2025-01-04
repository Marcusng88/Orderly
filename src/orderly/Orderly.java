/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package orderly;

import java.util.ArrayList;
import java.util.Scanner;

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

        mainMenu:
        while (true) { 
            // Fill tasks arraylist with database data
            tasks = todolist.readAll();
            Task manager = new Task();
            DependencyTask dependencyManager = new DependencyTask();

            // Display menu ; Prompt user for action
            int action = mainMenu();           
            input.nextLine();
            switch (action) {
                case 1 -> manager.viewAll(tasks);
                case 2 -> manager.newTask(todolist);
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
                            case 7 -> {break mgmtMenu;}
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
                case 6 -> System.out.println(); // recurrence task
                case 7 -> System.out.println(); // sort task
                case 8 -> manager.searchTask(tasks,todolist); // normal search
                case 9 -> manager.searchTasksVector(tasks,todolist); // vector search
                case 10 -> {
                    System.out.println(ANSI_RED + "Exiting Orderly..." + ANSI_RESET);
                    break mainMenu;
                }
                default -> System.out.println(ANSI_RED + "\nInvalid choice. Please Try again.\n" + ANSI_RESET);
            }
        }
        
    }
    
    private static int mainMenu(){
        System.out.println(ANSI_YELLOW + "\n=== Welcome to Orderly ===" + ANSI_RESET);
        System.out.println("What would you like to do?");
        System.out.println("1.  View Tasks");
        System.out.println("2.  Add a New Task");
        System.out.println("3.  Manage a Task");
        System.out.println("4.  Edit a Task");
        System.out.println("5.  Delete a Task");
        System.out.println("6.  Set a recurrence task");
        System.out.println("7.  Sort task");
        System.out.println("8.  Search a Task");
        System.out.println("9.  Search a Task (using similarity search)");
        System.out.println("10. Exit Program");
        System.out.print(ANSI_PURPLE + "Choose a task >> " + ANSI_YELLOW);

        return input.nextInt();
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