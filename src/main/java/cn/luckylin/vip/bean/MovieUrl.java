package cn.luckylin.vip.bean;

import lombok.Data;

import java.util.Date;

/**
* @Description: 影视链接
* @Author: zhouyulin
* @Date: 2019/12/27
*/
@Data
public class MovieUrl {

    //自增长id
    private Integer id;
    //影视id
    private Integer movieId;
    //影视集数
    private String setNum;
    //影视集数对应的url
    private String url;
    //标记，这个什么类型的下载链接
    private String flag;
    //创建时间
    private Date createDate;
    //创建人
    private String createBy;
}
