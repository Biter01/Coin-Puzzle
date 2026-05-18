import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.puzzle.coin.CoinAlgo;
import org.puzzle.coin.CoinBFS;
import org.puzzle.coin.MoveValidator;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class TestCoinBFS {
    CoinBFS coinBFS;
    CoinAlgo coinAlgo;

    @BeforeEach
    void setUp() {
        coinBFS = new CoinBFS();
        coinAlgo = new CoinAlgo();
    }

    @Test
    void testIfLegal() {
        int n1 = 7;
        String solution1 = coinBFS.solve(n1);
        System.out.println(solution1);
        assertTrue(MoveValidator.isLegalSolution(solution1.split("\n"), n1));

        int n2 = 13;
        String solution2 = coinBFS.solve(n2);
        assertTrue(MoveValidator.isLegalSolution(solution2.split("\n"), n2));
    }


    @Test
    void testOnCoinAlgo() {
        int n1 = 21;
        String solutionBfs = coinBFS.solve(n1);
        String[] arrayBfs = solutionBfs.lines().toArray(String[]::new);
        String[] expected = coinAlgo.solve(n1);
        System.out.println(Arrays.toString(expected));
        System.out.println(Arrays.toString(arrayBfs));
        assertEquals(expected.length, arrayBfs.length);
    }

    @Test
    void testIfOptimalSolution() {

        for(int n = 5; n <= 19; n += 2) {
            int m = (n - 1) / 2;
            int expectedSteps = m * (m + 1) * (2 * m + 1) / 6;
            String solutionBfs = coinBFS.solve(n);
            String[] arrayBfs = solutionBfs.lines().toArray(String[]::new);
            String[] expectedArr = coinAlgo.solve(n);
            assertEquals(expectedArr.length, arrayBfs.length);
            assertEquals(expectedSteps, coinBFS.solutionSteps);
            System.out.println("Expected for n " + n + " Steps " + expectedSteps + " With " + coinBFS.solutionSteps);

        }
    }

}
