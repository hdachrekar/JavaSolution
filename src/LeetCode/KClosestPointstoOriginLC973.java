package LeetCode;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

class Point{
    int x;
    int y;
    double dist;
    public Point(int a,int b,double d){
        x=a;
        y=b;
        dist=d;
    }
}
class distCompare implements Comparator<Point> {
    public int compare(Point p1,Point p2){
        if (p1.dist < p2.dist){
            return +1;
        } else if (p1.dist > p2.dist){
            return -1;
        }else{
            return 0;
        }

    }
}
public class KClosestPointstoOriginLC973 {
    public int[][] kClosest(int[][] points, int K) {
        distCompare dc = new distCompare();
        PriorityQueue<Point> queue = new PriorityQueue<Point>(K,dc);
        for(int i=0;i<points.length;i++){
            double dist = euclideanDistCalFromOrigin(points[i][0],points[i][1]);
            Point p = new Point(points[i][0],points[i][1],dist);
            queue.add(p);
            if (queue.size()>K){
                queue.poll();
            }
        }
        int[][]output = new int[K][2];
        for (int k=0;k<K;k++){
            Point p =  queue.poll();
            output[k][0] = p.x;
            output[k][1] = p.y;
        }
        return output;
    }
    public double euclideanDistCalFromOrigin(int x,int y){
        double result =  Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
        return result;
    }

    public static void main(String[] args) {
        int[][] points= new int[][]{{1,3},{-2,2},{2,-2}};
        int k = 2;
        int[][] lst = new KClosestPointstoOriginLC973().kClosest(points, k);
        for (int[] arr : lst) {
            System.out.print(Arrays.toString(arr));
        }
    }
}
