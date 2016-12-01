package core.spider;

import core.util.Config;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zsc on 2016/11/4.
 */
public class HtmlParserTool {
    public static Set<String> extractLinks(String html, String baseUri){
        Set<String> newUrl = new HashSet<String>();
        Document doc = Jsoup.parse(html, baseUri);
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String absHref = link.attr("abs:href");
            if (!absHref.equals("") && accept(absHref) && !fobidden(absHref)) {
                newUrl.add(absHref);
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

//    public static Set<String> extractLinks2(String url, LinkFilter filter){
//        Set<String> links = new HashSet<String>();
//        try {
//            Parser parser = new Parser(url);
////            parser.setEncoding("UTF-8");
//
//            NodeFilter frameFilter = new NodeFilter() {      //过滤节点
//                public boolean accept(Node node) {
//                    if(node.getText().startsWith("frame src=")) {
//                        return true;
//                    }
//                    else {
//                        return false;
//                    }
//                }
//            };
//
//            OrFilter linkFilter = new OrFilter(new NodeClassFilter(LinkTag.class), frameFilter);
//            NodeList list = parser.extractAllNodesThatMatch(linkFilter);           //获取所有合适的节点
//            for(int i = 0; i <list.size();i++)
//            {
//                Node tag = list.elementAt(i);
//                if(tag instanceof LinkTag) {                         //链接文字
//                    LinkTag linkTag = (LinkTag) tag;
//                    String linkUrl = linkTag.getLink();//url
//                    String text = linkTag.getLinkText();//链接文字
////                    System.out.println(linkUrl + "**********" + text);
//                    if(filter.accept(linkUrl))
//                        links.add(linkUrl);
//                }
//                else if (tag instanceof ImageTag)   //<img> 标签              //链接图片
//                {
//                    ImageTag image = (ImageTag) list.elementAt(i);
////                    System.out.print(image.getImageURL() + "********");//图片地址
//                    System.out.println(image.getText());//图片文字
//                    if(filter.accept(image.getImageURL()))
//                        links.add(image.getImageURL());
//                }
//                else//<frame> 标签
//                {
//                    //提取 frame 里 src 属性的链接如 <frame src="test.html"/>
//                    String frame = tag.getText();
//                    int start = frame.indexOf("src=");
//                    frame = frame.substring(start);
//                    int end = frame.indexOf(" ");
//                    if (end == -1)
//                        end = frame.indexOf(">");
//                    frame = frame.substring(5, end - 1);
//                    System.out.println(frame);
//                    if(filter.accept(frame))
//                        links.add(frame);
//                }
//            }
//
//            return links;
//        } catch (ParserException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
}
