/*************************************************************************
 * @brief LZWmod.java
 * @author Joshua Spisak <jjs231@pitt.edu>
 * @date 11/04/2019
 *
 * This code was taken from the provided LZW.java/extended lab code then
 *  modified to fulfill this assignment.
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
    //! Flag to use for printing out something only once
    private static boolean out_of_words = false;

    /**
     * @brief compresses the data from stdin, to stdout
     **/
    public static void compress() {
        // Set a bit in the file to determing whether or not it's allowing reset
        if(allow_reset) {
            BinaryStdOut.write(1, 1);
        } else {
            BinaryStdOut.write(0, 1);
        }
        // Set a bit to determine whether or not to allow dynamic lengths
        if(W_MIN == W_MAX) {
            BinaryStdOut.write(1, 1);
        } else {
            BinaryStdOut.write(0, 1);
        }
        int written_codewords = 0;

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
                ++written_codewords;
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
                } else if(!out_of_words && print_debugs) {
                    out_of_words = true;
                    System.err.println("Ran out of codewords! :( (I have written " + written_codewords + " so far...");
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
        if(print_debugs)
            System.err.println("Wrote " + written_codewords + " codewords.");
    } /* compress() */

    /**
     * @brief decompresses the data from stdin, to stdout
     **/
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
        if(BinaryStdIn.readInt(1) == 1) {
            if(print_debugs)
                System.err.println("Using static length code words.");
                System.err.println("Using dynamic length code words, W_MIN: " + W_MIN + " W_MAX: " + W_MAX + " W: " + W);
            W_MIN = W_MAX = W = 12;
            L = 1 << W;
        } else {
            W_MIN = 9;
            W_MAX = 16;
            W = 9;
            L = 1 << W;
            if(print_debugs)
                System.err.println("Using dynamic length code words, W_MIN: " + W_MIN + " W_MAX: " + W_MAX + " W: " + W);
        }
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
            // If we are not reaching the end of the current L
            // Or we aren't dynamically changing code length
            if(i < L || W_MIN == W_MAX) {
                codeword = BinaryStdIn.readInt(W);
            } else {
                if(W < W_MAX) {
                    codeword = BinaryStdIn.readInt(W + 1);
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
    } /* expand() */

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
    } /* main() */
} /* LZWmod */
