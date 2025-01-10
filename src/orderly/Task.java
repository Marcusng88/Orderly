package orderly;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Task {
    int taskID;
    String title,desc,status,dueDate,category,priority,vector;
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

    public Task(int taskID, String title, String desc, String status, String dueDate, String category, String priority,String vector) {
        this.taskID = taskID;
        this.title = title;
        this.desc = desc;
        this.status = status;
        this.dueDate = dueDate;
        this.category = category;
        this.priority = priority;
        this.vector = vector;
        
    }

    public void viewAll(ArrayList<Task> tasks) {
        Database todolist = new Database();
        System.out.println(ANSI_CYAN + "--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("| ID | Title                | Description                                | Status       | Due Date     | Category      | Priority   | Dependencies | Recurrence Interval |");
        System.out.println("|----|----------------------|--------------------------------------------|--------------|--------------|---------------|------------|--------------|---------------------|");

        for (Task task : tasks) {
            String[] descLines = splitField(task.desc, 52);
            String[] titleLines = splitField(task.title, 20);
            String dependencies = getDependencies(todolist, task.taskID);
            String recurrence = getRecurrence(todolist, task.taskID);

            int maxLines = Math.max(descLines.length, titleLines.length);
            for (int i = 0; i < maxLines; i++) {
                String titleLine = i < titleLines.length ? titleLines[i] : "";
                String descLine = i < descLines.length ? descLines[i] : "";

                if (i == 0) {
                    System.out.printf("| %-2d | %-20s | %-42s | %-12s | %-12s | %-13s | %-10s | %-12s | %-19s |\n",
                            task.taskID, titleLine, descLine, task.status, task.dueDate, task.category, task.priority, dependencies, recurrence);
                } else {
                    System.out.printf("| %-2s | %-20s | %-42s | %-12s | %-12s | %-13s | %-10s | %-12s | %-19s |\n",
                            "", titleLine, descLine, "", "", "", "", "", "");
                }
            }
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    }

    private String[] splitField(String desc, int maxLength) {
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

    private String getDependencies(Database todolist, int taskId) {
        String depSql = "SELECT dependency_id FROM task_dependencies WHERE task_id = ?";
        StringBuilder dependencies = new StringBuilder();
        try (PreparedStatement depStmt = todolist.db.prepareStatement(depSql)) {
            depStmt.setInt(1, taskId);
            ResultSet depRs = depStmt.executeQuery();
            while (depRs.next()) {
                dependencies.append(depRs.getInt("dependency_id")).append(" ");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return dependencies.length() > 0 ? dependencies.toString().trim() : "None";
    }

    private String getRecurrence(Database todolist, int taskId){
        String cmd = "SELECT recurrence_interval FROM tasks WHERE id = ?";
        String recurrence = "";
        try(PreparedStatement stmt = todolist.db.prepareStatement(cmd)){
            stmt.setInt(1, taskId);
            ResultSet result = stmt.executeQuery();
            if (result.next()) { 
                recurrence = result.getString("recurrence_interval");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return recurrence != null ? recurrence : "None";
    }

    //add new task
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

    // sort task
    public void sortTask(ArrayList<Task> tasks,Database db){
       
        TaskSorterMain res = new TaskSorterMain(tasks);
        
        ArrayList<Task> newDataToUpdate = res.sortTasks();
        int update = db.updateAfterSort(newDataToUpdate);
        int c = res.getChoice();
        if(update>=1){
            switch(c){
                case 1:
                    System.out.println(Orderly.ANSI_GREEN+"Tasks sorted by due date (Ascending)"+Orderly.ANSI_RESET);
                    break;
                case 2:
                    System.out.println(Orderly.ANSI_GREEN+"Tasks sorted by due date (Descending)"+Orderly.ANSI_RESET);
                    break;
                case 3:
                    System.out.println(Orderly.ANSI_GREEN+"Tasks sorted by priority (High to Low)"+Orderly.ANSI_RESET);
                    break;
                case 4:
                    System.out.println(Orderly.ANSI_GREEN+"Tasks sorted by priority (Low to high)"+Orderly.ANSI_RESET);
                    break;
            }

        }
        else{
            System.out.println(ANSI_RED+"Your tasks cannot be sorted .Please try again"+ANSI_RESET);
        }
    }

    // normal task searching
    public void searchTask(ArrayList<Task> tasks,Database db){
        ArrayList<Task> allWork = db.readAll();

        System.out.println(ANSI_GREEN+"=== Search Tasks ==="+ANSI_RESET);
        System.out.print("Enter a keyword to search by title or description: ");
        String keyword = input.nextLine();

        ArrayList<Task> res = SearchingTask.searchTasks(allWork, keyword);
        if (res.isEmpty()){
            System.out.println(Orderly.ANSI_RED+"No task found"+Orderly.ANSI_RESET);
        }
        else{
            for(Task element: res){
                Searching result =new Searching(element.title,element.dueDate,element.category,element.priority,element.status);
                String x = result.toString();
                System.out.println(x);
            }
        }


    }

    // vector search task
    public void searchTasksVector(ArrayList<Task> tasks,Database db){
        System.out.println(ANSI_YELLOW+"=== Search Tasks ==="+ANSI_RESET);
        System.out.print("Enter a keyword or phrase to search tasks: "+ANSI_RESET);
        String keyword = input.nextLine();
        
        List<String> res = VectorSearch.searchTasks(keyword);
        System.out.println(ANSI_GREEN+"=== Vector Search Results ==="+ANSI_RESET);
        for (String element: res){
            System.out.printf("%s\n",element.substring(0, element.indexOf("Recurrence")-2)); 
            
        }
    }

    //mark the task as complete
    public void markTaskComplete() {
        Database todolist = new Database();
        int target = 0;
        System.out.println(ANSI_YELLOW + "\n=== Mark Task Completion ===" + ANSI_RESET);

        while (true) { 
            try {
                System.out.print("Enter the task number you want to mark as complete: ");
                target = input.nextInt();
                input.nextLine();
                break;
            } catch (InputMismatchException e) {
                System.out.println(ANSI_RED + "Please enter a valid task ID." + ANSI_RESET);
                input.nextLine();
            }
        }
        
        try {

            // Fetch the task's dependencies
            String fetchSql = "SELECT dependency_id FROM task_dependencies WHERE task_id = ?";
            List<Integer> dependencies = new ArrayList<>();

            try (PreparedStatement fetchStmt = todolist.db.prepareStatement(fetchSql)) {
                fetchStmt.setInt(1, target);
                ResultSet rs = fetchStmt.executeQuery();

                while(rs.next()){
                    dependencies.add(rs.getInt("dependency_id"));
                }

            }

            // Check if all dependencies are complete
            for (int depId : dependencies) {
                String checkSql = "SELECT status FROM tasks WHERE id = ?";

                try (PreparedStatement checkStmt = todolist.db.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, depId);
                    ResultSet rs = checkStmt.executeQuery();
                    
                    if(rs.next()){
                        String dependencyStatus = rs.getString("status");

                        if (!"Complete".equalsIgnoreCase(dependencyStatus)) {
                            System.out.println(Orderly.ANSI_RED+"Warning: Task ID " + target + " cannot be marked as complete because it depends on Task ID " + depId + " which is not complete!"+Orderly.ANSI_RESET);
                            return;
                        }
                    } else{
                        System.out.println(Orderly.ANSI_RED+"Dependency Task ID " + depId + " not found in the tasks table."+Orderly.ANSI_RESET);
                        return;
                    }
                }
            }
            // Fetch title using target
            String fetchTitleSql = "SELECT title FROM tasks WHERE id = ?";
            String taskTitle = "";

            try (PreparedStatement fetchStmt = todolist.db.prepareStatement(fetchTitleSql)) {
                fetchStmt.setInt(1, target);
                ResultSet rs = fetchStmt.executeQuery();

                if (rs.next()) {
                    taskTitle = rs.getString("title");
                } else {
                    System.out.println("Task ID " + target + " not found.");
                    return;
                }
            }
            

            //if all dependencies are complete, mark task as complete
            String updateSql = "UPDATE tasks SET status = 'Complete' WHERE id = ?";
            try(PreparedStatement updateStmt = todolist.db.prepareStatement(updateSql)){
                updateStmt.setInt(1, target);
                updateStmt.executeUpdate();
                System.out.println(Orderly.ANSI_GREEN+"Task \"" + taskTitle + "\" marked as complete!"+Orderly.ANSI_RESET);
            }
            
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }


    public void setTitle(ArrayList<Task> tasks,Database db){
        System.out.println(ANSI_YELLOW + "\n=== Change Task Title ===" + ANSI_RESET);
        int update = 0;
        int target = 0;
        String newData;

        while (true) { 
            try {
                System.out.print("Enter the task number you want to modify: ");
                target = input.nextInt();
                input.nextLine();
                System.out.print("Enter a new title for the task: ");
                newData = input.nextLine();
                break;
            } catch (InputMismatchException e) {
                System.out.println(ANSI_RED + "Please enter a valid task ID." + ANSI_RESET);
                input.nextLine();
            }
        }
        
        for(Task task : tasks){
            if(task.taskID == target){
                update = db.updateTask(target, "title", newData);
                if(update >= 1){
                    System.out.println(ANSI_GREEN + "Task title successfully changed to \"" + newData + "\"!\n" + ANSI_RESET);
                }else{
                    System.out.println(ANSI_RED + "Failed to update task title\n" + ANSI_RESET);
                }
            }
        }
        if (update == 0) System.out.println(ANSI_RED + "This task does not exist. Please try again." + ANSI_RESET);
    }

    public void setDescription(ArrayList<Task> tasks,Database db){
        System.out.println(ANSI_YELLOW + "\n=== Change Task Description ===" + ANSI_RESET);
        int update = 0;
        int target = 0;
        String newData;

        while (true) { 
            try {
                System.out.print("Enter the task number you want to modify: ");
                target = input.nextInt();
                input.nextLine();
                System.out.print("Enter a new description for the task: ");
                newData = input.nextLine();
                break;
            } catch (InputMismatchException e) {
                System.out.println(ANSI_RED + "Please enter a valid task ID." + ANSI_RESET);
                input.nextLine();
            }
        }
        
        for(Task task : tasks){
            if(task.taskID == target){
                update = db.updateTask(target, "description", newData);
                if(update >= 1){
                    System.out.println(ANSI_GREEN + "Task description successfully changed for \"" + task.title + "\"!\n" + ANSI_RESET);
                }else{
                    System.out.println(ANSI_RED + "Failed to update task description\n" + ANSI_RESET);
                }
            }
        }
        if (update == 0) System.out.println(ANSI_RED + "This task does not exist. Please try again." + ANSI_RESET);
    }

    public void setDueDate(ArrayList<Task> tasks,Database db){
        System.out.println(ANSI_YELLOW + "\n=== Change Task Due Date ===" + ANSI_RESET);
        int update = 0;
        int target = 0;

        while (true) { 
            try {
                System.out.print("Enter the task number you want to modify: ");
                target = input.nextInt();
                input.nextLine();
                break;
            } catch (InputMismatchException e) {
                System.out.println(ANSI_RED + "Please enter a valid task ID." + ANSI_RESET);
                input.nextLine();
            }
        }
        
        while (true) { 
            System.out.print("Enter a new due date for the task: ");
            String newData = input.nextLine();
            if(isValidDate(newData)){
                for(Task task : tasks){
                    if(task.taskID == target){
                        update = db.updateTask(target, "due_date", newData);
                        if(update >= 1){
                            System.out.println(ANSI_GREEN + "Task due date successfully changed for \"" + task.title + "\"!\n" + ANSI_RESET);
                        }else{
                            System.out.println(ANSI_RED + "Failed to update task due date\n" + ANSI_RESET);
                        }
                    }
                }
                break;
            }else{
                System.out.println(ANSI_RED + "Invalid date. Please try again." + ANSI_RESET);
            }
        }
        if (update == 0) System.out.println(ANSI_RED + "This task does not exist. Please try again." + ANSI_RESET);
    }

    public void setCategory(ArrayList<Task> tasks,Database db){
        System.out.println(ANSI_YELLOW + "\n=== Change Task Category ===" + ANSI_RESET);
        int update = 0;
        int target = 0;
        String newData;

        while (true) { 
            try {
                System.out.print("Enter the task number you want to modify: ");
                target = input.nextInt();
                input.nextLine();
                System.out.print("Enter a new category for the task: ");
                newData = input.nextLine();
                break;
            } catch (InputMismatchException e) {
                System.out.println(ANSI_RED + "Please enter a valid task ID." + ANSI_RESET);
                input.nextLine();
            }
        }
        
        for(Task task : tasks){
            if(task.taskID == target){
                update = db.updateTask(target, "category", newData);
                if(update >= 1){
                    System.out.println(ANSI_GREEN + "Task category successfully changed for \"" + task.title + "\"!\n" + ANSI_RESET);
                }else{
                    System.out.println(ANSI_RED + "Failed to update task category\n" + ANSI_RESET);
                }
            }
        }
        if (update == 0) System.out.println(ANSI_RED + "This task does not exist. Please try again." + ANSI_RESET);
    }

    public void setPriority(ArrayList<Task> tasks,Database db){
        System.out.println(ANSI_YELLOW + "\n=== Change Task Priority ===" + ANSI_RESET);
        int update = 0;
        int target = 0;
        String newData;

        while (true) { 
            try {
                System.out.print("Enter the task number you want to modify: ");
                target = input.nextInt();
                input.nextLine();
                System.out.print("Enter a new priority for the task: ");
                newData = input.nextLine();
                break;
            } catch (Exception e) {
                System.out.println(ANSI_RED + "Please enter a valid task ID." + ANSI_RESET);
                input.nextLine();
            }
        }
        
        for(Task task : tasks){
            if(task.taskID == target){
                update = db.updateTask(target, "priority", newData);
                if(update >= 1){
                    System.out.println(ANSI_GREEN + "Task priority successfully changed for \"" + task.title + "\"!\n" + ANSI_RESET);
                }else{
                    System.out.println(ANSI_RED + "Failed to update task priority\n" + ANSI_RESET);
                }
            }
        }
        if (update == 0) System.out.println(ANSI_RED + "This task does not exist. Please try again." + ANSI_RESET);
    }

    // task deletion
    public void deleteTask(ArrayList<Task> tasks,Database db){
        System.out.println(ANSI_YELLOW + "\n=== Delete Task ===" + ANSI_RESET);
        int update = 0;
        int target = 0;

        while (true) { 
            try {
                System.out.print("Enter the task number you want to delete: ");
                target = input.nextInt();
                input.nextLine();
                break;
            } catch (InputMismatchException e) {
                System.out.println(ANSI_RED + "Please enter a valid task ID." + ANSI_RESET);
                input.nextLine();
            } 
        }

        for(Task task : tasks){
            if(task.taskID == target){
                update = db.deleteTask(target);
                if(update >= 1){
                    System.out.println(ANSI_GREEN + "Task \"" + task.title + "\" was deleted successfully!\n" + ANSI_RESET);
                }else{
                    System.out.println(ANSI_RED + "Failed to delete task\n" + ANSI_RESET);
                }
            } 
        }

        if (update == 0) System.out.println(ANSI_RED + "This task does not exist. Please try again." + ANSI_RESET);
    }
}