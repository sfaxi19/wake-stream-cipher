import java.io.*;

/**
 * Created by sfaxi19 on 03.12.16.
 */
public class Wake {

    private int[] table;
    private static int tt[] = {
            0x726a8f3b,
            0xe69a3b5c,
            0xd3c71fe5,
            0xab3c73d2,
            0x4d3a8eb3,
            0x0396d6e8,
            0x3d4c2f7a,
            0x9ee27cf3
    };
    private static final int ENC_MOD = 1;
    private static final int DEC_MOD = 2;

    public Wake() {

    }

    StringBuilder binString = new StringBuilder();

    private void dataCollectionForTest(int gamma) {
        String str = Integer.toBinaryString(gamma);
        StringBuffer bin = new StringBuffer();
        for (int i = 0; i < 32 - str.length(); i++) {
            bin.append("0");
        }
        bin.append(str);
        binString.append(bin.toString());
    }


    public static int[] funcF(int[] in, int k, boolean perm) {
        int[] s = new int[4];
        s[0] = (in[0] * k) % 55;
        s[1] = (in[1] * k) % 55;
        s[2] = (in[2] * k) % 55;
        s[3] = (in[3] * k) % 55;
        //permutation
        if (perm) {
            int tmp = 0;
            tmp = s[0];
            s[0] = s[3];
            s[3] = tmp;
        }
        return s;
    }

    public static int[] sum(int[] x, int[] y) {
        int[] sum = new int[4];
        for (int i = 0; i < 4; i++) {
            sum[i] = (55 + (x[i] - y[i])) % 55;
        }
        return sum;
    }

    public static int checkR3(String str, int t) {
        int sum = 0;
        int length = str.length();
        str = str + str;
        for (int i = 0; i < length; i++) {
            if (str.charAt(i) == str.charAt(i + t)) {
                sum++;
            } else {
                sum--;
            }
        }
        return sum;
    }

    public static void decoder(int[] crypt) {
        int k1 = 10;
        int k2 = 10;
        int k3 = 38;
        int[] l = new int[4];
        int[] r = new int[4];
        l[0] = crypt[0];
        l[1] = crypt[1];
        l[2] = crypt[2];
        l[3] = crypt[3];

        r[0] = crypt[4];
        r[1] = crypt[5];
        r[2] = crypt[6];
        r[3] = crypt[7];
        int[] tmp = r;
        r = l;
        System.out.println("R2 = " + r[0] + " " + r[1] + " " + r[2] + " " + r[3]);
        l = sum(tmp, funcF(r, k3, true));
        System.out.println("L2 = " + l[0] + " " + l[1] + " " + l[2] + " " + l[3]);

        tmp = r;
        r = l;
        System.out.println("R1 = " + r[0] + " " + r[1] + " " + r[2] + " " + r[3]);
        l = sum(tmp, funcF(r, k2, true));
        System.out.println("L1 = " + l[0] + " " + l[1] + " " + l[2] + " " + l[3]);

        tmp = r;
        r = l;
        l = sum(tmp, funcF(r, k1, true));
        System.out.println("enc = " + l[0] + " " + l[1] + " " + l[2] + " " + l[3] + " " + r[0] + " " + r[1] + " " + r[2] + " " + r[3]);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int[] key = {0x50505050, 0x50505050, 0x50505050, 0x50505050};
        File file = new File("");
        file.getAbsolutePath();
        /*int[] crypt1 = {28, 17, 39, 11, 38, 9, 17, 21};
        int[] crypt2 = {37, 32, 3, 34, 7, 13, 30, 26};
        System.out.println(checkR3("010010000111000",3));
        decoder(crypt1);
        decoder(crypt2);*/
        Wake wake = new Wake();
        System.out.println(file.getAbsolutePath());
        DataInputStream in = new DataInputStream(new FileInputStream("img.bmp"));
        DataOutputStream out = new DataOutputStream(new FileOutputStream("out.bmp"));
        System.out.println("Encryption");
        wake.encryption(key, in, out, true);
        Thread.sleep(1000);
        DataInputStream enc = new DataInputStream(new FileInputStream("out.bmp"));
        DataOutputStream dec = new DataOutputStream(new FileOutputStream("dec.bmp"));

        BasicTests bTests = new BasicTests(wake.binString);
        bTests.frequencyTest();
        bTests.sequenceTest();
        bTests.seriesTest();
        bTests.autocorrelationTest(1024,1.96);
        //System.out.println("Test 1: ones = " + wake.ones + "  zeros = " + wake.zeros + "  result = ");
        System.out.println("Decryption");
        wake.decryption(key, enc, dec);
    }

