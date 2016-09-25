package minesweepergame;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class MineSweeperTest {

    private MineSweeper minesweeper;
    private boolean exposeNeighborsOfCalled;
    private ArrayList<Integer> rowsAndColumns = new ArrayList<>();

    private class MineSweeperWithExposeCell extends MineSweeper{
                                
        @Override
        void placeMines() { }

        @Override
        public void exposeCell(int row, int col) {
            rowsAndColumns.add(row);
            rowsAndColumns.add(col);

        }
    }

    @Test
    public void canary()
    {
        assertTrue(true);
    }

    @Before
    public void setUp()
    {
        minesweeper = new MineSweeper();
        exposeNeighborsOfCalled = false;
    }

    @Test
    public void enumTests()
    {
        MineSweeper.CellState.valueOf(MineSweeper.CellState.EXPOSED.toString());
        MineSweeper.GameState.valueOf(MineSweeper.GameState.PROGRESS.toString());
    }

    @Test
    public void exposeAnUnExposedCell()
    {
        minesweeper.exposeCell(8,8);

        assertEquals(MineSweeper.CellState.EXPOSED, minesweeper.getCellState(8,8));
    }

    @Test
    public void exposeAnExposedCell()
    {
        minesweeper.exposeCell(0,0);
        minesweeper.exposeCell(0,0);

        assertEquals(MineSweeper.CellState.EXPOSED, minesweeper.getCellState(0,0));
    }

    @Test
    public void exposeTwoDifferentCells()
    {
        minesweeper.mineCell[1][2] = false;
        minesweeper.exposeCell(1,2);
        minesweeper.exposeCell(2,3);

        assertEquals(MineSweeper.CellState.EXPOSED, minesweeper.getCellState(1,2));
        assertEquals(MineSweeper.CellState.EXPOSED, minesweeper.getCellState(2,3));
    }

    @Test
    public void exposeCellRowLargerThanMaxValue() {
        try
        {
            minesweeper.exposeCell(10, 0);
            fail("Expected exception for out of row bounds");
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            assertFalse(exposeNeighborsOfCalled);
        }
    }

    @Test
    public void exposeCellRowSmallerThanMinValue() {
        try
        {
            minesweeper.exposeCell(-1, 0);
            fail("Expected exception for out of row bounds");
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            assertFalse(exposeNeighborsOfCalled);
        }
    }

    @Test
    public void exposeCellColumnLargerThanMaxValue() {
        try
        {
            minesweeper.exposeCell(0, 10);
            fail("Expected exception for out of column bounds");
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            assertFalse(exposeNeighborsOfCalled);
        }
    }

    @Test
    public void exposeCellColumnSmallerThanMinValue() {
        try
        {
            minesweeper.exposeCell(0, -1);
            fail("Expected exception for out of column bounds");
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            assertFalse(exposeNeighborsOfCalled);
        }
    }

    @Test
    public void exposeCellWhenRowColumnAreBeyondBoundary()
    {
        MineSweeper mineSweeperTest = new MineSweeper() {
            @Override
            public boolean isAdjacent(int row, int col) { return true; }
        };
        try
        {
            mineSweeperTest.exposeCell(-2, -1);
            fail("Expected exception for out of bounds");
            mineSweeperTest.exposeCell(12,13);
            fail("Expected exception for out of bounds");
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            assertFalse(exposeNeighborsOfCalled);
        }

    }

    @Test
    public void exposeNeighborsOfCalledByExposeCell() {
        MineSweeper mineSweeperTest;
        mineSweeperTest = new MineSweeper() {
            @Override
            public void exposeNeighborsOf(int row, int col) {
                exposeNeighborsOfCalled = true;
            }
            @Override
            public boolean isAdjacent(int row, int col)
            {
                return false;
            }
        };
        mineSweeperTest.mineCell[5][6] = false;
        mineSweeperTest.exposeCell(5, 6);

        assertEquals(MineSweeper.CellState.EXPOSED, mineSweeperTest.getCellState(5,6));
        assertTrue(exposeNeighborsOfCalled);
    }

    @Test
    public void exposeNeighborsOfNotCalledWhenExposedCellReexposed () {
        MineSweeper mineSweeperTest;
        mineSweeperTest = new MineSweeper() {
            @Override
            public void exposeNeighborsOf(int row, int col) {
                exposeNeighborsOfCalled = true;
            }
            @Override
            public boolean isAdjacent(int row, int col)
            {
                return false;
            }
        };

        minesweeper.mineCell[7][7] = false;
        mineSweeperTest.exposeCell(7,7);

        assertTrue(exposeNeighborsOfCalled);

        exposeNeighborsOfCalled = false;

        mineSweeperTest.exposeCell(7,7);

        assertFalse(exposeNeighborsOfCalled);
    }

    @Test
    public void exposeNeighborsOfWhenCellIsAdjacentCell(){
        MineSweeper mineSweeperTest = new MineSweeper() {
            @Override
            public void exposeNeighborsOf(int row, int col) {
                exposeNeighborsOfCalled = true;
            }

            @Override
            public boolean isAdjacent(int row, int col) { return true; }
        };

        mineSweeperTest.exposeCell(2,3);

        assertFalse(exposeNeighborsOfCalled);
    }

    @Test
    public void exposeNeighborsOfWhenCellIsAdjacentAndMinedCell(){
        MineSweeper mineSweeperTest = new MineSweeper() {
            @Override
            public void exposeNeighborsOf(int row, int col) {
                exposeNeighborsOfCalled = true;
            }

            @Override
            public boolean isAdjacent(int row, int col) { return true; }
        };
        mineSweeperTest.mineCell[2][3]=true;
        mineSweeperTest.exposeCell(2,3);

        assertFalse(exposeNeighborsOfCalled);
    }

    @Test
    public void exposeNeighborsOfCallsExposeCells()
    {
        MineSweeper mineSweeper = new MineSweeperWithExposeCell();
        mineSweeper.mineCell[3][4] = false;

        mineSweeper.exposeNeighborsOf(3,4);
        List <Integer> expectedNeighborRowsAndCols = Arrays.asList(2, 3, 2, 4, 2, 5, 3, 3, 3, 5, 4, 3, 4, 4, 4, 5);

        assertEquals(expectedNeighborRowsAndCols,rowsAndColumns);
    }

    @Test
    public void exposeNeighborsOfOnBottomRightCell()
    {
        MineSweeper mineSweeper = new MineSweeperWithExposeCell();
        mineSweeper.mineCell[9][9] = false;

        mineSweeper.exposeNeighborsOf(9, 9);
        List <Integer> expectedNeighborRowsAndCols = Arrays.asList(8, 8, 8, 9, 9, 8);

        assertEquals(expectedNeighborRowsAndCols,rowsAndColumns);
    }

    @Test
    public void exposeNeighborsOfOnBottomLeftCell()
    {
        MineSweeper mineSweeper = new MineSweeperWithExposeCell();
        mineSweeper.mineCell[9][0] = false;

        mineSweeper.exposeNeighborsOf(9, 0);
        List <Integer> expectedNeighborRowsAndCols = Arrays.asList(8, 0, 8, 1, 9, 1);

        assertEquals(expectedNeighborRowsAndCols,rowsAndColumns);
    }

    @Test
    public void exposeNeighborsOfOnTopRightCell()
    {
        MineSweeper mineSweeper = new MineSweeperWithExposeCell();
        mineSweeper.mineCell[0][9] = false;

        mineSweeper.exposeNeighborsOf(0, 9);
        List <Integer> expectedNeighborRowsAndCols = Arrays.asList(0, 8, 1, 8, 1, 9);

        assertEquals(expectedNeighborRowsAndCols,rowsAndColumns);

    }

    @Test
    public void exposeNeighborsOnTopLeftCell(){

        MineSweeper mineSweeper = new MineSweeperWithExposeCell();
        mineSweeper.mineCell[0][0] = false;

        mineSweeper.exposeNeighborsOf(0, 0);
        List <Integer> expectedNeighborRowsAndCols = Arrays.asList(0, 1, 1, 0, 1, 1);

        assertEquals(expectedNeighborRowsAndCols,rowsAndColumns);

    }

    @Test
    public void exposeNeighborsOfWhenCalledOnTheSameCell()
    {
        MineSweeper mineSweeperTest;
        mineSweeperTest = new MineSweeper() {
            @Override
            public void exposeNeighborsOf(int row, int col) {
                exposeNeighborsOfCalled = false;
            }
            @Override
            public boolean isAdjacent(int row, int col)
            {
                return false;
            }
        };

        minesweeper.mineCell[7][7] = false;
        mineSweeperTest.exposeCell(7,7);
        mineSweeperTest.exposeNeighborsOf(7,7);

        assertFalse(exposeNeighborsOfCalled);

    }


    @Test
    public void exposeAfterMinedCellIsExposed()
    {
        minesweeper.mineCell[3][4] = true;

        minesweeper.exposeCell(3,4);

        assertEquals(MineSweeper.CellState.EXPOSED, minesweeper.getCellState(3,4));

        minesweeper.exposeCell(4,4);

        assertEquals(MineSweeper.CellState.UNEXPOSED, minesweeper.getCellState(4,4));
    }

    @Test
    public void verifyMinesCount() {
        int numberOfMines = 0;
        for(int row = 0; row < 10; row++) {
            for(int column = 0; column < 10; column++) {
                if(minesweeper.mineCell[row][column])
                    numberOfMines++;
            }
        }
        assertEquals(10, numberOfMines);
    }

    @Test
    public void minesAreGeneratedRandomly() {
        MineSweeper minesweeperToCheckRandomMines = new MineSweeper();
        boolean unique = false;

        for(int row = 0; row < 10; row++) {
            for(int col = 0; col < 10; col++) {
                if(minesweeper.mineCell[row][col] != minesweeperToCheckRandomMines.mineCell[row][col])
                    unique = true;
            }
        }

        assertTrue(unique);
    }

    @Test
    public void sealAnUnexposedCell(){
        minesweeper.toggleSeal(3, 4);

        assertEquals(MineSweeper.CellState.SEALED, minesweeper.getCellState(3, 4));
    }

    @Test
    public void sealAnExposedCell(){
        minesweeper.exposeCell(6, 6);
        minesweeper.toggleSeal(6, 6);

        assertEquals(MineSweeper.CellState.EXPOSED, minesweeper.getCellState(6, 6));
    }

    @Test
    public void sealedCellCannotBeExposed(){
        minesweeper.toggleSeal(8, 6);
        minesweeper.exposeCell(8, 6);

        assertEquals(MineSweeper.CellState.SEALED, minesweeper.getCellState(8, 6));
    }

    @Test
    public void unexposeASealedCell(){

        minesweeper.grid[3][2]= MineSweeper.CellState.SEALED;
        minesweeper.toggleSeal(3, 2);

        assertEquals(MineSweeper.CellState.UNEXPOSED, minesweeper.getCellState(3, 2));

    }

    @Test
    public void verifyIsAnAdjacentCell()
    {
        minesweeper.mineCell[6][6] = true;
        minesweeper.mineCell[6][7] = false;
        assertTrue(minesweeper.isAdjacent(6,7));
    }

    @Test
    public void isAdjacentWhenMine()
    {

        minesweeper.mineCell[4][2] =true;
        assertFalse(minesweeper.isAdjacent(4,2));
    }

    @Test
    public void toggleSealWhenNumberOfSealedCellsIsMoreThanTen()
    {


        minesweeper.toggleSeal(1,4);
        minesweeper.toggleSeal(2,4);
        minesweeper.toggleSeal(3,4);
        minesweeper.toggleSeal(4,4);
        minesweeper.toggleSeal(5,4);
        minesweeper.toggleSeal(6,4);
        minesweeper.toggleSeal(7,4);
        minesweeper.toggleSeal(8,4);
        minesweeper.toggleSeal(9,4);
        minesweeper.toggleSeal(1,5);
        minesweeper.toggleSeal(2,5);
        assertEquals(MineSweeper.CellState.UNEXPOSED, minesweeper.getCellState(2,5));
    }

    @Test
    public void gameIsLost(){
        minesweeper.mineCell[2][7] = true;

        minesweeper.exposeCell(2,7);

        assertEquals(MineSweeper.GameState.LOSE , minesweeper.gameStatus());
    }

    @Test
    public void gameInProgress(){

        minesweeper.mineCell[1][2] = false;

        minesweeper.exposeCell(1,2);

        assertEquals(MineSweeper.GameState.PROGRESS, minesweeper.gameStatus());
    }

    @Test
    public void gameIsWon(){
    for (int i=0; i<10; i++)
        for (int j=0; j<10; j++)
        {
            if (minesweeper.mineCell[i][j] && minesweeper.grid[i][j] != MineSweeper.CellState.SEALED)
                minesweeper.toggleSeal(i,j);

            if (!minesweeper.mineCell[i][j])
                minesweeper.exposeCell(i,j);
        }
        assertEquals(MineSweeper.GameState.WIN, minesweeper.gameStatus());
    }
}