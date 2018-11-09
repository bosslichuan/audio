// a phantomjs example
var page = require('webpage').create();
phantom.outputEncoding="gbk";
page.settings.loadImages = false;  //为了提升加载速度，不加载图片
page.settings.resourceTimeout = 10000;//超过10秒放弃加载
page.settings.userAgent = 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Mobile Safari/537.36';
page.onConsoleMessage = function(msg){
    console.log("page: " + msg);
};
page.open("http://m.aaqqy.com/vod-play-id-15065-src-1-num-2.html", function(status) {
    if ( status === "success" ) {
        console.log(page.title);
    } else {
        console.log("Page failed to load.");
    }
    page.close();
    phantom.exit(0);
});