    public void decryption(int[] key, DataInputStream in, DataOutputStream out) throws IOException {
        encryption(key, DEC_MOD, in, out, false);
    }

    public void encryption(int[] key, DataInputStream in, DataOutputStream out) throws IOException {
        encryption(key, ENC_MOD, in, out, false);
    }

    public void encryption(int[] key, DataInputStream in, DataOutputStream out, boolean test) throws IOException {
        encryption(key, ENC_MOD, in, out, test);
    }

    public void encryption(int[] key, int mod, DataInputStream in, DataOutputStream out, boolean test) throws IOException {
        generationSbox(key);
        int a = key[0];
        int b = key[1];
        int c = key[2];
        int d = key[3];
        Integer data = getWordFromFile(in);
        while (data != null) {
            //data = 0x0;
            // key = d
            //System.out.println("word: " + Integer.toHexString(data));
            int outData = d ^ data;
            if (test) {
                dataCollectionForTest(d);
            }
            saveWordToFile(outData, out);
            if (mod == ENC_MOD) {
                // System.out.println("encr: " + Integer.toHexString(outData));
                a = functionM(a, outData);
            } else {
                //  System.out.println("decr: " + Integer.toHexString(outData));
                a = functionM(a, data);
            }
            b = functionM(b, a);
            c = functionM(c, b);
            d = functionM(d, c);
            data = getWordFromFile(in);
        }
    }

    private int functionM(int x, int y) {
        return ((x + y) >> 8) ^ table[(x + y) & 0xff];
    }

    private int[] generationSbox(int key[]) {
        table = new int[257];
        int x, z;
        //
        // Fill t-table
        //
        for (int i = 0; i < 4; i++) {
            table[i] = key[i];
        }
        // printm(table);
        for (int i = 4; i < 256; i++) {
            x = table[i - 4] + table[i - 1];
            //System.out.println("x: " + x);
            table[i] = (x >>> 3) ^ tt[x & 7];
            //System.out.println("t: " + Integer.toBinaryString(table[i]));
        }
        //
        // Mix first entries
        //
        for (int i = 0; i < 23; i++) {
            table[i] += table[i + 89];
        }
        x = table[33];
        z = table[59] | 0x01000001;
        z = z & 0xff7fffff;
        //
        // Change top byte to a permutation etc
        //
        for (int i = 0; i < 256; i++) {
            x = (x & 0xff7fffff) + z;
            table[i] = table[i] & 0x00ffffff ^ x;
        }
        table[256] = table[0];
        x &= 255;
        for (int i = 0; i < 256; i++) {
            table[i] = table[x = (table[i ^ x] ^ x) & 255];
        }
        return table;
    }

    private void printm(int[] m) {
        for (int i = 0; i < m.length; i++) {
            System.out.print(m[i] + "  ");
        }
        System.out.println();
    }

    private static Integer getWordFromFile(final DataInputStream in) throws IOException {
        byte[] dataBytes = new byte[4];
        int er = in.read(dataBytes, 0, dataBytes.length);
        if (er == -1) {
            System.out.println("error -1");
            return null;
        }
        if (er < 4) {
            System.out.println("er < 4");
            for (int i = er; i < 4; i++) {
                dataBytes[i] = (byte) 0xff;
            }
        }
        //System.out.println("word++");
        int word = (
                ((((int) dataBytes[0]) & 0xff) << 24) |
                        ((((int) dataBytes[1]) & 0xff) << 16) |
                        ((((int) dataBytes[2]) & 0xff) << 8) |
                        (((int) dataBytes[3]) & 0xff)
        );
        return word;
    }

    private static void saveWordToFile(int word, final DataOutputStream out) throws IOException {
        byte[] bytes = new byte[4];
        bytes[3] = (byte) (word & 0xff);
        bytes[2] = (byte) ((word >> 8) & 0xff);
        bytes[1] = (byte) ((word >> 16) & 0xff);
        bytes[0] = (byte) ((word >> 24) & 0xff);
        out.write(bytes, 0, bytes.length);
    }

    private static void printFile(DataInputStream in) throws IOException {
        String str = in.readLine();
        while (str != null) {
            System.out.println(str);
            str = in.readLine();
        }
    }
}
