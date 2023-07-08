package com.example.demo.controller.file_controller;

import cn.hutool.log.StaticLog;
import com.example.demo.pojo.file.Chunk;
import com.example.demo.pojo.result.ResultVO;
import com.example.demo.service.file.ChunkService;
import com.example.demo.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import java.util.*;

@RestController
@RequestMapping("/file")
public class FileController {

//    private final String filePath = "D:\\test_file_upload";
    private final String filePath = "/data/movie";

//    private final String filePathTemp = "D:\\test_file_upload";
    private final String filePathTemp = "/data/movie";

    @Resource
    ChunkService chunkServiceImpl;

    @RequestMapping("/previewPDF")
    public void previewPDF(HttpServletResponse response, @RequestBody Map<String, String> map) {
        // 读取文件的path
        String path = map.get("path");
        // 具体的pdf名
        int pageNum = Integer.parseInt(map.get("pageNum"));
        // 文件名
        String filename = map.get("filename");
        String realPath = path + File.separator + filename + File.separator + pageNum + ".pdf";

        System.out.println("预览pdf的真正路径是：" + realPath);

        preview(response, realPath, filename);
    }

    public static void preview(HttpServletResponse response, String filePath, String filename) {
        ServletOutputStream out;
        InputStream inputStream;
        try {
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition", "inline;fileName=" + filename + ".pdf");
            File file = new File(filePath);
            // 使用输入读取缓冲流读取一个文件输入流
            inputStream = new FileInputStream(file);
            // 利用response获取一个字节流输出对象
            out = response.getOutputStream();
            // 定义个数组，由于读取缓冲流中的内容
            byte[] buffer = new byte[1024];
            int n;
            // while循环一直读取缓冲流中的内容到输出的对象中
            while ((n = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            // 写出到请求的地方
            out.flush();
            inputStream.close();
            out.close();
            System.out.println("预览pdf的写入response完成。");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            StaticLog.error("Exception", e);
        }
    }

    @RequestMapping("/uploadFile")
    public ResultVO<String> upload(HttpServletRequest request, Chunk chunk) throws Exception {
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (isMultipart) {
            String filename = chunk.getFilename();

            MultipartFile file = chunk.getFile();
            if (file == null) {
                throw new Exception("参数验证失败！");
            }

            Integer chunkNumber = chunk.getChunkNumber();
            if (chunkNumber == null) {
                chunkNumber = 0;
            }

            File outFile = new File(filePathTemp + File.separator + chunk.getIdentifier(), chunkNumber + ".part");

            InputStream inputStream = file.getInputStream();
            FileUtils.copyInputStreamToFile(inputStream, outFile);
        }

        return new ResultVO<>("上传成功");
    }

    @RequestMapping("/mergeFile")
    public ResultVO<String> mergeFile(String filename, String guid, String actor) throws Exception {

        File file = new File(filePathTemp + File.separator + guid);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                File partFile = new File(filePath + File.separator + filename);
                for (int i = 1; i <= files.length; i++) {
                    File s = new File(filePathTemp + File.separator + guid, i + ".part");
                    FileOutputStream destTempfos = new FileOutputStream(partFile, true);
                    FileUtils.copyFile(s, destTempfos);
                    destTempfos.close();
                }
                FileUtils.deleteDirectory(file);
            }
        }
        String type = filename.split("\\.")[1];
        // 添加到数据库
        chunkServiceImpl.insert(filename, filePath, type, actor);
        return new ResultVO<>("合并成功");
    }

    @RequestMapping("/getImageUrl")
    public ResultVO<String> getImageUrl(@RequestBody Map<String, String> map) {
        String url = map.get("url");

        return new ResultVO<>(new Utils().getVideoImage(url));
    }



    @RequestMapping("/downloadB")
    public ResultVO<String> downloadVideoFromBili(@RequestBody Map<String, String> map) throws IOException {

        String url = map.get("url");
        String filename = map.get("filename");
        // 服务器存储路径
         String path = "/data/movie";
        // 本地测试路径
//         String path = "D:\\video";

        new Utils().getVideo(url, filename, path);

        return new ResultVO<>(filename + ".mp4");
    }

    @RequestMapping("/downloadS")
    public void downloadFromServer(@RequestBody Map<String, String> map, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fileName = map.get("fileName");
        String path = map.get("path");
        String str = "D:\\BaiduNetdiskDownload\\01书籍" + "\\" + fileName;
        String type = map.get("type");
        String rootPath = "/data/movie/" + fileName;
        if (type != null && !type.equals("mp4")) {
            rootPath = rootPath + "." + type;
        }
//        String rootPath = "D:\\video\\" + fileName;
        System.out.println("获取文件在服务器的路径：" + rootPath);
        File file = new File(rootPath);

        InputStream inputStream = new FileInputStream(file);
        int totalSize = inputStream.available();

        byte[] buffer = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            //文件是否存在
            if (file.exists()) {
                //设置响应
                response.setContentType("application/octet-stream;charset=UTF-8");
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Content-Length", totalSize + "");
                os = response.getOutputStream();
                bis = new BufferedInputStream(new FileInputStream(file));
                while(bis.read(buffer) != -1){
                    os.write(buffer);
                }
                System.out.println("文件下载的写入response完成");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(bis != null) {
                    bis.close();
                }
                if(os != null) {
                    os.flush();
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("开始返回结果");
    }

    public void getSinglePDF(String path, String filename) {
        String realPath = path + File.separator + filename;
        File indexFile = new File(realPath);// 这是对应文件名
        PDDocument document = null;
        // 这里是服务器写入地址
        String[] arr = filename.split("\\.");
        String singlePath = path + File.separator + arr[0] + File.separator;

        // 这里是写入地址
//        String singlePathWindows = "D:\\BaiduNetdiskDownload\\01书籍\\Java+并发编程实战\\";

        Map<String, Object> returnMap = new HashMap<>();

        try {
            document = PDDocument.load(indexFile);
            int totalPages = document.getNumberOfPages();
            // 封装pdf总页数
            for (int i = 1; i <= totalPages; i++) {
                String name = String.valueOf(i);
                Splitter splitter = new Splitter();
                splitter.setStartPage(i);
                splitter.setEndPage(i);
                List<PDDocument> pages = splitter.split(document);
                ListIterator<PDDocument> iterator = pages.listIterator();
                while (iterator.hasNext()) {
                    File file = new File(singlePath + name + ".pdf");
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    PDDocument pd = iterator.next();
                    File newFile = new File(singlePath + name + ".pdf");
                    if (newFile.exists()) {
                        newFile.delete();
                    }
                    pd.save(singlePath + name + ".pdf");
                    pd.close();
                    if (newFile.exists()) {
                        returnMap.put("path", newFile.getPath());
                    }
                }
            }
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}