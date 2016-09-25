package minesweepergame.ui;

import minesweepergame.MineSweeper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static minesweepergame.MineSweeper.CellState.*;

public class MineSweeperGUI extends JFrame {

    private static class Cell extends JButton{

        final int row;
        final int column;
        Cell(int row, int col) {
            this.row = row;
            this.column = col;
            setSize(100, 100);
        }

    }

    private static final int SIZE = 10;
    private MineSweeper mineSweeper;
    private static ArrayList<Cell> cellArrayList = new ArrayList<>();
    private JPanel mainPanel = new JPanel();
    private static JFrame frame;
    private Image newFlag;
    private Image newMine;


    private MineSweeperGUI()
    {
        init();
        addAndArrangePanels(this);
    }

    private void init(){

        mineSweeper = new MineSweeper();
        mainPanel.setLayout(new GridLayout(10, 10));
        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                Cell cell = new Cell(i, j);
                cell.setBackground(Color.BLACK);
                cellArrayList.add(cell);
                mainPanel.add(cell);
                cell.addMouseListener(new CellClickedHandler());
            }
        }

        try
        {
            Image flag = ImageIO.read(getClass().getResource("/flag.png"));
            newFlag = flag.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);

            Image mine = ImageIO.read(getClass().getResource("/mine.png"));
            newMine = mine.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
        }

        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void addAndArrangePanels(JFrame frame) {
        JPanel controlPanel = new JPanel();
        GridLayout gridLayout = new GridLayout(1,2);
        controlPanel.setLayout(gridLayout);
        frame.getContentPane().add(controlPanel);
        frame.getContentPane().add(mainPanel);
    }

    private void showAll() {
        for (Cell cell : cellArrayList) {
                boolean mine = mineSweeper.mineCell[cell.row][cell.column];
                if (mine) {
                    cell.setIcon(new ImageIcon(newMine));
                    cell.setBackground(Color.RED);
                    cell.setEnabled(false);
                } else {
                    cell.removeMouseListener(new CellClickedHandler());
                    cell.setBackground(Color.WHITE);
                    cell.setIcon(null);
                    cell.setEnabled(false);
                    int count = mineSweeper.getNumberOfAdjacentMines(cell.row, cell.column);
                    if (count > 0) {
                        cell.setText(Integer.toString(mineSweeper.getNumberOfAdjacentMines(cell.row, cell.column)));
                    }
                }
            }
    }
    public static void main(String[] args){
        EventQueue.invokeLater(() -> {
            frame = new MineSweeperGUI();
            frame.setSize(500, 500);
            frame.setVisible(true);

        });

    }

    private class CellClickedHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            Cell clickedCell = (Cell) mouseEvent.getSource();
            if(SwingUtilities.isRightMouseButton(mouseEvent) || mouseEvent.isControlDown()){
                handleRightClick(clickedCell);
            }
            else{
                handleLeftClick(clickedCell);
            }
            checkWinOrLoss(clickedCell);
        }

        void handleRightClick(Cell cell) {
            mineSweeper.toggleSeal(cell.row, cell.column);
            if(mineSweeper.getCellState(cell.row, cell.column) == SEALED){
                cell.setIcon(new ImageIcon(newFlag));
                cell.setEnabled(false);
            }
            if(mineSweeper.getCellState(cell.row, cell.column) == UNEXPOSED){
                cell.setIcon(null);
                cell.setEnabled(true);
            }
        }

        void handleLeftClick(Cell cell) {
            if(mineSweeper.getCellState(cell.row, cell.column) != SEALED) {
                mineSweeper.exposeCell(cell.row, cell.column);
                if (mineSweeper.getNumberOfAdjacentMines(cell.row, cell.column) == 0) {
                    displayCells();
                }
                if (mineSweeper.isAdjacent(cell.row, cell.column)) {
                    cell.setText(Integer.toString(mineSweeper.getNumberOfAdjacentMines(cell.row, cell.column)));
                    cell.setBackground(Color.WHITE);
                }
            }
        }

        void displayCells(){
            for(Cell cell : cellArrayList) {
                if(mineSweeper.getCellState(cell.row, cell.column) == EXPOSED) {
                    if(mineSweeper.isAdjacent(cell.row, cell.column)) {
                        cell.setText(Integer.toString(mineSweeper.getNumberOfAdjacentMines(cell.row, cell.column)));
                    }
                    cell.setBackground(Color.WHITE);
                    cell.setSelected(true);
                }
            }
        }

        void checkWinOrLoss(Cell cell) {
            if (mineSweeper.gameStatus() == MineSweeper.GameState.LOSE) {
                cell.setIcon(new ImageIcon(newMine));
                cell.getDisabledIcon();
                showAll();
                JOptionPane.showMessageDialog(cell, "You Lost Game Try again");
            }
            if (mineSweeper.gameStatus() == MineSweeper.GameState.WIN) {
                JOptionPane.showMessageDialog(cell, "You Win");
            }
        }
    }
}