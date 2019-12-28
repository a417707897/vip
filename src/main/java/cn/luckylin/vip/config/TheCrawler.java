package cn.luckylin.vip.config;

import cn.luckylin.vip.bean.MovieDetails;
import cn.luckylin.vip.bean.MovieUrl;
import cn.luckylin.vip.bean.Movies;
import cn.luckylin.vip.config.redis.CacheUtils;
import cn.luckylin.vip.utils.DateUtils;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @Description: 爬虫的具体配置
 * @Author: zhouyulin
 * @Date: 2019/11/18
 */
@Slf4j
@Component
public class TheCrawler implements PageProcessor {
    //设置全局爬虫的配置，重试次数，间隔时间等等
    private Site site = Site.me()
            .setRetryTimes(4)
            .setSleepTime(1000)
            .setTimeOut(100000);

    private static Integer maxPage;

    //判断是否是页面的url
    private final static String PGURL = "m=vod-index";

    //判断是不是详情的url
    private final static String DETAILURL = "vod-detail-id";

    //redis标题  www.zuidazy1.net
    private final static String ZUIDAZY2 = "zuidazy2.net";

    Movies movies = null;

    /**
     * @Description: 爬虫的具体逻辑的编写【数据的过滤加抽取】
     * @Param: [page]
     * @return: void
     * @Author: zhouyulin
     * @Date: 2019/11/18
     */
    @Override
    public void process(Page page) {
        //获取url判断使用哪个爬取方法
        Selectable url = page.getUrl();
        if (url == null) {
            return;
        }

        //page爬取id
        if (url.regex(PGURL).match()) {
            this.pgProcess(page);
        } else if (url.regex(DETAILURL).match()) {
            this.detailProcess(page);
        }

    }

    /**
     * @Description: 爬取分页数据的id
     * @Param: [page]
     * @return: void
     * @Author: zhouyulin
     * @Date: 2019/11/19
     */
    private void pgProcess(Page page) {
        //获取一个html对象，来抽取结果
        Html html = page.getHtml();

        //获取当前page
        Integer pageNum = this.judgePage(html);
        if (1 == pageNum) {
            //如果是第一页求出最大页//获取明前最大的page，就是尾页的href里的值
            List<Selectable> nodes = html.xpath("//div[@class='pages']/a")
                    .regex("(?<=vod-index-pg-).+(?=\\.html)").nodes();
            //获取最大的page  <a target="_self" href="/?m=vod-index-pg-792.html" class="pagelink_a">尾页</a>
            maxPage = Integer.valueOf(nodes.get(nodes.size() - 1).toString());
            log.info("最大page求得为：{}", maxPage);
            if (maxPage == -1) {
                //先直接返回，不做处理；
                return;
            }
        } else if (-1 == pageNum) {
            //先直接返回，不做处理；
            return;
        }
        //过滤出所有的视频链接
        Selectable xpath = html.xpath("div[@class='xing_vb']/ul/li")
                .regex("(?<=<li><span class=\"tt\">).+(?=</li>)");  //第二层正则过滤走其他的标签
        //获得所有的视屏id+date更新时间，做去重操作
        Map<String, String> tempMap = this.ExtractingID(xpath);

        //如果当前页小于最大页，继续爬，如果等于或者大于那就停止
        if (pageNum < maxPage) {
            page.addTargetRequest("http://zuidazy2.net/?m=vod-index-pg-" + (pageNum + 1) + ".html");
        }

        /*
         * 判断是否需要去爬取最新的数据，并且去重
         * key是id
         * value是影视更新时间
         */
        tempMap.forEach((id, value) -> {
            //如果两个值任意一个为空，跳出本次循环
            if (StringUtils.isEmpty(id) && StringUtils.isEmpty(value)) {
                return;
            }
            //取除redis数据，做更新操作
            String date = (String) CacheUtils.hget(ZUIDAZY2, id);
            //如果value和name不一样说明有更新的内容，我们爬取详情页面
            if (!value.equals(date)) {
                page.addTargetRequest("http://zuidazy2.net/?m=vod-detail-id-" + id + ".html");
                //并且把缓存数据添加到redis
                CacheUtils.hset(ZUIDAZY2, id, value.trim());
                //把更新的资源日志打印出来
                if (StringUtils.isNoneEmpty(date)) {
                    log.info("存入reids，id为{}, 以前的值{}, 现在的值{}", id, date, value);
                }
            }
        });
    }

