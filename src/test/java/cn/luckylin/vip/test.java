package cn.luckylin.vip;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class test {

    @Test
    public void test01() {
        List<String> arr = new ArrayList<>();

        arr.add("2020-01-10");
        arr.add("2020-01-11");
        arr.add("2020-01-12");
        String temp = "";
        for (int i = 0; i < arr.size(); i++) {
            String[] split = arr.get(i).split("-");
            if (i == 0) {
                temp += split[0] + split[1] + split[2];
            } else {
                temp += ("|" + split[2]);
            }

        }
        //20200110|10|11|12|13
        System.out.println("temp = " + temp);




    }


}
