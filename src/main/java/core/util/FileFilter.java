package core.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsc on 2017/6/24.
 * 获取文件中符合条件的question文件（关注数大于100 19w）
 */
public class FileFilter {
    private static String input = "D:\\SpiderDownload\\-zhihu-2016-12-16-21-54-32";
    private static String output = "D:\\copy\\";
    private static List<File> fileList = new ArrayList<File>();
    private static int count = 0;
    private static int i = 16;
    public static void main(String args[]) throws IOException{
        long a = System.currentTimeMillis();


        File folder = new File(input);
//        getFiles(folder);
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2016-12-17-22-26-49"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2016-12-18-21-47-53"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2016-12-19-11-02-33"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2016-12-19-15-09-59"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2016-12-19-17-20-49"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2016-12-19-22-13-59"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2016-12-20-11-28-37"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2016-12-20-17-32-12"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2016-12-20-17-36-59"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2016-12-20-22-35-42"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2016-12-21-11-15-40"));

//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2016-12-22-22-39-36"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2016-12-23-22-09-19"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2016-12-24-21-51-42"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2016-12-25-23-32-45"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2016-12-26-21-48-14"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2016-12-27-22-47-37"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2016-12-28-22-28-57"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2016-12-29-21-34-21"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2016-12-30-21-26-15"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2016-12-31-22-35-23"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2017-01-04-22-15-42"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2017-01-05-22-26-52"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2017-01-08-20-50-58"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2017-01-09-22-07-28"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2017-01-10-22-34-32"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2017-01-11-22-25-33"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2017-01-12-23-11-14"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2017-01-13-21-38-52"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2017-01-14-22-49-01"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2017-01-15-22-34-49"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2017-01-15-23-26-45"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2017-01-16-22-54-36"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2017-01-17-22-49-53"));
//        getFiles(new File("D:\\SpiderDownload\\-zhihu-2017-01-18-22-54-26"));
//        saveObj(fileList, "D:\\fileList.bin");
        genFile();




        System.out.println("ccc " + count);
        System.out.println("size " + fileList.size());
        long b = System.currentTimeMillis();
        System.out.println("time" + ( b - a));
    }


    private static void genFile() throws IOException{
        Document doc;
        String str;
        int quality;
        for (File file : fileList) {
            doc = Jsoup.parse(file, "UTF-8");
            try {
                str = doc.select("div.zh-question-followers-sidebar").select("strong").text();
                if (str == null || str.length() == 0) {
                    continue;
                }
                quality = Integer.valueOf(str);
                if (quality >= 100 && quality < 1000) {
                    if (count <= 10000) {
                        nioTransferCopy(file, new File("E:\\SpiderCopy\\" + i + "\\" + file.getName()));
                        count++;
                    } else {
                        count = 0;
                        i++;
                    }

                }
            } catch (NullPointerException e) {
                System.out.println("空指针");
                e.printStackTrace();
            } catch (NumberFormatException e) {
                System.out.println("格式错误");
                e.printStackTrace();
            }

        }

    }

    private static void nioTransferCopy(File source, File target) throws IOException{
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inStream.close();
            in.close();
            outStream.close();
            out.close();
        }
    }

    private static void saveObj(Object object,String path) throws IOException{
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(new File(path)));
            oos.writeObject(object);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("write model error");
        } finally {
            if (oos != null) {
                oos.close();
            }
        }
    }

    //得到所有文件
    private static void getFiles(File file) {
        if (file.isDirectory() && (file.getName().startsWith("-zhihu-")) || file.getName().startsWith("Spider")) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isDirectory() && f.getName().startsWith("-zhihu-")) {
                    getFiles(f);
                } else {
                    if (f.getName().startsWith("www.zhihu.com_question")) {
                        fileList.add(f);
                    }
                }
            }
        }
    }
}
