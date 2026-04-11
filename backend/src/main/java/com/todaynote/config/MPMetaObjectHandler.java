package com.todaynote.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
/**
 * MyBatis-Plus 自动填充处理器，用于统一维护创建时间和更新时间。
 */
public class MPMetaObjectHandler implements MetaObjectHandler {

    /**
     * 在插入数据时自动填充创建时间和更新时间。
     *
     * @param metaObject 当前元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }

    /**
     * 在更新数据时自动刷新更新时间。
     *
     * @param metaObject 当前元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }
}
