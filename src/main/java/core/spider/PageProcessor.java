package core.spider;

import core.util.Config;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;


/**
 * Created by zsc on 2016/8/13.
 */
public class PageProcessor implements Callable {
    private Scheduler scheduler;

    public PageProcessor(Scheduler scheduler) {
        this.scheduler = scheduler;

        //创建文件夹，已创建过，用于创建多个文件夹@Deprecated
//        File folder = new File(Config.downloadPath);
//        if (!folder.exists()) {
//            folder.mkdirs();
//        }

    }


    public Boolean call() {

        //获取url和下载数据，注意同步
        int count = 0;
        //每个线程提取100个网页
        while (count++ < 2333) {
            String url = scheduler.getURL();
            try {
                if (url != null) {
                    String html = Downloader.downloadPage(url);
                    if (html == null) {
                        continue;//相当于没有得到页面数据
                    }
                    //得到当前URL的html和host，用来生成html中的绝对路径
                    Set<String> newUrl = HtmlParserTool.extractLinks(html, Config.domainName);
                    scheduler.insertNewURL(newUrl);
                } else {
                    System.out.println("队列为空，结束");
                    break;
                }
//            } catch (NullPointerException e) {
//                System.out.println("NullPointerException: " + count);
//                e.printStackTrace();
//            } catch (NoSuchElementException e) {
//                System.out.println("NoSuchElementException: " + count);
//                e.printStackTrace();
//            } catch (ConcurrentModificationException e) {
//                System.out.println("ConcurrentModificationException: " + count);
//                e.printStackTrace();
//            } catch (IndexOutOfBoundsException e) {
//                System.out.println("IndexOutOfBoundsException: " + count);
//                e.printStackTrace();
            } catch (IOException e) {
                //超时则添加到待爬取队列尾部，等待下一次爬取，若再次失败，继续...
                scheduler.recallURL(url);
                System.out.println("超时");
                e.printStackTrace();
            } catch (Exception e) {
                //捕获除了超时外的其他错误
                System.out.println("其他错误");
                e.printStackTrace();
            }

        }
        System.out.println(Thread.currentThread().getName() + "结束： " + count);
        return true;
    }

    //注意，最好还能够探测链接建立的时间，从而把持续时间太长的链接直接kill掉(源代码应该没有)，防止在一个url上停留过久的问题
//    public void run() {
//        int counter = 0;
//        while (counter++ <= 2)        //每个线程提取100个网页
//        {
//            URL url = disp.getURL();
//            System.out.println("in running: " + ID + " get url: " + url.toString());
//            String htmlDoc = client.getDocumentAt(url);
//
//            //doanalyzer完成url解析并且返回，保存指定格式的doc
//            //htmlDoc可能为空，即没有获得页面的代码信息，这样就需要删除
//            if (htmlDoc.length() != 0) {
//                List<String> newURL = analyzer.doAnalyzer(bfWriter, url, htmlDoc);
//                if (newURL.size() != 0)
//                    disp.insertNewURL(newURL);
//            }
//
//        }
//
//    }
}
