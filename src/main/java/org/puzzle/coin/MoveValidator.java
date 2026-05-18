package org.puzzle.coin;

import java.util.ArrayList;
import java.util.List;

public class MoveValidator {

    /**
     * Checks whether a single transition from {@code before} to {@code after}
     * is a legal move under the puzzle rules.
     */
    public static boolean isLegalMove(String before, String after) {
        // (1) Both states must be valid configurations.
        if (!isValidState(before) || !isValidState(after)) {
            return false;
        }

        // (2) Coin counts must match.
        if (countChar(before, 'O') != countChar(after, 'O')) return false;
        if (countChar(before, 'X') != countChar(after, 'X')) return false;

        // (3) Try every possibl e move from `before` and see if any reaches `after`.
        for (String reachable : allLegalMoves(before)) {
            if (reachable.equals(after)) return true;
        }
        return false;
    }

    /**
     * Validates a complete solution sequence: every consecutive pair must be
     * a legal move, the first state must be the alternating start, and the
     * last must be the sorted goal.
     */
    public static boolean isLegalSolution(String[] sequence, int n) {
        if (sequence.length < 1) return false;
        if (!sequence[0].equals(buildInitialString(n))) return false;
        if (!sequence[sequence.length - 1].equals(buildGoalString(n))) return false;

        for (int i = 0; i + 1 < sequence.length; i++) {
            if (!isLegalMove(sequence[i], sequence[i + 1])) {
                return false;
            }
        }
        return true;
    }

    // ─── State validity ────────────────────────────────────────────────────

    /**
     * A valid state is a string over {O, X, _} where:
     *  - the first and last characters are O or X (no boundary gaps),
     *  - underscores appear only as a single contiguous block of exactly 2,
     *  - that block lies strictly between two coins.
     */
    static boolean isValidState(String s) {
        if (s.isEmpty()) return false;
        if (s.charAt(0) == '_' || s.charAt(s.length() - 1) == '_') return false;

        int firstUnderscore = s.indexOf('_');
        if (firstUnderscore == -1) {
            // No gap: just check all chars are O or X.
            return s.chars().allMatch(c -> c == 'O' || c == 'X');
        }

        // Must be exactly two underscores, contiguous.
        int lastUnderscore = s.lastIndexOf('_');
        if (lastUnderscore - firstUnderscore != 1) return false;
        if (countChar(s, '_') != 2) return false;

        // All other chars must be O or X.
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (i == firstUnderscore || i == lastUnderscore) continue;
            if (c != 'O' && c != 'X') return false;
        }
        return true;
    }

    // ─── Move enumeration ──────────────────────────────────────────────────

    /**
     * Returns all states reachable from `before` by exactly one legal move.
     */
    static List<String> allLegalMoves(String before) {
        List<String> result = new ArrayList<>();

        // For every adjacent unequal pair (OX or XO):
        for (int i = 0; i + 1 < before.length(); i++) {
            char a = before.charAt(i);
            char b = before.charAt(i + 1);
            if (!(a == 'O' && b == 'X') && !(a == 'X' && b == 'O')) continue;

            String pair = "" + a + b;

            // Lift the pair: replace positions i, i+1 with "__".
            String lifted = before.substring(0, i) + "__" + before.substring(i + 2);

            // The lifted string may now have one or two gaps. Try each legal placement.
            // Placement A: append to left end.
            tryPlace(lifted, pair, "left", result);
            // Placement B: append to right end.
            tryPlace(lifted, pair, "right", result);
            // Placement C: into an existing 2-gap (other than the one just created).
            tryPlace(lifted, pair, "gap", result);
        }
        return result;
    }

    private static void tryPlace(String lifted, String pair, String mode, List<String> out) {
        String candidate;
        switch (mode) {
            case "left":
                candidate = pair + lifted;
                break;
            case "right":
                candidate = lifted + pair;
                break;
            case "gap":
                // Find a 2-gap in `lifted` that is NOT the one we just created.
                // Since lifting just made a gap, and at most one other gap may exist,
                // we look for a gap and try filling it. If lifted has two gaps, fill
                // either and trim possible boundary gaps.
                candidate = null;
                int firstGap = lifted.indexOf("__");
                while (firstGap != -1) {
                    String filled = lifted.substring(0, firstGap) + pair + lifted.substring(firstGap + 2);
                    String trimmed = trimBoundaryGaps(filled);
                    if (isValidState(trimmed)) {
                        out.add(trimmed);
                    }
                    firstGap = lifted.indexOf("__", firstGap + 1);
                }
                return;
            default:
                throw new IllegalStateException();
        }
        String trimmed = trimBoundaryGaps(candidate);
        if (isValidState(trimmed)) out.add(trimmed);
    }

    /**
     * Removes leading and trailing "__" sequences from a state string.
     * Models the rule "boundary gaps vanish".
     */
    static String trimBoundaryGaps(String s) {
        while (s.startsWith("__")) s = s.substring(2);
        while (s.endsWith("__")) s = s.substring(0, s.length() - 2);
        return s;
    }

    // ─── Helpers ───────────────────────────────────────────────────────────

    private static int countChar(String s, char c) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) if (s.charAt(i) == c) count++;
        return count;
    }

    private static String buildInitialString(int n) {
        return "OX".repeat(n / 2) + "O";
    }

    private static String buildGoalString(int n) {
        int golds = (n + 1) / 2;
        return "O".repeat(golds) + "X".repeat(n - golds);
    }
}