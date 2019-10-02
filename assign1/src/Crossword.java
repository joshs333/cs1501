/**
 * @file Crossword.java
 * @author Joshua Spisak <jjs231@pitt.edu>
 * @date 09/18/2019
 * @brief loads in a dictionary and a crossword and finds a valid solution
 **/
import java.io.*;
import java.util.*;

/**
 * @brief finds a valid crossword solution
 **/
public class Crossword {
    //! The Dictionary to query
    static DictInterface dictionary;
    //! The crossword state
    static CrosswordState crossword;
    //! Whether or not to only find the first solution
    static boolean find_first_solution = true;
    //! Verbosity Level
    static int verbosity = 0;
    //! Number of solutions found
    static int solutions = 0;

    /**
     * @brief receives commmand line arguments, loads the dictionary and
     *  crossword, executes the solve.
     **/
    public static void main(String[] args) throws IOException {
        if(args.length < 2) {
            System.out.println("Insufficient arguments provided.");
            System.out.println("Please provide the following: <dictionary> <crossword file> <verbosity>");
            return;
        }

        // Load in the dictionary.
        dictionary = new MyDictionary();
        Scanner dictionary_scan = new Scanner(new FileInputStream(args[0]));
        while (dictionary_scan.hasNext()) {
            String next_line = dictionary_scan.nextLine();
            dictionary.add(next_line);
        }

        // Create the crossword
        crossword = new CrosswordState(args[1]);

        // Set the verbosity
        if(args.length == 4) {
            verbosity = Integer.parseInt(args[2]);
        }

        if(verbosity > 0) {
            System.out.println("Start Crossword:");
            crossword.print();
        }

        // Execute the solve
        solve(0, 0);
        // Only print the number of solutions if verbosity is up or not finding
        // the first solution
        if(verbosity > 0) {
            System.out.println("Final Solutions: " + solutions);
        }
        if(!find_first_solution && verbosity == 0) {
            System.out.println(solutions);
        }
    } /* main(String[] args) */

    /**
     * @brief tests a specific cell of the current crossword state
     * @param[in] row the row number to check
     * @param[in] column the column number to check
     * @returns true if it is valid, false if not
     **/
    public static boolean test(int row, int column) {
        // Whether or not the next parts are in this word or we are completing the word now
        boolean can_use_next_horiz = crossword.isWord(row, column + 1);
        boolean can_use_next_vert = crossword.isWord(row + 1, column);
        // Gets the validity for that string
        int horiz_validity = dictionary.searchPrefix(crossword.getHorizontal(row, column));
        int vert_validity = dictionary.searchPrefix(crossword.getVertical(row, column));

        // Default to no solution first
        if(horiz_validity > 0 && vert_validity > 0) {
            // For both horiz/vertical validities
            // If we can use the next in that direction
            if(can_use_next_horiz) {
                // If it is not a prefix then return false
                if(!(horiz_validity == 1 || horiz_validity == 3))
                    return false;
            } else {
                // make sure it is a word
                if(horiz_validity < 2)
                    return false;
            }
            if(can_use_next_vert) {
                if(!(vert_validity == 1 || vert_validity == 3))
                    return false;
            } else {
                if(vert_validity < 2)
                    return false;
            }
            return true;
        } /* if(horiz_validity && vert_validity) */
        return false;
    } /* test(int row, int column) */

    /**
     * @brief finds the next cell to solve (and calls solve) or marks a successs
     * @param[in] row is the current row
     * @param[in] column is the current column
     **/
    public static void solve_next(int row, int column) {
        if(column < crossword.length() - 1) {
            solve(row, column + 1);
        } else if(row < crossword.length() - 1) {
            solve(row + 1, 0);
        } else {
            // If there is none then this is a success board
            if(verbosity >= 2) {
                System.out.println("Success!!!");
                crossword.print();
            }
            ++solutions;
            if(verbosity >= 1 && solutions % 20 == 0) {
                System.out.println("Solutions = " + solutions);
            }
            if(find_first_solution) {
                crossword.print();
            }
        }
    } /* solve_next(int row, int column) */

    /**
     * @brief solves for a certain row and column
     * @param[in] row is the row to solve
     * @param[in] column is the column to solve
     **/
    public static void solve(int row, int column) {
        // Checks if it is part of a word
        if(crossword.isWord(row, column)) {
            if(verbosity >= 3) {
                System.out.println("** Testing at " + row + ", " + column + " **");
                crossword.print();
            }
            // sees if we can modify the letter, or we just need to test it
            if(crossword.canChange(row, column)) {
                // Try each letter
                for(char new_char = 'a'; new_char <= 'z'; ++new_char) {
                    crossword.set(row, column, new_char);
                    if(test(row, column)) {
                        solve_next(row, column);
                    }
                    crossword.unset(row, column);
                }
            } else {
                // We can test with the current letter
                if(test(row, column)) {
                    solve_next(row, column);
                }
            }
        } else {
            // It's not a part of the word so let's just solve the next one...
            solve_next(row, column);
        }
    } /* solve(int row, int column) */
} /* Crossword */
