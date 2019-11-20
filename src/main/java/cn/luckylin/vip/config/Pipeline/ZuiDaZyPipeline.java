package cn.luckylin.vip.config.Pipeline;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.Map;

/**
* @Description: zuidazy1的爬虫保存接口
* @Author: zhouyulin
* @Date: 2019/11/20
*/
public class ZuiDaZyPipeline implements Pipeline {

    /**
    * @Description: 保存把爬出来的结果保存到数据库里
    * @Param: [resultItems, task]
    * @return: void
    * @Author: zhouyulin
    * @Date: 2019/11/20
    */
    @Override
    public void process(ResultItems resultItems, Task task) {
        Map<String, Object> all = resultItems.getAll();
        if (all==null || all.size()==0) {
            return;
        }
        //遍历
        all.forEach((key,value)->{
            System.out.println(key+"-->"+value);
        });
    }
}
