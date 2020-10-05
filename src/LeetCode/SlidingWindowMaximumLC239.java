package LeetCode;

import java.util.*;

public class SlidingWindowMaximumLC239 {
    public int[] maxSlidingWindow(int[] nums, int k) {
        int n= nums.length;
        int[] out = new int[n-k+1];
        Deque<Integer> queue = new LinkedList<>();
        int i=0;
        for(;i<k;i++) {
            while (!queue.isEmpty() && nums[queue.peekLast()] <= nums[i]) {
                queue.removeLast();
            }
            queue.addLast(i);
        }
            for(;i<n;i++){
                out[i-k]= nums[queue.peekFirst()];
                while(!queue.isEmpty() && queue.peekFirst() <= i-k){
                    queue.removeFirst();
                }
                // THIS LESS THAN EQUAL IS IMPORTANT CONDITION
                while(!queue.isEmpty() && nums[queue.peekLast()]<=nums[i]){
                    queue.removeLast();
                }
                queue.addLast(i);
            }
            out[i-k]=queue.peekFirst();
        return  out;
        }



    public static void main(String[] args) {
        //int[] nums = {4,3,1,2,5,3,4,7,1,9};
        //int[] nums = {6,5,4,3,2,13,5,7,8,6,5};
       // int k=3;
      //  int[] ans = new SlidingWindowMaximumLC239().maxSlidingWindow(nums,k);
      //  System.out.println(Arrays.toString(ans));

        int[] nums1 = {1,-1};
        int k1=1;
        int[] ans1 = new SlidingWindowMaximumLC239().maxSlidingWindow(nums1,k1);
        System.out.println(Arrays.toString(ans1));
    }
}
