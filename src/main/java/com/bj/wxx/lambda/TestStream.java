package com.bj.wxx.lambda;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 流的测试
 *
 * @author wangxinxin07
 * @date 2020/2/17
 */
public class TestStream {


    public static void main(String[] args) {
//        testIterator();

//        testCollect();

//        testMap();
//        testMap2();

//        testFilter();

//        testFlatMap();

//        testMin();

//        testMax();

//        testReduce();

//        testIntStream();

    }

    private static void testIntStream() {
//        List<Integer> list = Arrays.asList(1, 2, 3);
//        Stream<Integer> stream = list.stream();
//        System.out.println(stream.getClass());

        int max = Arrays.asList("aa", "bbb", "cccc").stream().mapToInt(ele -> ele.length()).max().getAsInt();
        IntSummaryStatistics intSummaryStatistics = Arrays.asList("aa", "bbb", "cccc").stream().mapToInt(ele -> ele.length()).summaryStatistics();
//        intSummaryStatistics.get
        System.out.println(max);

    }

    private static void testReduce() {
        List<Integer> list = Arrays.asList(1, 2, 33, 4, 888, 34, 609);
        Integer reduce = list.stream().reduce(0, (acc, ele) -> acc + 1);
        System.out.println(reduce);

        Integer result = list.stream().reduce((acc, ele) -> acc + ele).get();
        System.out.println(result);

        Integer result2 = list.stream().reduce((acc, ele) -> Math.max(acc, ele)).get();
        System.out.println(result2);

        Integer result3 = list.stream().reduce((acc, ele) -> Math.min(acc, ele)).get();
        System.out.println(result3);
    }

    private static void testMax() {
        List<Integer> list = Arrays.asList(1, 2, 33, 4, 888, 34, 609);
        Integer result = list.stream().max(Comparator.comparing(ele -> ele)).get();
        System.out.println(result);
    }

    private static void testMin() {
        List<Integer> list = Arrays.asList(1, 2, 33, 4, 888, 34, 609);
        Integer result = list.stream().min(Comparator.comparing(ele -> ele)).get();
        System.out.println(result);
    }

    private static void testFlatMap() {
        List<String> list = Arrays.asList("hello", "world");
        List<String> result = list.stream().flatMap(ele -> Arrays.stream(ele.split(""))).distinct().collect(Collectors.toList());
        System.out.println(result);
        //[h, e, l, o, w, r, d]
    }

    private static void testFilter() {
        List<String> list = Arrays.asList("a", "b", "c", "abc");
        List<String> result = list.stream().filter(ele -> ele.startsWith("a")).collect(Collectors.toList());
        System.out.println(result);
    }

    private static void testMap2() {
        List<Integer> list = Arrays.asList(1, 2, 10);
        List<Boolean> result = list.stream().map(ele -> ele.equals(1)).collect(Collectors.toList());
        System.out.println(result);
    }

    private static void testMap() {
        List<String> list = Arrays.asList("a", "b", "hello");
        List<String> result = list.stream().map(ele -> ele.toUpperCase()).collect(Collectors.toList());
        System.out.println(result);
    }

    private static void testCollect() {

        List<String> collect = Stream.of("a", "b", "c").collect(Collectors.toList());
        System.out.println(collect);

    }

    private static void testIterator() {
        List<String> nameList = new ArrayList<>();
        nameList.add("zhangsan");
        nameList.add("lisi");
        nameList.add("wangwu");

        for (String name : nameList) {
            System.out.println("name=" + name);
        }
        System.out.println("----------------------");

        nameList.stream().forEach(name -> System.out.println(name));
    }

}
