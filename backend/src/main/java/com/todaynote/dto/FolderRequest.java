package com.todaynote.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FolderRequest {

    @NotBlank(message = "文件夹名称不能为空")
    private String name;

    private String description;

    private Integer sortOrder;
}
