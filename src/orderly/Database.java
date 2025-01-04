package orderly;

import java.sql.*;
import java.util.ArrayList;

public class Database {
    public Database(){}

    public ArrayList<Task> readAll(){
        ArrayList<Task> tasks = new ArrayList<>();

        try {
            Connection db = DriverManager.getConnection("jdbc:mysql://localhost:3306/todolist","root","fop2024");
            Statement stmt = db.createStatement();
            ResultSet result = stmt.executeQuery("select * from tasks");

            while(result.next()){
                Task task = new Task(
                    result.getInt("id"), 
                    result.getString("title"), 
                    result.getString("description"), 
                    result.getString("status"), 
                    result.getString("due_date"), 
                    result.getString("category"), 
                    result.getString("priority"));

                tasks.add(task);
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }

        return tasks;
    }

    public int insertTask(String title,String desc,String dueDate,String category,String priority){
        try{
            Connection db = DriverManager.getConnection("jdbc:mysql://localhost:3306/todolist","root","fop2024");
            String query = "INSERT INTO tasks (title, description, due_date, category, priority) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setString(1, title);
            stmt.setString(2, desc);
            stmt.setString(3, dueDate);
            stmt.setString(4, category);
            stmt.setString(5, priority);
            return stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return 0;
        }
    }

    public int updateTask(int target,String field,String newData){
        try {
            Connection db = DriverManager.getConnection("jdbc:mysql://localhost:3306/todolist","root","fop2024");
            String query = "UPDATE tasks SET " + field + " = ? WHERE id = ?";
            PreparedStatement stmt = db.prepareStatement(query);
            stmt.setString(1, newData);
            stmt.setInt(2, target);
            return stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return 0;
        }
    }
}
