package com.bj.wxx.lambda;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 第六章练习 流的并行化
 *
 * @author wangxinxin07
 * @date 2020/3/23
 */
public class TestChapterSix {
    @Data
    @AllArgsConstructor
    static class Person {
        Integer age;
        String name;
    }

    public static void main(String[] args) {

//        testCalcSum();

        testParallelSetAll();


    }

    //数组并行设置
    private static void testParallelSetAll() {
        int[] nums = new int[10];
        Arrays.parallelSetAll(nums, i -> i);
        System.out.println(JSON.toJSONString(nums));
    }

    /**
     * 计算求和
     */
    private static void testCalcSum() {
        List<Person> personList = new ArrayList<>();
        for (int i = 0; i < 10000000; i++) {
            Person p1 = new Person(11,"zhansan1");
            personList.add(p1);
        }

        /*long time1 = System.currentTimeMillis();
        int sumAge = personList.stream().mapToInt(Person::getAge).sum();
        System.out.println(sumAge);
        long time2 = System.currentTimeMillis();
        System.out.println("spend:" + (time2 - time1));*/

        long time3 = System.currentTimeMillis();
        int sumAge2 = personList.parallelStream().mapToInt(Person::getAge).sum();
        System.out.println(sumAge2);
        long time4 = System.currentTimeMillis();
        System.out.println("spend:" + (time4 - time3));

    }

}
