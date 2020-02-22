package com.bj.wxx.lambda.testDefault;

import java.util.function.Consumer;

/**
 * @author wangxinxin07
 * @date 2020/2/22
 */
public interface Father {

    public void fly();

    default void run(Consumer<Father> action) {
        action.accept(this);
    }

    /**
     * jdk8新特性：接口静态方法
     */
    public static void sing() {
        System.out.println("i am sing a song");
    }

}
