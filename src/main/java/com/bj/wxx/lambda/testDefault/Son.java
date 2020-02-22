package com.bj.wxx.lambda.testDefault;

/**
 * @author wangxinxin07
 * @date 2020/2/22
 */
public class Son implements Father {
    @Override
    public void fly() {
        System.out.println("i am son,i am fly");
    }

    public static void main(String[] args) {
        Son son = new Son();
        son.fly();
        son.run(ele -> ele.fly());
    }
}
