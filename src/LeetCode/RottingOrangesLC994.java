package LeetCode;

import java.util.LinkedList;
import java.util.Queue;

class Pair{
    int x;
    int y;
    public Pair(int a,int b){
        this.x=a;
        this.y=b;
    }
}
public class RottingOrangesLC994 {
    public int orangesRotting(int[][] grid) {
        int total=0;
        int rotten=0;
        int minutes=0;
        Queue<Pair> queue = new LinkedList<Pair>();
        for(int i=0;i<grid.length;i++){
            for(int j=0;j<grid[0].length;j++){
                if(grid[i][j] ==1 || grid[i][j] == 2) total++;
                if(grid[i][j]==2) {
                    queue.offer(new Pair(i,j));
                }
            }
        }
        int[][] direction={{-1,0},{1,0},{0,1},{0,-1}};
        if(total==0) return 0;
        while(!queue.isEmpty()){
            int size = queue.size();
            rotten+=size;
            if(total==rotten) return minutes;
            minutes++;
            for(int i=0;i<size;i++){
                Pair p = queue.peek();
                int move_right = p.y +  direction[2][1];
                int move_left = p.y + direction[3][1];
                int move_up = p.x + direction[0][0];
                int move_down = p.x +  direction[1][0];

                if(move_down < grid.length && grid[move_down][p.y]==1){
                    grid[move_down][p.y] = 2;
                    queue.offer(new Pair(move_down,p.y));
                }
                if(move_up >= 0 && grid[move_up][p.y]==1){
                    grid[move_up][p.y] = 2;
                    queue.offer(new Pair(move_up,p.y));
                }
                if(move_right < grid[0].length && grid[p.x][move_right]==1){
                    grid[p.x][move_right] = 2;
                    queue.offer(new Pair(p.x,move_right));
                }
                if(move_left >= 0 && grid[p.x][move_left]==1){
                    grid[p.x][move_left] = 2;
                    queue.offer(new Pair(p.x,move_left));
                }
                queue.poll();
            }
        }
        return -1;
    }

    public static void main(String[] args) {
    int[][] grid= {{2,1,1},{1,1,0},{0,1,1}};
    int output = new RottingOrangesLC994().orangesRotting(grid);
        System.out.println(output);
    }
}
