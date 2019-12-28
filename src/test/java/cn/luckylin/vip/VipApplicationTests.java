package cn.luckylin.vip;

import cn.luckylin.vip.bean.MovieDetails;
import cn.luckylin.vip.bean.MovieUrl;
import cn.luckylin.vip.config.Pipeline.ZuiDaZyPipeline;
import cn.luckylin.vip.config.TheCrawler;
import cn.luckylin.vip.config.error.HttpClientDownloader;
import cn.luckylin.vip.config.redis.CacheUtils;
import cn.luckylin.vip.mapper.MovieDetailsMapper;
import cn.luckylin.vip.mapper.MovieUrlMapper;
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

import java.util.List;
import java.util.function.Consumer;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
class VipApplicationTests {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MovieDetailsMapper movieDetailsMapper;

    @Autowired
    private MovieUrlMapper movieUrlMapper;

    @Test
    void test01() {
        //开启爬虫
        Spider.create(new TheCrawler())
                .addUrl("http://zuidazy2.net/?m=vod-index.html")
                .thread(8)
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

    @Test
    public void movieDetailTest(){
        List<MovieDetails> movieDetails = movieDetailsMapper.selectAll();
        if (movieDetails != null && movieDetails.size() > 0) {
            for (int i = 0; i < movieDetails.size(); i++) {
                System.out.println("movieDetails = " + movieDetails.get(i));
            }
        }
    }

    @Test
    public void movieUrl(){
        List<MovieUrl> movieUrls = movieUrlMapper.selectAll();
        if (movieUrls != null && movieUrls.size() > 0) {
            movieUrls.forEach(new Consumer<MovieUrl>() {
                @Override
                public void accept(MovieUrl movieUrl) {
                    System.out.println("movieUrl = " + movieUrl);
                }
            });
        }



    }


}
