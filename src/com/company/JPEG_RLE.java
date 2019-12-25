package com.company;

import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class JPEG_RLE {
    private static List<Integer> stringToNumbers(String text) {
        return Arrays.stream(text.split(","))
                .map(String::trim)
                .mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());
    }

    private static String getAdditionalBits(int n) {
        String ret = Integer.toBinaryString(Math.abs(n));
        if (n < 0) ret = inverseString(ret);
        return ret;
    }

    private static int decodeAdditionalBits(String bits) {
        if (bits.isEmpty()) return 0;
        if (bits.charAt(0) == '0') return -Integer.parseInt(inverseString(bits), 2);
        return Integer.parseInt(bits, 2);
    }

    private static String inverseString(String x) {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < x.length(); ++i) {
            if (x.charAt(i) == '1') temp.append('0');
            else temp.append('1');
        }
        return temp.toString();
    }

    private static Pair<List<Pair<Integer, Integer>>, List<String>> prepareDataToCompress(List<Integer> data) {
        List<Pair<Integer, Integer>> pairs = new ArrayList<>();
        List<String> additionalBits = new ArrayList<>();
        int lastIdx = 0;
        for (int i = 0; i < data.size(); ++i) {
            if (data.get(i) != 0) {
                String additional = getAdditionalBits(data.get(i));
                additionalBits.add(additional);

                pairs.add(new Pair<>(i - lastIdx, additional.length()));
                lastIdx = i + 1;
            }
        }
        pairs.add(Huffman.EOB);
        return new Pair<>(pairs, additionalBits);
    }

    /*
    First Line is the bits stream
    remaining lines are the dictionary

     */
    private static Pair<String, Map<String, Pair<Integer, Integer>>> prepareDataToDecompress(String data) {
        String[] textArray = data.split("\n");
        String bitsText = textArray[0];
        Map<String, Pair<Integer, Integer>> dict = new LinkedHashMap<>();
        for (String line : textArray) {
            if (line == null || line.isEmpty()) continue;
            String[] lineData = line.split(Huffman.dictionarySplitter);
            if (lineData.length == 3) dict.put(lineData[0], new Pair<>(Integer.parseInt(lineData[1]), Integer.parseInt(lineData[2])));
        }

        return new Pair<>(bitsText, dict);
    }

    public static String Compress(String text) {
        Pair<List<Pair<Integer, Integer>>, List<String>> preparedData = prepareDataToCompress(stringToNumbers(text));
        List<Pair<Integer, Integer>> data = preparedData.getKey();
        List<String> additionalBits = preparedData.getValue();

        String compressedData = Huffman.encode(data);
        for (String additionalBit : additionalBits) {
            compressedData = compressedData.replaceFirst(Huffman.encodingSplitter, additionalBit);
//           compressedData = compressedData.replaceFirst(Huffman.encodingSplitter, "," + additionalBit + " ");
        }
        compressedData += ('\n' + Huffman.dictionaryToString());
        return compressedData;
    }

    public static String Decompress(String data) {
        Pair<String, Map<String, Pair<Integer, Integer>>> preparedData = prepareDataToDecompress(data);
        String bitsText = preparedData.getKey();
        Map<String, Pair<Integer, Integer>> dict = preparedData.getValue();

        StringBuilder ret = new StringBuilder();
        String currentCode = "";

        Pair<Integer, Integer> currentPair;
        for (int i = 0; i < bitsText.length(); ++i) {
            currentCode += bitsText.charAt(i);
            currentPair = dict.getOrDefault(currentCode, null);

            if (currentPair != null && !currentPair.equals(Huffman.EOB)) {
                currentCode = "";
                int numberOfBits = currentPair.getValue();

                StringBuilder currentBits = new StringBuilder();
                while (numberOfBits-- > 0) currentBits.append(bitsText.charAt(++i));

                int zeros = currentPair.getKey();
                int number = decodeAdditionalBits(currentBits.toString());

                while (zeros-- > 0) ret.append("0,");
                ret.append(number).append(",");
            }
        }
        return ret.toString().substring(0, ret.length() - 1); // to remove the last comma
    }


}
