import java.io.*;
import java.util.*;

public class CrosswordState {
    private char[][] grid;
    private int dimension;

    public CrosswordState(String filename) throws IOException {
        Scanner fileScan = new Scanner(new FileInputStream(filename));
        if(fileScan.hasNext()) {
            String st = fileScan.nextLine();
            dimension = Integer.parseInt(st);
            grid = new char[dimension][dimension];
        }
        int read = 0;
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
            if(i < dimension) {
                System.out.println("Error, not enough columns to fill dimensions.");
            }
            ++read;
        }
        if(read < dimension) {
            System.out.println("Error, not enough rows supplied to fill dimensions.");
        }
    }

    public boolean isWord(int row, int column) {
        boolean within_dimensions = (row >= 0 && row < dimension) && (column >= 0 && column < dimension);
        if(within_dimensions) {
            boolean usable_character = grid[row][column] != '-';
            return usable_character;
        }
        return false;
    }

    public boolean canChange(int row, int column) {
        if(isWord(row, column)) {
            boolean usable_character = !Character.isUpperCase(grid[row][column]);
            return usable_character;
        }
        return false;
    }

    public void set(int row, int column, char val) {
        if(canChange(row, column)) {
            grid[row][column] = Character.toLowerCase(val);
        }
    }

    public void unset(int row, int column) {
        if(canChange(row, column)) {
            grid[row][column] = '+';
        }
    }

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

    public char[][] snapshot() {
        char[][] return_grid = new char[dimension][dimension];
        for(int i = 0; i < dimension; ++i) {
            for(int j = 0; j < dimension; ++j) {
                return_grid[i][j] = grid[i][j];
            }
        }
        return return_grid;
    }

    public void print() {
        for(int i = 0; i < dimension; ++i) {
            for(int j = 0; j < dimension; ++j) {
                System.out.print(grid[i][j]);
            }
            System.out.println("");
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < dimension; ++i) {
            for(int j = 0; j < dimension; ++j) {
                result.append(grid[i][j]);
            }
            result.append("\n");
        }
        return result.toString();
    }

    public int length() {
        return dimension;
    }
}
