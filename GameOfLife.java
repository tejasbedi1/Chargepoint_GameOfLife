package random;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

public class GameOfLife {

    /**
     * SETTINGS
     */
    private static final char CELL = '#';
    private static final char SPACE = '.';
    private static final int GEN_DELAY = 150;
    private static final int ROWS = 30;
    private static final int COLS = 40;

    /**
     * Instance VARS
     */
    private int rows;
    private int cols;
    private char[][] board;
    private Set<String> liveCells;
    private Set<String> deadCells;

    public GameOfLife(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.board = new char[rows][cols];
        this.liveCells = new HashSet<>();
        this.deadCells = new HashSet<>();
    }

    /**
     * Set the board for teh first time
     * @param initialBoard
     */
    public void setBoard(char[][] initialBoard) {

        this.board = initialBoard;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (board[row][col] == CELL) {
                    liveCells.add(row + "," + col);
                } else {
                    deadCells.add(row + "," + col);
                }
            }
        }
    }

    /**
     * Generational Logic
     * We run the loop using live cells and not all positions
     */
    public void nextGeneration() {
        Set<String> newLiveCells = new HashSet<>();
        Set<String> newDeadCells = new HashSet<>();
        Set<String> cellsToCheck = new HashSet<>(liveCells);

        for (String cell : liveCells) {
            String[] parts = cell.split(",");
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int r = row + i;
                    int c = col + j;
                    if (r >= 0 && r < rows && c >= 0 && c < cols) {
                        cellsToCheck.add(r + "," + c);
                    }
                }
            }
        }

        for (String cell : cellsToCheck) {
            String[] parts = cell.split(",");
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);
            int liveNeighbors = countLiveNeighbors(row, col);

            if (board[row][col] == CELL) {
                // Rule 1 & Rule 3
                if (liveNeighbors < 2 || liveNeighbors > 3) {
                    newDeadCells.add(row + "," + col);
                } else {
                    newLiveCells.add(row + "," + col);
                }
            } else {
                // Rule 4
                if (liveNeighbors == 3) {
                    newLiveCells.add(row + "," + col);
                } else {
                    newDeadCells.add(row + "," + col);
                }
            }
        }

        liveCells = newLiveCells;
        deadCells = newDeadCells;

        updateBoard();
    }

    /**
     * Count neighbours that our live
     * @param row
     * @param col
     * @return
     */
    private int countLiveNeighbors(int row, int col) {
        int liveNeighbors = 0;
        int[] directions = {-1, 0, 1};

        for (int i : directions) {
            for (int j : directions) {
                if (!(i == 0 && j == 0)) {
                    int r = row + i;
                    int c = col + j;

                    if (r >= 0 && r < rows && c >= 0 && c < cols) {
                        if (board[r][c] == CELL) {
                            liveNeighbors++;
                        }
                    }
                }
            }
        }
        return liveNeighbors;
    }

    /**
     * Update board with updated cell information
     */
    private void updateBoard() {

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (liveCells.contains(row + "," + col)) {
                    board[row][col] = CELL;
                } else {
                    board[row][col] = SPACE;
                }
            }
        }
    }

    /**
     * Print the Board with Generation, Live and Dead Ceels
     * @param gen
     */
    public void printBoard(int gen) {
        System.out.println("GENERATION: " + gen + " | LIVE: " + liveCells.stream().count());
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                System.out.print(board[row][col] + "  ");
            }
            System.out.println();
        }
    }

    /**
     *  Clear Windows Console
     */
    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param shape
     * @param midRow
     * @param midCol
     * @param rows
     * @param cols
     * @return
     */
    public static char[][] getShape(String shape, int midRow, int midCol) {
        char[][] board = new char[ROWS][COLS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = SPACE;
            }
        }

        switch (shape.toLowerCase()) {
            case "block":
                board[midRow - 1][midCol - 1] = CELL;
                board[midRow - 1][midCol] = CELL;
                board[midRow][midCol - 1] = CELL;
                board[midRow][midCol] = CELL;
                break;
            case "blinker":
                board[midRow - 1][midCol] = CELL;
                board[midRow][midCol] = CELL;
                board[midRow + 1][midCol] = CELL;
                break;
            case "glider":
                board[midRow - 1][midCol] = CELL;
                board[midRow][midCol + 1] = CELL;
                board[midRow + 1][midCol - 1] = CELL;
                board[midRow + 1][midCol] = CELL;
                board[midRow + 1][midCol + 1] = CELL;
                break;
            case "beehive":
                board[midRow][midCol] = CELL;
                board[midRow][midCol + 1] = CELL;
                board[midRow + 1][midCol - 1] = CELL;
                board[midRow + 1][midCol + 2] = CELL;
                board[midRow + 2][midCol] = CELL;
                board[midRow + 2][midCol + 1] = CELL;
                break;
            case "loaf":
                board[midRow][midCol + 1] = CELL;
                board[midRow][midCol + 2] = CELL;
                board[midRow + 1][midCol] = CELL;
                board[midRow + 1][midCol + 3] = CELL;
                board[midRow + 2][midCol + 1] = CELL;
                board[midRow + 2][midCol + 3] = CELL;
                board[midRow + 3][midCol + 2] = CELL;
                break;
            case "boat":
                board[midRow][midCol] = CELL;
                board[midRow][midCol + 1] = CELL;
                board[midRow + 1][midCol] = CELL;
                board[midRow + 1][midCol + 2] = CELL;
                board[midRow + 2][midCol + 1] = CELL;
                break;
            case "beacon":
                board[midRow - 1][midCol - 1] = CELL;
                board[midRow - 1][midCol] = CELL;
                board[midRow][midCol - 1] = CELL;
                board[midRow + 1][midCol + 2] = CELL;
                board[midRow + 2][midCol + 1] = CELL;
                board[midRow + 2][midCol + 2] = CELL;
                break;
            case "pulsar":
                int[][] pulsarCoords = {
                        {midRow - 2, midCol - 4}, {midRow - 2, midCol - 3}, {midRow - 2, midCol - 2},
                        {midRow - 2, midCol + 2}, {midRow - 2, midCol + 3}, {midRow - 2, midCol + 4},
                        {midRow - 4, midCol - 2}, {midRow - 3, midCol - 2}, {midRow - 4, midCol + 2}, {midRow - 3, midCol + 2},
                        {midRow + 2, midCol - 4}, {midRow + 2, midCol - 3}, {midRow + 2, midCol - 2},
                        {midRow + 2, midCol + 2}, {midRow + 2, midCol + 3}, {midRow + 2, midCol + 4},
                        {midRow + 4, midCol - 2}, {midRow + 3, midCol - 2}, {midRow + 4, midCol + 2}, {midRow + 3, midCol + 2},
                        {midRow - 4, midCol + 6}, {midRow - 3, midCol + 6}, {midRow - 2, midCol + 6},
                        {midRow + 2, midCol + 6}, {midRow + 3, midCol + 6}, {midRow + 4, midCol + 6},
                        {midRow - 4, midCol - 6}, {midRow - 3, midCol - 6}, {midRow - 2, midCol - 6},
                        {midRow + 2, midCol - 6}, {midRow + 3, midCol - 6}, {midRow + 4, midCol - 6},
                };
                for (int[] coord : pulsarCoords) {
                    board[coord[0]][coord[1]] = CELL;
                }
                break;
            case "lwss":
                int[][] lwssCoords = {
                        {midRow - 1, midCol + 2}, {midRow - 1, midCol + 4},
                        {midRow, midCol + 1}, {midRow, midCol + 5},
                        {midRow + 1, midCol + 5},
                        {midRow + 2, midCol + 1}, {midRow + 2, midCol + 4},
                        {midRow + 3, midCol + 2}, {midRow + 3, midCol + 3},
                };
                for (int[] coord : lwssCoords) {
                    board[coord[0]][coord[1]] = CELL;
                }
                break;
            default:
                System.out.println("Invalid shape.");
                break;
        }
        return board;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of generations:");
        int generations = scanner.nextInt();
        System.out.print("Choose control mode - manual(m)/automatic(a)):");
        String controlMode = scanner.next();
        System.out.print("Choose a shape (block/blinker/glider/beehive/loaf/boat/beacon/pulsar/lwss):");
        String shape = scanner.next();

        GameOfLife game = new GameOfLife(ROWS, COLS);

        // Initializing the board with the Glider pattern in the middle
        int midRow = ROWS / 2;
        int midCol = COLS / 2;
        char[][] initialBoard = getShape(shape, midRow, midCol);

        if (initialBoard == null) {
            System.out.println("Invalid shape. Exiting.");
            return;
        }

        game.setBoard(initialBoard);
        // Initial State
        clearConsole();
        game.printBoard(0);

        if (controlMode.equalsIgnoreCase("manual") || controlMode.equalsIgnoreCase("m") ) {
            for (int i = 0; i < generations; i++) {
                System.out.println("Press Enter to generate the next generation...");
                scanner.nextLine();
                game.nextGeneration();
                clearConsole();
                game.printBoard(i+1);
            }
        } else if (controlMode.equalsIgnoreCase("automatic") || controlMode.equalsIgnoreCase("a") ) {
            for (int i = 0; i < generations; i++) {
                try {
                    Thread.sleep(GEN_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                game.nextGeneration();
                clearConsole();
                game.printBoard(i+1);
            }
        } else {
            System.out.println("Invalid control mode. Please choose 'manual' or 'automatic'.");
        }

        scanner.close();
    }
}
