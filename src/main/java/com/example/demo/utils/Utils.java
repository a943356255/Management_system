package com.example.demo.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bytedeco.javacv.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class Utils {

    public static void main(String[] args) throws IOException {
        String url = "https://www.bilibili.com/video/BV1eD4y1y7mU/?spm_id_from=333.880.my_history.page.click&vd_source=47073b9a4320333c2e8d3544a7fc9448";
        Utils utils = new Utils();
//        utils.getVideo(url, "test1");
    }

    public static String getTime() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now().withNano(0);
        return today + " " + now;
    }

    public static ResponseEntity<InputStreamResource> download(String filePath, String newName) {
        ResponseEntity<InputStreamResource> response = null;
        try {
            InputStream inputStream = new FileInputStream(new File(filePath));
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Content-Disposition",
                    "attachment; filename="
                            + new String(newName.getBytes("gbk"), "iso8859-1"));
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            response = ResponseEntity.ok().headers(headers)
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(new InputStreamResource(inputStream));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return response;
    }

    public static String getType(String filename) {
        return Objects.requireNonNull(filename).substring(filename.lastIndexOf("."));
    }

    public void getVideo(String url, String filename, String path) throws IOException {
        String[] baseUrl = getBaseUrl(url);
        test(baseUrl[0], "video", path);
        test(baseUrl[1], "audio", path);
        System.out.println("getVideo的path =" + path);
        merge(filename, path);
    }

    public String getVideoImage(String url) {
        String html = test(url);
        Document document = Jsoup.parse(html);
        Elements elements = document.select("meta[itemprop=image]");

        String imageUrl = null;
        if (elements != null) {
            Element element = elements.get(0);
            imageUrl = element.attr("content");
        }
        return imageUrl;
    }

    public String test(String url) {

        // 生成httpClient，相当于打开浏览器
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;

        // 创建get请求，相当于在浏览器地址栏输入 网址
        HttpGet request = new HttpGet(url);
        request.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");

        try {
            // 发去http请求
            response = httpClient.execute(request);
            // 判断相应状态
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 获取响应内容
                HttpEntity httpEntity = response.getEntity();

                String html = EntityUtils.toString(httpEntity, "utf-8");

                return html;
            } else {
                // 请求不成功
                System.out.println(EntityUtils.toString(response.getEntity(), "utf-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpClient);
        }

        return null;
    }

    // 获取音频和视频的url
    public String[] getBaseUrl(String url) throws IOException {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;

        HttpGet optionRequest = new HttpGet(url);
        optionRequest.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");

        response = httpClient.execute(optionRequest);

        HttpEntity httpEntity = response.getEntity();

        String html = EntityUtils.toString(httpEntity, "utf-8");
        Document document = Jsoup.parse(html);

        // 获取script标签
        Elements elements = document.getElementsByTag("script");
        // 取第三个，有baseUrl
        Element element = elements.get(2);
        String jsonStr = element.data().substring(20);

        // 获取json中的baseUrl
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        JSONObject data = (JSONObject) jsonObject.get("data");
        JSONObject dash = (JSONObject) data.get("dash");
        JSONArray video = (JSONArray) dash.get("video");
        JSONArray audio = (JSONArray) dash.get("audio");
        JSONObject audioZero = (JSONObject) audio.get(0);
        JSONObject videoZero = (JSONObject) video.get(0);

        String[] str = new String[2];
        str[0] = (String) videoZero.get("baseUrl");
        str[1] = (String) audioZero.get("baseUrl");
        return str;
    }

    // 合并音频和视频
    public void merge(String filename, String path) {

        String outPut = path + File.separator + filename + ".mp4";

        System.out.println("merge中的path = " + outPut);

        String video = path + File.separator + "video.mp4";
        String audio = path + File.separator + "audio.mp3";
        System.out.println("进入 合并");

        FrameRecorder recorder = null;
        FrameGrabber grabber1 = null;
        FrameGrabber grabber2 = null;
        try {
            //抓取视频帧
            grabber1 = new FFmpegFrameGrabber(video);
            System.out.println(grabber1);
            //抓取音频帧
            grabber2 = new FFmpegFrameGrabber(audio);

            grabber1.start();
            grabber2.start();
            //创建录制
            recorder = new FFmpegFrameRecorder(outPut,
                    grabber1.getImageWidth(),
                    grabber1.getImageHeight(),
                    grabber2.getAudioChannels());
            recorder.setFormat("mp4");
            recorder.setFrameRate(grabber1.getFrameRate());
            recorder.setSampleRate(grabber2.getSampleRate());
            recorder.setVideoBitrate(4000000);
            recorder.setAudioQuality(0);

            recorder.start();
            Frame frame1;
            Frame frame2;
            //先录入视频
            while ((frame1 = grabber1.grabFrame()) != null) {
                recorder.record(frame1);
            }
            //然后录入音频
            while ((frame2 = grabber2.grabFrame()) != null) {
                recorder.record(frame2);
            }
            grabber1.stop();
            grabber2.stop();
            recorder.stop();
        } catch (Exception e) {
            System.out.println("出现错误");
            e.printStackTrace();
        } finally {
            try {
                if (recorder != null) {
                    recorder.release();
                }
                if (grabber1 != null) {
                    grabber1.release();
                }
                if (grabber2 != null) {
                    grabber2.release();
                }
            } catch (FrameRecorder.Exception | FrameGrabber.Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("结束合并");
    }

    public void test(String url, String fileName, String path) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;

        HttpGet optionRequest = new HttpGet(url);
        optionRequest.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
        optionRequest.setHeader("accept", "*/*");
        optionRequest.setHeader("origin", "https://www.bilibili.com");
        optionRequest.setHeader("referer", "https://www.bilibili.com/video/BV1kW4y1m7mN?spm_id_from=333.851.b_7265636f6d6d656e64.5&vd_source=47073b9a4320333c2e8d3544a7fc9448");
        optionRequest.setHeader("range", "bytes=1065-1744");
        optionRequest.setHeader("accept-language", "zh-CN,zh;q=0.9");
        optionRequest.setHeader("accept-encoding", "identity");
        optionRequest.setHeader("sec-ch-ua", "Chromium\";v=\"104\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"104");

        response = httpClient.execute(optionRequest);
        // 判断相应状态
        String size = response.getHeaders("Content-Range")[0].getValue().split("/")[1];

        System.out.println("size = " + size);
        optionRequest.setHeader("range", "bytes=0-" + size);
        // 再次发起请求，拿全部数据
        response = httpClient.execute(optionRequest);
        HttpEntity httpEntity = response.getEntity();

        byte[] bytes = EntityUtils.toByteArray(httpEntity);
        DataOutputStream out = null;
        if (fileName.equals("video")) {
            out = new DataOutputStream(new FileOutputStream(path + File.separator + fileName + ".mp4"));
        } else {
            out = new DataOutputStream(new FileOutputStream(path + File.separator + fileName + ".mp3"));
        }

        out.write(bytes);
        out.close();

        HttpClientUtils.closeQuietly(response);
        HttpClientUtils.closeQuietly(httpClient);
    }
}
