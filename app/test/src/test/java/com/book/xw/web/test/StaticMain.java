package com.book.xw.web.test;

import org.apache.commons.lang3.RandomUtils;

import java.util.*;

public class StaticMain {

    public static void main(String[] args) {
        Thread t = new Thread(()->{
            while (true){
                System.out.println("test");
            }
        });
        t.start();
        String s = null;
        if(s.equals("3")){
            System.out.println("haha");
        }
    }

    public static boolean hasGroupsSizeX(int[] deck) {
        if(deck == null || deck.length == 0){
            return false;
        }
        String s = "";
        Character c = 'c';
        StringBuffer sb = new StringBuffer();
        sb.append(c);

        List<Integer> list = new ArrayList<>();
        list.sort((o1, o2) -> o2-o1);
        Map<Integer,Integer> map = new HashMap();
        for(int i : deck){
            map.put(i, map.getOrDefault(i, 0) + 1);
        }
        int res = 0;
        for(int i : map.values()){
            res = gcd(i, res);
        }
        return res > 1;
    }

    public static int gcd(int a, int b) {
        return b > 0 ? gcd(b, a % b) : a;
    }
}
