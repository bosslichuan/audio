package com.mv.beans;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * @author guanghan
 * @date 2018/11/1
 */
public class Movie {
    private String name;
    private String heros;
    private String utime;
    private String brief;
    private String cover;
    private String url;
    private List<String> playUrls;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeros() {
        return heros;
    }

    public void setHeros(String heros) {
        this.heros = heros;
    }

    public String getUtime() {
        return utime;
    }

    public void setUtime(String utime) {
        this.utime = utime;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getPlayUrls() {
        return playUrls;
    }

    public void setPlayUrls(List<String> playUrls) {
        this.playUrls = playUrls;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
