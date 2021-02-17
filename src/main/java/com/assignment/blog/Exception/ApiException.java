package com.assignment.blog.Exception;

import lombok.Data;

import java.util.Date;
@Data
public class ApiException {

    private Date timestamp;
    private String message;
    private String details;

    public ApiException(Date timestamp , String message , String details) {
        super();
        this.timestamp = timestamp;
        this.message   = message;
        this.details   = details;
    }
}
