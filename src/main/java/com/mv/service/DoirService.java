package com.mv.service;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
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

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
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

    @Resource
    private WebClient webClient;

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
                movie.setUrl("http://m.aaxxy.com" + u.select(".play-img").attr("href"));
                return movie;
            }).collect(Collectors.toList());

            movies.stream().forEach(m -> {
                try {
                    Document document = curl(m.getUrl());
                    Elements playUl = document.select(".plau-ul-list");
                    m.setPlayUrls(playUl.stream().map(p -> {
                        String preUrl = "http://m.aaxxy.com";
                        try {
                            preUrl = p.select("a").attr("href");
                            preUrl = "http://m.aaxxy.com" + preUrl;
                            Document platomJs = platomJs(preUrl);
                            String iframeUrl = platomJs.select("iframe").get(1).attr("src");
                            Document curl = platomJs(iframeUrl.startsWith("http") ? iframeUrl : "http:" + iframeUrl);
                            System.out.println(curl.select("video"));
                            preUrl = curl.select("video").attr("src");
                            if (StringUtils.isEmpty(preUrl)) {
                                preUrl = iframeUrl;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return preUrl;

                    }).collect(Collectors.toList()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                CommonPool.getMovieList().add(m);
            });

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }

    public Document getDocumentWithJs(String url) throws IOException, InterruptedException {
        //是否使用不安全的SSL
        webClient.getOptions().setUseInsecureSSL(true);
        //启用JS解释器，默认为true
        webClient.getOptions().setJavaScriptEnabled(true);
        //禁用CSS
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setUseInsecureSSL(true);
        //js运行错误时，是否抛出异常
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        //状态码错误时，是否抛出异常
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        //是否允许使用ActiveX
        webClient.getOptions().setActiveXNative(true);
        //等待js时间
        webClient.waitForBackgroundJavaScript(60 * 1000);
        //设置Ajax异步处理控制器即启用Ajax支持
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        //设置超时时间
        webClient.getOptions().setTimeout(10000);
        //不跟踪抓取
        webClient.getOptions().setDoNotTrackEnabled(false);
        webClient.getOptions().setScreenHeight(1334);
        webClient.getOptions().setScreenWidth(750);
        webClient.getJavaScriptEngine().setJavaScriptTimeout(3000);
        webClient.setJavaScriptTimeout(5000);
        WebRequest request = new WebRequest(new URL(url));
        request.setAdditionalHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        request.setAdditionalHeader("Accept-Encoding", "gzip, deflate");
        request.setAdditionalHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        request.setAdditionalHeader("Cache-Control", "no-cache");
        request.setAdditionalHeader("Connection", "keep-alive");
        request.setAdditionalHeader("Charset", "utf-8");
        request.setCharset(Charset.defaultCharset());
        request.setAdditionalHeader("Host", "aaxxy.com");
        request.setAdditionalHeader("Pragma", "no-cache");
        request.setAdditionalHeader("Referer", "http://aaxxy.com/");
        request.setAdditionalHeader("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Mobile Safari/537.36");
        try {
            //模拟浏览器打开一个目标网址
            HtmlPage htmlPage = webClient.getPage(request);
            htmlPage.executeJavaScript("MacPlayer.Init();");
            htmlPage.executeJavaScript("MacPlayer.Show();");
            ScriptResult result = htmlPage.executeJavaScript("MacPlayer.PlayUrl");
            System.err.println(result.getJavaScriptResult().toString());

            //为了获取js执行的数据 线程开始沉睡等待
            webClient.waitForBackgroundJavaScript(10000);
            //以xml形式获取响应文本
            String xml = htmlPage.asXml();
            //并转为Document对象return
            return Jsoup.parse(xml);
            //System.out.println(xml.contains("结果.xls"));//false
        } catch (FailingHttpStatusCodeException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


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
     * phantomjs需要区分不同的环境路径
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
