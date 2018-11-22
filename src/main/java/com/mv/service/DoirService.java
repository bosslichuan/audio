package com.mv.service;

import com.mv.beans.Movie;
import com.mv.common.CommonPool;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author guanghan
 * @date 2018/11/2
 */
@Service
public class DoirService {

    static String host = "http://m.aaxxy.com";

    private static final Logger logger = LoggerFactory.getLogger(DoirService.class);

    @Scheduled(cron = "12 23 00 * * ?")
    public void patching() {
        logger.info("[计划任务]-开始扫描");
        CommonPool.removeAll();
        int startPage = 1;
        while (startPage(startPage)) {
            try {
                TimeUnit.SECONDS.sleep(3);
                startPage++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        logger.info("[计划任务]-扫描完成");
    }

    /**
     * 开始抓取当前页的数据并提取必要的元素
     *
     * @param page
     * @return
     */
    public boolean startPage(int page) {
        page = 0 == page ? 1 : page;
        logger.info("[当前处理页:{}]", page);
        try {
            Document doc = curl(String.format("http://aaxxy.com/vod-type-id-1-pg-%d.html", page));
            if (null == doc) {
                logger.error("[打开列表]-doc is null");
                return false;
            }
            Elements grid = doc.select("#contents");
            if (CollectionUtils.isEmpty(grid)) {
                logger.error("[打开列表]-contents is empty");
                return false;
            }
            Elements ul = grid.select("li");
            if (CollectionUtils.isEmpty(ul)) {
                logger.error("[打开列表]-ul is empty");
                return false;
            }
            List<Movie> movies = ul.parallelStream().map(u -> {
                Movie movie = new Movie();
                movie.setName(u.select("h3").text());
                movie.setBrief(u.select(".plot").text());
                movie.setCover(u.select("img").attr("data-original").split("url=")[1]);
                movie.setHeros(u.select(".actor").text());
                movie.setUtime(u.select(".long").text());
                movie.setUrl(host + u.select(".play-img").attr("href"));
                return movie;
            }).collect(Collectors.toList());

            movies.stream().forEach(m -> {
                try {
                    Document document = curl(m.getUrl());
                    Elements playUl = document.select(".plau-ul-list");
                    m.setPlayUrls(playUl.stream().map(p -> {
                        String preUrl = host;
                        try {
                            preUrl = p.select("a").attr("href");
                            preUrl = host + preUrl;
                            logger.info("[开始分析预览页],url={}", preUrl);
                            Document platomJs = platomJs(preUrl);
                            Elements iframes = platomJs.select("iframe");
                            if (CollectionUtils.isEmpty(iframes)) {
                                logger.error("[打开播放页]-没有找到iframe");
                                return preUrl;
                            }

                            if (1 == iframes.size()) {
                                logger.error("[打开播放页]-只找到一个iframe");
                                return preUrl;
                            }

                            String iframeUrl = iframes.get(1).attr("src");
                            Document playDoc = platomJs(iframeUrl.startsWith("http") ? iframeUrl : "http:" + iframeUrl);
                            if (null == playDoc) {
                                logger.error("[打开播放页]-只找到一个iframe");
                                return preUrl;
                            }
                            preUrl = playDoc.select("video").attr("src");
                            if (StringUtils.isEmpty(preUrl)) {
                                preUrl = iframeUrl;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return preUrl;

                    }).collect(Collectors.toList()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                CommonPool.getMovieMap().put(m.getName(), m);
            });

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 静态页面读取
     *
     * @param url
     * @return
     * @throws IOException
     */
    public Document curl(String url) throws IOException {
        return Jsoup.connect(url)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .header("Cache-Control", "no-cache")
                .header("Connection", "keep-alive")
                .header("Host", "aaxxy.com")
                .header("Pragma", "no-cache")
                .header("Referer", "http://aaxxy.com/")
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Mobile Safari/537.36")
                .timeout(5000)
                .get();
    }

    /**
     * 采用phantomjs读取JS动态渲染的页面
     * phantomjs需要区分不同的环境路径, 服务器路径为/opt/js/play.js, 本机环境为/js/play.js
     *
     * @param url
     * @return
     */
    public Document platomJs(String url) {
        Document doc = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            String cmd = "phantomjs --ssl-protocol=any /opt/js/play.js " + url;
            Process process = runtime.exec(cmd);
            logger.info("[执行命令]:{}", cmd);
            InputStream inputStream = process.getInputStream();
            String string = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            doc = Jsoup.parse(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }
}
