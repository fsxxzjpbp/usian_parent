package com.usian.service;


import com.github.pagehelper.PageHelper;
import com.usian.mapper.SearchItemMapper;
import com.usian.pojo.SearchItem;
import com.usian.utils.JsonUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchItemServiceImpl implements SearchItemService {

    @Autowired
    private SearchItemMapper searchItemMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Value("${ES_INDEX_NAME}")
    private String ES_INDEX_NAME;

    @Value("${ES_TYPE_NAME}")
    private String ES_TYPE_NAME;

    /**
     * 导入商品数据
     *
     * @return
     */
    @Override
    public Boolean importAll() {
        // 创建index，type，mapping
        // 先判断索引库是否存在
        try {
            if (!isExistsIndex()) {
                createIndex();
            }
            int page = 1;
            while (true) {
                PageHelper.startPage(page, 1000);
                // 查询数据库
                List<SearchItem> searchItemList = searchItemMapper.ListSearchItem();
                if (searchItemList == null || searchItemList.size() == 0) {
                    break;
                }
                // 导入es索引库中
                BulkRequest bulkRequest = new BulkRequest();
                for (SearchItem searchItem : searchItemList) {
                    bulkRequest.add(new IndexRequest(ES_INDEX_NAME, ES_TYPE_NAME, searchItem.getId()).source(JsonUtils.objectToJson(searchItem), XContentType.JSON));
                }
                restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                page++;
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<SearchItem> list(String q, Integer pages, Integer rows) {
        try {
            SearchRequest searchRequest = new SearchRequest(ES_INDEX_NAME);
            searchRequest.types(ES_TYPE_NAME);
            // 根据条件查询
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            String[] fieldNames = new String[]{"item_title", "item_desc", "item_sell_point", "item_category_name"};
            searchSourceBuilder.query(QueryBuilders.multiMatchQuery(q, fieldNames));
            /*
             *  分页
             * page from size
             * 1    0    20
             * 2    20   40
             * 3    40   60
             *
             * from = (page-1)*size
             */
            searchSourceBuilder.from((pages - 1) * rows);
            searchSourceBuilder.size(rows);
            // 高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.preTags("<font color='red'>");
            highlightBuilder.postTags("</font>");
            highlightBuilder.field("item_title");
            searchSourceBuilder.highlighter(highlightBuilder);
            searchRequest.source(searchSourceBuilder);
            // 返回查询结果
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] searchHits = response.getHits().getHits();
            List<SearchItem> searchItemArrayList = new ArrayList<>();
            for (SearchHit searchHit : searchHits) {
                SearchItem searchItem = JsonUtils.jsonToPojo(searchHit.getSourceAsString(), SearchItem.class);

                Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
                if (highlightFields != null && highlightFields.size() > 0) {
                    searchItem.setItem_title(highlightFields.get("item_title").getFragments()[0].toString());
                }
                searchItemArrayList.add(searchItem);
            }
            return searchItemArrayList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int insertDocument(String itemId) throws IOException {
        //根据ID查询
        SearchItem searchItem = searchItemMapper.getSearchItemByItemId(Long.parseLong(itemId));
        // 添加到es中
        IndexRequest indexRequest = new IndexRequest(ES_INDEX_NAME, ES_TYPE_NAME);
        indexRequest.source(JsonUtils.objectToJson(searchItem), XContentType.JSON);
        IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        return response.getShardInfo().getSuccessful();
    }

    /**
     * 创建索引库
     *
     * @throws IOException
     */
    private void createIndex() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(ES_INDEX_NAME);
        createIndexRequest.settings("{\n" +
                "    \"number_of_shards\": 2,\n" +
                "    \"number_of_replicas\": 1\n" +
                "  }", XContentType.JSON);
        createIndexRequest.mapping(ES_TYPE_NAME, "{\n" +
                "  \"_source\": {\n" +
                "    \"excludes\": [\n" +
                "      \"item_desc\"\n" +
                "    ]\n" +
                "  },\n" +
                "  \"properties\": {\n" +
                "    \"id\":{\n" +
                "      \"type\": \"keyword\"\n" +
                "    },\n" +
                "    \"item_title\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\"\n" +
                "    },\n" +
                "    \"item_sell_point\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\"\n" +
                "    },\n" +
                "    \"item_price\": {\n" +
                "      \"type\": \"float\"\n" +
                "    },\n" +
                "    \"item_image\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"index\": false\n" +
                "    },\n" +
                "    \"item_category_name\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\"\n" +
                "    },\n" +
                "    \"item_desc\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\"\n" +
                "    }\n" +
                "  }\n" +
                "}", XContentType.JSON);
        restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
    }

    /**
     * 判断索引库是否存在
     *
     * @return
     * @throws IOException
     */
    private Boolean isExistsIndex() throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest();
        getIndexRequest.indices(ES_INDEX_NAME);
        return restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
    }
}
