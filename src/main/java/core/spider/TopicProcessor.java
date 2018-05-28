package core.spider;

import core.util.Config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by zsc on 2017/6/13.
 * 用来获取关键词的url
 */
public class TopicProcessor implements Callable {
    private Scheduler scheduler;
    private String str;
    private HashMap<Integer, String> hashMap;
    private int[] flags;

    public TopicProcessor(Scheduler scheduler, String url) {
        this.scheduler = scheduler;
        this.str = url;
        hashMap = new HashMap<Integer, String>();
        hashMap.put(0, "content");
        hashMap.put(1, "people");
        hashMap.put(2, "topic");
        flags = new int[3];
        for (int i = 0; i < flags.length; i++) {
            flags[i] = 1;
        }
    }

    private void execute(int i, String url) {
        try {
            String html = Downloader.downloadTopicPage(url);
            if (html.equals("{\"paging\":{\"next\":\"\"},\"htmls\":[]}")) {
                flags[i] = 0;
                return;
            }
            Set<String> newStr = HtmlParserTool.extract10Links(html, Config.domainName);
            scheduler.redisInsertTopicURL(newStr);
        } catch (IOException e) {
            System.out.println("超时" + url);
            scheduler.redisInsertErrorUnvisitedURL(url);
            e.printStackTrace();
        } catch (Exception e) {
            //捕获除了超时外的其他错误
            System.out.println("其他错误" + url);
            scheduler.redisInsertErrorUnvisitedURL(url);
            e.printStackTrace();
        }
    }

    public Boolean call() {
        String url = null;
        try {
            if (Config.redisEnable) {
                for (int i = 0; i < Integer.MAX_VALUE; i += 10) {
                    if (flags[0] == 0 && flags[1] == 0 &&flags[2] == 0 ) {
                        break;
                    }
                    for (int j = 0; j < 3; j++) {
                        if (flags[j] == 1) {
                            url = "/r/search?q=" + str + "&correction=0&type=" + hashMap.get(j) + "&offset=" + i;
                            execute(j, url);
                        }
                    }
                }

                int count = 0;//超时次数，超过则结束
                while (true) {
                    if (count > 5) {
                        break;
                    }
                    String str = scheduler.redisTopicGetURL();
                    if (str != null) {
                        System.out.println("ErrorUrls");
                        try {
                            String html = Downloader.downloadTopicPage(url);
                            if (html.equals("{\"paging\":{\"next\":\"\"},\"htmls\":[]}")) {
                                break;
                            }
                            Set<String> newStr = HtmlParserTool.extract10Links(html, Config.domainName);
                            scheduler.redisInsertTopicURL(newStr);
                        } catch (IOException e) {
                            System.out.println("再次超时" + url);
                            scheduler.redisInsertErrorUnvisitedURL(url);
                            count++;
                            e.printStackTrace();
                        } catch (Exception e) {
                            //捕获除了超时外的其他错误
                            System.out.println("再次错误" + url);
                            scheduler.redisInsertErrorUnvisitedURL(url);
                            count++;
                            e.printStackTrace();
                        }
                    } else {
                        break;
                    }
                }
            } else {
                throw new NullPointerException("redis未开启");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return true;
    }
}
