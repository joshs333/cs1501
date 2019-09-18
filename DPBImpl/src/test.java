package blah;
import blah.mutil.junk;

public class test {
    public static void main(String[] args) {
        for(int i = 0; i < args.length; ++i) {
            System.out.println(args[i]);
        }
        System.out.println("Hello!");
        junk.print_hello();
    }
}
