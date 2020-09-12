package LeetCode;

import java.util.ArrayList;
import java.util.List;

public class PancakeSortingLC969 {
    public List<Integer> pancakeSort(int[] A) {
        List<Integer> res=new ArrayList();
        for(int i=A.length-1;i>0;i--){
            int idx=findMax(A,i);
            flip(A,idx);
            flip(A,i);
            res.add(idx+1);
            res.add(i+1);
        }
        return res;
    }
    public static void flip(int[] arr,int k){
        int i=0, j=k;
        while (i<=j){
            int temp =arr[i];
            arr[i]=arr[j];
            arr[j]=temp;
            i++;
            j--;
        }
    }
    public static int findMax(int[] arr,int k){
        int ans=0;
        //int number=Integer.MIN_VALUE;
        for(int i=0;i<=k;i++){
            if(arr[i]>arr[ans]){
                ans=i;
            }

        }
        return ans;
    }

    public static void main(String[] args) {
        int[] A={3,2,4,1};
        System.out.println(new PancakeSortingLC969().pancakeSort(A));
    }
}
