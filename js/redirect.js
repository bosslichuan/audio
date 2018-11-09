var page = require('webpage').create();

var system = require('system');

var url = system.args[1];

page.viewportSize = {
    width: 1280,
    height: 1014
};

var renderPage = function (url) {
    page = require('webpage').create();

    page.onNavigationRequested = function(url, type, willNavigate, main) {
        if (main && url!=myurl) {
            myurl = url;
            page.close();
            setTimeout('renderPage(myurl)',1000); //Note the setTimeout here
        }
    };

    page.open(url, function(status) {
        if (status==="success") {
            // 页面渲染需要时间，延迟2秒取渲染的页面内容
            setTimeout(function(){
                console.log(page.content);
                //page.render('yourscreenshot.png');
                phantom.exit(0);
            } , 2000)

        } else {
            console.log("failed")
            phantom.exit(1);
        }
    });

}


renderPage(url);