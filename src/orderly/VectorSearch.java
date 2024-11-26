package orderly;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
            String sentencesJson = String.format("""
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

}





