package fr.wollfie.serial_arm_com.maths;

import java.util.Arrays;

public class LinearSystem {

    public static int countSol(int coeff[], int n, int rhs)
    {
        // Create and initialize a table to
        // store results of subproblems
        int dp[] = new int[rhs + 1];
        Arrays.fill(dp, 0);
        dp[0] = 1;

        // Fill table in bottom up manner
        for (int i = 0; i < n; i++)
            for (int j = coeff[i]; j <= rhs; j++)
                dp[j] += dp[j - coeff[i]];

        return dp[rhs];
    }
}
