import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.puzzle.coin.CoinAlgo;
import static org.junit.jupiter.api.Assertions.*;


public class CoinAlgoTest {
    CoinAlgo coinAlgo;

    @BeforeEach
    void setUp() {
        coinAlgo = new CoinAlgo();
    }

    @Test
    void testOddTooSmall() {
        int n = 3;
        assertThrows(IllegalArgumentException.class, () -> coinAlgo.solve(n));
    }

    @Test
    void testEven() {
        int n1 = 4;
        assertThrows(IllegalArgumentException.class, () -> coinAlgo.solve(n1));

        int n2 = 22;
        assertThrows(IllegalArgumentException.class, () -> coinAlgo.solve(n2));
    }

    @Test
    void testOddSmallest() {
        int n = 5;
        String[] expected = {"OXOXO", "OXOOX", "OOXOX", "O__OXOX", "OXOO__X", "OOOXX"};
        assertArrayEquals(expected, coinAlgo.solve(n));
    }

    @Test
    void testOddSmall() {
        int n = 9;
        String[] expected = {
                "OXOXOXOXO",
                "OXOXOXOOX",
                "OXOXOOXOX",
                "OXOOXOXOX",
                "OOXOXOXOX",
                "O__OXOXOXOX",
                "OXOOXOXO__X",
                "OOXOXOOXX",
                "O__OXOOXXOX",
                "OXOOXOOX__X",
                "OOXOOXOXX",
                "O__OOXOXXOX",
                "OXOOOXOX__X",
                "OOOXOXOXX",
                "OO__OXOXXOX",
                "OOXOOXOX__X",
                "O__OOXOXOXX",
                "OXOOOXO__XX",
                "OOOXOOXXX",
                "OO__OOXXXOX",
                "OOXOOOXX__X",
                "O__OOOXXOXX",
                "OXOOOOX__XX",
                "OOOOXOXXX",
                "OOO__OXXXOX",
                "OOOXOOXX__X",
                "OO__OOXXOXX",
                "OOXOOOX__XX",
                "O__OOOXOXXX",
                "OXOOOO__XXX",
                "OOOOOXXXX"
        };
        assertArrayEquals(expected, coinAlgo.solve(n));
    }

    @Test
    void testOddBigger() {
        int n = 11;
        String[] expected = {
                "OXOXOXOXOXO",
                "OXOXOXOXOOX",
                "OXOXOXOOXOX",
                "OXOXOOXOXOX",
                "OXOOXOXOXOX",
                "OOXOXOXOXOX",
                "O__OXOXOXOXOX",
                "OXOOXOXOXO__X",
                "OOXOXOXOOXX",
                "O__OXOXOOXXOX",
                "OXOOXOXOOX__X",
                "OOXOXOOXOXX",
                "O__OXOOXOXXOX",
                "OXOOXOOXOX__X",
                "OOXOOXOXOXX",
                "O__OOXOXOXXOX",
                "OXOOOXOXOX__X",
                "OOOXOXOXOXX",
                "OO__OXOXOXXOX",
                "OOXOOXOXOX__X",
                "O__OOXOXOXOXX",
                "OXOOOXOXO__XX",
                "OOOXOXOOXXX",
                "OO__OXOOXXXOX",
                "OOXOOXOOXX__X",
                "O__OOXOOXXOXX",
                "OXOOOXOOX__XX",
                "OOOXOOXOXXX",
                "OO__OOXOXXXOX",
                "OOXOOOXOXX__X",
                "O__OOOXOXXOXX",
                "OXOOOOXOX__XX",
                "OOOOXOXOXXX",
                "OOO__OXOXXXOX",
                "OOOXOOXOXX__X",
                "OO__OOXOXXOXX",
                "OOXOOOXOX__XX",
                "O__OOOXOXOXXX",
                "OXOOOOXO__XXX",
                "OOOOXOOXXXX",
                "OOO__OOXXXXOX",
                "OOOXOOOXXX__X",
                "OO__OOOXXXOXX",
                "OOXOOOOXX__XX",
                "O__OOOOXXOXXX",
                "OXOOOOOX__XXX",
                "OOOOOXOXXXX",
                "OOOO__OXXXXOX",
                "OOOOXOOXXX__X",
                "OOO__OOXXXOXX",
                "OOOXOOOXX__XX",
                "OO__OOOXXOXXX",
                "OOXOOOOX__XXX",
                "O__OOOOXOXXXX",
                "OXOOOOO__XXXX",
                "OOOOOOXXXXX"
        };
        assertArrayEquals(expected, coinAlgo.solve(n));

    }


    @Test
    void testSolutionOnSteps() {

        for(int n = 5; n <= 101; n += 2) {
            int m = (n - 1) / 2;
            int expectedSteps = m * (m + 1) * (2 * m + 1) / 6;
            String[] toTestArr = coinAlgo.solve(n);
            assertEquals(expectedSteps, toTestArr.length-1);
            System.out.println("Expected for n " + n + " Steps " + expectedSteps + " With " + (toTestArr.length-1));

        }
    }
}