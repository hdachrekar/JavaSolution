package Pramps;
/*
Root of Number
Many times, we need to re-implement basic functions without using any standard library functions already implemented. For example, when designing a chip that requires very little memory space.

In this question we’ll implement a function root that calculates the n’th root of a number. The function takes a nonnegative number x and a positive integer n, and returns the positive n’th root of x within an error of 0.001 (i.e. suppose the real root is y, then the error is: |y-root(x,n)| and must satisfy |y-root(x,n)| < 0.001).

Don’t be intimidated by the question. While there are many algorithms to calculate roots that require prior knowledge in numerical analysis (some of them are mentioned here), there is also an elementary method which doesn’t require more than guessing-and-checking. Try to think more in terms of the latter.

input:  x = 7, n = 3
output: 1.913

input:  x = 9, n = 2
output: 3
*/
public class NthRootofNumber {
    static double result;

    static double root(double x, int n) {
        // your code goes here

        double low = 0.0;
        double high = x;
        bs(low, high, n, x);
        return result;
    }

    public static void bs(double low, double high, int n, double x) {
        double mid = (high + low) / 2;
        if ((Math.abs(Math.pow(mid, n) - x) < 0.001) || (Math.abs(Math.pow(mid, n) - x) == 0.0)) {
            result = mid;
        } else {
            if (Math.pow(mid, n) > x) {
                bs(low, mid, n, x);
            } else if (Math.pow(mid, n) < x) {
                bs(mid, high, n, x);
            }
        }
    }

    public static void main(String[] args) {

        double x = 27;
        int n = 3;
        System.out.println(root(x, n));
    }
}
