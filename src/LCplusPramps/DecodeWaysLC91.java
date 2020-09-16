package LCplusPramps;

import java.util.HashMap;
import java.util.Map;

public class DecodeWaysLC91 {
    Map<Integer,Integer> memorization = new HashMap<>();
    public int numDecodings(String s) {
        if(s == null || s.length()==0){
            return 0;
        }
        return numDecodehelper(0,s);
    }
    public int numDecodehelper(int index,String s){
        if(index==s.length()){
            return 1;
        }

        if (s.charAt(index) == '0' ){
            return 0;
        }

        if(index==s.length()-1){
            return 1;
        }
        if (memorization.containsKey(index)) {
            return memorization.get(index);
        }
        int ans = numDecodehelper(index+1,s);
        if (Integer.parseInt(s.substring(index, index+2)) <= 26) {
            ans+=numDecodehelper(index+2,s);
        }
        memorization.put(index,ans);
        return ans;
    }
    public static void main(String[] args) {
        String abc ="226";
        System.out.println(new DecodeWaysLC91().numDecodings(abc));
    }
}