    /**
     * @Description: 爬取详情页面
     * @Param: [page]
     * @return: void
     * @Author: zhouyulin
     * @Date: 2019/11/19
     */
    private void detailProcess(Page page) {
        MovieUrl movieUrl = null;
        try {
            movies = new Movies();
            String movieId = page.getUrl().regex("(?<==vod-detail-id-).+?(?=\\.html)").toString();
            //详情对象
            MovieDetails movieDetails = new MovieDetails();
            movieDetails.setMovieId(movieId);
            //获取爬取的html对象来抽取结果
            Html html = page.getHtml();

            /*详情的xpath*/
            Selectable xpathDetail = html.xpath("div[@class='vodBox']");
            //主题详情内容爬取
            /* 封面url */
            String url = xpathDetail.xpath("div[@class='vodImg']")
                    .regex("(?<=src=\").+?(?=\")").toString();
            movieDetails.setImageUrl(url);
            /*影视标题*/
            String title = xpathDetail.xpath("div[@class='vodh']")
                    .regex("(?<=<h2>).+?(?=</h2>)").toString();
            movieDetails.setTitle(title);
            /*影视更新集数*/
            String smallNode = xpathDetail.xpath("div[@class='vodh']/span")
                    .regex("(?<=<span>).+?(?=</span>)").toString();
            movieDetails.setSmallNote(smallNode);
            /*影视评分*/
            String score = xpathDetail.xpath("div[@class='vodh']").regex("(?<=<label>).+?(?=</label>)").toString();
            if (score != null) {
                movieDetails.setScore(Double.valueOf(score));
            } else {
                movieDetails.setScore(0.0);
            }
            /*影视具体信息*/
            List<Selectable> nodes = xpathDetail.xpath("div[@class='vodinfobox']")
                    .regex("(?<=<li>).+?(?=</li>)|(?<=<li class=\"sm\">).+?(?=</li>)").nodes();
            if (nodes != null && nodes.size() > 0) {
                for (int i = 0; i < nodes.size(); i++) {
                    //whitch处理数据
                    this.switchProcessData(movieDetails, nodes.get(i));
                }
            }
            //影视介绍爬取
            String introduction = xpathDetail.regex("(?<=<span class=\"more\").+?(?=</span>)")
                    .regex("(?<=\">).+").toString();
            movieDetails.setIntroduction(introduction);

            movies.setMovieDetails(movieDetails);
            /* 爬取影视链接 */
            Selectable xpathUrl = html.xpath("div[@class='vodplayinfo']");
            //播放类型：zuidam3u8
            //获取url
            List<Selectable> urls = xpathUrl.xpath("div[@id='play_1']/ul").regex("(?<=<li>).+?(?=</li>)").nodes();
            if (urls != null && urls.size() > 0) {
                for (int i = 0; i < urls.size(); i++) {
                    movieUrl = new MovieUrl();
                    Selectable selectable = urls.get(i);
                    String url1 = selectable.regex("(?<=value=\").+?(?=\")").toString();
                    //添加对象
                    movieUrl.setFlag("m3u8");
                    movieUrl.setUrl(url1);
                    movieUrl.setSetNum(i+1);
                    movieUrl.setCreateDate(new Date());
                    movieUrl.setMovieId(movieId);
                    //存入list
                    movies.getM3u8Url().add(movieUrl);
                }
            }
            //播放类型直连的



            System.out.println("movieDetails = " + movieDetails);

        } catch (Exception e) {
            log.info("爬取详情页面出现异常，url为【{}】", page.getUrl());
            e.printStackTrace();
        }


    }

    @Override
    public Site getSite() {
        return site;
    }

    /**
     * @Description: whitch处理数据
     * @Param: [movieDetails, selectable]
     * @return: void
     * @Author: zhouyulin
     * @Date: 2019/12/28
     */
    private void switchProcessData(MovieDetails movieDetails, Selectable selectable) {
        String key = selectable.regex(".+?(?=：)").toString().trim();
        String value = selectable.regex("(?<=<span>).+?(?=<)").toString();
        //有些数据不需要处理了，我们过滤即可
        String eq = "别名_今日播放量_总播放量_总评分数_评分次数";
        if (eq.contains(key)) {
            return;
        }
        //不同的数据不同的存储
        switch (key) {
            case "导演":
                movieDetails.setDirector(value);
                break;
            case "主演":
                movieDetails.setActor(value);
                break;
            case "类型":
                movieDetails.setType(value);
                break;
            case "地区":
                movieDetails.setArea(value);
                break;
            case "语言":
                movieDetails.setLanguage(value);
                break;
            case "上映":
                Date date = DateUtils.processDifferentDateByStr(value);
                movieDetails.setReleaseTime(date);
                break;
            case "片长":
                if (value != null) {
                    movieDetails.setMovieLength(Integer.valueOf(value));
                } else {
                    movieDetails.setMovieLength(0);
                }
                break;
            case "更新":
                movieDetails.setMovieUpdateTime(DateUtils.processDifferentDateByStr(value));
                break;
            default:
                log.info("没有张对应的处理结果");
        }
    }

    /**
     * @Description: 抽取全部的url ID+name在做后面的处理
     * @Param: [selectable]
     * @return: java.util.List<java.lang.Integer>
     * @Author: zhouyulin
     * @Date: 2019/11/18
     */
    private Map<String, String> ExtractingID(Selectable selectables) {
        List<Selectable> nodes = selectables.nodes();
        Map<String, String> tempMap = new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            //取出匹配的链接
            Selectable selectable = nodes.get(i);
            //匹配出所有的数字id
            String id = selectable.regex("(?<==vod-detail-id-).+?(?=\\.html\")").toString();
            //取出所有的标题，存入redis，来判断是否去重
            //String title = selectable.regex("(?<=\"_blank\">).+(?=</a>)").toString();
            //取出所有的更新时间
            String date = selectable.regex("(?<=class=\"xing_vb7\">).+(?=</span>)|(?<=class=\"xing_vb6\">).+(?=</span>)")
                    .toString()
                    .trim();
            tempMap.put(id, date);
        }

        return tempMap;
    }

    /**
     * @Description: 正则过滤判断当前是第几页
     * @Param: [selectable]
     * @return: java.lang.String
     * @Author: zhouyulin
     * @Date: 2019/11/19
     */
    public Integer judgePage(Selectable selectable) {
        Selectable regex = null;
        try {
//            regex = selectable.regex("(?<=<span class=\"pagenow\">).+(?=</span>");
            regex = selectable.xpath("span[@class='pagenow']")
                    .regex("(?<=\"pagenow\">).+(?=</span>)");
            System.out.println("regex = " + regex);
            log.info("页数过滤完成，页数为：{}", regex.toString());
            return Integer.valueOf(regex.toString());
        } catch (Exception e) {
            log.info("过滤失败，出现异常建议直接-1");
            e.printStackTrace();
            return -1;
        }
    }

}
