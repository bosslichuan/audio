package com.mv.controller;

import com.mv.beans.Movie;
import com.mv.common.CommonPool;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author guanghan
 * @date 2018/11/2
 */
@Controller
@RequestMapping
public class IndexController {

    @RequestMapping("/index")
    private String index(Integer page, String name, Model model) {

        List<Movie> movies = new ArrayList<>();
        if (!StringUtils.isEmpty(name)) {
            List<Movie> finalMovies = new ArrayList<>();
            CommonPool.getMovieMap().forEach((k, v) -> {
                if (!StringUtils.isEmpty(k) && k.contains(name)) {
                    finalMovies.add(v);
                }
            });
            movies.addAll(finalMovies);
            model.addAttribute("totalPage", -1);
            model.addAttribute("page", 1);
        } else {
            List<Movie> movieList = new ArrayList<>(CommonPool.getMovieMap().values());
            Integer pageSize = 20;
            page = Optional.ofNullable(page).orElse(1);
            if (!CollectionUtils.isEmpty(movieList) && movieList.size() >= 20) {
                pageSize = 20;
            }
            Integer toltalPage = movieList.size() / 20;
            if (page > toltalPage) {
                page = toltalPage;
            }
            if (pageSize > movieList.size()) {
                pageSize = movieList.size();
            }
            if (0 == page) {
                page++;
            }
            movies = movieList.subList((page - 1) * pageSize, (page - 1) * pageSize + pageSize);
            model.addAttribute("totalPage", toltalPage);
            model.addAttribute("page", page);
        }
        model.addAttribute("movies", movies);
        model.addAttribute("name", name);
        return "index.html";
    }
}
