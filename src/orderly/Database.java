package orderly;

import java.sql.*;
import java.util.ArrayList;

import com.google.gson.Gson;

public class Database {
    public Connection db;

    public Database(){
        try {
            db = DriverManager.getConnection("jdbc:mysql://localhost:3306/todolist","root","fop2024");
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
}