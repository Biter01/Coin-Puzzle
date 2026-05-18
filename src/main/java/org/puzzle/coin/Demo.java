package org.puzzle.coin;

import java.util.Arrays;

public class Demo {
    public static void main(String[] args) {
        //Example n=7 could be any odd number
        int n = 7;

        CoinAlgo coinAlgo = new CoinAlgo();

        System.out.println("Initial String " + coinAlgo.buildInitialString(n));
        System.out.println("Solution String " + coinAlgo.buildSolutionString(n));

        String[] solution = coinAlgo.solve(n);

        System.out.println(Arrays.toString(solution));

        if(!MoveValidator.isLegalSolution(solution, n)) {
            System.out.println("The solution is not legal!");
        } else {
            System.out.println("The solution is legal.");
        }


        CoinBFS coinBFS = new CoinBFS();

        int m = (n - 1) / 2;
        int expected = m * (m + 1) * (2 * m + 1) / 6;
        long t0 = System.nanoTime();
        String sol = coinBFS.solve(n);
        long ms = (System.nanoTime() - t0) / 1_000_000;
        int moves = (int) sol.chars().filter(c -> c == '\n').count();
        System.out.println(moves + " expected " + expected + " in " + ms/1000.0 + " s");
        System.out.println(coinBFS.bfsSteps + " bfsSteps");
        System.out.println(sol);

    }
}
