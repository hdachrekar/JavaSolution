package LeetCode;
/*
Given a 2d grid map of '1's (land) and '0's (water), count the number of islands. An island is surrounded by water and is formed by connecting adjacent lands horizontally or vertically. You may assume all four edges of the grid are all surrounded by water.
Example 1:
Input: grid = [
  ["1","1","1","1","0"],
  ["1","1","0","1","0"],
  ["1","1","0","0","0"],
  ["0","0","0","0","0"]
]
Output: 1
Example 2:
Input: grid = [
  ["1","1","0","0","0"],
  ["1","1","0","0","0"],
  ["0","0","1","0","0"],
  ["0","0","0","1","1"]
]
Output: 3
*/
public class NumberofIslandsLC200 {
    public int numIslands(char[][] grid) {
        // your code goes here
        if (grid == null || grid.length == 0) {
            return 0;
        }
        int gridRows  = grid.length;
        int gridColumns =  grid[0].length;
        if (gridRows==0 || gridColumns==0) return 0;
        int count=0;
        for (int i=0;i<gridRows;i++){
            for (int j=0;j<gridColumns;j++){
                if(grid[i][j]=='1') {
                    count++;
                    dfs(grid,i,j);
                }

            }

        }
        return count;
    }
    static void dfs(char[][]grid, int i,int j){
        int gridRows  = grid.length;
        int gridColumns =  grid[0].length;
        if (i>=gridRows || j>=gridColumns || i<0 || j<0 || grid[i][j] == '0')
            return ;
        grid[i][j]= '0';
        dfs(grid,i+1,j);
        dfs(grid,i-1,j);
        dfs(grid,i,j+1);
        dfs(grid,i,j-1);
    }

    public static void main(String[] args) {
        char[][]grid = {{'1','1','1','1','0'},{'1','1','0','1','0'},{'1','1','0','0','0'},{'0','0','0','0','0'}};
        System.out.println(new NumberofIslandsLC200().numIslands(grid));
    }
}
