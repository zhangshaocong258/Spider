package core.spider;

import core.util.Config;

/**
 * Created by zsc on 2016/11/23.
 */
public class Main {
    public static void main(String[] args) {
        HttpClientTool.getInstance();//首先初始化httpclient和context，否则出错，后面初始化可能没有得到正确的context
        Spider.getInstance().start(Config.startURL);
    }
}
