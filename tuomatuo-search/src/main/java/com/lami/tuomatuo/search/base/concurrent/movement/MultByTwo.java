package com.lami.tuomatuo.search.base.concurrent.movement;

/**
 * Created by xujiankang on 2016/5/13.
 */
public class MultByTwo {

    public static void main(String[] args) {
        int i;
        int num = 0xFFFFFFE;
        for (i = 0; i < 4; i++){
            num = num << 1;
            System.out.println(num);
        }

    }

}
