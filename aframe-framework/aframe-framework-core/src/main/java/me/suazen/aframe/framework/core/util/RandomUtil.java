package me.suazen.aframe.framework.core.util;

import java.util.HashSet;
import java.util.Set;

/**
 * @author sujizhen
 * @date 2023-06-21
 **/
public class RandomUtil extends cn.hutool.core.util.RandomUtil {
    public static int[] randomInts(int min,int max,int n){
        Set<Integer> set = new HashSet<>();
        do {
            // 调用Math.random()方法
            int num = (int) (Math.random() * (max - min)) + min;
            // 将不同的数存入HashSet中
            set.add(num);
            // 如果存入的数小于指定生成的个数，则调用递归再生成剩余个数的随机数，如此循环，直到达到指定大小
        } while (set.size() < n);
        int[] array = new int[n];
        int i = 0;
        for (int a : set) {
            array[i] = a;
            i++;
        }
        return array;
    }
}
