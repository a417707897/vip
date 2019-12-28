package cn.luckylin.vip.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * @Description: 影视详情
 * @Author: zhouyulin
 * @Date: 2019/12/27
 */
@Data
@Table(name = "MOVIE_DETAILS")
public class MovieDetails {

    //自增长id
    @Column(name = "ID")
    private Integer id;

    //影视id，也是影视唯一标识
    @Column(name = "MOVIE_ID")
    private String movieId;

    //影视标题
    @Column(name = "TITLE")
    private String title;

    //影视导演
    @Column(name = "DIRECTOR")
    private String director;

    //影视演员
    @Column(name = "ACTOR")
    private String actor;

    //封面图片链接
    @Column(name = "IMAGE_URL")
    private String imageUrl;

    //影视评分
    @Column(name = "SCORE")
    private Double score;

    //小说明
    @Column(name = "SMALL_NOTE")
    private String smallNote;

    //影视类型
    @Column(name = "TYPE")
    private String type;

    //影视地区
    @Column(name = "AREA")
    private String area;

    //影视语言
    @Column(name = "LANGUAGE")
    private String language;

    //上映时间
    @Column(name = "RELEASE_TIME")
    private Date releaseTime;

    //影视片长
    @Column(name = "MOVIE_LENGTH")
    private Integer movieLength;

    //影视更新时间
    @Column(name = "MOVIE_UPDATE_TIME")
    private Date movieUpdateTime;

    //影视介绍
    @Column(name = "INTRODUCTION")
    private String introduction;

    //影视总播放量
    @Column(name = "TOTAL_PLAYBACK")
    private Integer totalPlayback;

    //今日播放量
    @Column(name = "TODAY_PLAYBACK")
    private Integer todayPlayback;

    //创建时间
    @Column(name = "CREATE_DATE")
    private Date createDate;

    //创建人
    @Column(name = "CREATE_BY")
    private String createBy;

    //更新时间
    @Column(name = "UPDATE_DATE")
    private Date updateDate;

    //更新人
    @Column(name = "UPDATE_BY")
    private String updateBy;

}
