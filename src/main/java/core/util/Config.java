package core.util;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by zsc on 2016/10/28.
 */
public class Config {
//    public static final String CRAWL_PATH = "https://www.zhihu.com/people/zhang-jia-wei";
//    public static final String CRAWL_LIMIT_PATH = "https://www.zhihu.com";
//    public static final String CRAWL_VISITED_FRONTIER = "d:\\cache\\hevisited";
//    public static final String CRAWL_UNVISITED_FRONTIER = "d:\\cache\\heunvisited";
//    public static final String CRAWL_DOWNLOAD_PATH = "D:\\SpiderDownload\\zhihu\\";
//    public static final int CRAWL_THREAD_NUM = 4;

    /**
     * 下载网页线程数
     */
    public static int thread_num;


    /**
     * 验证码路径
     */
    public static String captcha;


    /**
     * 知乎账号
     */
    public static String account;


    /**
     * 知乎密码
     */
    public static String password;

    /**
     * cookie路径
     */
    public static String cookiePath;

    /**
     * 下载保存地址
     */
    public static String downloadPath;

    /**
     * 爬虫入口
     */
    public static String  startURL;

    /**
     * 网页限制
     */
    public static String domainName;

    /**
     * 登出
     */
    public static String logout;

    /**
     * 私信
     */
    public static String inbox;

    /**
     * 设置
     */
    public static String settings;

    /**
     * 直播
     */
    public static String lives;

    /**
     * 版权
     */
    public static String copyright;


    /**
     * #号
     */
    public static String symbol;

    /**
     * ？号
     */
    public static String questionMark;




    static {
        Properties properties = new Properties();
        try {
            properties.load(Config.class.getResourceAsStream("/config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        thread_num = Integer.valueOf(properties.getProperty("threadNum"));
        captcha = properties.getProperty("captcha");
        account = properties.getProperty("account");
        password = properties.getProperty("password");
        cookiePath = properties.getProperty("cookies");
        downloadPath = properties.getProperty("download");
        startURL = properties.getProperty("startURL");
        domainName = properties.getProperty("domainName");
        logout = properties.getProperty("logout");
        inbox = properties.getProperty("inbox");
        settings = properties.getProperty("settings");
        lives = properties.getProperty("lives");
        copyright = properties.getProperty("copyright");
        symbol = properties.getProperty("symbol");
        questionMark = properties.getProperty("questionMark");
    }

}
