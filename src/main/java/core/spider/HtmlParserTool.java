package core.spider;

import core.util.Config;
import core.util.RedisSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zsc on 2016/11/4.
 */
public class HtmlParserTool {

    public static Set<String> extract10Links(String html, String baseUri){
        Set<String> newUrl = new HashSet<String>();
        Document doc = Jsoup.parse(html, baseUri);
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String absHref = link.attr("abs:href");
            if (!absHref.equals("") && accept(absHref) && filter(absHref)) {
                if (absHref.substring(22, 27).equals("topic")) {
                    absHref = new StringBuilder().append(absHref).append("/hot").toString();
                }
                System.out.println("str " + absHref);
                newUrl.add(absHref.substring(21));//去掉前面的域名
            }
        }
        return newUrl;
    }

    private static boolean filter(String url) {
        Pattern question = Pattern.compile("^(https://www.zhihu.com/question/([0-9]+$))");
        Pattern topic = Pattern.compile("^(https://www.zhihu.com/topic/([0-9]+$))");
        Pattern people = Pattern.compile("^(https://www.zhihu.com/people/(.*))/answers");
        Pattern org = Pattern.compile("^(https://www.zhihu.com/org/(.*))/answers");//同people

        Matcher questionMatcher = question.matcher(url);
        Matcher topicMatcher = topic.matcher(url);
        Matcher peopleMatcher = people.matcher(url);
        Matcher orgMatcher = org.matcher(url);

        return questionMatcher.matches() || topicMatcher.matches() || peopleMatcher.matches() || orgMatcher.matches();

    }

    public static Set<String> extractLinks(String html, String baseUri){
        Set<String> newUrl = new HashSet<String>();
        Document doc = Jsoup.parse(html, baseUri);
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String absHref = link.attr("abs:href");
            //判断是否存入Redis，原来是(Config.redisEnable ? !RedisSet.visitedUrlContains(absHref) : true))，简化后如下，insert已经判断，不要了
            if (!absHref.equals("") && accept(absHref) && !fobidden(absHref)) {
                newUrl.add(absHref.substring(21));//去掉前面的域名
            }
        }
        return newUrl;
    }

    //忽略的url
    private static boolean fobidden(String url) {
        return url.startsWith(Config.logout) || url.startsWith(Config.inbox) ||
                url.startsWith(Config.settings) || url.startsWith(Config.lives) ||
                url.startsWith(Config.copyright) || url.startsWith(Config.symbol) ||
                url.startsWith(Config.questionMark);
    }

    public static boolean accept(String url) {
//            Pattern pattern = Pattern.compile("^((https|http|ftp|rtsp|mms)?://)"
//                    + "+(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?"
//                    + "(([0-9]{1,3}\\.){3}[0-9]{1,3}"
//                    + "|"
//                    + "([0-9a-z_!~*'()-]+\\.)*"
//                    + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\."
//                    + "[a-z]{2,6})"
//                    + "(:[0-9]{1,4})?"
//                    + "((/?)|"
//                    + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$");
//            Matcher matcher = pattern.matcher(url);
//            boolean isMatch= matcher.matches();
//            if(isMatch && url.startsWith(Config.domainName)) {
//                return true;
//            }
//            else {
//                return false;
//            }
//            return (isMatch && belongToDomainName(url));
        return url.startsWith(Config.domainName);

    }

    //判断是否属于此域名，主要是判断zhuanlan.zhihu.com，以及回答中的外链，必须去除
    private static boolean belongToDomainName(String urlStr) {
        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String host = url.getHost();
        return host.endsWith(Config.domainName);
    }
}
