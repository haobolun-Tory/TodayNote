package com.todaynote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.todaynote.entity.Folder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FolderMapper extends BaseMapper<Folder> {
}
