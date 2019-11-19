package cn.luckylin.vip;

import cn.luckylin.vip.config.TheCrawler;
import cn.luckylin.vip.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
class VipApplicationTests {

    @Test
    void contextLoads() {
        //开启爬虫
        Spider.create(new TheCrawler())
                .addUrl("http://www.zuidazy1.net/?m=vod-index-pg-1.html")
                .thread(3)
                .run();
    }

}
