
package LeetCode;

public class BullsandCowsLC299 {
    public static String getHint(String secret, String guess) {
        StringBuilder result = new StringBuilder();
        int bulls=0;
        int cows=0;
        int i=0,j=0;
        int[] count= new int[10];
        while( i < secret.length() || j < guess.length() ){
            if(secret.charAt(i) == guess.charAt(j)){
                bulls++;
            }else{
                count[secret.charAt(i)-'0'] +=1;
                count[guess.charAt(j)-'0'] -=1;
            }
            i++;
            j++;
        }
        int countcows=0;
        for(int k=0;k<10;k++){
            if (count[k]>0){
                countcows+=count[k];
            }
        }
        cows=secret.length()-bulls-countcows;
        result.append(String.valueOf(bulls))
                .append('A')
                .append(String.valueOf(cows))
                .append('B');
        return result.toString();
    }

    public static void main(String[] args) {
        String secret = "1123", guess = "0111";
        System.out.println(getHint(secret,guess));
        String secret1 = "1807", guess1 = "7810";
        System.out.println(getHint(secret1,guess1));
        String secret2 = "1122", guess2="3456";
        System.out.println(getHint(secret2,guess2));
    }
}
