package com.xuecheng.media;

import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.po.MediaFiles;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author wangzan
 * @version 1.0
 * @description 大文件分块、合并
 * @date 2023/1/26
 */
@SpringBootTest
public class BigFileTest {
    @Test
    void testChunk() throws IOException {
        File sourceFile = new File("D:\\xc_edu\\3.avi");
        // 分块文件存储路径
        String chunkPath = "D:\\xc_edu\\chunk\\";
        File chunkFolderPath = new File(chunkPath);
        if (!chunkFolderPath.exists()) {
            chunkFolderPath.mkdirs();
        }
        //分块大小
        long chunkSize = 1024 * 1024 * 1;
        //分块数量
        long chunkNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        System.out.println("分块总数：" + chunkNum);
        //缓冲区大小
        byte[] b = new byte[1024];
        //使用RandomAccessFile访问文件
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");
        //分块
        for (int i = 0; i < chunkNum; i++) {
            //创建分块文件
            File file = new File(chunkPath + i);
            if (file.exists()) {
                file.delete();
            }
            boolean newFile = file.createNewFile();
            if (newFile) {
                //向分块文件中写数据
                RandomAccessFile raf_write = new RandomAccessFile(file, "rw");
                int len = -1;
                while ((len = raf_read.read(b)) != -1) {
                    raf_write.write(b, 0, len);
                    if (file.length() >= chunkSize) {
                        break;
                    }
                }
                raf_write.close();
                System.out.println("完成分块" + i);
            }

        }
        raf_read.close();

    }

    @Test
    void testMerge() throws IOException {
        File sourceFile = new File("D:\\xc_edu\\3.avi");

        // 分块文件存储路径
        String chunkPath = "D:\\xc_edu\\chunk\\";
        File chunkFolderPath = new File(chunkPath);
        if (!chunkFolderPath.exists()) {
            chunkFolderPath.mkdirs();
        }
        File mergeFile = new File("D:\\xc_edu\\3_01.avi");
        boolean mergeFileNewFile = mergeFile.createNewFile();
        // 按照顺序依次向合并文件写数据
        // 按文件名升序排列
        File[] chunkFiles = chunkFolderPath.listFiles();
        List<File> chunkFileList = Arrays.asList(chunkFiles);
        Collections.sort(chunkFileList, new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
        //缓冲区大小
        byte[] b = new byte[1024];
        for (File file : chunkFileList) {
            RandomAccessFile raf_read = new RandomAccessFile(file, "r");
            int len = -1;
            while ((len = raf_read.read(b)) != -1) {
                raf_write.write(b, 0, len);
            }
            raf_read.close();
        }
        raf_write.close();
        // 校验合并后的文件是否正确
        FileInputStream sourceFileStream = new FileInputStream(sourceFile);
        FileInputStream mergeFileStream = new FileInputStream(mergeFile);
        String sourceMd5Hex = DigestUtils.md5Hex(sourceFileStream);
        String mergeMd5Hex = DigestUtils.md5Hex(mergeFileStream);
        if (sourceMd5Hex.equals(mergeMd5Hex)) {
            System.out.println("合并成功");
        }

    }


}
