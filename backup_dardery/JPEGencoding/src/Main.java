import javafx.util.Pair;

import java.util.*;
//judge: 1001001000111010010100010100111110011
public class Main {
    private static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("1- Compress\n2- Decompress");
            int v = in.nextInt();
            switch (v) {
                case 1:
                    compressProcedure();
                    break;
                case 2:
                    decompressProcedure();
                    break;
                default:
                    return;
            }
        }
    }

    private static void compressProcedure() {
        System.out.println("Enter no. values: ");
        int n = in.nextInt();

        System.out.println("Enter values: ");

        List<Integer> v = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            v.add(in.nextInt());
        }

        JPEGEncoding jpeg = new JPEGEncoding();
        jpeg.compress(v);
        System.out.println(jpeg.getCompresssionResult());

        for(Map.Entry<Pair<Integer, Integer>, String> s : jpeg.getDictionary().entrySet()){
            System.out.println(s.getKey().getKey() + " " + s.getKey().getValue() + " " + s.getValue());
        }
    }

    private static void decompressProcedure() {
        System.out.println("Enter no. dictionary values: ");
        int n = in.nextInt();

        System.out.println("Enter triplets: int int string");
        Map<Pair<Integer, Integer>, String> mp = new HashMap<>();
        for (int i = 0; i < n; i++) {
            mp.put(new Pair<>(in.nextInt(), in.nextInt()), in.next());
        }

        System.out.println("Enter string: ");
        String bits = in.next();

        JPEGEncoding jpeg = new JPEGEncoding();
        jpeg.setDictionary(mp);
        jpeg.decompress(bits);

        List<Integer> ret = jpeg.getDecompressionResult();

        for(int v : ret){
            System.out.print(v + " ");
        }
        System.out.println();
    }

}
