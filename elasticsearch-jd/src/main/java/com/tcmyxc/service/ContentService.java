package com.tcmyxc.service;

import com.alibaba.fastjson.JSON;
import com.tcmyxc.pojo.Product;
import com.tcmyxc.utils.HtmlParseUtil;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetFieldMappingsRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author tcmyxc
 * @date 2021/1/6 20:06
 */
@Service
public class ContentService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //解析数据放入ES索引中
    public Boolean parseContent(String keyword) throws IOException {
        List<Product> contents = new HtmlParseUtil().parseTmall(keyword);
        //System.out.println(contents.toString());
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2s");

        for (int i = 0; i < contents.size(); i++) {
            //System.out.println(JSON.toJSONString(contents.get(i)));
            //索引需要以小写字母开头
            bulkRequest.add(
                    new IndexRequest("tmall_index")
                    .source(JSON.toJSONString(contents.get(i)), XContentType.JSON)
            );
        }

        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

        return !bulk.hasFailures();
    }

    //实现搜索功能
    public List<Map<String, Object>> searchPage(String keyword, int pageNo, int pageSize) throws IOException {
        if(pageNo <= 1){
            pageNo = 1;
        }

        //先判断关键字是否存在于索引库，如果不存在，爬数据，建立索引库
        if(!parseContent(keyword)) {
            return null;
        }

        //条件搜索
        SearchRequest request = new SearchRequest("tmall_index");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //构建高亮，先把高亮放进去，然后再从搜索结果把高亮部分拿出来
        HighlightBuilder highlighter = new HighlightBuilder();
        highlighter.field("productTitle");
        highlighter.preTags("<span style='color:red'>");
        highlighter.postTags("</span");
        searchSourceBuilder.highlighter(highlighter);

        //分页
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);
        //精准匹配
        TermQueryBuilder queryBuilder = QueryBuilders.termQuery("productTitle", keyword);

        searchSourceBuilder.query(queryBuilder)
                .timeout(new TimeValue(30, TimeUnit.SECONDS));

        request.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        //解析结果
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit documentFields : searchResponse.getHits()) {
            //解析高亮的字段
            Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();
            HighlightField title = highlightFields.get("productTitle");
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();//获取原来的结果
            if(title != null){
                //如果高亮字段存在，替换原有的字段
                Text[] fragments = title.fragments();
                String newTitle = "";
                for (Text fragment : fragments) {
                    newTitle += fragment;
                }
                sourceAsMap.put("productTitle", newTitle);
            }

            list.add(sourceAsMap);
        }

        return list;
    }
}
