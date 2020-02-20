package com.bj.wxx.lambda;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;


/**
 * lambda初次测试
 *
 * @author wangxinxin07
 * @date 2020/1/15
 */
public abstract class TestLambda {

    public static void main(String[] args) {
//        createThread();

//        createListener();

//        lambdaTest();

    }

    interface IntPred<T> {
        boolean test(T value);
    }

    public abstract boolean check(IntPred<Integer> predicate);

    public abstract boolean check(Predicate<IntPred> predicate);

    private static void lambdaTest() {
        Runnable noArguments = () -> System.out.println("this is no arguments lambda");

        ActionListener oneArgument = (ActionEvent event) -> System.out.println("this is one argument lambda");

        BinaryOperator<Long> add = (x, y) -> x + y;

        Button button = new Button();
        button.addActionListener(event -> System.out.println(""));

        BinaryOperator<Long> add2 = (x, y) -> {
            Long k = 3 * x;
            return k - y;
        };

    }

    private static void createListener() {
        Button button = new Button("点击");
        button.addActionListener((ActionEvent event) -> System.out.println("我被点击了"));
    }

    private static void createThread() {
        String name = getName();
        new Thread(() -> {
            System.out.println("2020年2月16日16:35:08.userName=" + name);
        }).start();
    }

    private static String getName() {
        return "zhangsan";
    }

}
