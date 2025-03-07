package com.maphaze.soulforge.filesync.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.maphaze.soulforge.filesync.core.entity.UploadPart;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface UploadPartMapper extends BaseMapper<UploadPart> {
    @Select("SELECT part_number, etag FROM upload_part WHERE upload_id = #{uploadId}")
    @MapKey("part_number")
    Map<Integer,String> selectFileUploadParts(String uploadId);
}
