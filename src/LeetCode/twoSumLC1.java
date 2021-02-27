package LeetCode;
import java.util.*;
public class twoSumLC1 {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> weightIndMap = new HashMap<Integer, Integer>();
        for(int i=0;i<nums.length;i++){
            weightIndMap.put(nums[i],i);
        }
        for(int i=0;i<nums.length;i++){
            if(weightIndMap.containsKey(target-nums[i]) && i != weightIndMap.get(target-nums[i]) ){
                int[] result= new int[2];
                result[0]= i;
                result[1]=weightIndMap.get(target-nums[i]);
                return result;
            }
        }
        return new int[]{};
    }

    public static void main(String[] args) {
        int nums[] = {2,7,11,15};
        int target=9;
        System.out.println(Arrays.toString(new twoSumLC1().twoSum(nums,target)));
    }
}
