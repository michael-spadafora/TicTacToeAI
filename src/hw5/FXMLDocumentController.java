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

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * this class controls the TicTacToe game
 *
 * @author mike spad
 */
public class FXMLDocumentController extends Application implements Initializable {

    //private Label label;
    @FXML
    private Button box1; //first square
    @FXML
    private Button box3;//3rd square
    @FXML
    private Button box5; //5th square
    @FXML
    private Button box2; //2nd square
    @FXML
    private Button box6; // 6th square
    @FXML
    private Button box9;//9th square
    @FXML
    private Button box8;//8th square
    @FXML
    private Button box4;//4th square
    @FXML
    private Button box7;//7th square
    @FXML
    private TextField outputBox; // the textfield below the tictactoe board
    @FXML
    private Button undo; //the undo button
    @FXML
    private TextField loseOutputBox; // % lose output box on 2nd tab
    @FXML
    private TextField winOutputBox;// % win output box on 2nd tab
    @FXML
    private TextField drawOutputBox;// % draw output box on 2nd tab

    private GameBoard gameboard; //the current GameBoard
    private GameTree gametree; // the GameTree that was created when game started
    private GameBoardNode currNode; //the current node which contains the current gamestate

    private Button[] buttons; //the array of tictactoe buttons
    @FXML
    private TextArea actionLog; //the log of previous moves
    private String undoString; //the string representation of previous moves
    private boolean gamePlaying = false; //if game has been started and if game isnt ended yet

    /**
     * launches the application
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(FXMLDocumentController.class, args);
    }

    /**
     * opens the FXML document
     *
     * @param primaryStage the stage to be set
     * @throws Exception if document is not found
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(
                "FXMLDocument.fxml"));

        Parent root = fxmlLoader.load();

        primaryStage.setTitle("TicTacToe");
        primaryStage.setScene(new Scene(root, 1600, 600));

        primaryStage.show();

    }

    /**
     * initializes buttons to a button array containing all the buttons on the
     * TicTacToe board post: buttons now contains box1 through box9
     *
     * @param url the url
     * @param rb the resource bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        buttons = new Button[]{box1, box2, box3, box4, box5, box6, box7, box8, box9};

    }

    /**
     * pre: game has started and not ended. happens when a person clicks a tic
     * tac toe button. if the game has started and not ended, it will set the
     * box clicked to an "X" representing a player move and then calculate and
     * make a computer move. otherwise it will display an error message
     *
     * @param event the click of a button in the array buttons
     */
    @FXML
    private void fillBox(ActionEvent event) {
        if (!gamePlaying) {
            outputBox.setText("Choose a mode before playing!");
            return;

        }
        Button b = (Button) (event.getSource());
        if (!"".equals(b.getText())) {
            outputBox.setText("Hey! that box is already taken!");
            return;
        }

        b.setText("X");
        int buttonIndex = getButtonIndex(b) - 1;
        undoString += "Player: " + (buttonIndex + 1) + "\n";
        gametree.makeMove(buttonIndex);
        currNode = gametree.getCursor();
        if (currNode.isEnd()) {
            Box winner = currNode.getWinner();
            if (winner == Box.EMPTY) {
                outputBox.setText("We tied!");
                winOutputBox.setText("0");
                drawOutputBox.setText("1");
                loseOutputBox.setText("0");
            }
            if (winner == Box.O) {
                outputBox.setText("Haha! I won!");
                winOutputBox.setText("1");
                drawOutputBox.setText("0");
                loseOutputBox.setText("0");
            }
            if (winner == Box.X) {
                outputBox.setText("You win!");
                winOutputBox.setText("0");
                drawOutputBox.setText("0");
                loseOutputBox.setText("1");
            }
            gamePlaying = false;
        } else {
            int computerMove = findBestMove();
            makeComputerMove(computerMove);
            currNode = gametree.getCursor();
            if (currNode.isEnd()) {
                Box winner = currNode.getWinner();
                if (winner == Box.EMPTY) {
                    outputBox.setText("We tied!");
                    winOutputBox.setText("0");
                    drawOutputBox.setText("1");
                    loseOutputBox.setText("0");
                }
                if (winner == Box.O) {
                    outputBox.setText("Haha! I won!");
                    winOutputBox.setText("1");
                    drawOutputBox.setText("0");
                    loseOutputBox.setText("0");
                }
                if (winner == Box.X) {
                    outputBox.setText("You win!");
                    winOutputBox.setText("0");
                    drawOutputBox.setText("0");
                    loseOutputBox.setText("1");

                }
                gamePlaying = false;

            }

            refreshUndoBox();
            if (gamePlaying) {
                winOutputBox.setText(String.format("%.2f", currNode.getWinProb()));
                drawOutputBox.setText(String.format("%.2f", currNode.getDrawProb()));
                loseOutputBox.setText(String.format("%.2f", currNode.getLoseProb()));
            }

        }

    }

    /**
     * post: GameTree is navigated based on the move made, and the box is set to
     * an O navigates the GameTree to make a move, and sets the box's text that
     * is selected to an O
     *
     * @param computerMove the move to be made
     */
    private void makeComputerMove(int computerMove) {
        buttons[computerMove].setText("O");
        undoString += "Computer: " + (computerMove + 1) + "\n";
        refreshUndoBox();
        gametree.makeMove(computerMove);

    }

