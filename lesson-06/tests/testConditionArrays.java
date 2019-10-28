
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class testConditionArrays {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { new int[]{1,4,4,4}, true },
                { new int[]{1,4,1,4}, true },
                { new int[]{1,4,1,4,3}, false },
                { new int[]{1,1,1,1,1}, false }
        });
    }

    private int[] input;
    private boolean expected;

    public testConditionArrays(int[] input, boolean expected) {
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void mainTest() {
        Assert.assertEquals(ArraysAlgs.haveInclusionByRule(input), expected);
    }
}