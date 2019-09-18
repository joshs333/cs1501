import java.io.*;
import java.util.*;

public class Crossword {
    static DictInterface dictionary;
    static CrosswordState crossword;
    static boolean find_first_solution = false;
    static int verbosity = 0;
    static int solutions = 0;

    public static void main(String[] args) throws IOException {
        if(args.length < 3) {
            System.out.println("Insufficient arguments provided.");
            System.out.println("Please provide the following: <dictionary> <crossword file> <verbosity>");
            return;
        }

        dictionary = new MyDictionary();

        // Load in the dictionary.
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
        if(verbosity > 0) {
            System.out.println("Final Solutions: " + solutions);
        } else {
            System.out.println(solutions);
        }
    }

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
                if(!(vert_validity == 1 || vert_validity == 3)) {
                    return false;
                }
            } else {
                if(vert_validity < 2)
                    return false;
            }
            return true;
        } /* if(horiz_validity && vert_validity) */
        return false;
    }

    public static boolean test_next(int row, int column) {
        if(column < crossword.length() - 1) {
            return solve(row, column + 1);
        } else if(row < crossword.length() - 1) {
            return solve(row + 1, 0);
        } else {
            // If there is none then this is a success board
            // solve is already true
            if(verbosity >= 2) {
                System.out.println("Success!!!");
                crossword.print();
            }
            ++solutions;
            if(verbosity >= 1 && solutions % 20 == 0) {
                System.out.println("Solutions = " + solutions);
            }
            return true;
        }
    }

    public static boolean solve(int row, int column) {
        // Checks if it is part of a word
        if(crossword.isWord(row, column)) {
            if(verbosity >= 3) {
                System.out.println("** Testing at " + row + ", " + column + " **");
                crossword.print();
            }
            // sees if we can modify the letter, or we just need to test it
            if(crossword.canChange(row, column)) {
                for(char new_char = 'a'; new_char <= 'z'; ++new_char) {
                    crossword.set(row, column, new_char);
                    boolean solved;
                    if(test(row, column)) {
                        solved = test_next(row, column);
                    } else {
                        solved = false;
                    }
                    if(solved && find_first_solution) {
                        return true;
                    }
                    crossword.unset(row, column);
                }
                return false;
            } else {
                boolean solved;
                if(test(row, column)) {
                    if(test_next(row, column) && find_first_solution) {
                        return true;
                    }
                } else {
                    return false;
                }
            }
            // If we get here we can just assume we didn't find a solution
            return false;
        } else {
            // We can't do anything so go to next coord.
            if(column < crossword.length() - 1) {
                return solve(row, column + 1);
            } else if(row < crossword.length() - 1) {
                return solve(row + 1, 0);
            } else {
                // If there is none then this is a success board
                if(verbosity >= 1) {
                    System.out.println("Success!!!");
                    crossword.print();
                }
                ++solutions;
                if(verbosity >= 1 && solutions % 20 == 0) {
                    System.out.println("Solutions = " + solutions);
                }
                return true;
            }
        }
    }
}
