package orderly;

import java.sql.*;
import java.util.ArrayList;

import com.google.gson.Gson;


public class Database {
    public Connection db;

    public Database(){
        try {
            db = DriverManager.getConnection("jdbc:mysql://localhost:3306/todolist","root","755788");
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    public ArrayList<Task> readAll(){
        ArrayList<Task> tasks = new ArrayList<>();

        try {
            Statement stmt = db.createStatement();
            ResultSet result = stmt.executeQuery("select * from tasks");

            while(result.next()){
                if(result.getString("recurrence_interval") != null){
                    RecurringTask task = new RecurringTask(
                        result.getInt("id"),
                        result.getString("title"),
                        result.getString("description"),
                        result.getString("status"),
                        result.getString("due_date"),
                        result.getString("category"),
                        result.getString("priority"),
                        result.getString("recurrence_interval"),
                        result.getString("vector"));
                        
                    tasks.add(task);
                }
                else if(result.getString("dependencies") != null){
                    DependencyTask task = new DependencyTask(
                            result.getInt("id"),
                            result.getString("title"),
                            result.getString("description"),
                            result.getString("status"),
                            result.getString("due_date"),
                            result.getString("category"),
                            result.getString("priority"),
                            result.getString("dependencies"),
                            result.getString("vector"));

                    tasks.add(task);

                }else{
                    Task task = new Task(
                            result.getInt("id"),
                            result.getString("title"),
                            result.getString("description"),
                            result.getString("status"),
                            result.getString("due_date"),
                            result.getString("category"),
                            result.getString("priority"),
                            result.getString("vector"));

                    tasks.add(task);
                }
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }

        return tasks;
    }

    public int insertTask(String title,String desc,String dueDate,String category,String priority){
        try{
            
            // combine title and description to a sentence and use it to generate vector embedding
            String sentence = title + " " + desc;
            double[] embedding = VectorSearch.getEmbeddings(sentence);
            Gson gson = new Gson();
            // convert vectors into json string
            String embeddingString = gson.toJson(embedding);

            String query = "INSERT INTO tasks (title, description, due_date, category, priority, vector) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setString(1, title);
            stmt.setString(2, desc);
            stmt.setString(3, dueDate);
            stmt.setString(4, category);
            stmt.setString(5, priority);
            stmt.setString(6, embeddingString);
            return stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return 0;
        }
        
    }

    public int insertRecurring(String title,String desc,String dueDate,String category,String priority,String recurrence){
        try{
            // combine title and description to a sentence and use it to generate vector embedding
            String sentence = title + " " + desc;
            double[] embedding = VectorSearch.getEmbeddings(sentence);
            Gson gson = new Gson();
            // convert vectors into json string
            String embeddingString = gson.toJson(embedding);

            String query = "INSERT INTO tasks (title, description, due_date, category, priority, recurrence_interval, vector) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setString(1, title);
            stmt.setString(2, desc);
            stmt.setString(3, dueDate);
            stmt.setString(4, category);
            stmt.setString(5, priority);
            stmt.setString(6, recurrence);
            stmt.setString(7, embeddingString);
            return stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return 0;
        }
    }


    public int updateTask(int target,String field,String newData){
        try {
            
            String updateQuery = "UPDATE tasks SET " + field + " = ? WHERE id = ?";
            String title = "";
            String desc = "";
            String selectQuery = "SELECT title,description FROM tasks WHERE id = ?";
            
            // update the latest info first
            try (PreparedStatement updateStmt = db.prepareStatement(updateQuery)) {
                updateStmt.setString(1, newData);
                updateStmt.setInt(2, target);
                updateStmt.executeUpdate();
                
            }
            // query for title and description
            try (PreparedStatement selectStmt = db.prepareStatement(selectQuery)){
                selectStmt.setInt(1, target);
                ResultSet res = selectStmt.executeQuery();
                if(res.next()){
                    title = res.getString("title");
                    desc = res.getString("description");
                }
            }


            // Combine title and description to form the new sentence
            String newSentence = title + " " + desc;

            // regenerate the vector for modified description or title
            double[] newEmbeddings = VectorSearch.getEmbeddings(newSentence);
            Gson gson = new Gson();
            String embeddingString = gson.toJson(newEmbeddings);

            // Update the task with the new data and embeddings
            String updateQueryVector = "UPDATE tasks SET vector = ? WHERE id = ?";
            try (PreparedStatement updateStmt = db.prepareStatement(updateQueryVector)) {
                
                updateStmt.setString(1, embeddingString);
                updateStmt.setInt(2, target);
                return updateStmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return 0;
        }
    }

    public int deleteTask(int target){
        try {
            String query = "DELETE FROM tasks WHERE id = ?";
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setInt(1, target);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return 0;
        }
    }
    public int updateAfterSort(ArrayList<Task> data){
        try{
            String selectQuery = "SELECT id FROM tasks";
            PreparedStatement pstmt = db.prepareStatement(selectQuery);
            ResultSet idColumn = pstmt.executeQuery();
            ArrayList<Integer> idList = new ArrayList<>();
            while(idColumn.next()){
                int res = idColumn.getInt("id");
                idList.add(res);
            }
            int i = 0;
            for(Task task : data){
                // current row got recurrenceInternal
                if(task instanceof RecurringTask){
                    String update = "UPDATE tasks SET title=? , description=? , status=? , due_date=? , category=? , priority=? , recurrence_interval=?  , vector=? WHERE id=? ";
                    PreparedStatement stmt = db.prepareStatement(update);
                    RecurringTask recurringTask = (RecurringTask) task;
 
                    stmt.setString(1, recurringTask.title);
                    stmt.setString(2, recurringTask.desc);
                    stmt.setString(3, recurringTask.status);
                    stmt.setString(4, recurringTask.dueDate);
                    stmt.setString(5, recurringTask.category);
                    stmt.setString(6, recurringTask.priority);
                    stmt.setString(7, recurringTask.recurrence);
                    stmt.setString(8,recurringTask.vector);
                    
                    stmt.setInt(9, idList.get(i)); 
                    i++;
                    stmt.addBatch();
                    
                    stmt.executeBatch();
                }
                // current row got dependencies
                else if(task instanceof DependencyTask){
                    String update = "UPDATE tasks SET title=? , description=? , status=? , due_date=? , category=? , priority=? , dependencies=? , vector=? WHERE id=? ";
                    PreparedStatement stmt = db.prepareStatement(update);
                    DependencyTask dependencyTask = (DependencyTask) task;
 
                    stmt.setString(1, dependencyTask.title);
                    stmt.setString(2, dependencyTask.desc);
                    stmt.setString(3, dependencyTask.status);
                    stmt.setString(4, dependencyTask.dueDate);
                    stmt.setString(5, dependencyTask.category);
                    stmt.setString(6, dependencyTask.priority);
                    stmt.setString(7, dependencyTask.dependency);
                    stmt.setString(8,dependencyTask.vector);
                    
                    stmt.setInt(9, idList.get(i)); 
                    i++;
                    stmt.addBatch();
                    
                    stmt.executeBatch();
                }
                // current row dont have recurrenceInterval and dependency
                else {
                    String update = "UPDATE tasks SET title=? , description=? , status=? , due_date=? , category=? , priority=? , vector=? WHERE id=? ";
                    PreparedStatement stmt = db.prepareStatement(update);

 
                    stmt.setString(1, task.title);
                    stmt.setString(2, task.desc);
                    stmt.setString(3, task.status);
                    stmt.setString(4, task.dueDate);
                    stmt.setString(5, task.category);
                    stmt.setString(6, task.priority);
                    stmt.setString(7,task.vector);
                    
                    stmt.setInt(8, idList.get(i)); 
                    i++;
                    stmt.addBatch();
                    
                    stmt.executeBatch();
                }
    
            }
            return 1;

            
        }catch(SQLException e){
            System.out.println("SQL Error: " + e.getMessage());
            return 0;
        }
    }
    
}