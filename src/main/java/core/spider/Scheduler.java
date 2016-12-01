package core.spider;

import core.util.LinkQueue;
import core.util.Config;
import java.util.*;

/**
 * Created by zsc on 2016/8/13.
 * <p>
 * 广度优先遍历
 * 多线程设计
 */
public class Scheduler {
    private static int threads = Config.thread_num;

    public synchronized String getURL() {
        while (true) {
            try {
                if (!LinkQueue.unVisitedUrlsEmpty()) {
                    String url = LinkQueue.getUnVisitedUrl().get(0);
                    //判断要爬取的url是否已经爬过，若爬过，则进行下一次循环
                    //保证VisitedUrl与最后生成的doc一致，目前超时则放入待爬取队列尾端，若还是连接失败，继续放至尾端
                    //并且保证此URL合法，因为添加URL的时候LinkQueue已经做了处理，除非初始URL有问题
                    if (!LinkQueue.getVisitedUrl().contains(url)) {
                        LinkQueue.addVisitedUrl(url);//addUnvisititedUrl保证此时url不会错误
                        LinkQueue.removeUnvisitedUrl(url);
                    } else {
                        LinkQueue.removeUnvisitedUrl(url);
                        continue;
                    }
                    return url;
                } else {
                    threads--;
                    if (threads > 0) {
                        wait();//InterruptedException
                        threads++;
                    } else {
                        notifyAll();
                        return null;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        //堆栈无数据，不能出栈
//        while (LinkQueue.unVisitedUrlsEmpty()) {
//            try {
//                System.out.println("list无数据，等待");
//                wait(); // 等待生产者写入数据insert
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        this.notify();
//        String url = LinkQueue.getUnVisitedUrl().get(0);
//        LinkQueue.addVisitedUrl(url);
//        LinkQueue.removeUnvisitedUrl(url);
//
//        return url;
    }

    //下载失败，重新添加回待爬取队列
    public synchronized void recallURL(String URL) {
        LinkQueue.addUnvisititedUrl(URL);
        LinkQueue.removeVisitedUrl(URL);
    }

    public synchronized void insertNewURL(Set<String> newURL) {
        for (String url : newURL) {
            LinkQueue.addUnvisititedUrl(url);
        }

        //添加完毕后，如果URL队列不为空，则唤起
        if (!LinkQueue.unVisitedUrlsEmpty() && threads < Config.thread_num) {
            notifyAll();
        }
    }

//    public synchronized void insertNewURL(URL url) {
//        if (!urls.contains(url) && !visitedURLs.contains(url))
//            urls.add(url);
//    }

//    public synchronized void insertNewURL(List<String> analyzedURL) {
//        for (String url : analyzedURL) {
//            if (!LinkQueue.getUnVisitedUrl().contains(url) && !LinkQueue.getUnVisitedUrl().contains(url)) {
//                LinkQueue.addUnvisititedUrl(url);
//            }
//        }
//
//        //添加完毕后，如果URL队列不为空，则唤起
//        if (!LinkQueue.unVisitedUrlsEmpty()) {
//            notifyAll();
//        }
//    }


}
