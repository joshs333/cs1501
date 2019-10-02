/**
 * @file DLBTest.java
 * @author Joshua Spisak <jjs231@pitt.edu>
 * @date 09/18/2019
 * @brief runs a DLB tree to demonstrate the delete() method
 **/

import java.io.*;
import java.util.*;

public class DLBTest {
    public static void main(String [] args) throws IOException {
        // Get CLI Arguments (dictionary file)
        String file_name;
        if(args.length > 0) {
            file_name = args[0];
        } else {
            file_name = "dict8.txt";
        }
        // Number of initial words to laod
        int count = 10;
        if(args.length > 1) {
            count = Integer.parseInt(args[1]);
            if(count < 10) {
                count = 10;
            }
        }
        Scanner fileScan = new Scanner(new FileInputStream(file_name));

        String st;
        StringBuilder sb;
        DLB dlb = new DLB();

        // Load in the first so many words of the dictionary
        int i = 0;
        while (fileScan.hasNext() && i < count) {
            st = fileScan.nextLine();
            dlb.add(st);
            ++i;
        }

        // Basic Demonstration
        System.out.println("DLB Tree with first " + count + " elements from dictionary.");
        dlb.print_nodes(false);
        dlb.delete("aba");
        dlb.delete("abash");
        System.out.println("DLB Tree with 'aba' and 'abash' deleted.");
        dlb.print_nodes(false);

        // Set up help menu
        StringBuilder help = new StringBuilder();
        help.append("You can do the following commands: \n");
        help.append("    add <word> [<word_1> <word_2>] - adds <word> and any added <word_*> to the dictionary.\n");
        help.append("    delete <word> [<word_1> <word_2>] - deletes <word> and any added <word_*> from the dictionary.\n");
        help.append("    list - prints the current dictionary\n");
        help.append("    quit - exits the program\n");
        help.append("    help - prints this help menu\n");
        System.out.print(help);

        // Run main loop
        Scanner user_scanner = new Scanner(System.in);
        while(true) {
            System.out.print("> ");
            String input = user_scanner.nextLine();
            String[] split_input = input.split(" ");
            if(split_input.length == 0) {
                System.out.println("Not enough arguments. Exitting program.");
                return;
            }
            if(split_input[0].equals("add")) {
                if(split_input.length < 2) {
                    System.out.println("Not enough arguments. Exitting program.");
                    return;
                }
                for(int j = 1; j < split_input.length; ++j) {
                    dlb.add(split_input[j]);
                }
            } else if(split_input[0].equals("delete")) {
                if(split_input.length < 2) {
                    System.out.println("Not enough arguments. Exitting program.");
                    return;
                }
                for(int j = 1; j < split_input.length; ++j) {
                    if(dlb.delete(split_input[j])) {
                        System.out.println("Deleted: " + split_input[j]);
                    } else {
                        System.out.println("Unable to delete: " + split_input[j]);
                    }
                }
            } else if(split_input[0].equals("list")) {
                dlb.print_nodes(false);
            // I also allow exit instead of quit because it can be habit to type either to quit
            } else if(split_input[0].equals("quit") || split_input[0].equals("exit")) {
                return;
            } else if(split_input[0].equals("help")) {
                System.out.print(help);
            } else {
                System.out.println("Unknown command sequence: [" + input + "]. Feel free to try 'help' or 'quit'.");
            }
        }
    }
}
