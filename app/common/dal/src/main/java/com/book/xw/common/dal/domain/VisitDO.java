package com.book.xw.common.dal.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name="visit")
public class VisitDO implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Integer visits;

    @Column
    private Date gmtCreate;

    @Column
    private Date gmtModified;
}
