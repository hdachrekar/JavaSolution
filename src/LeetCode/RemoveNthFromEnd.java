package LeetCode;
class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}
public class RemoveNthFromEnd {
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy= new ListNode(-1);
        dummy.next = head;
        int length =0;
        if (n<=0) return head;
        ListNode prevDelete=head;
        while(prevDelete != null){
            prevDelete=prevDelete.next;
            length++;
        }
        int i=length-n;
        prevDelete = dummy;
        while(i > 0 ){
            if(head == null){
                return null;
            }
            prevDelete =  prevDelete.next;
            i--;
        }

        prevDelete.next = prevDelete.next.next;
        return dummy.next;
    }

    public static void main(String[] args) {
        int n=2;
        ListNode five = new ListNode(5);
        ListNode four = new ListNode(4,five);
        ListNode three = new ListNode(3,four);
        ListNode two = new ListNode(2,three);
        ListNode one = new ListNode(1,two);
        ListNode result =new RemoveNthFromEnd().removeNthFromEnd(one,n);
        while(result != null){
            System.out.println(result.val);
            result=result.next;
        }


    }
}
