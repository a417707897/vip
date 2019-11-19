package cn.luckylin.vip.config;

import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

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
            .setSleepTime(3000);

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
        //过滤出所有的视频链接
        Selectable xpath = html.xpath("div[@class='xing_vb']/ul/li/span/a");
        //获得所有的视屏id
        List<Integer> integers = this.ExtractingID(xpath);
        //获取明前最大的page，就是尾页的href里的值
        List<Selectable> nodes1 = html.xpath("//div[@class='pages']/a").nodes();
        //获取最大的page
        String pageMax = this.judgePage(nodes1.get(nodes1.size() - 1));
        System.out.println("pageMax = " + pageMax);
        System.out.println("-------");
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
        for (int i = 0; i < nodes.size(); i++) {
            //取出匹配的链接
            Selectable selectable = nodes.get(i);
            System.out.println("selectable = " + selectable);
            //匹配出所有的数字
            Selectable regex = selectable.regex("(?<==vod-detail-id-).+?(?=\\.html\")");
            System.out.println("regex.toString() = " + regex.toString());
            //匹配出所有标题
            Selectable te = selectable.regex("(?<=target=\"_blank\">).+(?=</a>)");

        }

        return null;
    }

    /**
     * @Description: 正则过滤判断当前是第几页
     * @Param: [selectable]
     * @return: java.lang.String
     * @Author: zhouyulin
     * @Date: 2019/11/19
     */
    public String judgePage(Selectable selectable) {
        Selectable regex = selectable.regex("(?<=vod-index-pg-).+(?=\\.html)");
        log.info("页数过滤完成，页数为：{}",regex.toString());
        return regex.toString();
    }

}
