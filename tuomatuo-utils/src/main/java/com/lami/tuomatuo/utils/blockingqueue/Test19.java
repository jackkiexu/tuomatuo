package com.lami.tuomatuo.utils.blockingqueue;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * One reason to use SynchronousQueue is to improve application performance. If you must have a hand-off between threads, you will need some synchronization object. If you can satisfy the conditions required for its use, SynchronousQueue is the fastest synchronization object I have found. Others agree.
 *
 * http://stackoverflow.com/questions/8591610/when-should-i-use-synchronousqueue
 *
 * Created by xjk on 2016/10/17.
 */
public class Test19 {
    public static void main(String[] args) {
        SynchronousQueue<Integer> queue = new SynchronousQueue<Integer>();
        new Customer(queue).start();
        new Product(queue).start();
    }
    static class Product extends Thread{

        List<Integer> result = new LinkedList<Integer>();
        int i = 0;

        SynchronousQueue<Integer> queue;
        public Product(SynchronousQueue<Integer> queue){
            this.queue = queue;
        }
        @Override
        public void run(){
            while(true){
                if(i++ > 10){
                    break;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int rand = new Random().nextInt(1000);

                result.add(rand);

                try {
                    queue.put(rand);
                    System.out.println("生产了一个产品：, rand:"+rand+ "等待三秒后运送出去...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("生产 result:"+result);
        }
    }
    static class Customer extends Thread{
        SynchronousQueue<Integer> queue;
        public Customer(SynchronousQueue<Integer> queue){
            this.queue = queue;
        }

        List<Integer> result = new LinkedList<Integer>();
        int i = 0;

        @Override
        public void run(){
            int rand = 0;
            while(true){

                if(i> 10){
                    break;
                }

                try {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    rand = queue.take();
                    System.out.println("消费了一个产品:"+ rand);
                } catch (Exception e) {
                    System.out.println("消费 result:"+result);
                    break;
                }
                result.add(rand);
                System.out.println("------------------------------------------");
            }

        }
    }
    /**
     * 运行结果：
     *  生产了一个产品：464
     等待三秒后运送出去...
     消费了一个产品:773
     ------------------------------------------
     生产了一个产品：547
     等待三秒后运送出去...
     消费了一个产品:464
     ------------------------------------------
     生产了一个产品：87
     等待三秒后运送出去...
     消费了一个产品:547
     ------------------------------------------
     */
}
