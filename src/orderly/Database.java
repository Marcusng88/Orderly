package orderly;

import java.sql.*;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.mysql.cj.protocol.Resultset;

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
                if(result.getString("dependencies") == null){
                    Task task = new Task(
                        result.getInt("id"), 
                        result.getString("title"), 
                        result.getString("description"), 
                        result.getString("status"), 
                        result.getString("due_date"), 
                        result.getString("category"), 
                        result.getString("priority"));
    
                    tasks.add(task);
                }else{
                    DependencyTask task = new DependencyTask(
                        result.getInt("id"), 
                        result.getString("title"), 
                        result.getString("description"), 
                        result.getString("status"), 
                        result.getString("due_date"), 
                        result.getString("category"), 
                        result.getString("priority"),
                        result.getString("dependencies"));

                    tasks.add(task);
                }
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }

        return tasks;
    }
    public ArrayList<Task> readAllData(){
        ArrayList<Task> tasksForSorting = new ArrayList<>();

        try {
            Statement stmt = db.createStatement();
            ResultSet result = stmt.executeQuery("SELECT * from tasks");

            while(result.next()){
                Task task = new Task(
                result.getInt("id"), 
                result.getString("title"), 
                result.getString("description"), 
                result.getString("status"), 
                result.getString("due_date"), 
                result.getString("category"), 
                result.getString("priority"));
                result.getString("recurrence_interval");
                result.getString("dependencies");
                result.getString("vector");


                tasksForSorting.add(task);
            }
           
        }catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }

        return tasksForSorting;
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

            String update = "UPDATE tasks SET title=? , description=? , status=? , due_date=? , category=? , priority=? , recurrence_interval=? , dependencies=? , vector=? WHERE id=? ";
            PreparedStatement stmt = db.prepareStatement(update);
            
            for (int i = 0; i < data.size() ; i++) {
                
                
                stmt.setString(1, data.get(i).title);
                stmt.setString(2, data.get(i).desc);
                stmt.setString(3, data.get(i).status);
                stmt.setString(4, data.get(i).dueDate);
                stmt.setString(5, data.get(i).category);
                stmt.setString(6, data.get(i).priority);
                stmt.setString(7, data.get(i).recurrenceInterval);
                stmt.setString(8, data.get(i).dependencies);
                stmt.setString(9,data.get(i).vector);
                stmt.setInt(10, idList.get(i)); 
                stmt.addBatch();
            }
            stmt.executeBatch();
            return 1;

            
        }catch(SQLException e){
            System.out.println("SQL Error: " + e.getMessage());
            return 0;
        }
    }
}