package core.spider;

import core.util.Config;

/**
 * Created by zsc on 2016/11/23.
 * 只爬取特定（单个）网页时，start内输入网页名，去除主域名www.zhihu.com
 * config中threadNum=1，redisEnable=false
 * 下载页面注意people的关注数默认大于100才下载
 */
public class Main {
    public static void main(String[] args) {
        HttpClientTool.getInstance();//首先初始化httpclient和context，否则出错，后面初始化可能没有得到正确的context
        if (Config.topicCrawler) {
            System.out.println("主题爬虫");
            String[] content = {"腾讯", "阿里", "阿里巴巴", "京东", "网易", "滴滴", "亚马逊", "美团", "百度", "携程", "搜狗",
                    "美团点评", "微博", "今日头条", "华为", "蘑菇街", "中兴", "淘宝", "天猫", "支付宝", "QQ", "微信", "迅雷",
                    "谷歌", "Google", "微软", "电子科技大学"};
            topicCrawler(content);
        }
        Spider.getInstance().start(Config.startURL);
    }

    private static void topicCrawler(String[] content) {
        Spider.getInstance().start(content);
    }
}
