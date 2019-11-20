package cn.luckylin.vip;

import cn.luckylin.vip.config.Pipeline.ZuiDaZyPipeline;
import cn.luckylin.vip.config.TheCrawler;
import cn.luckylin.vip.config.error.HttpClientDownloader;
import cn.luckylin.vip.config.redis.CacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.RedisScheduler;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
class VipApplicationTests {

    @Autowired
    private RestTemplate restTemplate;

    @Test
    void test01() {
        //开启爬虫
        Spider.create(new TheCrawler())
                .addUrl("http://www.zuidazy1.net/?m=vod-index-pg-1.html")
                .thread(10)
                .setDownloader(new HttpClientDownloader())
                //指定保存结果操作对象
                .addPipeline(new ZuiDaZyPipeline())
                //设置Redis存放链接，来去重链接
                //.setScheduler(new RedisScheduler("60.205.210.108"))
                .run();
    }

    @Test
    public void test02(){
        ResponseEntity<String> forEntity = restTemplate.getForEntity("http://www.82190555.com/index/qqvod.php?url=https://v.qq.com/x/cover/h05z5bsjxw544er.html", String.class);

        HttpStatus statusCode = forEntity.getStatusCode();
        System.out.println("statusCode = " + statusCode);

        String body = forEntity.getBody();
        System.out.println("body = " + body);
    }

    @Test
    public void redisTest(){
        CacheUtils.hset("demo", "demo", "1");
        CacheUtils.hset("demo", "demo", "2");
        CacheUtils.hset("demo", "demo", "3");


    }

}
