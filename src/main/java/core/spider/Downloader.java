package core.spider;

import core.util.Config;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zsc on 2016/8/13.
 * 下载doc
 */
public class Downloader {
    private static final String CHARSET = "UTF-8";

    public Downloader() {
    }

    //连接网站，下载页面
    public static String downloadPage(String url) throws IOException {
        String htmlDoc = null;

        CloseableHttpClient httpClient = HttpClientTool.getInstance().getCloseableHttpClient();
        HttpClientContext context = HttpClientTool.getInstance().getHttpClientContext();
        HttpGet httpGet = new HttpGet(url);//get就可以，只要有context就行

//        //建造者模式，设置超时，在closeableHttpClient中设置，在这里设置则报错
//        RequestConfig requestConfig = RequestConfig.custom().
//                setSocketTimeout(5000).
//                setConnectTimeout(3000).build();
//        httpGet.setConfig(requestConfig);


        CloseableHttpResponse response = httpClient.execute(httpGet, context);//超时报错IOException，还报其他错

        HttpEntity httpEntity = response.getEntity();
        StatusLine statusLine = response.getStatusLine();

        //如果是转移，然后？？？
//            if (statusLine.getStatusCode() == HttpStatus.SC_MOVED_PERMANENTLY ||
//                    statusLine.getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY ||
//                    statusLine.getStatusCode() == HttpStatus.SC_SEE_OTHER ||
//                    statusLine.getStatusCode() == HttpStatus.SC_TEMPORARY_REDIRECT) {
//                System.out.println("重定向");
//                Header header = httpGet.getFirstHeader("location");
//                if (header != null) {
//                    String newUrl = header.getValue();
//                    if (newUrl == null || newUrl.equals("")) {
//                        newUrl = "/";
//                        HttpGet redirect = new HttpGet(newUrl);
//                    }
//                }
//            }

        //连接成功
        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
            if (httpEntity == null) {
                throw new ClientProtocolException("Response contains no content");
            } else {
                InputStream inputStream = httpEntity.getContent();//报错IOException
                htmlDoc = IOUtils.toString(inputStream);//得到html内容，是否为空已判断！！！！！可用下面的方式
//                htmlDoc = EntityUtils.toString(httpEntity);//httpGet直接得到html，post得到json
                String filename = getFilenameByUrl(url, httpEntity.getContentType().getValue());
                System.out.println("filename: " + filename);
                saveDoc(url, filename, htmlDoc);
            }
        }
        response.close();//关闭，报错IOException
        return htmlDoc;
    }

    //得到html后缀，将url中的/替换为_，否则保存文件出错
    private static String getFilenameByUrl(String url, String contentType) {
        //contentType:  text/html; charset=UTF-8

        //http://为7，https://为8
        if (url.startsWith("http://")) {
            url = url.substring(7);
        } else if (url.startsWith("https://")) {
            url = url.substring(8);
        }
        //后缀为html，则保存为html
        /**
         * repalceAll正则\\\\
         */
        if (contentType.indexOf("html") != -1) {
            url = url.replaceAll("[\\\\?/:*|<>\"]", "_") + ".html";
            return url;
        } else {
            url = url.replaceAll("[\\\\?/:*|<>\"]", "_") + contentType.substring(contentType.lastIndexOf('/') + 1);
            return url;
        }
    }

    //得到原始html内容@Deprecated，最好使用stringwrite
    private static String getHtmlDoc(InputStream inputStream) throws IOException {
        StringBuffer document = new StringBuffer();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, CHARSET));
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (!line.trim().isEmpty())
                document.append(line + "\n");
        }

        return document.toString();
    }


    //函数完成一些分析的功能，抽取doc中的url并且返回，其次，将抽取了内容的doc
    //按照指定的格式写入文件中进行保存
//    public ArrayList<URL> doAnalyzer(BufferedWriter bfWriter, URL url, String htmlDoc) {
//
//        System.out.println("in doing analyzer the size of doc is: " + htmlDoc.length());
//
//        ArrayList<URL> urlInHtmlDoc = (new HtmlParser()).urlDetector(htmlDoc);
////        saveDoc(bfWriter, url, htmlDoc);
//
//        return urlInHtmlDoc;
//    }


    /**
     * 格式参照：
     * 每条记录：头部 + 空行 + 数据 + 空行
     * 头部为若干属性，每个属性是非空行，包含属性名 + 冒号 + 属性值
     * 第一个属性为 version:1.0，最后一个属性为数据长度属性，length：8021，是data的长度，不包含空行
     * example:
     * version:1.0
     * url:http//www.pku.edu.cn
     * origin:http://www.somewhere.cn
     * date:Tue, 15 Apr 2003 08:13:06 GMT
     * ip:162.105.129.12
     * length:18133
     *
     * @param url
     * @param fileName
     * @param htmlDoc  保存html内容到.html文件中 Done
     */
    private static void saveDoc(String url, String fileName, String htmlDoc) throws IOException {


        BufferedWriter bfWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Config.downloadPath +
                File.separator + fileName), CHARSET));//IOException

        String versionStr = "<version>" + "version:1.0" + "</version>";

        String URLStr = "<url>" + url + "</url>";

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = "<date>" + sdf.format(date) + "</date>";

        InetAddress address = InetAddress.getByName(new URL(url).getHost());//IOException
        String IPStr = address.toString();
        IPStr = "<ip>" + IPStr.substring(IPStr.indexOf("/") + 1, IPStr.length()) + "</ip>";

        String htmlLen = "<length>" + htmlDoc.length() + "</length>";


        //数据部分，只删除people中的script，尽量保留原html
        if (url.startsWith(Config.people)) {
            Document document = Jsoup.parse(htmlDoc);
//            Element tittle = document.select("title").first();
//            Element content = document.select("div[class=zm-editable-content]").first();
            //保证title和content不为空，@Deprecated
//            String wordLen = "<wordLength>" + (((tittle == null) ? 0 : tittle.text().replace(" ", "").length()) +
//                    ((content == null) ? 0 : content.text().replace(" ", "").length())) + "</wordLength>";
            Elements links = document.select("script");
            for (int i = 0; i < links.size(); i++) {
                if (links.get(i).attr("data-reactid").equals("21")) {
                    links.get(i).remove();
                }
//                links.get(i).removeAttr("src");
//                System.out.println("script: " + links.get(i).toString());
            }
            htmlDoc = document.html();//保证保存的html没有script，而返回的htmlDoc有script
        }
        bfWriter.write(htmlDoc);//IOException

        //html数据头部分，放在尾部
        bfWriter.newLine();
        bfWriter.write(versionStr);//所有writeIOException
        bfWriter.newLine();
        bfWriter.write(URLStr);
        bfWriter.newLine();
        bfWriter.write(dateStr);
        bfWriter.newLine();
        bfWriter.write(IPStr);
        bfWriter.newLine();
        bfWriter.write(htmlLen);
//        bfWriter.newLine();
//        bfWriter.write(wordLen);
        bfWriter.flush();//IOException


    }
}
