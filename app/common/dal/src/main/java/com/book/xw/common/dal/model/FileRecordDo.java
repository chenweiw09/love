package com.book.xw.common.dal.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class FileRecordDo implements Serializable {

    private Long id;

    private String originFileName;

    private String fileName;

    // M为单位
    private Double fileSize;

    private String dirPath;

    private String desc;

    private Date gmtCreate = new Date();

    private Date gmtModified = new Date();
}
