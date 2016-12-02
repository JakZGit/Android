package com.google.engedu.blackhole;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import android.view.View;
import android.widget.Button;


/* Class that represent the state of the game.
 * Note that the buttons on screen are not updated by this class.
 */
public class BlackHoleBoard {
    // The number of turns each player will take.
    public final static int NUM_TURNS = 10;
    // Size of the game board. Each player needs to take 10 turns and leave one empty tile.
    public final static int BOARD_SIZE = NUM_TURNS * 2 + 1;
    // Relative position of the neighbors of each tile. This is a little tricky because of the
    // triangular shape of the board.
    public final static int[][] NEIGHBORS = {{-1, -1}, {0, -1}, {-1, 0}, {1, 0}, {0, 1}, {1, 1}};
    // When we get to the Monte Carlo method, this will be the number of games to simulate.
    private static final int NUM_GAMES_TO_SIMULATE = 5000;
    // The tiles for this board.
    private BlackHoleTile[] tiles;
    // The number of the current player. 0 for user, 1 for computer.
    private int currentPlayer;
    // The value to assign to the next move of each player.
    private int[] nextMove = {1, 1};
    // A single random object that we'll reuse for all our random number needs.
    private static final Random random = new Random();

    // Constructor. Nothing to see here.
    BlackHoleBoard() {
        tiles = new BlackHoleTile[BOARD_SIZE];
        reset();
    }

    // Copy board state from another board. Usually you would use a copy constructor instead but
    // object allocation is expensive on Android so we'll reuse a board instead.
    public void copyBoardState(BlackHoleBoard other) {
        this.tiles = other.tiles.clone();
        this.currentPlayer = other.currentPlayer;
        this.nextMove = other.nextMove.clone();
    }

    // Reset this board to its default state.
    public void reset() {
        currentPlayer = 0;
        nextMove[0] = 1;
        nextMove[1] = 1;
        for (int i = 0; i < BOARD_SIZE; i++) {
            tiles[i] = null;
        }
    }

    // Translates column and row coordinates to a location in the array that we use to store the
    // board.
    protected int coordsToIndex(int col, int row) {
        return col + row * (row + 1) / 2;
    }

    // This is the inverse of the method above.
    protected Coordinates indexToCoords(int i) {
        Coordinates result = null;
        // TODO: Compute the column and row number for the ith location in the array.
        result = new Coordinates(0,0);
        result.y = ((int)java.lang.Math.sqrt(8*i+1)-1)/2;
        result.x = i-(result.y*(result.y+1))/2;

        // The row number is the triangular root of i as explained in wikipedia:
        // https://en.wikipedia.org/wiki/Triangular_number#Triangular_roots_and_tests_for_triangular_numbers
        // The column number is i - (the number of tiles in all the previous rows).
        // This is tricky to compute correctly so use the unit test in BlackHoleBoardTest to get it
        // right.
        return result;
    }

    // Getter for the number of the player's next move.
    public int getCurrentPlayerValue() {
        return nextMove[currentPlayer];
    }

    // Getter for the number of the current player.
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    // Check whether the current game is over (only one blank tile).
    public boolean gameOver() {
        int empty = -1;
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (tiles[i] == null) {
                if (empty == -1) {
                    empty = i;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    // Pick a random valid move on the board. Returns the array index of the position to play.
    public int pickRandomMove() {
        ArrayList<Integer> possibleMoves = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (tiles[i] == null) {
                possibleMoves.add(i);
            }
        }
        return possibleMoves.get(random.nextInt(possibleMoves.size()));
    }

    // Pick a good move for the computer to make. Returns the array index of the position to play.
    public int pickMove() {
        // TODO: Implement this method have the computer make a move.
        // At first, we'll just invoke pickRandomMove (above) but later, you'll need to replace
        // it with an algorithm that uses the Monte Carlo method to pick a good move.
        HashMap<Integer,Integer> scores = new HashMap<Integer,Integer>();
        for(int i=0;i<NUM_GAMES_TO_SIMULATE;i++){
            BlackHoleBoard copyBoard = new BlackHoleBoard();
            copyBoard.copyBoardState(this);

            //store the first random move
            int position = pickRandomMove();
            int firstPositionKey = position;
            copyBoard.setValue(position);

            if(!scores.containsKey(position))
                scores.put(position,0);

            while(!copyBoard.gameOver()){
                position = pickRandomMove();
                copyBoard.setValue(position);

            }

            //the lower the score the better, getScore computes score of user, we want score to work
            //in favor of computer, next line caculates the average of the existing value(score) in the hashmap
            //with the new score computed that starting with the same move (key)
            scores.put(firstPositionKey, (scores.get(firstPositionKey)+copyBoard.getScore())/2);
        }

        //find the entry with the minimum value (points) and return that first move(key)
        Map.Entry<Integer, Integer> minEntry = null;
        for (Map.Entry<Integer, Integer> entry : scores.entrySet()) {
            if (minEntry == null
                    || entry.getValue().compareTo(minEntry.getValue()) < 0) {
                minEntry = entry;
            }
        }

        return minEntry.getKey();
    }

    // Makes the next move on the board at position i. Automatically updates the current player.
    public void setValue(int i) {
        tiles[i] = new BlackHoleTile(currentPlayer, nextMove[currentPlayer]);
        nextMove[currentPlayer]++;
        currentPlayer++;
        currentPlayer %= 2;
    }

    /* If the game is over, computes the score for the current board by adding up the values of
     * all the tiles that surround the empty tile.
     * Otherwise, returns 0.
     */
    public int getScore() {
        int score = 0;
        // TODO: Implement this method to compute the final score for a given board.
        // Find the empty tile left on the board then add/substract the values of all the
        // surrounding tiles depending on who the tile belongs to.
        ArrayList<BlackHoleTile> list = new ArrayList<>();
        for(int i=0;i<tiles.length;i++){
            if(tiles[i]==null){
                list = getNeighbors(indexToCoords(i));
                break;
            }
        }

        for(int j=0;j<list.size();j++){
            if(list.get(j).player == 1)
                score+=list.get(j).value;
            else
                score-=list.get(j).value;
        }

        return score;
    }

    // Helper for getScore that finds all the tiles around the given coordinates.
    private ArrayList<BlackHoleTile> getNeighbors(Coordinates coords) {
        ArrayList<BlackHoleTile> result = new ArrayList<>();
        for(int[] pair : NEIGHBORS) {
            BlackHoleTile n = safeGetTile(coords.x + pair[0], coords.y + pair[1]);
            if (n != null) {
                result.add(n);
            }
        }
        return result;
    }

    // Helper for getNeighbors that gets a tile at the given column and row but protects against
    // array over/underflow.
    private BlackHoleTile safeGetTile(int col, int row) {
        if (row < 0 || col < 0 || col > row) {
            return null;
        }
        int index = coordsToIndex(col, row);
        if (index >= BOARD_SIZE) {
            return null;
        }
        return tiles[index];
    }
}
