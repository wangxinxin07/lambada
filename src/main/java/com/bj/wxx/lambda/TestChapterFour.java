package com.bj.wxx.lambda;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author wangxinxin07
 * @date 2020/2/22
 */
public class TestChapterFour {


    public static void main(String[] args) {

//        testForeach();

//        testStream();

//        testStreamOf();

        testOptional();
    }

    private static void testOptional() {
        Optional<String> op = Optional.of("aaa");
        op.orElse("ccc");

        String value = op.orElseGet(() -> {
            System.out.println("1111");
            return "ccc";
        });
    }

    private static void testStreamOf() {
        Stream.of("aaaa");
    }

    private static void testStream() {
        List<String> list = Arrays.asList("aaa", "bbb", "ccc");
        Stream<String> stream = list.stream();

    }

    private static void testForeach() {
        List<String> list = Arrays.asList("aaa", "bbb", "ccc");
        Iterator<String> iterator = list.iterator();
        iterator.forEachRemaining(ele -> System.out.println(ele));
    }


}
