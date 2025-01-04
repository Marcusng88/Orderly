package orderly;

import java.util.*;

public class SearchingTask {
    public static ArrayList<Task> searchTasks(ArrayList<Task> tasks, String keyword) {
        ArrayList<Task> results = new ArrayList<>();
        for (Task task : tasks) {
            if (task.title.indexOf(keyword)!=-1 ||
                task.desc.indexOf(keyword)!=-1) {
                results.add(task);
            }
        }
        return results;
    }
}
class Searching {
    private String title;
    private String dueDate;
    private String category;
    private String priority;
    private String status;

    // Constructor
    public Searching(String title, String dueDate, String category, String priority, String status) {
        this.title = title;
        this.dueDate = dueDate;
        this.category = category;
        this.priority = priority;
        this.status = status;
    }
    @Override
    public String toString() {
        return String.format("[%s] %s - Due: %s - Category: %s - Priority: %s",
                status, title, dueDate, category, priority);
    }
}


