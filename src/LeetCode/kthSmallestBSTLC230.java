package LeetCode;

  class TreeNode {
      int val;
      TreeNode left;
      TreeNode right;
      TreeNode() {}
      TreeNode(int val) { this.val = val; }
      TreeNode(int val, TreeNode left, TreeNode right) {
          this.val = val;
          this.left = left;
          this.right = right;
      }
  }

class kthSmallestBSTLC230 {
    int count=0;
    int output=Integer.MAX_VALUE;
    public int kthSmallest(TreeNode root, int k) {
        inOrderTraversal(root,k);
        return output;
    }
    public void inOrderTraversal(TreeNode root, int k){
        if (root==null || count > k){
            return ;
        }
        inOrderTraversal(root.left,k);
        if(++count==k){
            output=root.val;
            return;
        }
        inOrderTraversal(root.right,k);
    }

    public static void main(String[] args) {
        int k = 3;
        TreeNode one = new TreeNode(1,null,null);
        TreeNode two = new TreeNode(2,one,null);
        TreeNode four = new TreeNode(4,null,null);
        TreeNode three = new TreeNode(3,two,four);
        TreeNode six = new TreeNode(6,null,null);
        TreeNode five = new TreeNode(5,three,six);
        TreeNode root = five ;
        int result = new kthSmallestBSTLC230().kthSmallest(root,k);
        System.out.println(result);
    }
}