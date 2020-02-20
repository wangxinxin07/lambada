package com.bj.wxx.lambda;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 流的测试
 *
 * @author wangxinxin07
 * @date 2020/2/17
 */
public class TestStream {


    public static void main(String[] args) {
        testIterator();
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
