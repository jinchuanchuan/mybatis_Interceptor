package com.java.mmzsit.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: TableColumnDTO.java
 * @Author: jinchuanchuan@longfor.com
 * @Date: 2022/1/11 6:26 下午:00
 * @Description: 脱敏表与字段DTO
 */
@Data
public class TableColumnDTO implements Serializable {

    static final long serialVersionUID = 5258023939705867591L;

    private String tableName;

    private List<String> columns;
}
