package com.example.demo.helper;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.helper.ElasticsearchConnect;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sf.json.JSONObject;

@Service
public class KafkaPublisher {
	
	@Autowired
	private KafkaTemplate<String,String> kafkaTemplate; 
	@Autowired
	private ElasticsearchConnect elasticSearchConnect;
	
	public void publish(String topic,String message) {
		ProducerRecord<String, String> record = new ProducerRecord<String, String>("index", "Msg-Async", message); 
		kafkaTemplate.send(record);
	}
	
	RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
	IndexRequest request = new IndexRequest("my_index");
	
	
	@KafkaListener(containerFactory = "kafkaListenerContainerFactory", topics = "index")
    public void consumerListener(String record) throws IOException {
		JSONObject jsonObj = JSONObject.fromObject(record);
		JSONObject valueObj = jsonObj.getJSONObject("node");
		
		request.id(valueObj.getString("objectId"));
		if(!valueObj.containsKey("planType")) {
			request.routing(jsonObj.getString("parent_id"));
		}
		request.source(valueObj, XContentType.JSON); 
		IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
		
		System.out.println("--------------index record------------------");
		System.out.println(record);
    }
	
	
}
