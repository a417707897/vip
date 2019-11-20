package cn.luckylin.vip.config;

import cn.luckylin.vip.config.redis.CacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            .setRetryTimes(3)
            .setSleepTime(10)
            .setTimeOut(1000000);

    private static Integer maxPage;

    //判断是否是页面的url
    private final static String PGURL = "vod-index-pg-";

    //判断是不是详情的url
    private final static String DETAILURL = "vod-detail-id-";

    //redis标题  www.zuidazy1.net
    private final static String ZUIDAZY1 = "www.zuidazy1.net";


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
        Integer pageNum = this.judgePage(page.getUrl());
        if (1 == pageNum) {
            //如果是第一页求出最大页//获取明前最大的page，就是尾页的href里的值
            List<Selectable> nodes1 = html.xpath("//div[@class='pages']/a").nodes();
            //获取最大的page
            maxPage = this.judgePage(nodes1.get(nodes1.size() - 1));
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
        Selectable xpath = html.xpath("div[@class='xing_vb']/ul/li/span/a");
        //获得所有的视屏id+视屏名称，做去重操作
        Map<String, String> tempMap = this.ExtractingID(xpath);

        //如果当前页小于最大页，继续爬，如果等于或者大于那就停止
        if (pageNum < maxPage) {
            page.addTargetRequest("http://www.zuidazy1.net/?m=vod-index-pg-" + (pageNum + 1) + ".html");
        }

        /*
         * 判断是否需要去爬取最新的数据
         * key是id
         * value是影视名称
         */
        tempMap.forEach((id, value) -> {
            //如果两个值任意一个为空，跳出本次循环
            if (StringUtils.isEmpty(id) && StringUtils.isEmpty(value)) {
                return;
            }
            //取除redis数据，做更新操作
            String name = (String) CacheUtils.hget("www.zuidazy1.net", id);
            //如果value和name不一样说明有更新的内容，我们爬取详情页面
            if (!value.equals(name)) {
                page.addTargetRequest("http://www.zuidazy1.net/?m=vod-detail-id-" + id + ".html");
                //并且把缓存数据添加到redis
                CacheUtils.hset("www.zuidazy1.net", id, value.trim());
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
        page.putField("demo", "demo" + page.getUrl());
    }

    @Override
    public Site getSite() {
        return site;
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
            System.out.println("selectable = " + selectable);
            //匹配出所有的数字id
            String id = selectable.regex("(?<==vod-detail-id-).+?(?=\\.html\")").toString();
            //取出所有的标题，存入redis，来判断是否去重
            String title = selectable.regex("(?<=\"_blank\">).+(?=</a>)").toString();
            tempMap.put(id, title);
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
            regex = selectable.regex("(?<=vod-index-pg-).+(?=\\.html)");
            log.info("页数过滤完成，页数为：{}", regex.toString());
            return Integer.valueOf(regex.toString());
        } catch (Exception e) {
            log.info("过滤失败，出现异常建议直接-1");
            e.printStackTrace();
            return -1;
        }
    }

}
