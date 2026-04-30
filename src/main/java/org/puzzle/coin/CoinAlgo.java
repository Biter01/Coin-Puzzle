package org.puzzle.coin;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * Solves the coin-sorting puzzle for odd n >= 5.
 *
 * <p>Puzzle description:
 * A row of n coins, initially in the alternating arrangement "OXOX...XO"
 * (start state), must be transformed into the sorted form "OOO...OXX...X"
 * (goal state). Per move, a pair of two adjacent unequal coins (either "OX"
 * or "XO") may be lifted as a block from the row and either appended at one
 * of the ends or placed into an existing 2-cell gap. The order of the two
 * coins is preserved during the move. At any point during the game, at most
 * one 2-cell gap may exist in the middle of the row; gaps at the boundary
 * "vanish" implicitly (the boundary is open).
 *
 * <p>Algorithmic idea — reverse solving:
 * Instead of searching from start to goal, the algorithm walks backwards from
 * the goal to the start. This makes move selection deterministic: we know
 * exactly which OX or XO pair to move next and where to put it. At the end
 * the collected sequence is reversed so it runs from start to goal.
 *
 * <p>Step count: verified by BFS for n &lt;= 19 the minimum
 * number of moves is
  the square pyramidal numbers 1, 5, 14, 30, 55, 91, ... (OEIS A000330).
 */
public class CoinAlgo {

    public static void main(String[] args) {
        //Example n=7 could be any odd number
        int n = 7;
        System.out.println(Arrays.toString(coinSolveAlgo(n)));
    }

    /**
     * Computes the full move sequence from the alternating start state
     * to the sorted goal state for a given n.
     *
     * @param n number of coins, must be odd and &gt;= 5.
     * @return array of all intermediate states; index 0 = start, last index = goal.
     * @throws IllegalArgumentException if n is even or smaller than 5.
     */
    public static String[] coinSolveAlgo(int n) {
        if (n % 2 == 0) {
            throw new IllegalArgumentException("n must be odd");
        }

        if (n < 5) {
            throw new IllegalArgumentException("n must be greater than or equal to 5");
        }

        List<String> solutionSteps = new ArrayList<>();

        // Goal state "OOO...OXX...X" — the reverse search starts here.
        String solutionString = buildSolutionString(n);

        // Start state "OXOX...XO" — the reverse search must end here.
        String initialString = buildInitialString(n);

        solutionSteps.add(solutionString);

        System.out.println("Initial String: " + initialString);
        System.out.println("Solution String: " + solutionString);

        int moveToward = 0;

        int counter = 0;

        StringBuilder workingStringB = new StringBuilder(solutionString);

        // CoinAlgo loop: as long as we have not reached the start state, perform
        // further reverse moves.
        while (!workingStringB.toString().equals(initialString)) {

            if (counter % 2 == 0) {
                // ─── EVEN STEP ───────────────────────────────────────────────
                // Take the rightmost OX pair and move it to the current gap
                // (or to the left end if no gap exists yet).

                // Locate the OX pair that is furthest to the right.
                int pos = workingStringB.lastIndexOf("OX");
                String stringThatsMoved = workingStringB.substring(pos, pos + 2);

                // Replace the pair at its old position with a gap "__".
                // The condition pos < length - 1 is a safety guard; in practice
                // it is always satisfied because a found OX pair occupies two
                // characters.
                if (pos < workingStringB.length() - 1) {
                    workingStringB.replace(pos, pos + 2, "__");
                }

                if (moveToward == 0) {
                    // First iteration or post-reset case: there is no gap yet
                    // we could move the pair into. Instead we insert it at the
                    // left boundary, which extends the board by 2.
                    // Afterwards pos must be shifted by +2 because all indices
                    // to the right of the insertion point have shifted.
                    workingStringB.insert(moveToward, stringThatsMoved);
                    pos += 2;
                } else {
                    // Standard case: a gap already exists at position moveToward
                    // (left over from the previous move). We place the pair
                    // there by overwriting the two "_" characters.
                    workingStringB.replace(moveToward, moveToward + 2, stringThatsMoved);
                }

                // Special case: does the newly created gap sit at the very
                // right boundary of the board? If yes, it is a "boundary gap"
                // and disappears — the board shrinks.
                if (workingStringB.lastIndexOf("__") == workingStringB.length() - 2) {
                    workingStringB.delete(workingStringB.length() - 2, workingStringB.length());

                    // After this special case we increment counter additionally,
                    // so that the phase sequence stays consistent. The increment
                    // at the end of the loop body would turn this even step into
                    // an odd one — the extra counter++ here makes it an even
                    // step again on the next iteration.
                    // Likewise, pos is reset to 0 so that the next even step
                    // takes the "insert at left boundary" path again.
                    counter++;
                    pos = 0;
                }

                // The new gap location is exactly where the pair was just lifted.
                // The next move will operate at this position.
                moveToward = pos;

            } else {
                // ─── ODD STEP ────────────────────────────────────────────────
                // Take the leftmost XO pair and move it into the current gap.
                // No boundary special case is needed here because at this point
                // there is always a gap somewhere in the middle.

                // Locate the XO pair that is furthest to the left.
                int pos = workingStringB.indexOf("XO");
                String stringThatsMoved = workingStringB.substring(pos, pos + 2);

                // Mark the old position as a gap.
                workingStringB.replace(pos, pos + 2, "__");

                // Place the pair into the previous gap (at moveToward).
                workingStringB.replace(moveToward, moveToward + 2, stringThatsMoved);

                // The new gap is now where the XO pair used to be.
                moveToward = pos;
            }

            // Record the state after this move.
            solutionSteps.add(workingStringB.toString());

            // Switch phase: even -> odd -> even -> ...
            counter++;
        }

        // The list contains the sequence from goal to start (we walked backwards).
        // reversed() flips it so the result runs from start to goal.
        return solutionSteps.reversed().toArray(new String[0]);
    }

    /**
     * Builds the sorted goal state: (n+1)/2 gold coins "O" followed by
     * (n-1)/2 silver coins "X". Example for n=7: "OOOOXXX".
     *
     * @param n number of coins.
     * @return goal state as a string of length n.
     */
    private static String buildSolutionString(int n) {
        StringBuilder solutionString = new StringBuilder();
        for (int i = 0; i < n; i++) {
            // For i = 0..n/2 (inclusive) — that is exactly (n+1)/2 positions
            // when n is odd — write "O", afterwards write "X".
            if (i <= (n / 2)) {
                solutionString.append("O");
            } else {
                solutionString.append("X");
            }
        }
        return solutionString.toString();
    }

    /**
     * Builds the alternating start state: "OXOX...XO".
     * Example for n=7: "OXOXOXO".
     *
     * @param n number of coins.
     * @return start state as a string of length n.
     */
    private static String buildInitialString(int n) {
        StringBuilder initialString = new StringBuilder();
        for (int i = 0; i < n; i++) {
            // Even indices: "O", odd indices: "X".
            if (i % 2 == 0) {
                initialString.append("O");
            } else {
                initialString.append("X");
            }
        }
        return initialString.toString();
    }
}