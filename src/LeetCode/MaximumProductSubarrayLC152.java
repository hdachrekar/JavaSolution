package LeetCode;

public class MaximumProductSubarrayLC152 {
    public int maxProduct(int[] nums) {
        int curr_max_prod_sub = nums[0];
        int curr_min_prod_sub = nums[0];
        int ans = nums[0];
        int prev_max_prod_sub = nums[0];
        int prev_min_prod_sub = nums[0];

        for (int i = 1; i < nums.length; i++) {
            curr_max_prod_sub = Math.max(Math.max(prev_max_prod_sub * nums[i], prev_min_prod_sub * nums[i]), nums[i]);
            curr_min_prod_sub = Math.min(Math.min(prev_min_prod_sub * nums[i], prev_max_prod_sub * nums[i]), nums[i]);
            ans = Math.max(curr_max_prod_sub, ans);
            prev_max_prod_sub = curr_max_prod_sub;
            prev_min_prod_sub = curr_min_prod_sub;
        }
        return ans;
    }

    public static void main(String[] args) {
        int[] nums = {2, 3, -2, 4};
        System.out.println(new MaximumProductSubarrayLC152().maxProduct(nums));
    }
}
