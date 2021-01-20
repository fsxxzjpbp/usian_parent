package com.usian.mapper;

import com.usian.pojo.SearchItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchItemMapper {
    List<SearchItem> ListSearchItem();

    SearchItem getSearchItemByItemId(Long itemId);
}
