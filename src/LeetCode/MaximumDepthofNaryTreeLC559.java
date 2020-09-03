package LeetCode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/*
559. Maximum Depth of N-ary Tree
Given a n-ary tree, find its maximum depth.

The maximum depth is the number of nodes along the longest path from the root node down to the farthest leaf node.

Nary-Tree input serialization is represented in their level order traversal, each group of children is separated by the null value (See examples).
Example :
Input: root = [1,null,3,2,4,null,5,6]
Output: 3

* */
class Node {
    public int val;
    public List<Node> children;

    public Node() {}

    public Node(int _val) {
        val = _val;
    }

    public Node(int _val, List<Node> _children) {
        val = _val;
        children = _children;
    }
};
public class MaximumDepthofNaryTreeLC559 {
    public int maxDepthBFS(Node root){
        if(root == null) return 0;
        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);
        int depth = 0;
        while(!queue.isEmpty())
        {
            int size = queue.size();
            for(int i = 0; i < size; i++)
            {
                Node current = queue.poll();
                if (current.children != null){
                    for(Node child: current.children) {
                        if (child!=null)
                            queue.offer(child);
                    }
                }
            }
            depth++;
        }
        return depth;
    }
    public static int maxDepthRecursion(Node n){
        if (n == null) {
            return 0;
        }
        int maxDepth = 0;
        if (n.children != null){
            for(Node child : n.children){
                if (child!=null){
                    maxDepth = Math.max(maxDepth, maxDepthRecursion(child));
                }
            }
        }
        return maxDepth + 1;

    }
    public static int maxDepth=0;
    public static int maxDepthDFS(Node root){
        int depth=1;
        DFS(root,depth);
        return maxDepth;
    }

    private static void DFS(Node root, int depth) {
        if (root==null)  return ;
        maxDepth=Math.max(depth,maxDepth);
        if (root.children != null){
            for(Node child : root.children){
                DFS(child,depth+1);
            }
        }
    }

    public static void main(String[] args) {
        List<Node> node3 = new LinkedList<Node>();
        node3.add(new Node(5, null));
        node3.add(new Node(6, null));

        List<Node> node1 = new LinkedList<Node>();
        node1.add(new Node(3, node3));
        node1.add(new Node(2, null));
        node1.add(new Node(4, null));

        Node root = new Node(1, node1);
        System.out.println( "By BFS : " + new MaximumDepthofNaryTreeLC559().maxDepthBFS(root));
        System.out.println( "By DFS : " + new MaximumDepthofNaryTreeLC559().maxDepthDFS(root));
        System.out.println( "By Recursion : " + new MaximumDepthofNaryTreeLC559().maxDepthRecursion(root));
    }
}
