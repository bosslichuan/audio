package com.mv.controller;

import com.mv.beans.Movie;
import com.mv.common.CommonPool;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

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
    private String index(Integer page, Model model) {
        List<Movie> movieList = CommonPool.getMovieList();
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
        List<Movie> movies = movieList.subList((page - 1) * pageSize, (page - 1) * pageSize + pageSize);
        model.addAttribute("movies", movies);
        model.addAttribute("totalPage", toltalPage);
        model.addAttribute("page", page);
        return "index.html";
    }
}
