package com.xuecheng.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.media.model.po.MediaProcess;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wangzan
 * @since 2023-01-25
 */
public interface MediaProcessService extends IService<MediaProcess> {
    /**
     * @param shardIndex 分配序号
     * @param shardTotal 分片总数
     * @param count      获取记录数
     * @return java.util.List<com.xuecheng.media.model.po.MediaProcess>
     * @description 获取待处理任务
     * @date 2023/1/27
     **/
    List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);

    /**
     * @param status   任务状态
     * @param fileId   文件id
     * @param url      url
     * @param errorMsg 错误信息
     * @param taskId   任务id
     * @return void
     * @description 保存任务结果
     * @date 2023/1/27
     **/
    void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg);
}