    /**
     * finds the best move based on 1: probability of the tree 2: win/loss in
     * the next turn and 3: if the player has selected opposite corners
     *
     * @return the position from 0 to 8 of the best move
     */
    public int findBestMove() {
        double minLoseProb = 1;
        double currLoseProb = 0;
        int bestMove = 0;
        currNode = gametree.getCursor();
        GameBoardNode[] nextMoves = currNode.getConfig();
        int i = 0;
        GameBoard currBoard = currNode.getBoard();
        int emptyCount = 0;

        for (int z = 0; z < 9; z++) {
            if (currBoard.getBox(z) == Box.EMPTY) {
                emptyCount++;
            }

        }
        if (emptyCount >= 5 && ((currBoard.getBox(0) == Box.X && currBoard.getBox(8) == Box.X)
                || (currBoard.getBox(2) == Box.X
                && currBoard.getBox(6) == Box.X))) {
            return 1;

        }

        for (GameBoardNode gbn : nextMoves) {
            if (gbn != null) {

                currLoseProb = gbn.getLoseProb();
                if (gbn.isEnd()) {
                    bestMove = i;
                    return bestMove;
                }

                if (!gbn.hasLoss() && gbn.getLoseProb() <= minLoseProb) {
                    bestMove = i;
                    minLoseProb = gbn.getLoseProb();
                }
            }
            i++;
        }

        return bestMove;

    }

    /**
     *
     * @param but the button that was pressed
     * @return the index of the button pressed
     */
    private int getButtonIndex(Button but) {
        if (but == box1) {
            return 1;
        }
        if (but == box2) {
            return 2;
        }
        if (but == box3) {
            return 3;
        }
        if (but == box4) {
            return 4;
        }
        if (but == box5) {
            return 5;
        }
        if (but == box6) {
            return 6;
        }
        if (but == box7) {
            return 7;
        }
        if (but == box8) {
            return 8;
        }
        if (but == box9) {
            return 9;
        } else {
            return 0;
        }
    }

    /**
     * pre: there are 2 moves to undo this will undo the last two moves in the
     * GameTree and refresh the gameboard to reflect the changes made
     *
     * @param event the press of the undo button
     */
    @FXML
    private void undoLastTwoMoves(ActionEvent event) {
        try {
            gametree.undoTwoMoves();
            currNode = gametree.getCursor();
            refreshGameBoard();
            undoString = undoString.substring(0, undoString.lastIndexOf("\n"));
            undoString = undoString.substring(0, undoString.lastIndexOf("\n"));
            undoString = undoString.substring(0, undoString.lastIndexOf("\n"));
            undoString += "\n";
            refreshUndoBox();
        } catch (IndexOutOfBoundsException ex) {
            undoString = "";
            refreshUndoBox();
        } catch (Exception ex) {
            outputBox.setText(ex.getMessage());
        }

    }

    /**
     * post: the GameTree is initialized to have the first move be an O. the
     * board is emptied and a move is made by the computer. starts a new game,
     * with the computer going first.
     *
     * @param event the press of the newGameComputerFirst button
     */
    @FXML
    private void newGameComputerFirst(ActionEvent event) {
        reset();
        gamePlaying = true;
        gametree = new GameTree(Box.O);
        undoString = "";
        refreshUndoBox();
        makeComputerMove(findBestMove());
        outputBox.setText("Okay! lets play a new game. I'll go first.");
    }

    /**
     * post: the GameTree is initialized to have the first move be an X and the
     * board is emptied
     *
     * @param event the press of the newGamePlayerFirst button
     */
    @FXML
    private void newGamePlayerFirst(ActionEvent event) {
        reset();
        gamePlaying = true;
        gametree = new GameTree(Box.X);
        undoString = "";
        refreshUndoBox();
        outputBox.setText("Okay! lets play a new game. you go first.");
    }

    /**
     * resets the gameBoard to be an empty GameBoard
     */
    private void reset() {
        gameboard = new GameBoard();
        for (int i = 0; i < 9; i++) {
            buttons[i].setText("");
        }

    }

    /**
     * refreshes the GameBoard to be up to date with the current node in the
     * GameTree
     */
    private void refreshGameBoard() {

        for (int i = 0; i < 9; i++) {
            if (currNode.getBoard().getBox(i) == Box.X) {
                buttons[i].setText("X");
            }
            if (currNode.getBoard().getBox(i) == Box.O) {
                buttons[i].setText("O");
            }
            if (currNode.getBoard().getBox(i) == Box.EMPTY) {
                buttons[i].setText("");
            }
        }
        winOutputBox.setText(String.format("%.2f", currNode.getWinProb()));
        drawOutputBox.setText(String.format("%.2f", currNode.getDrawProb()));
        loseOutputBox.setText(String.format("%.2f", currNode.getLoseProb()));

    }

    /**
     * refreshes the textField that is above the undo box, which displays the
     * moves made in the order that they were made
     */
    private void refreshUndoBox() {
        actionLog.setText(undoString);
    }

}
