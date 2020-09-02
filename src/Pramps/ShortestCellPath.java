package Pramps;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import sun.nio.cs.ext.MacThai;

import java.util.LinkedList;
import java.util.Queue;

/*
Shortest Cell Path
In a given grid of 0s and 1s, we have some starting row and column sr, sc and a target row and column tr, tc. Return the length of the shortest path from sr, sc to tr, tc that walks along 1 values only.

Each location in the path, including the start and the end, must be a 1. Each subsequent location in the path must be 4-directionally adjacent to the previous location.

It is guaranteed that grid[sr][sc] = grid[tr][tc] = 1, and the starting and target positions are different.

If the task is impossible, return -1.
Example
input:
grid = [[1, 1, 1, 1], [0, 0, 0, 1], [1, 1, 1, 1]]
sr = 0, sc = 0, tr = 2, tc = 0
output: 8
(The lines below represent this grid:)
1111
0001
1111
*
* */
class Node{
    int x , y , dist;
    Node(int x,int y,int dist){
        this.x=x;
        this.y=y;
        this.dist=dist;
    }
}
public class ShortestCellPath {
    private static final int[] row = {-1, 0, 0, 1};
    private static final int[] col = {0, -1, 1, 0};
    static int shortestCellPathBFS(int[][] grid, int sr, int sc, int tr, int tc) {
        boolean[][]visited = new boolean[grid.length][grid[0].length];
        Queue<Node> nodeQueue = new LinkedList<Node>();
        visited[sr][sc]=true;
        nodeQueue.add(new Node(sr,sc,0));
        int minDist = Integer.MAX_VALUE;
        while(!nodeQueue.isEmpty()){
            Node nodeElem = nodeQueue.poll();
             sr=nodeElem.x;
             sc=nodeElem.y;
             int dist=nodeElem.dist;
             if(sr==tr && sc==tc){
                 minDist=dist;break;
             }
             for(int direction=0;direction<4;direction++){
                 if(isWithinGridBoundaryandNotYetVisited(grid,sr+row[direction],sc+col[direction],visited)){
                     visited[sr+row[direction]][sc+col[direction]]=true;
                     nodeQueue.add(new Node(sr+row[direction],sc+col[direction],dist+1));
                 }
             }
        }
        if (minDist != Integer.MAX_VALUE){
            return  minDist;
        }
        return -1;
    }
    static boolean isWithinGridBoundaryandNotYetVisited(int[][] grid,int sr,int sc, boolean[][]visited){
        return (sr>=0 && sr<grid.length && sc >=0 && sc<grid[0].length && grid[sr][sc]==1 && !visited[sr][sc]);
    }
    public static void main(String[] args) {
        int[][] grid= {{1, 1, 1, 1}, {0, 0, 0, 1},{1, 1, 1, 1}};
        System.out.println("grid distance : " + shortestCellPathBFS(grid,0,0,2,0));
        int[][] nonreachablegrid= {{1, 1, 1, 1}, {0, 0, 0, 1},{1, 0, 1, 1}};
        System.out.println("nonreachablegrid distance : " + shortestCellPathBFS(nonreachablegrid,0,0,2,0));
    }

}
