package com.mv.audio;

import com.mv.service.DoirService;
import org.apache.commons.io.IOUtils;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AudioApplicationTests {

    static String exe = "C:\\Users\\guanghan\\Downloads\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe";

    @Resource
    DoirService doirService;

    @Test
    public void contextLoads() throws IOException, InterruptedException {
        Document document = doirService.platomJs("http://m.aaqqy.com/vod-play-id-15065-src-1-num-2.html");

        System.out.println(document.select("iframe").get(1).attr("src"));


    }

    public static void main(String[] args) throws Exception{
        System.out.println(exe);

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(exe + " --ssl-protocol=any js/play.js http://m.aaqqy.com/vod-play-id-15065-src-1-num-2.html");
        InputStream inputStream = process.getInputStream();
        String string = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        System.out.println(string);

    }

}
