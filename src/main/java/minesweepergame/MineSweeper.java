package minesweepergame;

import java.util.Random;

public class MineSweeper {

    private static final int SIZE = 10;
    CellState grid[][] = new CellState[SIZE][SIZE];
    public boolean mineCell[][] = new boolean[SIZE][SIZE];

    public enum GameState {LOSE, PROGRESS, WIN}
    public enum CellState {EXPOSED, UNEXPOSED, SEALED}

    public MineSweeper() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = CellState.UNEXPOSED;
                mineCell[i][j]=false;
            }
        }
        placeMines();
    }

    void placeMines()
    {
        int numOfMinesPlaced = 0;
        Random randNumGen = new Random();

        int numOfMines = 10;
        while (numOfMinesPlaced < numOfMines) {
            int xAxis = randNumGen.nextInt(SIZE % 11);
            int yAxis = randNumGen.nextInt(SIZE % 11);
            if (!mineCell[xAxis][yAxis]) {
                mineCell[xAxis][yAxis] = true;
                numOfMinesPlaced++;
            }
        }
    }

    public void exposeCell(int row, int col) {
        if(gameStatus() == GameState.PROGRESS)
        {
            if ((grid[row][col] == CellState.UNEXPOSED) ) {
                grid[row][col] = CellState.EXPOSED;

                if (!isAdjacent(row,col)) {
                    exposeNeighborsOf(row, col);
                }
            }
        }
    }


    public void exposeNeighborsOf(int row, int col){
       for (int i = row - 1; i < row + 2; i++) {
           for (int j = col - 1; j < col + 2; j++) {
               if (!(i == row && j == col) &&
                       !(i < 0 || i > SIZE - 1 || j < 0 || j > SIZE - 1) &&
                       !(mineCell[i][j])) {
                   exposeCell(i, j);
               }
           }
       }
    }

    public int getNumberOfAdjacentMines(int row, int col) {
        int numberOfAdjacentMines = 0;

        for (int i = row - 1; i < row + 2; i++) {
            for (int j = col - 1; j < col + 2; j++) {
                if (!(i == row && j == col) &&
                        !(i < 0 || i > SIZE - 1  || j < 0 || j > SIZE - 1) &&
                        mineCell[i][j])
                {

                    numberOfAdjacentMines++;

                }
            }
        }
        return numberOfAdjacentMines;
    }

    public boolean isAdjacent(int row, int col) {
        return getNumberOfAdjacentMines(row, col) != 0 && !mineCell[row][col];
    }

    public void toggleSeal(int row, int col)
    {
        int numOfSealedCells = 0;

        for(int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if(grid[i][j] == CellState.SEALED)
                    numOfSealedCells++;
            }
        }

        if (grid[row][col] == CellState.UNEXPOSED && numOfSealedCells < 10) {
            grid[row][col] = CellState.SEALED;
        }
        else if (grid[row][col] == CellState.SEALED) {
            grid[row][col] = CellState.UNEXPOSED;
        }
    }

    public CellState getCellState(int row, int col)
    {
        return grid[row][col];
    }

    public GameState gameStatus(){
        GameState gameState = GameState.PROGRESS;
        int numberofMInesSealed = 0;
        for(int i = 0; i < 10; i++)
        {
            for(int j = 0; j < 10; j++)
            {
                if((mineCell[i][j]) && (grid[i][j] == CellState.EXPOSED))
                {
                    gameState = GameState.LOSE;
                    return gameState;
                }
                if( (mineCell[i][j]) && (grid[i][j]== CellState.SEALED))
                {
                    numberofMInesSealed++;
                }
                if((mineCell[i][j]) && (grid[i][j] != CellState.SEALED))  {
                    gameState = GameState.PROGRESS;
                }
            }
        }

        if(numberofMInesSealed == 10)
            gameState = GameState.WIN;

        return gameState;
    }
}