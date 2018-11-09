package com.mv.util;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.net.URL;

/**
 * @author guanghan
 * @date 2018/11/6
 */
public class Ph {
    public static void main(String[] args) throws IOException {
        WebClient webClient=new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setCssEnabled(false);//关闭css
        webClient.getOptions().setJavaScriptEnabled(true);//关闭js
        webClient.getOptions().setDoNotTrackEnabled(true);
        webClient.getOptions().setPrintContentOnFailingStatusCode(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);//当JS执行出错的时候是否抛出异常, 这里选择不需要
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);//当HTTP的状态非200时是否抛出异常, 这里选择不需要
        webClient.getOptions().setActiveXNative(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());//很重要，设置支持AJAX
        webClient.getOptions().setScreenHeight(1334);
        webClient.getOptions().setScreenWidth(750);
        WebRequest request = new WebRequest(new URL("http://m.aaxxy.com/vod-play-id-8676-src-1-num-1.html"));
        request.setAdditionalHeader("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Mobile Safari/537.36");
//        webClient.getJavaScriptEngine().setJavaScriptTimeout(1000);
        final HtmlPage page=webClient.getPage(request);
        webClient.waitForBackgroundJavaScript(30000);
        System.err.println(page.asXml());
        webClient.close();
    }
}
