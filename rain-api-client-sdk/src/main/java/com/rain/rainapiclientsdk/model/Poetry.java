package com.rain.rainapiclientsdk.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Poetry implements Serializable {
    private String dynasty;

    private String author;

    private static final long serialVersionUID = 1L;
}