package main.t3;
import java.util.*;

/**
 * Artificial Intelligence responsible for playing the game of T3!
 * Implements the alpha-beta-pruning mini-max search algorithm
 */
public class T3Player {
    
    /**
     * Workhorse of an AI T3Player's choice mechanics that, given a game state,
     * makes the optimal choice from that state as defined by the mechanics of
     * the game of Tic-Tac-Total.
     * Note: In the event that multiple moves have equivalently maximal minimax
     * scores, ties are broken by move col, then row, then move number in ascending
     * order (see spec and unit tests for more info). The agent will also always
     * take an immediately winning move over a delayed one (e.g., 2 moves in the future).
     * @param state The state from which the T3Player is making a move decision.
     * @return The T3Player's optimal action.
     */
    public T3Action choose (T3State state) {
        // Initializes variables
        HashMap<Integer, T3Action> finalMoves = new HashMap<>();
        Integer maxEntry = Integer.MAX_VALUE;
        Integer lowestEntry = Integer.MIN_VALUE;
        T3Action Entry = null;

        // Stores the best moves in finalMoves
        finalMoves = generateBestMoves(state, Integer.MIN_VALUE, Integer.MAX_VALUE);

        // Loops through the moves, searching for the lowest depth win, if there is one.
        for(Map.Entry<Integer,T3Action> entry : finalMoves.entrySet()){
            if (entry.getKey() < maxEntry && entry.getKey() > 0){
                maxEntry = entry.getKey();
                Entry = entry.getValue();
            }
        }
        // If there is no win, Entry will be set to the next highest returned score.
        if (Entry == null){
            for(Map.Entry<Integer,T3Action> entry : finalMoves.entrySet()){
                if (entry.getKey() > lowestEntry && entry.getKey() <= 0){
                    lowestEntry = entry.getKey();
                    Entry = entry.getValue();
                }
            }
        }
        // Returns the most optimal move
        return Entry;
    }
    
    /**
     * Loops through the first set of transitions, storing the alpha-beta values for proper pruning.
     * This allows for the correct T3Action to be returned to the choose function.
     * Best moves to make are stored in a Map which is returned.
     * @param node The starting state of the game board.
     * @param a The alpha value.
     * @param b The beta value.
     * @return The Map containing the most optimal win, loss, and tie.
     */
    public HashMap<Integer, T3Action> generateBestMoves(T3State node, int a, int b){
        // Initializes variables
        HashMap<Integer, T3Action> finalMoves = new HashMap<>();
        int[] savedValues = new int[3];
        savedValues[1] = Integer.MIN_VALUE;
        savedValues[2] = Integer.MAX_VALUE;

        // Loops through intial T3Actions from the starting state.
        for(Map.Entry<T3Action,T3State> entry : node.getTransitions(node).entrySet()){
            // Finds the score for the action, using the a and b values stored from previous explorations of other states.
            int[] score = alphaBeta(entry.getValue(), savedValues[1], savedValues[2], 0, 1);

            // Adds the action to a map if there is no current key for the returned score. This prevents slower ties from replacing more optimal ties.
            if(!finalMoves.containsKey(score[0])){
                finalMoves.put(score[0], entry.getKey());
            }
            // sets a and b  to the proper value for the next loop
            if(score[2] > savedValues[1]){
                savedValues[1] = score[2];
            }
            savedValues[2] = Integer.MAX_VALUE;
        }
        return finalMoves;
    } 

    /**
     * Performs alpha-beta pruning on the nodes.
     * The score returned is positive for a win, negative for a loss, zero for a tie.
     * @param node The current state being recursed on.
     * @param a The alpha value.
     * @param b The beta value.
     * @param depth The depth which will be used for scoring. Less depth is better for wins.
     * @param turn The turn (0 is max, 1 is min).
     * @return The int array containing 3 numbers [the score, the alpha value, the beta value].
     */
    public int[] alphaBeta(T3State node, int a, int b, int depth, int turn){
        int v;
        int[] score = new int[3]; 
        
        // Returns the depth as a positive number if a win, negative if a loss, zero if a tie.
        if (node.getTransitions(node).isEmpty()){
            if (node.isWin() && turn == 1){
                score[0] = depth + 1;
            }
            if (node.isWin() && turn == 0){
                score[0] = -depth - 1;
            }
            if (node.isTie()){
                score[0] = 0;
            }
            // Stores proper values in the array to be returned       
            score[1] = a;
            score[2] = b;       
            return score;
        }

        if (turn == 0){
            v = Integer.MIN_VALUE;
            for(Map.Entry<T3Action,T3State> entry : node.getTransitions(node).entrySet()){  
                v = Math.max(v, alphaBeta(entry.getValue(), a, b, depth + 1, 1)[0]);
                a = Math.max(a, v);
                if (b <= a){
                    break;
                }
            }
            score[0] = v;
            score[1] = a;
            score[2] = b; 
            return score;
        }
        else {
            v = Integer.MAX_VALUE;
            for(Map.Entry<T3Action,T3State> entry : node.getTransitions(node).entrySet()){
                v = Math.min(v, alphaBeta(entry.getValue(), a, b, depth + 1, 0)[0]);
                b = Math.min(b, v);
                if (b <= a){
                    break;
                }
            }
            score[0] = v;
            score[1] = a;
            score[2] = b; 
            return score;
        }
    } 
}

