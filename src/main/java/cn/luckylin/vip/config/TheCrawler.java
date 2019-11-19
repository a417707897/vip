package cn.luckylin.vip.config;

import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 爬虫的具体配置
 * @Author: zhouyulin
 * @Date: 2019/11/18
 */
@Slf4j
public class TheCrawler implements PageProcessor {
    //设置全局爬虫的配置，重试次数，间隔时间等等
    private Site site = Site.me()
            .setRetryTimes(3)
            .setSleepTime(100);

    private static Integer maxPage;

    /**
     * @Description: 爬虫的具体逻辑的编写【数据的过滤加抽取】
     * @Param: [page]
     * @return: void
     * @Author: zhouyulin
     * @Date: 2019/11/18
     */
    @Override
    public void process(Page page) {
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
        //获得所有的视屏id
        List<Integer> integers = this.ExtractingID(xpath);
        page.putField("id", integers);
        //如果当前页小于最大页，继续爬，如果等于或者大于那就停止
        if (pageNum<maxPage) {
            page.addTargetRequest("http://www.zuidazy1.net/?m=vod-index-pg-"+(pageNum+1)+".html");
        }

    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        //开启爬虫
        Spider.create(new TheCrawler())
                .addUrl("http://www.zuidazy1.net/?m=vod-index-pg-1.html")
                .thread(3)
                .addPipeline(new JsonFilePipeline("F:\\webmagic\\"))
                .run();
    }

    /**
     * @Description: 抽取全部的url ID在做后面的处理
     * @Param: [selectable]
     * @return: java.util.List<java.lang.Integer>
     * @Author: zhouyulin
     * @Date: 2019/11/18
     */
    private List<Integer> ExtractingID(Selectable selectables) {
        List<Selectable> nodes = selectables.nodes();
        List<Integer> temp = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            //取出匹配的链接
            Selectable selectable = nodes.get(i);
            System.out.println("selectable = " + selectable);
            //匹配出所有的数字
            Selectable regex = selectable.regex("(?<==vod-detail-id-).+?(?=\\.html\")");
            temp.add(Integer.valueOf(regex.toString()));
        }

        return temp;
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
            log.info("页数过滤完成，页数为：{}",regex.toString());
            return Integer.valueOf(regex.toString());
        } catch (Exception e) {
            log.info("过滤失败，出现异常建议直接-1");
            e.printStackTrace();
            return -1;
        }
    }

}
