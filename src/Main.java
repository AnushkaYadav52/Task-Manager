import javax.swing.text.BadLocationException;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.awt.SystemColor.text;

class Task {
    String name;
    LocalDate dueDate;
    String description;
    boolean isCompleted;

    final String RESET = "\u001B[0m";
    final String ORANGE = "\u001B[38;2;255;165;0m";

    public Task(String name, LocalDate dueDate, String description, boolean isCompleted) {
        this.name = name;
        this.dueDate = dueDate;
        this.description = description;
        this.isCompleted = isCompleted;
    }

    public String toString(){
        return (ORANGE + "Task: " + RESET + name + " | " + ORANGE + "Description: " + RESET + description + " | " + ORANGE + "Due: " + RESET + dueDate + " | " + ORANGE + "Completed: " + RESET + isCompleted);
    }
    public void markAsCompleted(){
        isCompleted = true;
    }
    public LocalDate getDueDate() {
        return dueDate;
    }
}

public class Main {
    public static void main(String[] args) {

        final String RESET = "\u001B[0m";        // Reset to default
        final String BLUE = "\u001B[34m";        // Blue (menus/options)
        final String RED = "\u001B[31m";         // Red (warnings/errors)
        final String GREEN = "\u001B[32m";       // Green (success messages)
        final String YELLOW = "\u001B[33m";      // Yellow (alerts or highlights)
        final String PURPLE = "\u001B[35m";      // Purple (user prompts)

        ArrayList<Task> tasks = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("tasks.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // skip blank lines
                }

                String[] parts = line.split("\\|");
                if (parts.length < 4) {
                    System.out.println("Skipping malformed line: " + line);
                    continue; // skip lines that aren't complete
                }

                String name = parts[0].replace("Task:", "").replaceAll("\\[38;2;255;165;0m", "").replaceAll("\\[0m", "").trim();
                String description = parts[1].replace("Description:", "").replaceAll("\\[38;2;255;165;0m", "").replaceAll("\\[0m", "").trim();

                String rawDate = parts[2].replace("Due:", "").trim();

                String cleanDate = rawDate
                        .replaceAll("\u001B\\[[;\\d]*m", "")  // removes proper ANSI codes
                        .replaceAll("\\[\\d+(;\\d+)*m", "")   // removes leftover bracketed codes without ESC
                        .trim();
                LocalDate dueDate = LocalDate.parse(cleanDate);

                boolean isCompleted = Boolean.parseBoolean(parts[3].replace("Completed:", "").trim());

                Task task = new Task(name, dueDate, description, isCompleted);
                tasks.add(task);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Scanner scanner = new Scanner(System.in);
        int userChoice = 0;
        String taskName;
        String taskDescription;
        String taskDueDate;
        int taskNumber;
        LocalDate taskDueDateConverted;
        LocalDate today = LocalDate.now();

        System.out.println();
        System.out.println("======================================");
        System.out.println(BLUE + "Welcome to your Personal Task Manager!" + RESET);
        System.out.println("====== " + PURPLE + "Today's date: " + today + RESET + " ======");
        System.out.println("======================================");

        while (true) {
            System.out.println();
            System.out.println("============ "+ BLUE + "Task Manager" + RESET + " ============");
            System.out.println(YELLOW + "1." + RESET + " Add Task");
            System.out.println(YELLOW + "2." + RESET + " View All Tasks");
            System.out.println(YELLOW + "3." + RESET +  " View Tasks by Due Date");
            System.out.println(YELLOW + "4." + RESET + " Mark Task as Completed");
            System.out.println(YELLOW + "5." + RESET + " Remove Task");
            System.out.println(YELLOW + "6." + RESET + " Check Upcoming Deadlines (within 3 days)");
            System.out.println(YELLOW + "7." + RESET + " Exit");
            System.out.println("======================================");
            System.out.print(PURPLE + "Choose an option: " + RESET);

            try{
                userChoice = scanner.nextInt();
                scanner.nextLine();
            }
            catch (InputMismatchException e){
                scanner.nextLine();
            }

            if (userChoice == 1) {
                System.out.println();
                System.out.println("============== " + BLUE + "Add Task" + RESET + " ==============");

                System.out.print("Task Name: ");
                taskName = scanner.nextLine();

                System.out.print("Description: ");
                taskDescription = scanner.nextLine();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
                System.out.print("Due Date (yyyy-mm-dd): ");
                while (true) {
                    taskDueDate = scanner.nextLine();
                    try{
                        taskDueDateConverted = LocalDate.parse(taskDueDate, formatter);
                        break;
                    }
                    catch (DateTimeParseException e){
                        System.out.print(YELLOW + "Please input a valid date (yyyy-mm-dd, e.g., 2024-11-09): " + RESET);
                    }
                }
                boolean isCompleted = false;

                Task newTask = new Task(taskName, taskDueDateConverted, taskDescription, isCompleted);
                tasks.add(newTask);

                System.out.println();
                System.out.println(GREEN + "Task successfully added!" + RESET);

            } else if (userChoice == 2) {
                System.out.println();
                System.out.println("=========== " + BLUE + "View All Tasks" + RESET + " ===========");

                if (tasks.size() == 0) {
                    System.out.println(RED + "Task Manager is empty! Please input a task!" + RESET);
                } else {
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.print("(" + (i + 1) + ") ");
                        System.out.println((tasks.get(i).toString()));
                    }
                }
                System.out.println("======================================");


            } else if (userChoice == 3) {
                System.out.println();
                System.out.println("======= " + BLUE + "View Tasks by Due Date" + RESET + " =======");

                ArrayList<Task> tasksByDueDate = new ArrayList<>(tasks);

                Collections.sort(tasksByDueDate, Comparator.comparing(Task:: getDueDate));

                for (int i = 0; i < tasksByDueDate.size(); i++) {
                    System.out.print("(" + (i + 1) + ") ");
                    System.out.println((tasksByDueDate.get(i).toString()));
                }
                System.out.println("======================================");


                } else if (userChoice == 4) {
                System.out.println();
                System.out.println("======= " + BLUE + "Mark Task as Completed" + RESET + " =======");

                if (tasks.size() == 0) {
                    System.out.println(RED + "Task Manager is empty! Please input a task!" + RESET);

                }else {
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.print("(" + (i + 1) + ") ");
                        System.out.println((tasks.get(i).toString()));
                    }
                    System.out.println("======================================");

                    System.out.print(PURPLE + "Please enter the number of the task that you would like to mark as complete: " + RESET);
                    taskNumber = scanner.nextInt();

                    while (true) {
                        if (taskNumber >= 1 && taskNumber <= tasks.size()) {
                            taskNumber -= 1;
                            break;
                        } else {
                            System.out.print(YELLOW + "Please enter a valid task number: " + RESET);
                            taskNumber = scanner.nextInt();
                        }
                    }
                    tasks.get(taskNumber).markAsCompleted();
                    scanner.nextLine();

                    while (true) {
                        System.out.print(PURPLE + "Would you like to delete this task? (y/n):  " + RESET);
                        String input = scanner.nextLine().toLowerCase().trim();

                        if (input.equals("y")) {
                            tasks.remove(taskNumber);
                            System.out.println(GREEN + "Task removed!" + RESET);
                            break;
                        } else if (input.equals("n")) {
                            System.out.println(RED + "Task has not been removed!" + RESET);
                            break;
                        } else {
                            System.out.println(YELLOW + "Please enter a valid input: " + RESET);
                        }
                    }
                }

            } else if (userChoice == 5) {
                System.out.println();
                System.out.println("============= " + BLUE + "Remove Task" + RESET + " =============");

                if (tasks.size() == 0) {
                    System.out.println(RED + "Task Manager is empty! Please input a task!" + RESET);
                }
                else{
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.print("(" + (i + 1) + ") ");
                        System.out.println((tasks.get(i).toString()));
                    }
                    System.out.println("======================================");

                    System.out.print(PURPLE + "Please enter the number of the task that you would like to remove: " + RESET);
                    taskNumber = scanner.nextInt();

                    while (true) {
                        if (taskNumber >= 1 && taskNumber <= tasks.size()) {
                            taskNumber -= 1;
                            tasks.remove(taskNumber);
                            System.out.println(GREEN + "Task removed!" + RESET);
                            break;
                        } else {
                            System.out.print(YELLOW + "Please enter a valid task number: " + RESET);
                            taskNumber = scanner.nextInt();
                        }
                    }
                }

            } else if (userChoice == 6) {
                System.out.println();
                System.out.println("========== " + BLUE + "Upcoming Deadlines" + RESET + " ==========");

                ArrayList<Task> upcomingTasks = new ArrayList<>();
                long daysBetween;

                for (int i = 0; i < tasks.size(); i++) {
                    daysBetween = ChronoUnit.DAYS.between(today, tasks.get(i).dueDate);
                    if (daysBetween <= 3 && tasks.get(i).isCompleted == false){
                        upcomingTasks.add(tasks.get(i));
                    }
                }

                if (upcomingTasks.size() == 0){
                    System.out.println(RED + "No upcoming tasks within the next 3 days! " + RESET);
                }
                else {
                    Collections.sort(upcomingTasks, Comparator.comparing(Task:: getDueDate));

                    for (int i = 0; i < upcomingTasks.size(); i++) {
                        System.out.print("(" + (i + 1) + ") ");
                        System.out.println((upcomingTasks.get(i).toString()) + " | " + RED + "Days left: " + RESET + ChronoUnit.DAYS.between(today, upcomingTasks.get(i).dueDate));
                    }
                    System.out.println("======================================");
                }

            } else if (userChoice == 7) {
                System.out.println();
                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter("tasks.txt"));

                    for (Task task : tasks) {
                        writer.write("\n" + task.toString());
                    }
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println( GREEN + "Tasks have been saved! See you next time!" + RESET);
                break;

            } else {
                System.out.println(YELLOW + "Please enter a valid input and try again!" + RESET);
            }
        }
    }
}
