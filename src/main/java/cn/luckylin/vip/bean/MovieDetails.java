package cn.luckylin.vip.bean;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 影视详情
 * @Author: zhouyulin
 * @Date: 2019/12/27
 */
@Data
public class MovieDetails {

    //自增长id
    private Integer id;
    //影视id，也是影视唯一标识
    private Integer movieId;
    //影视标题
    private String title;
    //影视导演
    private String director;
    //影视演员
    private String actor;
    //封面图片链接
    private String imageUrl;
    //影视评分
    private String score;
    //影视类型
    private String type;
    //影视地区
    private String area;
    //影视语言
    private String language;
    //上映时间
    private String releaseTime;
    //影视片长
    private String movieLength;
    //影视更新时间
    private String movieUpdateTime;
    //影视介绍
    private String introduction;
    //影视总播放量
    private String totalPlayback;
    //今日播放量
    private String todayPlayback;
    //创建时间
    private Date createDate;
    //创建人
    private String createBy;
    //更新时间
    private Date updateDate;
    //更新人
    private String updateBy;

}
