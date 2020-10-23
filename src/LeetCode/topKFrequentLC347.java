package LeetCode;

import java.util.*;

public class topKFrequentLC347 {
    public int[] topKFrequent(int[] nums, int k) {
        if (nums == null || nums.length==0) return null;
        if (nums.length==1) return new int[]{nums[0]};
        Map<Integer,Integer> map = new HashMap<Integer,Integer>();
        for(int j=0;j<nums.length;j++){
            map.put(nums[j],map.getOrDefault(nums[j],0)+1);
        }
        PriorityQueue<Integer> heap = new PriorityQueue<>(k,(X,Y)->Integer.compare(map.get(Y),map.get(X)));
        //for(Map.Entry<Integer, Integer> FreqEntry:map.entrySet()){ // when u only need key and not value keyset is good.
            for (int n: map.keySet()) {
                heap.add(n);
            }
       //  }
        int[] resultArr = new int[k];
        for (int i=0; i < k; i++){
            resultArr[i] = heap.poll();
        }
        return resultArr;
    }
    public static void main(String[] args){
        int[] nums = {1,1,1,2,2,3};
        int[] nums1 = {1,2};
        int k = 2;
        int[] result = new topKFrequentLC347().topKFrequent(nums,k);
        int[] result1 = new topKFrequentLC347().topKFrequent(nums1,k);
        System.out.println(Arrays.toString(result));
        System.out.println(Arrays.toString(result1));
    }
}
