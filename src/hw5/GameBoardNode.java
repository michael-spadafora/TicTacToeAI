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
 * this class has a GameBoard that represents the current state of the board and
 * it has children who are also game board nodes. Additionally, it has a win
 * lose and draw probability
 *
 * @author mike spad
 */
public class GameBoardNode {

    private GameBoard board; // the board state at this node
    private boolean isEnd; // if the board shows a win/loss/draw state
    private Box currentTurn; //whos turn it currently is
    private Box winner; // if game is over, the winner of the game otherwise empty
    private GameBoardNode config[]; // children of thsi GameBoardNode
    private double winProb; // % of win leaves from this nodes
    private double loseProb; // % of lose leaves from this node
    private double drawProb; // % of draw leaves from this node
    private int wins, loss, draws; // # of win/lose/draw leaves from this node
    private GameBoardNode parent; // the parent of this node
    //private int numLeaf;

    /**
     * pre: currentTurn is not Box.EMPTY. board is not null.
     *
     * @param _board the board to be set
     * @param _currentTurn the currentTurn to be set
     * @throws IllegalArgumentException if _board is null or _currentTurn is
     * Box.Empty
     */
    public GameBoardNode(GameBoard _board, Box _currentTurn)
            throws IllegalArgumentException {
        if (_board == null || _currentTurn == Box.EMPTY) {
            throw new IllegalArgumentException("Invalid entry");
        }
        board = _board;
        currentTurn = _currentTurn;
        config = new GameBoardNode[9];
        wins = 0;
        loss = 0;
        draws = 0;
        //setProbabilities();

    }

    /**
     *
     * @param position the child of the tree to be gotten
     * @return the child at position
     */
    public GameBoardNode getChild(int position) {
        return config[position];
    }

    /**
     * will set the win, loss and draw probabilities based on the number of win,
     * loss, and drawn leaves
     */
    public final void setProbabilities() {
        getProbabilities(this);
        int numLeaf = wins + loss + draws;

        winProb = ((double) (wins)) / numLeaf;
        loseProb = ((double) (loss)) / numLeaf;
        drawProb = ((double) (draws)) / numLeaf;
    }

    /**
     * this will set the current child at position pos to box
     *
     * @param pos the position to be set
     * @param box the box to be set
     */
    public void setConfig(int pos, Box box) {
        Box[] nextBoard = board.getBoxes();
        nextBoard[pos] = box;
        GameBoard newBoard = new GameBoard(nextBoard);
        Box nextTurn = box;
        if (box == Box.O) {
            nextTurn = Box.X;

        }
        if (box == Box.X) {
            nextTurn = Box.O;
        }

        config[pos] = new GameBoardNode(newBoard, nextTurn);

    }

    /**
     *
     * @return if this node has a move where the player will win and the
     * computer will lose
     */
    public boolean hasLoss() {
        for (GameBoardNode gbn : config) {

            if (gbn != null && gbn.isWin() == Box.X) {
                return true;
            }

        }
        return false;
    }

    /**
     *
     * @param gbn the node to be checked
     * @return if the gbn is a leaf ie does not have children
     */
    private boolean isLeaf(GameBoardNode gbn) {
        for (int i = 0; i < 9; i++) {
            if (gbn.getChild(i) != null) {
                return false;
            }
        }
        //isEnd = true;
        return true;
    }

    /**
     * post: the number of wins, losses, and draws in the children are set gets
     * the number of wins, losses, and draws to be used in setProbabilities
     *
     * @param gbn the node to be checked
     */
    private void getProbabilities(GameBoardNode gbn) {
        if (gbn == null) {
            return;
        }
        //System.out.print("gbn is okay");
        for (int i = 0; i < 9; i++) {
            if (gbn.getChild(i) != null) {
                if (gbn.getChild(i).isWin() == Box.O) {
                    wins++;
                } else if (gbn.getChild(i).isWin() == Box.X) {
                    loss++;
                } else if (isLeaf(gbn.getChild(i))
                        && gbn.getChild(i).isWin() == Box.EMPTY) {
                    draws++;
                } else {
                    getProbabilities(gbn.getChild(i));
                }
            }
        }
        //int[] tempArray = {wins, loss, draws};
        //return tempArray;
    }

    /**
     * checks if this is a won game for either x's or o's. returns empty
     * otherwise
     *
     * @return the winner of the current GameBoardNode
     */
    public Box isWin() {
        Box[] boxes = board.getBoxes();
        for (int i = 0; i < 3; i++) {
            if (boxes[i] == boxes[i + 3]
                    && boxes[i + 3] == boxes[i + 6]) {
                if (boxes[i] != Box.EMPTY) {
                    winner = boxes[i];
                    isEnd = true;
                    return boxes[i];

                }
            }
        }
        for (int i = 0; i < 3; i++) {
            if (boxes[3 * i] == boxes[3 * i + 1]
                    && boxes[3 * i + 1] == boxes[3 * i + 2]) {
                if (boxes[3 * i] != Box.EMPTY) {
                    winner = boxes[3 * i];
                    isEnd = true;
                    return board.getBox(3 * i);
                }
            }
        }
        if (boxes[0] == boxes[4]
                && boxes[4] == boxes[8]) {
            if (boxes[0] != Box.EMPTY) {
                winner = boxes[0];
                isEnd = true;
                return board.getBox(0);
            }
        }
        if (boxes[2] == boxes[4]
                && boxes[4] == boxes[6] && boxes[2] != Box.EMPTY) {
            winner = boxes[2];
            isEnd = true;
            return board.getBox(2);
        }
        winner = Box.EMPTY;
        isEnd = false;
        return Box.EMPTY;

    }

    /**
     * sets isEnd and returns isEnd
     *
     * @return whether the game has ended
     */
    public Boolean isEnd() {
        if (isLeaf(this)) {
            isEnd = true;
        }
        return isEnd;
    }

    /**
     *
     * @return the probability of computer winning
     */
    public double getWinProb() {
        setProbabilities();
        return winProb;
    }

    /**
     *
     * @return the probability of computer losing
     */
    public double getLoseProb() {
        setProbabilities();
        return loseProb;
    }

    /**
     *
     * @return the probability of a draw
     */
    public double getDrawProb() {
        setProbabilities();
        return drawProb;
    }

    /**
     *
     * @return the current board of the GameBoardNode
     */
    public GameBoard getBoard() {
        return board;
    }

    /**
     *
     * @return the winner of this current GameBoardNode
     */
    public Box getWinner() {
        return winner;
    }

    /**
     *
     * @param parent the parent of this GameBoardNode to be set
     */
    public void setParent(GameBoardNode parent) {
        this.parent = parent;
    }

    /**
     *
     * @return the parent of this node
     */
    public GameBoardNode getParent() {
        return parent;
    }

    /**
     *
     * @return the children of this GameBoardNode
     */
    public GameBoardNode[] getConfig() {
        return config;
    }

}
