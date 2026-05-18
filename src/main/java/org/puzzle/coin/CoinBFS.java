package org.puzzle.coin;

import java.util.*;

/**
 * BFS solver for the coin sorting puzzle.
 *
 * <p>State encoding: a String over {O, X, _}, where '_' denotes an empty cell.
 * The row may grow or shrink as pairs are appended at the boundary; we trim
 * boundary underscores (canonical form) so translation-equivalent states are
 * treated as identical.
 *
 * <p>Game rule (enforced after every move):
 *   The row has at MOST ONE interior gap, and that gap has length EXACTLY 2.
 *   Underscores at the very left or right end don't count — they vanish.
 */
public final class CoinBFS {
    public long bfsSteps = 0;
    public int solutionSteps = 0;
    // ────────────────────────────────────────────────────────────────────────
    //  Public entry point
    // ────────────────────────────────────────────────────────────────────────


    public String solve(int n) {
        String start = buildStart(n);
        String goal  = buildGoal(n);
        return solve(start, goal);
    }

    public String solve(String start, String goal) {

        String s0 = canonical(start);
        String g  = canonical(goal);

        Queue<String> queue = new ArrayDeque<>();
        Map<String, String> parent = new HashMap<>();

        queue.add(s0);
        parent.put(s0, null);

        while (!queue.isEmpty()) {
            String state = queue.poll();
            if (state.equals(g)) return reconstruct(parent, state);

            for (String next : neighbors(state)) {
                if (!parent.containsKey(next)) {
                    parent.put(next, state);
                    queue.add(next);
                }
                bfsSteps++;
            }

        }
        return "no solution";
    }

    private  String reconstruct(Map<String, String> parent, String goal) {
        List<String> path = new ArrayList<>();
        for (String cur = goal; cur != null; cur = parent.get(cur)) path.add(cur);
        Collections.reverse(path);
        solutionSteps = path.size()-1;
        return String.join("\n", path);

    }

    // ────────────────────────────────────────────────────────────────────────
    //  Move generation
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Generate all legal successor states.
     *
     * <p>For each adjacent unequal pair at (i, i+1):
     *   1. Lift it — positions i, i+1 become underscores in `removed`.
     *   2. Determine drop sites in the POST-LIFT row:
     *        a) into the pre-existing interior 2-gap (if any), and
     *        b) at the left boundary, and
     *        c) at the right boundary.
     *      Dropping into the freshly-created lift gap is a no-op and skipped.
     *   3. For each drop, canonicalize and validate the result.
     */
    private  List<String> neighbors(String s) {
        List<String> result = new ArrayList<>();
        int n = s.length();

        // Where is the (at most one) pre-existing interior gap?
        int preGap = findInteriorGap(s);

        for (int i = 0; i < n - 1; i++) {
            char a = s.charAt(i);
            char b = s.charAt(i + 1);
            if (!((a == 'O' && b == 'X') || (a == 'X' && b == 'O'))) continue;

            String pair = "" + a + b;
            String removed = s.substring(0, i) + "__" + s.substring(i + 2);

            // ── (a) Drop into the PRE-EXISTING interior gap, if any.
            //        We use preGap (computed before the lift) so we never
            //        drop into the lift hole itself.
            if (preGap != -1) {
                String inserted = removed.substring(0, preGap) + pair + removed.substring(preGap + 2);
                addIfLegal(result, inserted);
            }

            // ── (b) Drop at left boundary.
            //        The lift hole becomes a gap; if it's adjacent to the
            //        edge it will be trimmed by canonical().
            addIfLegal(result, pair + removed);

            // ── (c) Drop at right boundary.
            addIfLegal(result, removed + pair);
        }
        return result;
    }

    private  void addIfLegal(List<String> result, String candidate) {
        String c = canonical(candidate);
        if (c.isEmpty()) return;
        if (isLegalState(c)) result.add(c);
    }

    // ────────────────────────────────────────────────────────────────────────
    //  State helpers
    // ────────────────────────────────────────────────────────────────────────

    /** Trim leading/trailing underscores — they're boundary, not interior. */
    private  String canonical(String s) {
        int lo = 0, hi = s.length() - 1;
        while (lo <= hi && s.charAt(lo) == '_') lo++;
        while (hi >= lo && s.charAt(hi) == '_') hi--;
        return (lo > hi) ? "" : s.substring(lo, hi + 1);
    }

    /**
     * Position of the (unique) interior 2-cell gap in a canonical string,
     * or -1 if none. Assumes input is already canonical.
     */
    private  int findInteriorGap(String s) {
        for (int i = 1; i < s.length() - 2; i++) {
            if (s.charAt(i) == '_' && s.charAt(i + 1) == '_') return i;
        }
        return -1;
    }

    /**
     * A state is legal if it has at most one maximal run of underscores,
     * and that run has length exactly 2.
     */
    private  boolean isLegalState(String s) {
        int runs = 0;
        int i = 0;
        while (i < s.length()) {
            if (s.charAt(i) == '_') {
                int j = i;
                while (j < s.length() && s.charAt(j) == '_') j++;
                int len = j - i;
                if (len != 2) return false;
                runs++;
                if (runs > 1) return false;
                i = j;
            } else i++;
        }
        return true;
    }

    private  String buildStart(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(i % 2 == 0 ? 'O' : 'X');
        return sb.toString();
    }

    private  String buildGoal(int n) {
        StringBuilder sb = new StringBuilder();
        int oCount = (n + 1) / 2;
        for (int i = 0; i < n; i++) sb.append(i < oCount ? 'O' : 'X');
        return sb.toString();
    }
}