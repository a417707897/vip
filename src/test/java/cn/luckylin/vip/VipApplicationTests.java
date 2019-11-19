package cn.luckylin.vip;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VipApplicationTests {

    @Test
    void contextLoads() {
        String s = "sss" + (1 + 1) + "sss";
        System.out.println("s = " + s);
    }

}
