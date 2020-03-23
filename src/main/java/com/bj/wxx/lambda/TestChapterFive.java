package com.bj.wxx.lambda;

import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * lambda第五章
 *
 * @author wangxinxin07
 * @date 2020/2/25
 */
@Slf4j
public class TestChapterFive {

    @Data
    @Builder
    static class Person{
        private String name;
        private int age;
    }

    public static void main(String[] args) {

//        testMethodQuote();

//        testSort();

//        testCollector();

//        testMaxBy();

//        testPartitionBy();

//        testGroupBy();

//        testJoining();

//        testCombine();

        testComputeIfAbsent();
    }

    private static void testComputeIfAbsent() {

        Map<String, Person> personMap = new HashMap<>();
        Person zhangsan = Person.builder().name("zhangsan").age(11).build();
        personMap.put(zhangsan.getName(), zhangsan);

        //不存在则新增并put到map中
        Person lisi = personMap.computeIfAbsent("lisi", name -> Person.builder().name(name).age(11).build());
        log.info("lisi={}", JSON.toJSONString(lisi));
        log.info("personMap={}", JSON.toJSONString(personMap));
    }

    //组合收集器
    private static void testCombine() {
        Person a = Person.builder().name("zhangsan").age(11).build();
        Person b = Person.builder().name("lisi").age(103).build();
        Person c = Person.builder().name("wangwu").age(20).build();
        Person d = Person.builder().name("zhaoliu").age(20).build();
        Stream<Person> stream = Stream.of(a, b, c, d);
        //按照年龄分组
//        Map<Integer, List<Person>> ageMap = stream.collect(Collectors.groupingBy(Person::getAge));
//        log.info("ageMap={}", JSON.toJSONString(ageMap));

//        Map<Integer, Long> ageCountMap = stream.collect(Collectors.groupingBy(Person::getAge, Collectors.counting()));
//        log.info("ageCountMap={}", JSON.toJSONString(ageCountMap));

        Map<Integer, List<String>> ageNameMap = stream.collect(Collectors.groupingBy(Person::getAge, Collectors.mapping(Person::getName, Collectors.toList())));
        log.info("ageNameMap={}", JSON.toJSONString(ageNameMap));


    }

    private static void testJoining() {

        Stream<String> stream = Stream.of("aa", "bb", "cc");

        String result = stream.collect(Collectors.joining("|", "[", "]"));
        log.info("result={}", result);
    }

    private static void testGroupBy() {
        Person a = Person.builder().name("zhangsan").age(11).build();
        Person b = Person.builder().name("lisi").age(103).build();
        Person c = Person.builder().name("zhangsan").age(20).build();
        Person d = Person.builder().name("lisi").age(50).build();
        Stream<Person> stream = Stream.of(a, b, c, d);

        Map<String, List<Person>> map1 = stream.collect(Collectors.groupingBy(Person::getName));
        System.out.println(JSON.toJSONString(map1));


    }

    private static void testPartitionBy() {
        Person a = Person.builder().name("zhangsan").age(11).build();
        Person b = Person.builder().name("zhangsan").age(103).build();
        Person c = Person.builder().name("zhangsan").age(20).build();
        Person d = Person.builder().name("zhangsan").age(50).build();
        Map<Boolean, List<Person>> result = Stream.of(a, b, c, d).collect(Collectors.partitioningBy(person -> person.getAge() > 100));
        System.out.println(JSON.toJSONString(result));

        Map<Boolean, List<Person>> result2 = Stream.of(a, b, c, d).collect(Collectors.partitioningBy(person -> person.getAge() > 100));

    }

    private static void testMaxBy() {
        Person a = Person.builder().name("zhangsan").age(11).build();
        Person b = Person.builder().name("zhangsan").age(103).build();
        Person c = Person.builder().name("zhangsan").age(20).build();
        Person d = Person.builder().name("zhangsan").age(50).build();

        Optional<Person> max = Stream.of(a, b, c, d).collect(Collectors.maxBy(Comparator.comparing(Person::getAge)));
        System.out.println(JSON.toJSONString(max));

        Optional<Person> min = Stream.of(a, b, c, d).collect(Collectors.minBy(Comparator.comparing(Person::getAge)));
        System.out.println(JSON.toJSONString(min));

        Double average = Stream.of(a, b, c, d).collect(Collectors.averagingInt(Person::getAge));
        System.out.println(average);

        Integer sum = Stream.of(a, b, c, d).collect(Collectors.summingInt(Person::getAge));
        System.out.println(sum);

    }

    //测试收集器
    private static void testCollector() {
        List<Integer> list = Stream.of(1, 3, 5, 343, 23, 6763, 22).collect(Collectors.toList());
        System.out.println(list);

        Set<Integer> set = Stream.of(1, 3, 5, 343, 23, 6763, 22).collect(Collectors.toSet());
        System.out.println(set);

        TreeSet<Integer> treeSet = Stream.of(1, 3, 5, 343, 23, 6763, 22).collect(Collectors.toCollection(TreeSet::new));
        System.out.println(treeSet);

        LinkedList<Integer> linkedList = Stream.of(1, 3, 5, 343, 23, 6763, 22).collect(Collectors.toCollection(LinkedList::new));
        System.out.println(linkedList);

        ArrayList<Integer> arrayList = Stream.of(1, 3, 5, 343, 23, 6763, 22).collect(Collectors.toCollection(ArrayList::new));
        System.out.println(arrayList);


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

//        Stream<Person> personStream = Stream.of(1).map(Person::new);
    }


}
