/*************************************************************************
 *  Compilation:  javac LZWmod.java
 *  Execution:    java LZWmod - < input.txt   (compress)
 *  Execution:    java LZWmod + < input.txt   (expand)
 *  Dependencies: BinaryStdIn.java BinaryStdOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *
 *************************************************************************/

public class LZWmod {
    //! Number of input chars
    private static final int R = 256;
    //! Current number of codewords (1 << W or 2^W)
    private static int L;
    //! Current width of codewords
    private static int W;
    //! Minimum width of codewords
    private static int W_MIN;
    //! Maximum width of codewords
    private static int W_MAX;
    //! Whether or not to allow resetting the dictionary
    private static boolean allow_reset;
    //! Whether or not to print debug statemnets
    private static boolean print_debugs;

    public static void compress() {
        if(allow_reset) {
            BinaryStdOut.write(1, 1);
        } else {
            BinaryStdOut.write(0, 1);
        }
        TSTmod<Integer> st = new TSTmod<Integer>();
        for (int i = 0; i < R; i++)
            st.put(new StringBuilder("" + (char) i), i);
        int code = R+1;  // R is codeword for EOF

        //initialize the current string
        StringBuilder current = new StringBuilder();
        //read and append the first char
        char c = BinaryStdIn.readChar();
        current.append(c);
        Integer codeword = st.get(current);
        while (!BinaryStdIn.isEmpty()) {
            codeword = st.get(current);
            //DONE: read and append the next char to current
            c = BinaryStdIn.readChar();
            current.append(c);

            if(!st.contains(current)){
                BinaryStdOut.write(codeword, W);
                if (code < L) {    // Add to symbol table if not full
                    st.put(current, code++);
                } else if(W < W_MAX){ // If we can increment W, do that
                    ++W;
                    L = L << 1;
                    st.put(current, code++);
                } else if(allow_reset) {
                    // Reset dictionary
                    W = W_MIN;
                    L = (1 << W);

                    st = new TSTmod<Integer>();
                    for (int i = 0; i < R; i++)
                        st.put(new StringBuilder("" + (char) i), i);
                    code = R+1;  // R is codeword for EOF
                    st.put(current, code++);
                }
                current = new StringBuilder();
                current.append(c);
            }
        }

        //DONE: Write the codeword of whatever remains
        //in current
        codeword = st.get(current);
        BinaryStdOut.write(codeword, W);

        BinaryStdOut.write(R, W); //Write EOF
        BinaryStdOut.close();
    }


    public static void expand() {
        if(BinaryStdIn.readInt(1) == 1) {
            if(print_debugs)
                System.err.println("Allowing reset.");
            allow_reset = true;
        } else {
            if(print_debugs)
                System.err.println("Disallowing reset.");
            allow_reset = false;
        }
        if(print_debugs)
            System.err.println(1 << W_MAX);
        String[] st = new String[1 << W_MAX];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);
        String val = st[codeword];

        while (true) {
            BinaryStdOut.write(val);
            if(i < L) {
                codeword = BinaryStdIn.readInt(W);
            } else {
                if(W < W_MAX) {
                    codeword = BinaryStdIn.readInt(W + 1);
                    if(print_debugs)
                        System.err.println(codeword);
                } else if(allow_reset) {
                    codeword = BinaryStdIn.readInt(W_MIN);
                } else {
                    codeword = BinaryStdIn.readInt(W);
                }
            }
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L) {
                st[i++] = val + s.charAt(0);
            } else if(W < W_MAX) {
                st[i++] = val + s.charAt(0);
                ++W;
                L = L << 1;
                if(print_debugs)
                    System.err.println("Going from " + (W - 1) + " to " + W + " L is now " + L);
            } else if(allow_reset) {
                // Reset dictionary
                W = W_MIN;
                L = (1 << W);

                for (i = 0; i < R; i++)
                    st[i] = "" + (char) i;
                st[i++] = "";
                st[i++] = val + s.charAt(0);
                if(print_debugs)
                    System.err.println("Going from " + W_MAX + " to " + W + " L is now " + L);
            }
            val = s;

        }
        BinaryStdOut.close();
    }



    public static void main(String[] args) {
        // Whether or not to allow resets
        allow_reset = false;
        for(int i = 1; i < args.length; ++i) {
            if(args[i].equals("r")) {
                allow_reset = true;
            }
        }
        // Whether or not to use static length code words
        W_MIN = W = 9;
        W_MAX = 16;
        for(int i = 1; i < args.length; ++i) {
            if(args[i].equals("s")) {
                W_MIN = W = 12;
                W_MAX = 12;
            }
        }
        L = 1 << W; // Set current L
        // Whether or not to print debug printouts
        print_debugs = false;
        for(int i = 1; i < args.length; ++i) {
            if(args[i].equals("d")) {
                print_debugs = true;
            }
        }

        if(print_debugs)
            System.err.println("Running with W_MIN: " + W + " W_MAX: " + W_MAX + " L: " + L);
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new RuntimeException("Illegal command line argument");
    }

}
