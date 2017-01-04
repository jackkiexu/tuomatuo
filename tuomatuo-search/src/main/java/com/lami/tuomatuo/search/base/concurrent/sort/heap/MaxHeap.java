package com.lami.tuomatuo.search.base.concurrent.sort.heap;

import org.apache.log4j.Logger;

import javax.xml.crypto.Data;
import java.util.Arrays;

/**
 * http://vickyqi.com/2015/11/19/%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84%E7%B3%BB%E5%88%97%E2%80%94%E2%80%94%E5%A0%86/
 * Created by xjk on 1/2/17.
 */
public class MaxHeap extends Heap{

    private static final Logger logger = Logger.getLogger(MaxHeap.class);

    public MaxHeap(int[] data) {
        super(data);
    }

    /**
     * {@inheritDoc}
     */
    public Heap buildHeap() {
        // 从最后一个节点的父节点开始构建堆
        int start = getParentIndex(length - 1);
        for (; start >= 0; start--) {
            adjustDownHeap(start);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Heap remove() {
        // 将最后一个节点与头结点交换
        swap(0, length - 1);
        // 重新复制一个数组
        int[] newData = new int[length - 1];
        System.arraycopy(data, 0, newData, 0, length - 1);
        this.data = newData;
        this.length = length - 1;
        // 从头开始调整堆
        adjustDownHeap(0);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Heap insert(int value) {
        // 插入到数组最后
        int[] newData = new int[length + 1];
        System.arraycopy(data, 0, newData, 0, length);
        newData[length] = value;
        this.data = newData;
        this.length = length + 1;
        // 从最后一个节点开始自下而上调整堆
        adjustUpHeap(this.length - 1);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void adjustDownHeap(int node) {
        int right = getRightChildIndex(node);// 右孩子
        int left = getLeftChildIndex(node);// 左孩子
        int max = node;// 三者最大的节点的索引
        if (right < length && data[right] > data[max]) {
            max = right;
        }
        if (left < length && data[left] > data[max]) {
            max = left;
        }
        if (max != node) {// 需要调整
            swap(node, max);
            adjustDownHeap(max);// 递归调整与根节点进行交换的节点，保证下层也是堆
        }
    }

    /**
     * 将新插入的节点与其父节点进行比价, 直到最上一层, 保证 叶数上的最上个节点永远是最大的一个 赞~\(≧▽≦)/~
     * {@inheritDoc}
     */
    public void adjustUpHeap(int node) {
        int parent = getParentIndex(node);// 父节点
        if (parent >= 0 && data[parent] < data[node]) {
            swap(node, parent);
            adjustUpHeap(parent);// 递归调整与根节点进行交换的节点，保证上层也是堆
        }
    }

    public static void main(String[] args) {
        int[] data = new int[10];
        for (int i = 0; i < data.length; i++) {
            data[i] = (int) (Math.random() * 100);
        }
        logger.info("Arrays.toString(data):"+Arrays.toString(data));
        Heap heap = new MaxHeap(data);
        heap.buildHeap();
        logger.info("MaxHeap after buildHeap : " + Arrays.toString(heap.data));
        heap.remove();
        logger.info("MaxHeap after remove: " + Arrays.toString(heap.data));
        heap.insert((int) (Math.random() * 100));
        logger.info("MaxHeap  after insert: " + Arrays.toString(heap.data));
    }
}
