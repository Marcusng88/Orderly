package orderly;

import java.util.*;
import java.time.LocalDate;

class TaskSorter {

    private ArrayList<Task> data;
    private int size;

    public TaskSorter(ArrayList<Task> allData) {
        this.data = allData;
        this.size = allData.size();
    }
    public ArrayList<Task> byDueDate(ArrayList<Task> data,boolean ascending){
        if (ascending){
            // ascending due date
            // earliest to latest
            for (int i=0;i<size;i++){
                for(int j = 0 ;j<size-1;j++){
                    LocalDate first = LocalDate.parse(data.get(j).dueDate);
                    LocalDate second = LocalDate.parse(data.get(j+1).dueDate);
                    if(first.isAfter(second)){
                        Task temp = data.get(j+1);
                        data.set(j+1,data.get(j));
                        data.set(j,temp);
                    }
                }
            }
            return data;
        }
        else{
            // descending due date
            // latest to earliest
            for(int i=0;i<size;i++){
                for(int j = 0 ;j<size-1;j++){
                    LocalDate first = LocalDate.parse(data.get(j).dueDate);
                    LocalDate second = LocalDate.parse(data.get(j+1).dueDate);
                    if(first.isBefore(second)){
                        Task temp = data.get(j+1);
                        data.set(j+1,data.get(j));
                        data.set(j,temp);
                    }
                }
  
            }
            return data;
        }
    }
    public  ArrayList<Task> byPriority(ArrayList<Task> data,boolean ascending){
        if(ascending){
            // ascending priority 
            // low to high 
            Collections.sort(data, new Comparator<Task>() {
                @Override
                public int compare(Task d1,Task d2){
                    // -1 means d1 comes before d2
                    // 1 means d2 comes before d1
                    // 0 means no change
                    if(d1.priority.equals("Low") && !d2.priority.equals("Low")) return -1;
                    if(d2.priority.equals("Low") && !d1.priority.equals("Low")) return 1;
                    
                    if(d1.priority.equals("Medium") && !d2.priority.equals("Medium")) return -1;
                    if(d2.priority.equals("Medium") && !d1.priority.equals("Medium")) return 1;

                    return 0;
                }
            });
            return data;
        }
        else{
            // descending priority
            // high to low
            Collections.sort(data, new Comparator<Task>() {
                @Override
                public int compare(Task d1,Task d2){
                    if(d1.priority.equals("High") && !d2.priority.equals("High")) return -1;
                    if(d2.priority.equals("High") && !d1.priority.equals("High")) return 1;
                    
                    if(d1.priority.equals("Medium") && !d2.priority.equals("Medium")) return -1;
                    if(d2.priority.equals("Medium") && !d1.priority.equals("Medium")) return 1;

                    return 0;
                }
            });
            return data;
        }
    }

}



 public class TaskSorterMain {

    private  ArrayList<Task> data;
    private  ArrayList<Task> newData;
    private  int choice ;
    public TaskSorterMain(ArrayList<Task> allData) {
        this.data = allData;
        this.newData = new ArrayList<Task>();
    }
    public ArrayList<Task> getNewData(){
        return newData;
    }
    public int getChoice(){
        return this.choice;
    }
    public ArrayList<Task> sortTasks(){
        Scanner scanner = new Scanner(System.in);
        outer:
        while (true) {
            System.out.println("\n=== Sort Tasks ==="+Orderly.ANSI_RESET);
            System.out.println("1. Ascending Order (Due Date)");
            System.out.println("2. Descending Order (Due Date)");
            System.out.println("3. Priority (High to Low)");
            System.out.println("4. Priority (Low to High)");
            System.out.println("5. Exit");

            while (true) { 
                System.out.printf("Pick a sort pattern >> ");
                try {
                    choice = scanner.nextInt();
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Please enter a number.");
                    scanner.nextLine();
                }
            }

            TaskSorter x = new TaskSorter(this.data);
        
            switch (choice) {
                case 1:
                    this.newData = x.byDueDate( data,true);

                    break outer;
                case 2:
                    this.newData = x.byDueDate( data,false);
                    
                    break outer;
                case 3:
                    this.newData = x.byPriority(data, false);
                    
                    break outer;
                case 4:
                    this.newData = x.byPriority(data, true);
                    
                    
                    break outer;
                case 5:
                    break outer;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 5.");
            }
        }
        return this.newData;
    }
}