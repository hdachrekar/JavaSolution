package LCplusPramps;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Merging2PackagesandTwoSumLC1 {
    static int[] getIndicesOfItemWeights(int[] arr, int limit) {
        // your code goes here
        Map<Integer,Integer> weightIndMap = new HashMap<Integer,Integer>();
        for(int i=0;i<arr.length;i++){
            weightIndMap.put(arr[i],i);
        }
        for(int i=0;i<arr.length;i++){
            if(weightIndMap.containsKey(limit-arr[i]) && i != weightIndMap.get(limit-arr[i]) ){
                int[] result= new int[2];
                if(i>weightIndMap.get(limit-arr[i])){
                    result[0]= i;
                    result[1]=weightIndMap.get(limit-arr[i]);
                }else{
                    result[1]= i;
                    result[0]=weightIndMap.get(limit-arr[i]);
                }

                return result;
            }
        }
        return new int[]{};
    }

    public static void main(String[] args) {
        int[] arr = {4, 6, 10, 15, 16};
        int lim = 21;
        System.out.println(Arrays.toString(getIndicesOfItemWeights(arr,lim)));
        int[] arreq = {4, 4};
        int limeq = 8;
        System.out.println(Arrays.toString(getIndicesOfItemWeights(arreq,limeq)));
        int[] arreql = {4, 4, 1};
        int limeql = 5;
        System.out.println(Arrays.toString(getIndicesOfItemWeights(arreql,limeql)));
        int[] arreqly = {3, 2, 4};
        int limeqly = 6;
        System.out.println(Arrays.toString(getIndicesOfItemWeights(arreqly,limeqly)));
    }
}
