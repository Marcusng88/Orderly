package orderly;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DependencyTask extends Task{
    List<Integer> dependencies;
    String dependency;
    Database todolist = new Database();

    public DependencyTask(){}

    public DependencyTask(int taskID, String title, String desc, String status, String dueDate, String category, String priority, String dependency){
        super(taskID,title,desc,status,dueDate,category,priority);
        this.dependency = dependency;
    }

    public DependencyTask(Task task){
        super(task.taskID,task.title,task.desc,task.status,task.dueDate,task.category,task.priority);
    }



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
        try(PreparedStatement fetchStmt = todolist.db.prepareStatement(fetchSql)){
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
            System.out.println("SQL Error: " + e.getMessage());
        }

        //remove task from recursion stack 
        recursionStack.remove(taskId);
        return false; 
    }

    public void setDependencies(){    
        System.out.println(ANSI_YELLOW + "\n=== Change Task Title ===" + ANSI_RESET);
        System.out.print("Enter the task number you want to modify: ");
        int dependentId = input.nextInt();
        input.nextLine();
        System.out.print("Enter a new title for the task: ");
        int dependencyId = input.nextInt();
        
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
            String dependentTitle; 

            try(PreparedStatement fetchStmt = todolist.db.prepareStatement(fetchsql)){
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
            String dependencyTitle;

            try(PreparedStatement fetchDependencyStmt = todolist.db.prepareStatement(fetchDependencyTitleSql)){
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
            try(PreparedStatement insertStmt = todolist.db.prepareStatement(updateSql)){
                insertStmt.setInt(1, dependentId);
                insertStmt.setInt(2, dependencyId);
                insertStmt.executeUpdate();
            }

            //display relationship 
            System.out.println("Task '"+ dependentTitle +"' now depends on '"+ dependencyTitle +"' (ID: "+ dependencyId +").");
            
        }catch(SQLException e){
            System.out.println("SQL Error: " + e.getMessage());
        }        
    }

    
}
