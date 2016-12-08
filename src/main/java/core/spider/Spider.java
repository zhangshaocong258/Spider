package core.spider;

import core.util.LinkQueue;
import core.util.Config;
import core.util.RedisSet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by zsc on 2016/8/13.
 */
public class Spider {

    private static class SpiderHolder {
        private static Spider spider = new Spider();
    }

    public static Spider getInstance() {
        return SpiderHolder.spider;
    }

    public Spider() {}

    private Scheduler scheduler = new Scheduler();
    private LoginTool loginTool = new LoginTool();



    /**
     * 启动线程gather，然后开始收集网页资料
     * 刚开始
     */
    public void start(String url) {
        //初始化URL表
        if (Config.redisEnable) {
            if (RedisSet.unVisitedUrlsEmpty() && RedisSet.visitedUrlsEmpty()) {
                RedisSet.initializeUrls(url);
            }
        } else {
            LinkQueue.initializeUrls(url);
        }

        //创建文件夹
        File folder = new File(Config.downloadPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        if (!loginTool.deserializeCookieStore(Config.cookiePath)) {
            System.out.println("进行登录...");
            loginTool.login(Config.account, Config.password);
        }


        long startTime = System.currentTimeMillis();
        System.out.println("采集开始");
        ExecutorService threadPool = Executors.newFixedThreadPool(4);
        ArrayList<Future<Boolean>> results = new ArrayList<Future<Boolean>>();
        for (int i = 0; i < Config.thread_num; i++) {
            PageProcessor pageProcessor = new PageProcessor(scheduler);
            results.add(threadPool.submit(pageProcessor));
        }

        //等待结果完成，相当于join
        for (int i = 0; i < results.size(); i++) {
            try {
                results.get(i).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } finally {
                threadPool.shutdown();
            }
        }

        //超过2s，报错
        if (Config.redisEnable) {
            RedisSet.save();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("采集结束,程序运行时间： " + (endTime - startTime) + "ms");

    }


//    public static void main(String[] args) {
//        List<String> urls = new ArrayList<String>();
//
//        //urls.add(new URL("http://ast.nlsde.buaa.edu.cn/"));
//        //urls.add(new URL("http://www.baidu.com"));
//        //urls.add(new URL("http://www.google.com"));
//        //urls.add(new URL("http://www.sohu.com"));
//        urls.add(Config.startURL);
////        urls.add("http://www.sina.com");
////        urls.add(new URL("http://edu.sina.com.cn/"));
////        urls.add(new URL("http://edu.163.com/"));
////        urls.add(new URL("http://ast.nlsde.buaa.edu.cn/"));
//        //urls.add(new URL("http://www.chsi.com.cn/"));
//        //urls.add(new URL("http://www.eol.cn/"));
//        //urls.add(new URL("http://www.edutv.net.cn/"));
//
//
//        Spider spider = new Spider(urls);
//        spider.start();
//
//    }
}
