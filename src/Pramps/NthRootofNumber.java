package Pramps;

public class NthRootofNumber {
    static double result ;
    static double root(double x, int n) {
        // your code goes here

        double low=0.0;
        double high = x;
         bs(low,high,n,x);
        return result;
    }
    public static void bs(double low ,double high,int n,double x){
        double mid =  (high+low)/2;
        if((Math.abs(Math.pow(mid,n) - x) < 0.001) || (Math.abs(Math.pow(mid,n) - x) == 0.0)){
            result=mid;
        }else{
            if( Math.pow(mid,n) > x ){
                bs( low , mid, n, x);
            }else if( Math.pow(mid,n) < x ){
                bs( mid , high, n, x);
            }
        }
    }

    public static void main(String[] args) {

        double x=27;
        int n = 3;
        System.out.println(root(x,n));
    }
}
