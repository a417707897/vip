package cn.luckylin.vip.utils;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
* @Description: 时间处理工具类
* @Author: zhouyulin
* @Date: 2019/12/28
*/

public class DateUtils {

    private static SimpleDateFormat ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");

    /**
    * @Description: 判断字符串长度，返回不同的时间格式
    * @Param: [strDate]
    * @return: java.util.Date
    * @Author: zhouyulin
    * @Date: 2019/12/28
    */
    public static Date processDifferentDateByStr(String strDate){
        try {
            if (strDate == null) {
                return new Date();
            }
            if (strDate.length() == 10) {
                return ymd.parse(strDate);
            } else if (strDate.length()==19){
                return ymdhms.parse(strDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Date();
    }
}
