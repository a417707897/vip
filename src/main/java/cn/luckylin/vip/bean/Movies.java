package cn.luckylin.vip.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
* @Description: 影视集合
* @Author: zhouyulin
* @Date: 2019/12/28
*/
@Data
public class Movies {

    public Movies(){
        this.m3u8Url = new ArrayList<>();
        this.llUrl = new ArrayList<>();
        this.xunLeiUrl = new ArrayList<>();
    }

    //影视详情
    private MovieDetails movieDetails;

    //m3u8
    private List<MovieUrl> m3u8Url;
    //ll
    private List<MovieUrl> llUrl;
    //xunlei
    private List<MovieUrl> xunLeiUrl;

}
