import java.util.ArrayList;

public class RunTests {
    public static void main(String[] args) {
        PHPArray<Integer> A = new PHPArray<>(2);

        for (int i = 9; i >= 0; i--) {
            A.put("Key" + i, i);
            A.put(i, i);
        }

        for (Integer X: A) {
            System.out.println("Next item is " + X);
        }

        PHPArray.Pair<Integer> val;
        while ((val = A.each()) != null) {
            System.out.println("Next key is " + val.key + " and val " + val.value);
        }
        A.reset();
        while ((val = A.each()) != null) {
            System.out.println("Next key is " + val.key + " and val " + val.value);
        }
        ArrayList<String> kees = A.keys();
        for (String s: kees)
            System.out.print(s + " ");
        System.out.println("\n");
        ArrayList<Integer> vals = A.values();
        for (Integer s: vals)
            System.out.print(s + " ");
        System.out.println("\n");

        A.showTable();
		A.forEach((x) -> { System.out.println(x); });
        PHPArray<String> string_thing = new PHPArray<String>();
        string_thing.put("this", "that");
        string_thing.put("nowthis", "nowthat");
        string_thing.array_flip();

        PHPArray.Pair<String> valss;
        while ((valss = string_thing.each()) != null) {
            System.out.println("Next key is " + valss.key + " and val " + valss.value);
        }
    }
}
