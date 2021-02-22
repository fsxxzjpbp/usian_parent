package com.usian.service;

import com.usian.pojo.DeDuplication;

public interface DeDuplicationService {
    DeDuplication selectdeDuplicationByTxNo(String txNo);

    void insertDeDuplication(String txNo);
}
