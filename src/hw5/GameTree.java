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
 * the GameTree class builds a tree of GameBoardNodes
 * @author mike spad
 */
public class GameTree {

    private GameBoardNode root; // the root of the tree
    private GameBoardNode cursor; //the current selected GameBoardNode
    

    /**
     * the constructor for the GameTree class
     * @param currTurn the Box who's move it currently is
     */
    public GameTree(Box currTurn) {
        root = new GameBoardNode(new GameBoard(), currTurn);

        root = buildTree(root, currTurn);
        cursor = root;
        root.setProbabilities();

    }

    /**
     * pre: position is between 0 and 9
     * this will change the current GameTree to the subtree signified by the 
     * position
     * @param position the tree to be gotten
     */
    public void makeMove(int position) {
        if (position < 0 || position > 8) {
            throw new IllegalArgumentException("Position not within range");

        }
        if (cursor.getChild(position) == null) {
            throw new IllegalArgumentException("Position already taken");
        }
        cursor = cursor.getChild(position);
        cursor.setProbabilities();

    }
    
    /**
     * pre: there has been a player move ie. there has been two moves so far
     * in the game.
     * post: the game has reverted to a state from two moves ago
     * this will undo two moves from the tree ie. it will get the parent of 
     * the parent of the current node
     * @throws Exception if the player has not made a move, OR if 
     * the game is over
     */
    public void undoTwoMoves() throws Exception{
        if (cursor.getParent() == null) {
            throw new Exception("Can't undo something that hasnt been done");       
        }
        if (cursor.getParent().getParent() == null) {
            throw new Exception("Can't undo something that hasnt been done");  
        }
        if (cursor.isEnd())
        {
            throw new Exception("Hey! the game is over, you can't undo anything");           
        }
        
        cursor = cursor.getParent().getParent();
    }

    /**
     * this will build a tree by filling the config of the first root
     * @param newRoot the base root whose config will be filled
     * @param currentTurn the turn that the tree will start at
     * @return the root of the tree
     */
    public static GameBoardNode buildTree(GameBoardNode newRoot,
            Box currentTurn) {
        int i = 0;
        
        Box nextTurn = invertBox(currentTurn);

        Box[] boxes = newRoot.getBoard().getBoxes();
        
        /*for (i = 0; i<9; i ++){
        System.out.println(boxes[0]);}*/
        for (i = 0; i < 9; i++) {
            if (boxes[i] == Box.EMPTY && checkWin(newRoot) == Box.EMPTY) {
                newRoot.setConfig(i, currentTurn);
                newRoot.getChild(i).setParent(newRoot);

            }
        }
        for (i = 0; i < 9; i++) {
            if (newRoot.getChild(i) != null) {
                buildTree(newRoot.getChild(i), nextTurn);
            }
        }
        //newRoot.setProbabilities();
        return newRoot;

        // return null;
    }

    /**
     * checks if the game state is a win
     *
     * @param node the node to be checked
     * @return the winner of the game, or Box.EMPTY if not won
     */
    public static Box checkWin(GameBoardNode node) {
        return (node.isWin());
    }

    /**
     * 
     * @return the probability of winning from current board state
     */
    public double cursorProbability() {
        return cursor.getWinProb();
    }

    /**
     * this will switch x boxes to o boxes and vice versa
     * @param box the box to be switched
     * @return  the switched box
     */
    private static Box invertBox(Box box) {
        if (box == Box.EMPTY) {
            return Box.EMPTY;
        }
        if (box == Box.X) {
            return Box.O;
        }
        if (box == Box.O) {
            return Box.X;
        }
        return null;
    }

    /**
     * 
     * @return the cursor of the GameTree
     */
    public GameBoardNode getCursor() {
        return cursor;
    }

    /**
     * 
     * @return the root of the GameTree
     */
    public GameBoardNode getRoot() {
        return root;
    }

}
