package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

/**
 * @author wangzan
 * @version 1.0
 * @description 媒资文件管理业务类
 * @date 2023/1/25
 */
public interface MediaFileService {

    /**
     * @param companyId           机构id
     * @param pageParams          分页参数
     * @param queryMediaParamsDto 查询条件
     * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
     * @description 媒资文件查询方法
     * @date 2023/1/25
     **/
    PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    /**
     * @param companyId           机构id
     * @param uploadFileParamsDto 文件信息
     * @param bytes               文件字节数组
     * @param folder              桶下的目录
     * @param objectName          对象名称
     * @return com.xuecheng.media.model.dto.UploadFileResultDto
     * @description 上传文件的通用接口
     * @date 2023/1/25
     **/
    UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] bytes, String folder, String objectName);

    /**
     * @param companyId
     * @param fileId
     * @param uploadFileParamsDto
     * @param bucket
     * @param objectName
     * @return com.xuecheng.media.model.po.MediaFiles
     * @description 将文件信息入库
     * @date 2023/1/26
     **/
    MediaFiles addMediaFilesToDb(Long companyId, String fileId, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName);

    /**
     * @param chunkFile  文件对象
     * @param bucket     文件桶
     * @param objectName 文件对象
     * @return java.io.File
     * @description 从文件系统下载文件
     * @date 2023/1/27
     **/
    File downloadFileFromMinIO(File chunkFile, String bucket, String objectName);

    /**
     * @return void
     * @description 上传文件到文件系统
     * @date 2023/1/27
     * @param    filePath    文件路径
     * @param    bucket    文件桶
     * @param    objectName    文件的上传路径
     **/
    void addMediaFilesToMinIO(String filePath, String bucket, String objectName);

    /**
     * @param fileMd5 文件的md5
     * @return com.xuecheng.base.model.RestResponse<java.lang.Boolean>
     * @description 检查文件是否存在
     * @date 2023/1/26
     **/
    RestResponse<Boolean> checkFile(String fileMd5);

    /**
     * @param fileMd5    文件的md5
     * @param chunkIndex 分块序号
     * @return com.xuecheng.base.model.RestResponse<java.lang.Boolean>
     * @description 检查分块是否存在
     * @date 2023/1/26
     **/
    RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);

    /**
     * @param fileMd5 文件md5
     * @param chunk   分块序号
     * @param bytes   文件字节
     * @return com.xuecheng.base.model.RestResponse
     * @description 上传分块
     * @date 2023/1/26
     **/
    RestResponse uploadChunk(String fileMd5, int chunk, byte[] bytes);

    /**
     * @param companyId           机构id
     * @param fileMd5             文件md5
     * @param chunkTotal          分块总和
     * @param uploadFileParamsDto 文件信息
     * @return com.xuecheng.base.model.RestResponse
     * @description 合并分块
     * @date 2023/1/26
     **/
    RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto);

    /**
     * @param id 文件id
     * @return com.xuecheng.media.model.po.MediaFiles 文件信息
     * @description 根据id查询文件信息
     * @date 2023/1/27
     **/
    MediaFiles getFileById(String id);

}
