package com.bj.wxx.lambda;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * lambda第五章
 *
 * @author wangxinxin07
 * @date 2020/2/25
 */
public class TestChapterFive {

    @Data
    static class Person{
        private String name;
        private int age;

        public Person(int age) {
            this.age = age;
        }

        public Person() {
        }
    }

    public static void main(String[] args) {

//        testMethodQuote();

//        testSort();

        testCollector();
    }

    //测试收集器
    private static void testCollector() {
        List<Integer> list = Stream.of(1, 3, 5, 343, 23, 6763, 22).collect(Collectors.toList());
        System.out.println(list);

        Set<Integer> set = Stream.of(1, 3, 5, 343, 23, 6763, 22).collect(Collectors.toSet());
        System.out.println(set);

        TreeSet<Integer> treeSet = Stream.of(1, 3, 5, 343, 23, 6763, 22).collect(Collectors.toCollection(TreeSet::new));
        System.out.println(treeSet);

    }

    //测试流的有序性
    private static void testSort() {
        HashSet<Integer> set = new HashSet<>(Arrays.asList(1, 3, 5, 343, 23, 6763, 22));
        List<Integer> collect = set.stream().collect(Collectors.toList());
        System.out.println(collect);

        List<Integer> sortList = set.stream().sorted().collect(Collectors.toList());
        System.out.println(sortList);

        List<Integer> unorder1 = set.stream().sorted().unordered().collect(Collectors.toList());
        List<Integer> unorder2 = set.stream().sorted().unordered().collect(Collectors.toList());
        List<Integer> unorder3 = set.stream().sorted().unordered().collect(Collectors.toList());
        System.out.println(unorder1);
        System.out.println(unorder2);
        System.out.println(unorder3);
    }

    //方法引用
    private static void testMethodQuote() {
//        Person lisi = Person.builder().name("zhangsan").age(10).build();
//        Stream.of(lisi).map(Person::getAge);
//        Stream.of(lisi).map(person -> person.getAge());

        Stream<Person> personStream = Stream.of(1).map(Person::new);
    }


}
