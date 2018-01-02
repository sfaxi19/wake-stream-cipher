/**
 * Created by sfaxi19 on 21.01.17.
 */
public class BasicTests {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    int ones = 0;
    int zeros = 0;
    int count = 0;
    int zz = 0;
    int zo = 0;
    int oz = 0;
    int oo = 0;
    int[] gap = new int[100];
    int[] block = new int[100];

    public BasicTests(StringBuilder binString) {
        this.binString = binString;
        count = binString.length();
        for (int i = 0; i < binString.length() - 1; i++) {
            if ((binString.charAt(i) == '0') && (binString.charAt(i + 1)) == '0') {
                zz++;
                zeros++;
            }
            if ((binString.charAt(i) == '0') && (binString.charAt(i + 1)) == '1') {
                zo++;
                zeros++;
            }
            if ((binString.charAt(i) == '1') && (binString.charAt(i + 1)) == '0') {
                oz++;
                ones++;
            }
            if ((binString.charAt(i) == '1') && (binString.charAt(i + 1)) == '1') {
                oo++;
                ones++;
            }
        }
        if (binString.charAt(binString.length() - 1) == '0') {
            zeros++;
        } else {
            ones++;
        }
    }

    StringBuilder binString = new StringBuilder();

    public void frequencyTest() {
        System.out.println("Ones = " + ones + "\nZeros = " + zeros + "\nCount = " + count);
        double ch = ((zeros - ones) * (zeros - ones));
        double result = ch / count;
        System.out.format("FrequencyTest: %f\n", result);
    }

    public void sequenceTest() {
        double zzq = zz * zz;
        double zoq = zo * zo;
        double ozq = oz * oz;
        double ooq = oo * oo;
        double oq = ones * ones;
        double zq = zeros * zeros;
        double sumq = (zzq + zoq + ozq + ooq);
        double firstDel = (4 / (count - 1));
        double secondDel = (2 / count);
        double mul1 = firstDel * sumq;
        double mul2 = secondDel * (oq + zq);
        double seqTestResult = mul1 - mul2 + 1;
        System.out.println("Sequence control:" + (count - (zz + zo + oz + oo)));
        System.out.format("SequenceTest: %f\n", seqTestResult);
    }

    public void seriesTest() {
        count = binString.length();
        boolean seriesGap = false;
        boolean seriesBlock = false;
        int lSeries = 0;
        for (int i = 0; i < binString.length(); i++) {
            if (binString.charAt(i) == '1') {
                if (seriesGap) {
                    seriesGap = false;
                    gap[lSeries]++;
                    lSeries = 0;
                }
                seriesBlock = true;
                lSeries++;
            }
            if (binString.charAt(i) == '0') {
                if (seriesBlock) {
                    seriesBlock = false;
                    block[lSeries]++;
                    lSeries = 0;
                }
                seriesGap = true;
                lSeries++;
            }
        }
        double sum1 = 0;
        double sum2 = 0;
        int k = 5;
        double x = 26.1245;
        for (int i = 1; i < k; i++) {
            double e = (count - i + 3) / Math.pow(2, i + 2);
            System.out.format("%d)Gaps: %8d Blocks: %8d\te: %5.3f\n", i, gap[i], block[i], e);
            sum1 += (Math.pow(block[i] - e, 2) / e);
            sum2 += (Math.pow(gap[i] - e, 2) / e);
        }
        double result = sum1 + sum2;
        System.out.format("SeriesTest: %3.3f. \nFor k = %d the test series was " +
                        ((result <= x) ? (ANSI_GREEN + "COMPLITED") : (ANSI_RED + "FAILED\n") + ANSI_RESET),
                result, k);
    }

    public void autocorrelationTest(int d, double x) {
        int sum = 0;
        for (int i = 0; i < binString.length() - d; i++) {
            sum += ((binString.charAt(i) == '0') ? 0 : 1) ^
                    ((binString.charAt(i + d) == '0') ? 0 : 1);
        }
        double result = (2 * (sum - (binString.length() - d) / 2)) / (Math.sqrt(binString.length() - sum));
        System.out.format("AutocorrelationTest: %3.3f. \nThe autocorrelation test was " +
                (((result <= x) ? (ANSI_GREEN + "COMPLITED") : (ANSI_RED + "FAILED")) + ANSI_RESET + "\n"),
                        result);
    }

    public void universalTest() {

    }

}
