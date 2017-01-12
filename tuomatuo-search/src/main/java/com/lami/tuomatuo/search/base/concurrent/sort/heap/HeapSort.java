package com.lami.tuomatuo.search.base.concurrent.sort.heap;

import java.util.Arrays;

/**
 * Created by xjk on 1/12/17.
 */
public class HeapSort {

    private static int[] sort = new int[]{1, 0, 10, 20, 3, 5, 6, 4, 9, 8, 12, 17, 34, 11};

    public static void main(String[] args) {
        buildMaxHeapify(sort);
        heapSort(sort);
        System.out.println(Arrays.toString(sort));
    }

    /**
     * 创建最大树
     * @param data
     */
    private static void buildMaxHeapify(int[] data){
        // 获取数组的最后一个父节点的index
        int startIndex = getParentIndex(data.length - 1);
        // 从最后一个父节点开始排序
        // 每次调用 maxHeapify 其实是将一颗二叉树中最大的值放到parent位置, 而这里是从底层往上
        // 从而最终效果是, 整个数组中的每棵树的最大值都在parent位置
        for(int i = startIndex; i >= 0; i--){
            maxHeapify(data, data.length, i);
        }
    }

    private static void heapSort(int[] data){
        // 1. 将最大树的头节点(即整棵树的最大值)与末尾节点交换
        // 2. 排除最大值, 数组的剩余部分进行最大值排序
        // 3. 重复 1, 2 步骤, 直达最顶的那个节点
        for(int i = data.length - 1; i > 0; i--){
            int temp = data[0];
            data[0] = data[i];
            data[i] = temp;
            maxHeapify(data, i, 0);
        }
    }

    /**
     * 创建最大堆
     * @param data
     * @param heapSize 需要创建最大堆的大小, 一般在sort时候会用到
     * @param index 当前需要创建最大堆的位置
     */
    private static void maxHeapify(int[] data, int heapSize, int index){
        // 当前点与左右子节点的比较
        int left = getLeftChildIndex(index);
        int right = getRightChildIndex(index);

        int largest = index;
        if(left < heapSize && data[index] < data[left]){
            largest = left;
        }
        if(right < heapSize && data[largest] < data[right]){
            largest = right;
        }

        // 获取子节点中的最大值后可能需要交换, 交换后最大的值就在父节点
        if(largest != index){
            int temp = data[index];
            data[index] = data[largest];
            data[largest] = temp;
            maxHeapify(data, heapSize, largest);
        }
    }

    /**
     * 获取父节点
     * @param current
     * @return
     */
    private static int getParentIndex(int current){
        return (current - 1) >>> 1;
    }

    /**
     * 获取左子节点的
     * @param current
     * @return
     */
    private static int getLeftChildIndex(int current){
        return (current << 1) + 1;
    }

    /**
     * 获取右子节点的坐标
     * @param current
     * @return
     */
    private static int getRightChildIndex(int current){
        return (current << 1) + 2;
    }

}
