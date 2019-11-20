package cn.luckylin.vip;

import cn.luckylin.vip.config.Pipeline.ZuiDaZyPipeline;
import cn.luckylin.vip.config.TheCrawler;
import cn.luckylin.vip.config.error.HttpClientDownloader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.RedisScheduler;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
class VipApplicationTests {

    @Test
    void contextLoads() {
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

}
