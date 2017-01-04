package com.lami.tuomatuo.search.base.concurrent.sort.heap.heapsort;

import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * heap 排序
 * https://zh.wikipedia.org/wiki/%E5%A0%86%E6%8E%92%E5%BA%8F
 *
 * Created by xjk on 1/2/17.
 */
public class HeapSort {

    private static final Logger logger = Logger.getLogger(HeapSort.class);

    private static int[] sort = new int[]{1,0,10,20,3,5,6,4,9,8,12,17,34,11}; // 14

    /**
     * 1. 将整个数组构建成一个堆得形式(最大堆, 每个树的root必定大于 leaf)
     * 2. 将整棵树的最大值与最小值进行互换
     * 3. 将上次末尾的节点放置到堆得最合适的节点位置
     * @param args
     */
    public static void main(String[] args) {
        buildMaxHeapify(sort);
        log(sort);
        heapSort(sort);
        log(sort);
    }

    /**
     * 1. 获取数组最后一个节点的 parent 的index
     * 2. 逐级向上, 将下面子节点中最大的值赋值给 parent
     * 3. 经过遍历后, index为0的节点的值最大
     * 4.
     * @param data
     */
    private static void buildMaxHeapify(int[] data){
        // 没有子节点的才需要创建最大堆, 从最后一个的父节点开始
        int startIndex = getParentIndex(data.length - 1);
        // 从尾端开始创建最大堆, 每次都是正确的堆
        for(int i = startIndex; i >= 0; i--){
            System.out.println();
            logger.info("begin maxHeapify  data.length : " + data.length + ", i : " + i + ", Arrays.toString(data):" + Arrays.toString(data));
            maxHeapify(data, data.length, i);
            logger.info("over maxHeapify  data.length : " + data.length + ", i : " + i + ", Arrays.toString(data):" + Arrays.toString(data));
            System.out.println();
        }
    }

    /**
     * 创建最大堆
     * @param data
     * @param heapSize 需要创建最大堆的大小, 一般在sort的时候用到, 因为
     *                 最多值放在末尾, 末尾就不再归入最大堆了
     * @param index 当前需要创建最大堆的位置
     */
    private static void maxHeapify(int[] data, int heapSize, int index){

        // 当前点与左右子节点比较
        int left = getChildLeftIndex(index);
        int right = getChildRightIndex(index);

        logger.info("maxHeapify init heapSize : " + heapSize + ", index : " + index + ", left:" + left + ", right:" + right + ", Arrays.toString(data):" + Arrays.toString(data));

        int largest = index;
        if(left < heapSize && data[index] < data[left]){ // 注意这里是小于, 不存在等于
            largest = left;
        }

        if(right < heapSize && data[largest] < data[right]){  // 注意这里是小于, 不存在等于
            largest = right;
        }

        // 得到最大值后可能需要交换, 如果交换了, 其子节点可能就不是最大堆了, 需要重新调整
        logger.info("maxHeapify result largest:" + largest + ", index : " + index + ", Arrays.toString(data):" + Arrays.toString(data));
        if(largest != index){
            int temp = data[index];
            data[index] = data[largest];
            data[largest] = temp;
            maxHeapify(data, heapSize, largest);
        }
    }

    /**
     *
     * 排序, 最大值放在末尾, 在排序后就成了递增的
     * @param data
     */
    private static void heapSort(int[] data){
        // 末尾与头交换, 交换后调整最大堆
        /**
         * 因为 buildMaxHeapify, 所以保证现在整个堆是一个最大堆(每颗树的root都大于其leaf)
         * 每次的for循环都会将下面节点中的最大的值放在root位置
         */
        for(int i = data.length - 1; i > 0; i--){
            int temp = data[0];
            data[0] = data[i];
            data[i] = temp;
            logger.info("maxHeapify begin , Arrays.toString(data):" + Arrays.toString(data) + ", i : " + i);
            maxHeapify(data, i, 0);
            logger.info("maxHeapify over , Arrays.toString(data):" + Arrays.toString(data) + ", i : " + i);
            logger.info("");
        }
    }

    /**
     * 父节点
     * @param current
     * @return
     */
    private static int getParentIndex(int current){
        return (current - 1) >> 1;
    }

    /**
     * 左子节点 position 注意括号, 加法优先级更高
     * @param current
     * @return
     */
    private static int getChildLeftIndex(int current){
        return (current << 1) + 1;
    }

    /**
     * 右子节点的 position
     * @param current
     * @return
     */
    private static int getChildRightIndex(int current){
        return (current << 1) + 2;
    }

    private static void log(int[] data){
        System.out.println();
        System.out.println("**-------------------------------------**");
        System.out.println();
        int pre = -2;
        for(int i = 0; i < data.length; i++){
            if(pre < (int)getLong(i+1)){
                pre = (int)getLong(i+1);
                System.out.println();
            }
            System.out.print(data[i] + " |");
        }
        System.out.println();
        System.out.println("**-------------------------------------**");
        System.out.println();
    }

    /**
     * 以2为底数的对数
     * @param param
     * @return
     */
    private static double getLong(double param){
        return Math.log(param) / Math.log(2);
    }
}
