package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileService;
import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wangzan
 * @version 1.0
 * @description 媒资文件管理业务实现类
 * @date 2023/1/25
 */
@Service
@Slf4j
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    MediaFilesMapper mediaFilesMapper;
    @Autowired
    MediaProcessMapper mediaProcessMapper;
    @Autowired
    MinioClient minioClient;
    @Value("${minio.bucket.files}")
    private String bucket_files;

    @Value("${minio.bucket.videofiles}")
    private String bucket_videoFiles;
    @Autowired
    MediaFileService currentProxy;

    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(queryMediaParamsDto.getFilename()), MediaFiles::getFilename, queryMediaParamsDto.getFilename());

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;

    }

    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] bytes, String folder, String objectName) {
        // 得到文件的md5值
        String fileMd5 = DigestUtils.md5Hex(bytes);

        if (StringUtils.isEmpty(folder)) {
            //自动生成目录的路径，当前年月日
            folder = getFileFolder(new Date(), true, true, true);
        } else if (folder.indexOf("/") < 0) {
            folder = folder + "/";
        }
        String filename = uploadFileParamsDto.getFilename();
        if (StringUtils.isEmpty(objectName)) {
            // 如果objectName为空，使用文件的md5值
            objectName = fileMd5 + filename.substring(filename.lastIndexOf("."));
        }
        objectName = folder + objectName;

        try {
            addMediaFilesToMinIO(bytes, bucket_files, objectName);
            // 保存到数据库
            MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_files, objectName);
            // 准备返回数据
            UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
            BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
            return uploadFileResultDto;
        } catch (Exception e) {
            log.debug("上传文件失败：{}" + e.getMessage());
            e.getStackTrace();
            XueChengPlusException.cast("上传过程中出错");

        }
        return null;
    }

    @Override
    @Transactional
    public MediaFiles addMediaFilesToDb(Long companyId, String fileId, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            // 封装数据
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileId);
            mediaFiles.setFileId(fileId);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);
            String extension = null;
            String filename = uploadFileParamsDto.getFilename();
            if (StringUtils.isNotEmpty(filename) && filename.contains(".")) {
                extension = filename.substring(filename.lastIndexOf("."));
            }
            String mimeType = getMimeTypeByExtension(extension);
            // 图片、MP4视频可以设置URL路径
            if (mimeType.contains("image") || mimeType.contains("mp4")) {
                mediaFiles.setUrl("/" + bucket + "/" + objectName);

            }
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setStatus("1");
            mediaFiles.setAuditStatus("002003");
            //插入文件表
            mediaFilesMapper.insert(mediaFiles);
            if (mimeType.equals("video/x-msvideo")) {
                MediaProcess mediaProcess = new MediaProcess();
                BeanUtils.copyProperties(mediaFiles, mediaProcess);
                //设置为未处理
                mediaProcess.setStatus("1");
                mediaProcessMapper.insert(mediaProcess);
            }
        }
        return mediaFiles;
    }

    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        // 在文件表中存在，并且在文件系统存在
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles != null) {
            //桶
            String bucket = mediaFiles.getBucket();
            //存储目录
            String filePath = mediaFiles.getFilePath();
            //文件流
            InputStream stream = null;
            try {
                stream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucket)
                                .object(filePath)
                                .build());

                if (stream != null) {
                    //文件已存在
                    return RestResponse.success(true);
                }
            } catch (Exception e) {

            }

        }
        //文件不存在
        return RestResponse.success(false);
    }

    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
        // 得到分块文件所在目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        // 分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunkIndex;
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(bucket_videoFiles).object(chunkFilePath).build();
        try {
            InputStream inputStream = minioClient.getObject(getObjectArgs);
            if (inputStream != null) {
                //分块已存在
                return RestResponse.success(true);
            }
        } catch (Exception e) {

        }
        //分块未存在
        return RestResponse.success(false);

    }

    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, byte[] bytes) {
        //得到分块文件的目录路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //得到分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunk;

        try {
            //将文件存储至minIO
            addMediaFilesToMinIO(bytes, bucket_videoFiles, chunkFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
            XueChengPlusException.cast("上传过程出错请重试");
        }
        return RestResponse.success();

    }

    @Override
    public RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) {
        // 下载分块
        File[] chunkFiles = checkChunkStatus(fileMd5, chunkTotal);
        String filename = uploadFileParamsDto.getFilename();
        String extension = filename.substring(filename.lastIndexOf("."));
        // 创建一个临时文件作为合并文件
        File tempMergeFile = null;
        try {
            tempMergeFile = File.createTempFile("merge", extension);
        } catch (IOException e) {
            XueChengPlusException.cast("创建临时合并文件出错");
        }
        try {
            // 开始合并
            byte[] b = new byte[1024];
            // 合并分块
            try (RandomAccessFile raf_write = new RandomAccessFile(tempMergeFile, "rw")) {
                for (File file : chunkFiles) {
                    try (RandomAccessFile raf_read = new RandomAccessFile(file, "r")) {
                        int len = -1;
                        while ((len = raf_read.read(b)) != -1) {
                            raf_write.write(b, 0, len);
                        }
                    }

                }
            } catch (IOException e) {
                XueChengPlusException.cast("合并文件过程出错");
            }
            // 校验合并后的文件是否正确
            try {
                FileInputStream mergeFileStream = new FileInputStream(tempMergeFile);
                String mergeMd5Hex = DigestUtils.md5Hex(mergeFileStream);
                if (!fileMd5.equals(mergeMd5Hex)) {
                    log.debug("合并文件校验不通过，文件路径:{}，原始文件md5:{}", tempMergeFile.getAbsolutePath(), fileMd5);
                    XueChengPlusException.cast("合并文件校验不通过");
                }
            } catch (IOException e) {
                log.debug("合并文件校验不通过，文件路径:{}，原始文件md5:{}", tempMergeFile.getAbsolutePath(), fileMd5);
                XueChengPlusException.cast("合并文件校验出错");
            }
            // 将合并后的文件上传到文件系统
            // 拿到合并文件在minio的存储路径
            String mergeFilePath = getFilePathByMd5(fileMd5, extension);
            addMediaFilesToMinIO(tempMergeFile.getAbsolutePath(), bucket_videoFiles, mergeFilePath);
            // 将文件信息写入表
            uploadFileParamsDto.setFileSize(Long.valueOf(mergeFilePath.length()));
            addMediaFilesToDb(companyId, fileMd5, uploadFileParamsDto, bucket_videoFiles, mergeFilePath);
            return RestResponse.success(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 删除临时文件
            if (chunkFiles != null) {
                for (File chunkFile : chunkFiles) {
                    if (chunkFile.exists()) {
                        chunkFile.delete();
                    }
                }
            }
            if (tempMergeFile != null) {
                tempMergeFile.delete();
            }
        }
    }

    @Override
    public MediaFiles getFileById(String id) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(id);
        if (mediaFiles == null) {
            XueChengPlusException.cast("文件不存在");
        }
        String url = mediaFiles.getUrl();
        if (StringUtils.isEmpty(url)) {
            XueChengPlusException.cast("文件还没处理，请稍后预览");
        }
        return mediaFiles;
    }

    /**
     * @param fileMd5
     * @param chunkTotal
     * @return java.io.File[] 分块文件数组
     * @description 下载分块
     * @date 2023/1/26
     **/
    private File[] checkChunkStatus(String fileMd5, int chunkTotal) {
        //得到分块文件的目录路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        File[] files = new File[chunkTotal];
        //检查分块文件是否上传完毕
        for (int i = 0; i < chunkTotal; i++) {
            String chunkFilePath = chunkFileFolderPath + i;
            //下载文件
            File chunkFile = null;
            try {
                chunkFile = File.createTempFile("chunk" + i, null);
            } catch (IOException e) {
                e.printStackTrace();
                XueChengPlusException.cast("下载分块时创建临时文件出错" + e.getMessage());
            }
            files[i] = downloadFileFromMinIO(chunkFile, bucket_videoFiles, chunkFilePath);
        }
        return files;
    }

    @Override
    public File downloadFileFromMinIO(File chunkFile, String bucket, String objectName) {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(bucket).object(objectName).build();
        try (InputStream inputStream = minioClient.getObject(getObjectArgs);
             FileOutputStream outputStream = new FileOutputStream(chunkFile)) {
            IOUtils.copy(inputStream, outputStream);
            return chunkFile;
        } catch (Exception e) {
            e.printStackTrace();
            XueChengPlusException.cast("查询分块文件出错");
        }
        return null;
    }

    //得到分块文件的目录
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
    }

    private String getFilePathByMd5(String fileMd5, String fileExt) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }

    // 将文件上传到分布式文件系统
    public void addMediaFilesToMinIO(byte[] bytes, String bucket, String objectName) {
        String extension = null;
        if (objectName.contains(".")) {
            extension = objectName.substring(objectName.lastIndexOf("."));
        }
        String contentType = getMimeTypeByExtension(extension);

        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            // 上传到minion
            PutObjectArgs putObjectArgs = PutObjectArgs.builder().bucket(bucket).object(objectName).stream(byteArrayInputStream, byteArrayInputStream.available(), -1).contentType(contentType).build();
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            e.getStackTrace();
            log.debug("上传文件到文件系统出错{}", e.getMessage());
            XueChengPlusException.cast("上传文件到文件系统出错");
        }
    }

    private String getMimeTypeByExtension(String extension) {
        // 资源的媒体类型
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (StringUtils.isNotEmpty(extension)) {
            ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
            if (extensionMatch != null) {
                contentType = extensionMatch.getMimeType();
            }
        }
        return contentType;
    }

    // 将文件上传到分布式文件系统
    @Override
    public void addMediaFilesToMinIO(String filePath, String bucket, String objectName) {
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder().bucket(bucket).object(objectName).filename(filePath).build();
            minioClient.uploadObject(uploadObjectArgs);
            log.debug("文件上传成功:{}", filePath);
        } catch (Exception e) {
            XueChengPlusException.cast("文件上传到文件系统失败");
        }
    }

    //根据日期拼接目录
    private String getFileFolder(Date date, boolean year, boolean month, boolean day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前日期字符串
        String dateString = sdf.format(new Date());
        //取出年、月、日
        String[] dateStringArray = dateString.split("-");
        StringBuffer folderString = new StringBuffer();
        if (year) {
            folderString.append(dateStringArray[0]);
            folderString.append("/");
        }
        if (month) {
            folderString.append(dateStringArray[1]);
            folderString.append("/");
        }
        if (day) {
            folderString.append(dateStringArray[2]);
            folderString.append("/");
        }
        return folderString.toString();
    }

}
