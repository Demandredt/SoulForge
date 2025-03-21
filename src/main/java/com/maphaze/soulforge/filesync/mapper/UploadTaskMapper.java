package com.maphaze.soulforge.filesync.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.maphaze.soulforge.filesync.core.entity.UploadTask;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.mapping.FetchType;

import java.io.Serializable;

public interface UploadTaskMapper extends BaseMapper<UploadTask> {

}
