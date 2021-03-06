package core.spider;

import core.util.Config;

import java.io.*;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by zsc on 2016/8/13.
 */
public class PageProcessor implements Callable {
    private Scheduler scheduler;
    private AtomicInteger atomicInteger;

    public PageProcessor(Scheduler scheduler, AtomicInteger atomicInteger) {
        this.scheduler = scheduler;
        this.atomicInteger = atomicInteger;

        //创建文件夹，已创建过，用于创建多个文件夹@Deprecated
//        File folder = new File(Config.downloadPath);
//        if (!folder.exists()) {
//            folder.mkdirs();
//        }

    }


    public Boolean call() {

        if (!Config.redisEnable) {
            //获取url和下载数据，注意同步
            int count = 0;
            //每个线程提取100个网页
            while (count++ < 1) {
                String str = scheduler.getURL();
                try {
                    if (str != null) {
                        String html = Downloader.downloadPage(str);
                        if (html == null) {
                            continue;//相当于没有得到页面数据
                        }
                        if (!Config.topicCrawler) {
                            //得到当前URL的html和host，用来生成html中的绝对路径
                            Set<String> newStr = HtmlParserTool.extractLinks(html, Config.domainName);
                            scheduler.insertNewURL(newStr);
                        }
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
                    scheduler.recallURL(str);
                    System.out.println("超时");
                    e.printStackTrace();
                } catch (Exception e) {
                    //捕获除了超时外的其他错误，
                    scheduler.recallURL(str);
                    System.out.println("其他错误");
                    e.printStackTrace();
                }

            }
            System.out.println(Thread.currentThread().getName() + "结束： " + count);
            return true;
        } else {
            //获取url和下载数据，注意同步
            int count = 0;
            //每个线程提取100个网页
            while (count++ < 20000) {
                String str = scheduler.redisGetURL();
                try {
                    if (str != null) {
                        String html = Downloader.downloadPage(str);
                        if (html == null) {
                            scheduler.redisRecallURL(str);
                            continue;//相当于没有得到页面数据
                        } else if (html == "410") {
                            scheduler.redisAdd410URL(str);
                            continue;
                        }
                        if (!Config.topicCrawler) {
                            //得到当前URL的html和host，用来生成html中的绝对路径
                            Set<String> newStr = HtmlParserTool.extractLinks(html, Config.domainName);
                            scheduler.redisInsertNewURL(newStr);
                        }
                        scheduler.redisAddVisitedURL(str);//下载成功后再入队列

                        if (atomicInteger.incrementAndGet() > 100) {
                            System.out.println("睡眠");
                            Thread.sleep(60000);
                            atomicInteger.set(0);
                        }
                    } else {
                        System.out.println("队列为空，结束");
                        break;
                    }

                }
//                catch (SocketException e) {
//                    //超时则添加到待爬取队列尾部，等待下一次爬取，若再次失败，继续...
//                    scheduler.redisRecallURL(str);
//                    System.out.println("SocketException " + str);
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    //超时则添加到待爬取队列尾部，等待下一次爬取，若再次失败，继续...
//                    scheduler.redisRecallURL(str);
//                    System.out.println("超时 " + str);
//                    e.printStackTrace();
//                }
                catch (Exception e) {
                    //捕获除了超时外的其他错误
                    scheduler.redisRecallURL(str);
                    System.out.println("其他错误");
                    e.printStackTrace();
                }

            }
//            System.out.println(Thread.currentThread().getName() + "结束： " + count);
            return true;
        }
    }
}
