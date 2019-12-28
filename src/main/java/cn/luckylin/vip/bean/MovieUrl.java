package cn.luckylin.vip.bean;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
* @Description: 影视链接
* @Author: zhouyulin
* @Date: 2019/12/27
*/
@Data
@Table(name = "MOVIE_URL")
public class MovieUrl {

    //自增长id
    @Column(name = "ID")
    private Integer id;

    //影视id
    @Column(name = "MOVIE_ID")
    private String movieId;

    //影视集数
    @Column(name = "SET_NUM")
    private Integer setNum;

    //影视集数对应的url
    @Column(name = "URL")
    private String url;

    //标记，这个什么类型的下载链接
    @Column(name = "FLAG")
    private String flag;

    //创建时间
    @Column(name = "CREATE_DATE")
    private Date createDate;

    //创建人
    @Column(name = "CREATE_BY")
    private String createBy;
}
