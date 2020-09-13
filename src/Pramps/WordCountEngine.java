package Pramps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class WordCountEngine {
    static String[][] wordCountEngine(String document) {
        String[] wordString = document.split(" ");
        int maxcount = 0;
        Map<String, Integer> hmap = new LinkedHashMap<>();
        Map<Integer, ArrayList<String>> resultMap = new LinkedHashMap<>();

        for (String str : wordString) {
            String strsanitize = sanitize(str);
            if (strsanitize.length() > 0) {
                int freq = hmap.getOrDefault(strsanitize, 0) + 1;
                String strword = strsanitize;
                hmap.put(strword, freq);
                maxcount = Math.max(freq, maxcount);
            }

        }

        for (Map.Entry<String, Integer> entry : hmap.entrySet()) {
            String word = entry.getKey();
            Integer freq = entry.getValue();
            ArrayList arrStringList = resultMap.getOrDefault(freq, new ArrayList<String>());
            arrStringList.add(word);
            resultMap.put(freq, arrStringList);
        }
        String[][] result = new String[hmap.size()][2];
        int counter = 0;
        for (int i = maxcount; i >= 0; i--) {
            if (resultMap.containsKey(i)) {
                int key = i;
                ArrayList<String> arrlist = resultMap.get(i);
                for (int j = 0; j < arrlist.size(); j++) {
                    String stringWord = arrlist.get(j);
                    result[counter][0] = stringWord;
                    result[counter][1] = String.valueOf(key);
                    counter++;
                }
            }
        }
        return result;
    }

    private static String sanitize(String inpstring) {
        StringBuilder strbuilder = new StringBuilder();
        for (char c : inpstring.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                strbuilder.append(c);
            }
        }
        return strbuilder.toString().toLowerCase();
    }


    public static void main(String[] args) {
        //String document="Practice makes perfect. you'll only get Perfect by practice. just practice!";
        String document = "Every book is a quotation; and every house is a quotation out of all forests, and mines, and stone quarries; and every man is a quotation from all his ancestors.";
        String[][] output = wordCountEngine(document);
        for (int j = 0; j < output.length; j++) {
            String[] strarr = new String[]{output[j][0], output[j][1]};
            System.out.println(Arrays.toString(strarr));
        }
    }
}
