package orderly;

import java.util.*;

class SearchingTask {
    private String title;
    private String description;
    private String dueDate;
    private String category;
    private String priority;
    private boolean isComplete;

    // Constructor
    public SearchingTask(String title, String description, String dueDate, String category, String priority, boolean isComplete) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.category = category;
        this.priority = priority;
        this.isComplete = isComplete;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - Due: %s - Category: %s - Priority: %s",
                isComplete ? "Complete" : "Incomplete", title, dueDate, category, priority);
    }
}

public class TaskSearchingMain {
    public static List<SearchingTask> searchTasks(List<SearchingTask> tasks, String keyword) {
        List<SearchingTask> results = new ArrayList<>();
        for (SearchingTask task : tasks) {
            if (task.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                task.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(task);
            }
        }
        return results;
    }
        //To test the functionality of the code 
       // Main method
    
   /* public static void main(String[] args) {
        // Sample tasks
        List<SearchingTask> tasks = Arrays.asList(
            new SearchingTask("Finish Assignment", "Complete the math assignment.", "2024-10-15", "Homework", "High", false),
            new SearchingTask("Grocery Shopping", "Buy milk, eggs, and bread.", "2024-10-16", "Personal", "Medium", false),
            new SearchingTask("Team Meeting", "Discuss project updates.", "2024-10-17", "Work", "High", true)
        );

        // User input
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Search Tasks ===");
        System.out.print("Enter a keyword to search by title or description: ");
        String keyword = scanner.nextLine();

        // Search tasks
        List<SearchingTask> results = searchTasks(tasks, keyword);

        // Display results
        System.out.println("=== Search Results ===");
        if (results.isEmpty()) {
            System.out.println("No tasks found.");
        } else {
            int index = 1;
            for (SearchingTask task : results) {
                System.out.println(index + ". " + task);
                index++;
            }
        }

        scanner.close();
    } 
   */
} 
