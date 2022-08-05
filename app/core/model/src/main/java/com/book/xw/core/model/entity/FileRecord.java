package com.book.xw.core.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class FileRecord implements Serializable {

    private Long id;

    private String originFileName;

    private String fileName;

    // M为单位
    private Double fileSize;

    private String dirPath;

    private String desc;

    private Date gmtCreate;

    private Date gmtModified;
}
