
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class testGetArray {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { new int[]{1, 2, 3, 4, 5, 4, 1}, new int[]{1} },
                { new int[]{1, 2, 3, 4, 5, 4, 1, 6}, new int[]{1, 6} },
                { new int[]{1, 2, 3, 4, 5, 4, 1, 6, 7}, new int[]{1, 6, 7} },
                { new int[]{4, 4, 4, 4}, new int[]{} }
        });
    }

    private int[] input;
    private int[] expected;

    public testGetArray(int[] input, int[] expected) {
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void mainTest() {
        Assert.assertTrue(Arrays.equals(ArraysAlgs.getArrayByRule(input), expected));
    }
}
