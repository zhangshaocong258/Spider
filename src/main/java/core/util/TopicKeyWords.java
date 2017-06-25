package core.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by zsc on 2017/6/24.
 * 获取话题广场的关键词
 */
public class TopicKeyWords {

    private static String key = "健康";
    private static String input = "E:\\新建文件夹\\" + key + ".html";
    private static String output = "E:\\新建文件夹\\" + key + ".txt";
    private static HashSet<String> hashSet = new HashSet<String>();

    private static HashSet<String> keyList = new HashSet<String>();
    private static List<File> fileList = new ArrayList<File>();


    public static void main(String args[]) throws IOException {
        genHashset();
//        html2Txt();
        genTxt();

    }

    private static void genTxt() throws IOException {
        File file = new File("E:\\2");
        getFiles(file);
        for (File f : fileList) {
            executeSeg(f);
        }
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("E:\\2\\out.txt")));
        for (String str : keyList) {
            bw.write(str);
            bw.newLine();
        }
        bw.flush();
        bw.close();
    }

    private static void executeSeg(File file) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line;
        while (((line = br.readLine()) != null) && !line.trim().equals("")) {
            if (!hashSet.contains(line)) {
                keyList.add(line);
            }

        }
        br.close();
    }

    //得到所有文件
    private static void getFiles(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    getFiles(f);
                } else {
                    fileList.add(f);
                }
            }
        }
    }

    private static void genHashset() {
        String[] content = {"腾讯", "阿里", "阿里巴巴", "京东", "网易", "滴滴", "亚马逊", "美团", "百度", "携程", "搜狗",
                "美团点评", "微博", "今日头条", "华为", "蘑菇街", "中兴", "淘宝", "天猫", "支付宝", "QQ", "微信", "迅雷",
                "谷歌", "Google", "微软", "电子科技大学"};
        String[] content2 = {"电影", "运动", "音乐", "健康", "摄影", "互联网", "金融", "美食", "动漫", "汽车", "教育",
                "历史", "投资", "法律", "创业", "科技", "游戏", "商业", "旅行", "职业", "体育", "电视剧", "书",
                "习惯", "体验", "睡眠", "恋爱", "后端", "程序员", "NBA", "成都", "房价", "两性关系", "赚钱", "性生活", "健身", "网盘", "骗局",
                "哈士奇", "柯基", "阿拉斯加", "猫", "直男癌", "男生", "女生", "公司", "拍案叫绝", "剩男", "剩女", "学校", "买房", "行业", "评价"};

        String[] content3 = {"手机", "电脑", "笔记本", "电动牙刷", "辞职", "三观", "辞职", "离职", "套路", "操作", "生活",
                "道理", "故事", "技巧", "学生", "人际关系", "社会", "底层", "阶级", "男友", "女友", "单身", "名字",
                "知识", "细思极恐", "鸡汤", "贫富差距", "脑残", "规律", "思维", "女神", "杭州", "年轻", "小米", "秘密", "无耻", "卧室", "面",
                "朋友", "人性", "优秀", "言论", "文化", "问题", "解决", "设计", "任务", "IT", "城市", "教训", "大神", "理财", "欢乐颂",
                "黑幕", "内幕", "真相", "美", "丑", "条件", "神器", "装修", "欢乐颂", "大神", "理财", "欢乐颂"};
        for (int i = 0; i < content.length; i++) {
            hashSet.add(content[i]);
        }
        for (int i = 0; i < content2.length; i++) {
            hashSet.add(content2[i]);
        }
        for (int i = 0; i < content3.length; i++) {
            hashSet.add(content3[i]);
        }
    }

    private static void html2Txt() throws IOException {

        File inFile = new File(input);
        InputStreamReader reader = null;
        StringWriter writer = new StringWriter();
        try {
            reader = new InputStreamReader(new FileInputStream(inFile));
            //将输入流写入输出流
            char[] buffer = new char[1024];
            int n = 0;
            while (-1 != (n = reader.read(buffer))) {
                writer.write(buffer, 0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        if (writer != null) {
            String html = writer.toString();
            Document document = Jsoup.parse(html);
            Elements keys = document.select("div[class=item even]").select("strong");
            File outFile = new File(output);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));

            for (Element element : keys) {
                if (!hashSet.contains(element.text())) {
                    bw.write(element.text());
                    bw.newLine();
                }
            }
            bw.flush();
            bw.close();
        }
    }
}
