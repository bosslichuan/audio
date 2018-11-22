package com.mv.common;

import com.mv.beans.Movie;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author guanghan
 * @date 2018/11/2
 */
public class CommonPool {
    public static Map<String, Movie> movieMap;

    static {
        movieMap = new HashMap<>(10);
    }


    public static Map<String, Movie> getMovieMap() {
        return movieMap;
    }

    public static void setMovieMap(Map<String, Movie> movieMap) {
        CommonPool.movieMap = movieMap;
    }

    public synchronized static void addAll(Map<String, Movie> map) {
        if (null == map) {
            movieMap = new HashMap<>(10);
        }
        CommonPool.movieMap.putAll(map);
    }

    public static void removeAll() {
        if (!CollectionUtils.isEmpty(movieMap)) {
            movieMap.clear();
        }
    }
}
