package com.lami.tuomatuo.search.base.concurrent.priorityqueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by xjk on 1/11/17.
 */
public class KPriorityQueueTest {

    public static void main(String[] args) {
        int a[] = {10, 40, 30, 60, 90, 70, 20, 50, 80};
        List<Integer> list = new ArrayList<>();
        for(int pice : a){
            list.add(pice);
        }
        KPriorityQueue<Integer> priorityQueue = new KPriorityQueue<Integer>(list);

        priorityQueue.heapify();
        priorityQueue.remove(30);
    }

}
