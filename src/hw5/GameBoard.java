/*
 * Michael Spadafora
 * Michael.spadafora@stonybrook.edu
 * ID:110992992
 * Homework #5
 * cse214 Recitation section 8
 * Recitation TA: Michael P Rizzo
 * Grading TA: Timothy Zhang
 */
package hw5;

/**
 * the board of the game, with 9 Boxes each representing a space
 * @author mike spad
 */
public class GameBoard {

    private Box[] board;
    private final int boardSize = 9;
    
    /**
     * sets up a new empty array of boxes to represent the gameboard
     */
    public GameBoard(){
        board = new Box[boardSize];
        for(int i = 0; i<9; i++){
            board[i] = Box.EMPTY;
        }   
    }
    
    /**
     * creates a new board from a box[]
     * @param newBoard the board to be set
     */
    public GameBoard(Box[] newBoard){
        board = newBoard;
        
       for(int i = 0; i<9; i++){
            if (board[i]== null)
            {
                board[i] = Box.EMPTY;
            
            }
            
        }
        
    }

    /**
     * 
     * @return all the boxes in board
     */
    public Box[] getBoxes() {
        Box[] tempBoxs = new Box[9];
        int i= 0;
        for(Box b: board){
            tempBoxs[i] = b;
            i++;
        }
        
        return tempBoxs;
    }
    
    
    /**
     * 
     * @param position the position of the board to be returned
     * @return the box at position in board
     */
    public Box getBox(int position){
        return board[position];
    }
    
    public void setBox(int position, Box box){
        
        
    }
    
    /**
     * 
     * @return string representation of the board
     */
    @Override
    public String toString()
    {
        String s ="";
        for(int i = 0; i<9; i++)
        {
            if (i+1%3 == 0){
                s+="\n";
            }
            
            s+=board[i];
        }
        return s;
    }
    
    
    
    
    
}
