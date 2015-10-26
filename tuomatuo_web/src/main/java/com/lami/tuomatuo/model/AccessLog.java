package com.lami.tuomatuo.model;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by xjk on 10/24/15.
 */
public class AccessLog implements Serializable{

    private Integer id;

    private String className;
    private String methodName;
    private String arguments;
    private BigInteger processTime;

    private String result;

    private String ip;
    private String httpHead;
}
