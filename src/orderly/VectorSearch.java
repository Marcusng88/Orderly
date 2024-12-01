package orderly;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;


// to run this VectorSearch class ,need to run the embedding_server.py file first

public class VectorSearch {

    // this will generate vectors embedding for the task description,it will return a double array
    public static double[] getEmbeddings(String sentence) {
        try {
            // API Endpoint for the Python Flask server or Hugging Face hosted model
            String endpoint = "http://localhost:5000/get_embeddings"; // Replace with your Hugging Face URL if applicable

            // Dynamically insert the sentence into the JSON payload
            String sentencesJson = String.format(
                    """
                    {
                        "sentences": ["%s"]
                    }
                    """, sentence);

            // Create HTTP Client
            HttpClient client = HttpClient.newHttpClient();

            // Create POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(sentencesJson))
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Print the response for debugging
            System.out.println("Response Code: " + response.statusCode());
            // System.out.println("Response Body: " + response.body());

            // Parse the JSON response to extract the embeddings
            JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();

            // Get the embeddings array (it's a 2D array where each element is an embedding vector)
            JsonArray embeddingsArray = jsonResponse.getAsJsonArray("embeddings");

            // The embeddings are inside the first element of the array
            JsonArray firstEmbeddingArray = embeddingsArray.get(0).getAsJsonArray();

            // Convert the inner array to a double array
            double[] embeddings = new double[firstEmbeddingArray.size()];

            // Iterate through the inner JSON array and extract each element (embedding value)
            for (int i = 0; i < firstEmbeddingArray.size(); i++) {
                embeddings[i] = firstEmbeddingArray.get(i).getAsDouble(); // Convert each element to double
            }

            return embeddings; // Return the full embedding array

        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null if there's an error
        }
    }
    
    //this will calculate the similarity of the two vectors(similarity between two sentences)
    public static double cosineSimilarity(double[] vectorA, double[] vectorB){
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        // this uses the formula cosine tetha = (dot product A and B)/(magnitude A * magnitude B)
        // to find cosine similarity 
        for (int i =0; i<vectorA.length;i++){
            dotProduct+= vectorA[i] + vectorB[i];
            normA+= Math.pow(vectorA[i], 2);
            normB+= Math.pow(vectorB[i], 2);
        }
        return (dotProduct/(Math.sqrt(normA)* Math.sqrt(normB)));
    }
    
    public static List<String> searchTasks(String query){
        List<String> results = new ArrayList<>();
        double[] queryVector = getEmbeddings(query);
        if (queryVector==null){
            return results;
        }
        
        //put the url for the database here
        String url = "";
        // database username here
        String user = "";
        // database password here;
        String password = "";

        //this also need modification,depends on the table created(just for temporary)
        String sql = "SELECT title,description,due_date,category,status,embedding FROM tasks";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()){

                while (rs.next()){
                    String title = rs.getString("title");
                    String description = rs.getString("description");
                    String dueDate = rs.getString("due_date");
                    String category = rs.getString("category");
                    String status = rs.getString("status");
                    String embeddingString = rs.getString("embedding").replace("[", "").replace("]", "");
                    //Arrays.stream converts the array of strings into stream of strings.Stream is sequence of elements that can be processed parallel
                    // the split (",") is to split the embedding e.g "0.1,0.2,0.3" into "0.1" "0.2" "0.3"
                    //.mapToDouble(Double::parseDouble) convert each string of a number into an actual double
                    double[] taskEmbedding = Arrays.stream(embeddingString.split(",")).mapToDouble(Double::parseDouble).toArray();

                    double similarity = cosineSimilarity(queryVector, taskEmbedding);
                    
                    results.add(String.format("Title: %s, Description: %s, Due Date: %s, Category: %s, Status: %s, Similarity: %.2f",
                    title,description,dueDate,category,status,similarity));
        }
                

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

        //this will sort the results in descending order,using comparator and lambda expressions
        results.sort((a,b)-> Double.compare(
                Double.parseDouble(b.split("Similarity: ")[1]),
                Double.parseDouble(a.split("Similarity: ")[1])
        ));

        return results;

    }
}






