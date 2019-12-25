import javafx.util.Pair;

import java.util.*;

public class JPEGEncoding {
    private String compresssionResult;
    private List<Integer> decompressionResult;
    private Map<Pair<Integer, Integer>, String> dictionary;

    public void compress(List<Integer> nums) {
        Map<Pair<Integer, Integer>, Integer> mp = new LinkedHashMap<>();

        int i;
        for (i = nums.size() - 1; i >= 0; --i) {
            if (nums.get(i) != 0) break;
        }
        nums.subList(i + 1, nums.size()).clear();

        List<Pair<Pair<Integer, Integer>, String>> lst = new LinkedList<>();
        int zeros = 0;
        for (int v : nums) {
            if (v == 0) zeros++;
            else {
                String bits = getBinaryRepresentation(v);
                int additional = bits.length();

                Pair<Integer, Integer> pr = new Pair<>(zeros, additional);
                mp.put(pr, mp.getOrDefault(pr, 0) + 1);
                lst.add(new Pair<>(pr, bits));

                zeros = 0;
            }
        }
        mp.put(new Pair<>(0, 0), 1);
        dictionary = HuffmanEncoding(mp);

        StringBuilder sb = new StringBuilder();

        for (Pair<Pair<Integer, Integer>, String> entity : lst) {
            sb.append(dictionary.get(entity.getKey()));
            sb.append(entity.getValue());
        }
        sb.append(dictionary.get(new Pair<>(0, 0)));
        compresssionResult = sb.toString();
    }

    public void decompress(String v) {
        List<Integer> output = new ArrayList<>();

        Map<String, Pair<Integer, Integer>> revDictionary = generateReverseDictionary(dictionary);
        int laststop = -1;
        for (int i = 0; i < v.length(); ++i) {
            String partway = v.substring(laststop + 1, i + 1);
            if (revDictionary.containsKey(partway)) {
                Pair<Integer, Integer> ret = revDictionary.get(partway);
                if (ret.getValue() == 0) break;
                for (int j = 0; j < ret.getKey(); ++j)
                    output.add(0);

                String encoding = v.substring(i + 1, i + 1 + ret.getValue());
                output.add(getInteger(encoding));

                i += ret.getValue();
                laststop = i;
            }
        }
        decompressionResult = output;
    }

    private Map<String, Pair<Integer, Integer>> generateReverseDictionary(Map<Pair<Integer, Integer>, String> dictionary) {
        Map<String, Pair<Integer, Integer>> ret = new LinkedHashMap<>();
        for (Map.Entry<Pair<Integer, Integer>, String> entry : dictionary.entrySet()) {
            ret.put(entry.getValue(), entry.getKey());
        }
        return ret;
    }

    public void setDictionary(Map<Pair<Integer, Integer>, String> dict) {
        dictionary = dict;
    }

    private static String onesComplement(String bits) {
        return bits.codePoints()
                .map(c -> c ^ 1)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private static String getBinaryRepresentation(int v) {
        String bits = Integer.toBinaryString(Math.abs(v));
        if (v < 0)
            return onesComplement(bits);
        else
            return bits;
    }

    private static int getInteger(String bits) {
        if (bits.charAt(0) == '0')
            return -Integer.parseInt(onesComplement(bits), 2);
        else
            return Integer.parseInt(bits, 2);

    }

    private static Map<Pair<Integer, Integer>, String> HuffmanEncoding(Map<Pair<Integer, Integer>, Integer> mp) {
        HuffmanEncoder<Pair<Integer, Integer>> encoder = new HuffmanEncoder<>();
        return encoder.findEncoding(mp);
    }

    public String getCompresssionResult() {
        return compresssionResult;
    }

    public List<Integer> getDecompressionResult() {
        return decompressionResult;
    }

    public Map<Pair<Integer, Integer>, String> getDictionary() {
        return dictionary;
    }
}
