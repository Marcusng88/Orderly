package orderly;

public class RecurringTask extends Task{
    String recurrence;

    public RecurringTask(int taskID, String title, String desc, String status, String dueDate, String category, String priority, String recurrence){
        super(taskID,title,desc,status,dueDate,category,priority);
        this.recurrence = recurrence;
    }
}