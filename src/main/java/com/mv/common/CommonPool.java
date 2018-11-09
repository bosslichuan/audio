package com.mv.common;

import com.mv.beans.Movie;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author guanghan
 * @date 2018/11/2
 */
public class CommonPool {
    public static List<Movie> movieList;

    static {
        movieList = new ArrayList<>(10);
    }

    public static List<Movie> getMovieList() {
        return movieList;
    }

    public static void setMovieList(List<Movie> movieList) {
        CommonPool.movieList = movieList;
    }

    public synchronized static void addAll(List<Movie> movieList) {
        if (null == movieList) {
            movieList = new ArrayList<>(10);
        }
        CommonPool.movieList.addAll(movieList);
    }

    public static void removeAll() {
        if (!CollectionUtils.isEmpty(movieList)) {
            movieList.clear();
        }
    }
}
