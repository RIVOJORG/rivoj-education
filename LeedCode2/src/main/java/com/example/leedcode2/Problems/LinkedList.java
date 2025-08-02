package com.example.leedcode2.Problems;

import java.util.ArrayList;
import java.util.Arrays;

public class LinkedList {
    public static void main(String[] args) {
        boolean valid = isValid("([])");
        System.out.println("valid = " + valid);

    }
    public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode res = new ListNode(0);
        ListNode cur = res;
        while (l1 != null && l2 != null) {
            if(l1.val <= l2.val){
                cur.next = l1;
                l1 = l1.next;
            }
            if(l2.val < l1.val ){
                cur.next = l2;
                l2 = l2.next;
            }
            cur = cur.next;
        }
        if(l1 != null){cur.next = l1;}
        if(l2 != null){cur.next = l2;}
        return res.next;
    }

    public ListNode deleteDuplicates83(ListNode head) {
        ListNode curr = head;
        while (curr.next !=  null){
            if(curr.val == curr.next.val){
                curr.next = curr.next.next;
            }else {
                curr = curr.next;
            }
        }
        return head;

    }

    public ListNode reverseList(ListNode head){
        ListNode hozirgi = head;
        ListNode oldingi = null;

        while (hozirgi != null) {
            ListNode keyingisi = hozirgi.next;
            hozirgi.next = oldingi;
            oldingi = hozirgi;
            hozirgi = keyingisi;
        }
        return oldingi;
    }

    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        if(headA == null || headB == null) return null;
        ListNode pointerA = headA;
        ListNode pointerB = headB;

        while (pointerA != pointerB){
            pointerA = (pointerA!=null)? pointerA.next: headB;
            pointerB = (pointerB!=null)? pointerB.next: headA;
        }
        return pointerA;

    }
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        m--;
        n--;
        while (m >= 0 && n >= 0) {
            if (nums1[m] > nums2[n]) {
                nums1[m + n + 1] = nums1[m--];
            } else {
                nums1[m + n + 1] = nums2[n--];
            }
        }
        while (n >= 0) {
            nums1[n] = nums2[n--];
        }
    }

    public void merge2(int[] nums1, int m, int[] nums2, int n) {
        System.arraycopy(nums2, 0, nums1, m, n);
        Arrays.sort(nums1);
    }
    public static int scoreOfString(String s) {
        int res = 0;
        for (int i = 1; i < s.length(); i++) {
            res += Math.abs(s.charAt(i-1) - s.charAt(i));
        }
        return res;
    }

    public static  int minimumOperations(int[] nums) {
        int res = 0;
        for (int num : nums) {
            if(num%3 != 0){res++;}
        }
        return res;
    }
    public static int differenceOfSums(int n, int m) {
        int res = 0;
        for (int i = 0; i < n; i++) {
            if (i % m != 0){
                res += i;
            }else {
                res -= i;
            }
        }
        return res;
    }
    public static boolean isValid(String s) {
        int curlRight = 0;
        int curlLeft = 0;
        int roundRight = 0;
        int roundLeft = 0;
        int squareRight = 0;
        int squareLeft = 0;
        for (int i = 0; i < s.length(); i++) {
            if(s.charAt(i) == ')'){
                roundRight++;
            }if(s.charAt(i) == '('){
                roundLeft++;
            }if(s.charAt(i) == ']'){
                squareRight++;
            }if(s.charAt(i) == '['){
                squareLeft++;
            }if(s.charAt(i) == '}'){
                curlRight++;
            }if(s.charAt(i) == '{'){
                curlLeft++;
            }
        }
        return ((curlRight+curlLeft)%2 == 0 && (roundRight+roundLeft)%2 == 0 && (squareRight+squareLeft)%2 == 0);
    }
    public static int hammingWeight(int n) {
        int res = 0;
        while(n > 0){
            res += n%2;
            n/=2;
        }
        return res;
    }

    public int[] minOperations(String boxes) {
        int length = boxes.length();
        int[] res = new int[length];
        ArrayList<Integer> nonEmptyBoxes = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            if(boxes.charAt(i) == '1'){
                nonEmptyBoxes.add(i);
            }
        }
        for (int i = 0; i < length; i++) {
            int tmp = 0;
            for (Integer nonEmptyIndex : nonEmptyBoxes) {
                tmp += Math.abs(i-nonEmptyIndex);
            }
            res[i] = tmp;
        }
        return res;
    }

}
