package com.lami.tuomatuo.search.base.concurrent.heapsort;

import java.util.Arrays;
import java.util.Random;

/**
 * http://vickyqi.com/2015/08/14/%E6%8E%92%E5%BA%8F%E7%AE%97%E6%B3%95%E7%B3%BB%E5%88%97%E2%80%94%E2%80%94%E5%A0%86%E6%8E%92%E5%BA%8F/
 * http://bubkoo.com/2014/01/14/sort-algorithm/heap-sort/
 * https://zh.wikipedia.org/wiki/%E5%A0%86%E6%8E%92%E5%BA%8F
 *
 * 选择排序: 堆排序
 *
 * Created by xjk on 1/2/17.
 */
public class KHeapSort {

    public static void main(String[] args) {
        Random ran = new Random();
        Integer[] data = new Integer[100000];
        for (int i = 0; i < data.length; i++) {
            data[i] = ran.nextInt(100000000);
        }
        KHeapSort.sort(data);
        System.out.println(Arrays.toString(data));
    }

    /**
     * 排序
     * @param data 待排序的数组
     * @param <T>
     */
    public static <T extends Comparable<T>> void sort(T[] data){
        long start = System.nanoTime();
        if(null == data){
            throw new NullPointerException("data");
        }

        if(data.length == 1){
            return;
        }
        buildMaxHeap(data);
        // 末尾与头交换, 交换后调整最大堆
        for(int i = data.length - 1; i > 0; i--){
            T temp = data[0];
            data[0] = data[i];
            data[i] = temp;
            adjustMexHeap(data, i, 0);
        }
        System.out.println("use time: " + (System.nanoTime() - start) / 1000000);
    }


    /**
     * 构建最大堆
     * @param data
     * @param <T>
     */
    public static <T extends Comparable<T>> void buildMaxHeap(T[] data){
        // 自下而上构建最大堆, 即从最后一个元素的父节点开始构建最大堆
        int start = getParentIndex(data.length - 1);
        for(;start >= 0; start--){
            adjustMexHeap(data, data.length, start);
        }
    }

    /**
     * 调整最大堆, 自下而上
     * @param data
     * @param heapSize 堆的大小, 即对data中从0开始到heapSize之间的元素构建最大堆
     * @param index 当前需要构建最大堆的位置
     * @param <T>
     */
    public static <T extends Comparable<T>> void adjustMexHeap(T[] data, int heapSize, int index){
        // 获取该元素左右子节点
        int left = getLeftChildIndex(index);
        int right = getRightChildIndex(index);
        int max = index;

        // 获取三个元素中最大值与父节点进行交换
        if(left < heapSize && data[max].compareTo(data[left]) < 0){
            max = left;
        }
        if(right < heapSize && data[max].compareTo(data[right]) < 0){
            max = right;
        }

        if(max != index){ // 这里进行第二次调整时就出现 max == index
            swap(data, index, max);
            adjustMexHeap(data, heapSize, max);
        }
    }

    /**
     * 交换节点
     * @param data
     * @param i
     * @param j
     * @param <T>
     */
    private static <T extends Comparable<T>> void swap(T[] data, int i, int j){
        T temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    /**
     * 获取父节点
     * @param i
     * @return
     */
    public static int getParentIndex(int i){
        return (i -1) >> 1;
    }

    /**
     * 获取左子节点位置
     * @param current
     * @return
     */
    private static int getLeftChildIndex(int current){
        return (current << 1) + 1;
    }

    /**
     * 获取右子节点位置
     * @param current
     * @return
     */
    private static int getRightChildIndex(int current){
        return (current << 1) + 2;
    }
}
