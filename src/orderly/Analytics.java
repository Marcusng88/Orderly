package orderly;

import java.util.ArrayList;

public class Analytics {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    int totalTasks = 0;
    int completed = 0;
    int pending = 0;
    double completionRate = 0;
    int homework;
    int personal;
    int work;

    public Analytics(){}

    public void analyzer(ArrayList<Task> tasks){
        for(Task task : tasks){
            totalTasks++;
            completed += task.status.equals("Complete") ? 1 : 0;
            pending += task.status.equals("Incomplete") ? 1 : 0;
            homework += task.category.equalsIgnoreCase("Homework") ? 1 : 0;
            personal += task.category.equalsIgnoreCase("Personal") ? 1 : 0;
            work += task.category.equalsIgnoreCase("Work") ? 1 : 0;
        }
        completionRate = getCompletionRate(totalTasks,completed);
        displayAnalytics();
    }

    private double getCompletionRate(int total, int completed){
        return completed == 0 ? 0 : (double) completed / total * 100;
    }

    private void displayAnalytics(){
        System.out.println(ANSI_YELLOW + "\n=== Analytics Dashboard ===" + ANSI_RESET);
        System.out.printf("- Total Tasks: %d\n",totalTasks);
        System.out.printf("- Completed: %d\n",completed);
        System.out.printf("- Pending: %d\n",pending);
        System.out.printf("- Completion Rate: %.1f%%\n",completionRate);
        System.out.printf("- Task Categories: Homework: %d, Personal: %d, Work: %d\n",homework,personal,work);
    }
}