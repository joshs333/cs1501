/**
 * @file Crossword.java
 * @author Joshua Spisak <jjs231@pitt.edu>
 * @date 09/18/2019
 * @brief able to load in a crossword puzzle from file and maintain a crossword state
 **/
import java.io.*;
import java.util.*;

/**
 * @brief maintains crossword state
 * @details represents the grid as a 2D array of chars. '+' can be filled in,
 *  and are filled in by lowercase characters. '-' are filled in slots.
 *  upper-case characters are values that are set by the puzzle.
 **/
public class CrosswordState {
    //! The values of the crossword
    private char[][] grid;
    //! The dimensions of the crossword grid
    private int dimension;

    /**
     * @brief initializes the crossword state from a file
     * @param filename the file to load the crossword from
     **/
    public CrosswordState(String filename) throws IOException {
        Scanner fileScan = new Scanner(new FileInputStream(filename));
        if(fileScan.hasNext()) {
            String st = fileScan.nextLine();
            dimension = Integer.parseInt(st);
            grid = new char[dimension][dimension];
        }
        int read = 0;
        // TODO(joshua.spisak): If I want to be fancier make exceptions for
        //  invalid crossword files.
        while (fileScan.hasNext()) {
            String row = fileScan.nextLine();
            int i;
            for(i = 0; i < row.length(); ++i) {
                grid[read][i] = row.charAt(i);
                // Predefined characters are upper case
                if(grid[read][i] != '+' && grid[read][i] != '-') {
                    grid[read][i] = Character.toUpperCase(grid[read][i]);
                }
            }
            // Make sure the correct number of columns for that row have been
            //  read in
            if(i < dimension) {
                System.out.println("Error, not enough columns to fill dimensions.");
            }
            ++read;
        }
        // Make sure the number of rows read in are the correct number
        if(read < dimension) {
            System.out.println("Error, not enough rows supplied to fill dimensions.");
        }
    }

    /**
     * @brief sees whether or not a cell is part of a word or is filled in
     * @param row the row to check
     * @param column the column to check
     * @return true if the cell is a word, false if not
     **/
    public boolean isWord(int row, int column) {
        boolean within_dimensions = (row >= 0 && row < dimension) && (column >= 0 && column < dimension);
        if(within_dimensions) {
            boolean usable_character = grid[row][column] != '-';
            return usable_character;
        }
        return false;
    }

    /**
     * @brief checks if the cell can be changed
     * @details this means that the cell is neither predefined as a certain
     *  letter nor filled in
     * @param row the row to check
     * @param column the column to check
     * @return true if the cell can be changed, false if not
     **/
    public boolean canChange(int row, int column) {
        if(isWord(row, column)) {
            // if it is part of a word then so long as its not upper case
            //  (predefined) it can be changed.
            boolean usable_character = !Character.isUpperCase(grid[row][column]);
            return usable_character;
        }
        return false;
    }

    /**
     * @brief sets the state of a certain cell
     * @param row the row to set
     * @param column the column to set
     * @param val the value to set the cell to
     **/
    public void set(int row, int column, char val) {
        if(canChange(row, column)) {
            grid[row][column] = Character.toLowerCase(val);
        }
    }

    /**
     * @brief unsets a certain cell
     * @param row the row to unset
     * @param column the column to unset
     **/
    public void unset(int row, int column) {
        if(canChange(row, column)) {
            grid[row][column] = '+';
        }
    }

    /**
     * @brief gets the horizantal string this cell is in
     * @details starts as far left on the board that the word goes,
     *  then right to that cell
     * @param row the row to check
     * @param column the column to check
     * @return a StringBuilder of the values
     **/
    public StringBuilder getHorizontal(int row, int column) {
        StringBuilder result = new StringBuilder();
        // Go left as far as we can until we hit the border or a
        int start_horiz = column;
        while(start_horiz - 1 >= 0 && grid[row][start_horiz - 1] != '-') {
            --start_horiz;
        }
        for(/* Using start_horiz */; start_horiz <= column && grid[row][start_horiz] != '-' && grid[row][start_horiz] != '+'; ++start_horiz) {
            result.append(Character.toLowerCase(grid[row][start_horiz]));
        }
        return result;
    }

    /**
     * @brief gets the vertical string this cell is in
     * @details starts as far up on the board that the word goes,
     *  then down to that cell
     * @param row the row to check
     * @param column the column to check
     * @return a StringBuilder of the values
     **/
    public StringBuilder getVertical(int row, int column) {
        StringBuilder result = new StringBuilder();
        // Go left as far as we can until we hit the border or a
        int start_vert = row;
        while(start_vert - 1 >= 0 && grid[start_vert - 1][column] != '-') {
            --start_vert;
        }
        for(/* Using start_vert */; start_vert <= row && grid[start_vert][column] != '-' && grid[start_vert][column] != '+'; ++start_vert) {
            result.append(Character.toLowerCase(grid[start_vert][column]));
        }
        return result;
    }

    /**
     * @brief made to get a snapshot of the state of the board to store it
     * @return a char array reflecting the state of the board
     *
     * TODO(joshua.spisak): make this a clone method instead?
     **/
    public char[][] snapshot() {
        char[][] return_grid = new char[dimension][dimension];
        for(int i = 0; i < dimension; ++i) {
            for(int j = 0; j < dimension; ++j) {
                return_grid[i][j] = grid[i][j];
            }
        }
        return return_grid;
    }

    /**
     * @brief prints the board state
     **/
    public void print() {
        System.out.println(toString());
    }

    /**
     * @brief converts the board into a string
     * @return a string containing board values
     **/
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < dimension; ++i) {
            for(int j = 0; j < dimension; ++j) {
                result.append(Character.toLowerCase(grid[i][j]));
            }
            if(i != dimension - 1) {
                result.append("\n");
            }
        }
        return result.toString();
    }

    /**
     * @brief gets the width/length of the grid
     * @return the dimension of the grid
     **/
    public int length() {
        return dimension;
    }
}
