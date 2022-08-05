package com.book.xw.core.model.repository;

import com.book.xw.common.dal.dao.FileRecordMapper;
import com.book.xw.common.dal.model.FileRecordDo;
import com.book.xw.core.model.entity.FileRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FileRecordRepository {

    @Autowired
    private FileRecordMapper fileRecordMapper;

    public int saveFileRecord(FileRecord fileRecord){
        FileRecordDo recordDo = new FileRecordDo();
        BeanUtils.copyProperties(fileRecord, recordDo);
        return fileRecordMapper.insert(recordDo);
    }

}
