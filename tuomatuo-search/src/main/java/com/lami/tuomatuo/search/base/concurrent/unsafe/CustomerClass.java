package com.lami.tuomatuo.search.base.concurrent.unsafe;

import lombok.Data;

/**
 * Created by xjk on 2016/5/13.
 */
@Data
public class CustomerClass {

    private byte byteField = 1;
    private short shortField = 2;
    private int intField = 3;
    private long longField = 6;
    private double doubleField = 5.5;
    private char charField = 'a';
    private String strField = "abc";
    private Person person;

    public CustomerClass() {}

    public CustomerClass(String strField) {
        this.strField = strField;
    }
    @Data
    public static class Person{
        int id;
        String name;

        public Person() {
        }

        public Person(int id) {
            this.id = id;
        }
    }
}
