package com.xcs.spring.bean;

import com.xcs.spring.annotation.MyAnnotation;
import com.xcs.spring.annotation.MyClassAnnotation;

import java.io.Serializable;

/**
 * @author xcs
 * @date 2023年10月31日 10时53分
 **/
@MyClassAnnotation
public final class MyBean extends MyAbstract implements Serializable {

    public String key;

    public String value;

    @MyAnnotation
    public static void myMethod1() {
        // 方法1的实现
    }

    @MyAnnotation
    public String myMethod2() {
        return "hello world";
    }

    public void myMethod3() {
        // 方法3的实现
    }

    public static class MyInnerClass {
        // 内部类的定义
    }
}
