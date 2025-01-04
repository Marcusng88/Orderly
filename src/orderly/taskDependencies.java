package orderly;
import java.sql.*;
import java.util.*;

public class taskDependencies {
    private Connection connection;
    
    public taskDependencies(){
        //to get the sql database
        try{
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/todolist", "root", "12345");
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("Error connecting to the database.");
        }
    }

    //method for cycle detection 
    private boolean hasCycle(int taskId, Set<Integer> visited, Set<Integer> recursionStack){

        //if task already in the current recurssion stack, a cycle is detected 
        if (recursionStack.contains(taskId)){
            return true; 
        }
        //if task is already visited and not in the current recurrsion stack, skip it
        if(visited.contains(taskId)){
            return false; 
        }

        //mark the task as visited and add to the recursion stack 
        visited.add(taskId);
        recursionStack.add(taskId);

        //get dependencies for the current task 
        String fetchSql = "SELECT dependency_id FROM task_dependencies WHERE task_id = ?";
        try(PreparedStatement fetchStmt = connection.prepareStatement(fetchSql)){
            fetchStmt.setInt(1, taskId);
            ResultSet rs = fetchStmt.executeQuery();

            //check each dependencies
            while (rs.next()) {
                int depId = rs.getInt("dependency_id");
                if(hasCycle(taskId, visited, recursionStack)){
                    return true;
                }
                
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        //remove task from recursion stack 
        recursionStack.remove(taskId);
        return false; 
    }

    //set dependencies to the task
    public void setDependencies(int dependentId, int dependencyId){     
        try{

            //check if adding this dependency creates a cycle
            Set<Integer> visited = new HashSet<>();
            Set<Integer> recursionStack = new HashSet<>();

            if (hasCycle(dependencyId, visited, recursionStack)){
                System.out.println("Error: This dependency will create a cycle!");
                return;
            }


            //fetch current dependent from database (no need to fetch dependencies here)
            String fetchsql = "SELECT title FROM tasks WHERE id = ?";
            String dependentTitle = ""; 

            try(PreparedStatement fetchStmt = connection.prepareStatement(fetchsql)){
                fetchStmt.setInt(1, dependentId);
                ResultSet rs = fetchStmt.executeQuery();

                if(rs.next()){
                    dependentTitle = rs.getString("title");
                }else {
                    System.out.println("Dependent task not found.");
                    return;
                }

            }

            //fetch the dependency tasks's title
            String fetchDependencyTitleSql = "SELECT title FROM tasks WHERE id = ?";
            String dependencyTitle = "";

            try(PreparedStatement fetchDependencyStmt = connection.prepareStatement(fetchDependencyTitleSql)){
                fetchDependencyStmt.setInt(1, dependencyId);
                ResultSet rs = fetchDependencyStmt.executeQuery();

                if(rs.next()){
                    dependencyTitle = rs.getString("title");
                }else{
                    System.out.println("Dependency task not found.");
                    return;
                }
            }

            //insert the dependencies in the task_dependencies
            String updateSql = "INSERT INTO task_dependencies (task_id, dependency_id) VALUES (?, ?)";
            try(PreparedStatement insertStmt = connection.prepareStatement(updateSql)){
                insertStmt.setInt(1, dependentId);
                insertStmt.setInt(2, dependencyId);
                insertStmt.executeUpdate();
            }

            //display relationship 
            System.out.println("Task '"+ dependentTitle +"' now depends on '"+ dependencyTitle +"' (ID: "+ dependencyId +").");
            
        }catch(SQLException e){
            e.printStackTrace();
        }        
    }

    //mark the task as complete
    public void markTaskComplete(int taskId) {
        try {

            // Fetch the task's dependencies
            String fetchSql = "SELECT dependency_id FROM task_dependencies WHERE task_id = ?";
            List<Integer> dependencies = new ArrayList<>();

            try (PreparedStatement fetchStmt = connection.prepareStatement(fetchSql)) {
                fetchStmt.setInt(1, taskId);
                ResultSet rs = fetchStmt.executeQuery();

                while(rs.next()){
                    dependencies.add(rs.getInt("dependency_id"));
                }

            }

            // Check if all dependencies are complete
            for (int depId : dependencies) {
                String checkSql = "SELECT status FROM tasks WHERE id = ?";

                try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, depId);
                    ResultSet rs = checkStmt.executeQuery();
                    
                    if(rs.next()){
                        String dependencyStatus = rs.getString("status");

                        if (!"Complete".equalsIgnoreCase(dependencyStatus)) {
                            System.out.println("Warning: Task ID " + taskId + " cannot be marked as complete because it depends on Task ID " + depId + " which is not complete!");
                            return;
                        }
                    } else{
                        System.out.println("Dependency Task ID " + depId + " not found in the tasks table.");
                        return;
                    }
                }
            }
            // Fetch title using taskId
            String fetchTitleSql = "SELECT title FROM tasks WHERE id = ?";
            String taskTitle = "";

            try (PreparedStatement fetchStmt = connection.prepareStatement(fetchTitleSql)) {
                fetchStmt.setInt(1, taskId);
                ResultSet rs = fetchStmt.executeQuery();

                if (rs.next()) {
                    taskTitle = rs.getString("title");
                } else {
                    System.out.println("Task ID " + taskId + " not found.");
                    return;
                }
            }
            

            //if all dependencies are complete, mark task as complete
            String updateSql = "UPDATE tasks SET status = 'Complete' WHERE id = ?";
            try(PreparedStatement updateStmt = connection.prepareStatement(updateSql)){
                updateStmt.setInt(1, taskId);
                updateStmt.executeUpdate();
                System.out.println("Task \"" + taskTitle + "\" marked as complete!");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //display all tasks
    public void displayTasks() {
        String sql = "SELECT * FROM tasks";
        try(Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)){
            System.out.println("");
            System.out.println("=== View All Tasks ===");
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String status = rs.getString("status");


                //fetch dependencies from task_dependencies
                String depSql = "SELECT dependency_id FROM task_dependencies WHERE task_id = ?";
                try(PreparedStatement depStmt = connection.prepareStatement(depSql)){
                    depStmt.setInt(1,id);
                    ResultSet depRs = depStmt.executeQuery();
                    StringBuilder dependencies = new StringBuilder();
                    while (depRs.next()){
                        dependencies.append(depRs.getInt("dependency_id")).append(" ");
                    }
                    System.out.println(id +". ["+ status +"] "+ title + (dependencies.length()> 0 ? "(Depends on: "+ dependencies +")" : ""));
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        taskDependencies manager = new taskDependencies();

        while(true){
            System.out.println("");
            System.out.print("""
                === To-Do List Menu ===
                1. Create a Task
                2. Set Task Dependency
                3. Mark Task As Complete
                4. View All Tasks
                5. Exit
                
                """);
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch(choice){
                case 1: 
                    System.out.print("Enter task title: ");
                    String title = scanner.nextLine();
                    manager.createTask(title);
                    break;
                case 2:
                    System.out.print("Enter the dependent ID: ");
                    int dependentId = scanner.nextInt();
                    System.out.print("Enter the task ID it depends on: ");
                    int dependencyId = scanner.nextInt();
                    manager.setDependencies(dependentId, dependencyId);
                    break; 
                case 3:
                    System.out.print("Enter the task ID you want to mark as complete: ");
                    int taskId = scanner.nextInt();
                    manager.markTaskComplete(taskId);
                    break;
                case 4: 
                    manager.displayTasks();
                    break; 
                case 5: 
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Try Again.");


            }

        }
        
    }


}