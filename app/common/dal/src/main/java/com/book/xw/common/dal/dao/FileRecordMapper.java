package com.book.xw.common.dal.dao;

import com.book.xw.common.dal.model.FileRecordDo;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRecordMapper {

    int insert(FileRecordDo fileRecordDo);

}
