package core.spider;

import core.util.Config;
import core.util.HttpClientUtil;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by zsc on 2016/11/23.
 */
public class LoginTool {
    //知乎首页
    private static String INDEX_URL = "https://www.zhihu.com";
    //邮箱登录地址
    private static String EMAIL_LOGIN_URL = "https://www.zhihu.com/login/email";
    //手机号码登录地址
    private static String PHONENUM_LOGIN_URL = "https://www.zhihu.com/login/phone_num";
    //登录验证码地址
    private static String YZM_URL = "https://www.zhihu.com/captcha.gif?type=login";

    /**
     * @param account  账户
     * @param password 密码
     * @return
     */
    public boolean login(String account, String password) {
        CloseableHttpClient httpClient = HttpClientTool.getInstance().getCloseableHttpClient();
        HttpClientContext context = HttpClientTool.getInstance().getHttpClientContext();
        String CAPTCHA = null;
        String loginState = null;
//        HttpGet getRequest = new HttpGet(INDEX_URL);
//        HttpClientUtil.getWebPage(httpClient,context, getRequest, "utf-8", false);
        HttpPost request = null;
        List<NameValuePair> formParams = new ArrayList<NameValuePair>();
        //通过邮箱登录
        request = new HttpPost(EMAIL_LOGIN_URL);
        CAPTCHA = identify(httpClient, context, YZM_URL);//肉眼识别验证码
        formParams.add(new BasicNameValuePair("email", account));
        formParams.add(new BasicNameValuePair("captcha", CAPTCHA));
        formParams.add(new BasicNameValuePair("_xsrf", ""));//这个参数可以不用
        formParams.add(new BasicNameValuePair("password", password));
        formParams.add(new BasicNameValuePair("remember_me", "true"));
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(formParams, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        request.setEntity(entity);
        loginState = HttpClientUtil.getWebPage(httpClient, context, request, "utf-8", false);//登录
        JSONObject jo = new JSONObject(loginState);
//        for (String str : jo.keySet()) {
//            System.out.println(str);
//        }
        if (jo.get("r").toString().equals("0")) {
            System.out.println("登录知乎成功");
            /**
             * 访问首页
             * 没必要了
             */
//            getRequest = new HttpGet("https://www.zhihu.com/people/zhang-jia-wei");
//            HttpClientUtil.getWebPage(httpClient, context, getRequest, "utf-8", true);
            /**
             * 序列化Cookies
             */
            HttpClientUtil.serializeObject(context.getCookieStore(), Config.cookiePath);
            return true;
        } else {
            System.out.println("登录知乎失败");
//            throw new RuntimeException(HttpClientUtil.decodeUnicode(loginState));
            return false;
        }
    }
    /**
     * 肉眼识别验证码
     * @param httpClient Http客户端
     * @param context Http上下文
     * @param url 验证码地址
     * @return
     */
    public String identify(CloseableHttpClient httpClient,HttpClientContext context, String url){
        String captchaPath = Config.captcha;
        String path = captchaPath.substring(0, captchaPath.lastIndexOf("/") + 1);
        String fileName = captchaPath.substring(captchaPath.lastIndexOf("/") + 1);
        HttpClientUtil.downloadFile(httpClient, context, url, path, fileName,true);
        System.out.println("请输入 " + captchaPath + " 下的验证码：");
        Scanner sc = new Scanner(System.in);
        String captcha = sc.nextLine();
        return captcha;
    }

    /**
     * 反序列化CookiesStore
     * @return
     */
    public boolean deserializeCookieStore(String path){
        try {
            CookieStore cookieStore = (CookieStore) HttpClientUtil.deserializeMyHttpClient(path);
            HttpClientTool.getInstance().getHttpClientContext().setCookieStore(cookieStore);
        } catch (Exception e){
            System.out.println("反序列化Cookie失败,没有找到Cookie文件");
            return false;
        }
        return true;
    }
}
