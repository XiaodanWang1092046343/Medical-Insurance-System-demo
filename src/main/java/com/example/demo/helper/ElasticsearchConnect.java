package com.example.demo.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import net.sf.json.JSONObject;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.IndicesClient;

@Service
public class ElasticsearchConnect {

	@Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
	
	public void consumePlanElastic(String record,IndexRequest request,RestHighLevelClient client) throws IOException{
		JSONObject jsonObj = JSONObject.fromObject(record);
		JSONObject valueObj = jsonObj.getJSONObject("node");
		
		request.id(valueObj.getString("objectId"));
		if(!valueObj.containsKey("planType")) {
			request.routing(jsonObj.getString("parent_id"));
		}
		request.source(valueObj, XContentType.JSON); 

		IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

	}
	
}