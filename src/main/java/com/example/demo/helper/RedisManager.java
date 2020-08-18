package com.example.demo.helper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;

@Service
public class RedisManager {
	
	public static Jedis jedis=new Jedis("127.0.0.1");
	
	//public final Jedis jedis;
	
	@Autowired
	private KafkaPublisher kafkaPublisher;
	
	@Autowired
	private KafkaDeleter kafkaDeleter;
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	Map<String, String> relationMap = new HashMap<String, String>();
	
	public RedisManager() {	
		//this.jedis = new Jedis("127.0.0.1");
	}
	
	public String insertObject(net.sf.json.JSONObject json) {
		//String key="Plan_"+json.getString("objectId");
		String key = "{\r\n \"objectType\":"+"\""+json.getString("objectType")+ "\""+",\r\n \"objectId\":"+"\""+json.getString("objectId")+"\""+"\r\n}";
		if(jedis.exists(key))
			return null;
		jedis.set(key, json.toString());
		
		for(Object k:json.keySet()) {
			String str=k.toString();
			if(json.optJSONObject(str)!=null) 
				insertObject(json.optJSONObject(str));
			if(json.optJSONArray(str)!=null) {
				net.sf.json.JSONArray tempList=json.optJSONArray(str);
				for(int j=0;j<tempList.size();j++) {
					net.sf.json.JSONObject temp=(net.sf.json.JSONObject) tempList.get(j);
					insertObject(temp);
				}
			}
		}
		return key;
		//return "{\n\"objectId\":\""+json.getString("objectId")+"\", \n\"objectType\": \""+json.getString("objectType")+"\"\n}";
	}
	
	/*public String findPlanById(String id) {
		String key="Plan_"+id;
		if(!jedis.exists(key))
			return null;
		return jedis.get(key);
	}
	*/
	
	public String findObjectById(String type,String id) {
		String key = "{\r\n \"objectType\":"+"\""+type+ "\""+",\r\n \"objectId\":"+"\""+id+"\""+"\r\n}";
		if(!jedis.exists(key))
			return null;
		return jedis.get(key);
	}
	
	/*public String findAllPlan(){
		Set<String> set=jedis.keys("*");
		String result="";
		//List<String> result=new ArrayList<>();
		for(String s:set) {
			result+=findPlanById(s.substring(5))+"\n";
			//result.add(findPlanById(s.substring(5)));
		}
		return result;
	}
	*/
	
	public String deleteObject(String type,String id){
		String target=findObjectById(type,id);
		Set<String> set=jedis.keys("*");
		boolean mark=false;
		for(Object s:set) {
			String temp=jedis.get(s.toString());
			if(temp.contains(target)) {
				jedis.del(s.toString());
				mark=true;
			}
		}
		if(mark)
			return "success";
		return "id doesn't exist";
	}
	
    public String deletePlan(String id) {
    	//String key="Plan_"+id;
    	String key = "{\r\n \"objectType\":"+"\""+"plan"+ "\""+",\r\n \"objectId\":"+"\""+id+"\""+"\r\n}";
    	if(!jedis.exists(key))
    		return null;
    	jedis.del(key);
    	return key;
    	//return "{\n\"objectId\":\""+id+"\", \n\"objectType\": \"plan\"\n}";
    }
    
    public void deleteAllPlan() {
    	jedis.flushDB();
    }
    
    public String updatePlan(String id,net.sf.json.JSONObject json) {
    	String key = "{\r\n \"objectType\":"+"\""+"plan"+ "\""+",\r\n \"objectId\":"+"\""+id+"\""+"\r\n}";
		if(!jedis.exists(key))
			return null;
		jedis.set(key, json.toString());
		return key;
    }
    
    public String savePlan(net.sf.json.JSONObject json) {
    	String key = "{\r\n \"objectType\":"+"\""+"plan"+ "\""+",\r\n \"objectId\":"+"\""+json.getString("objectId")+"\""+"\r\n}";
		jedis.set(key, json.toString());
		return key;
    }
    
    public net.sf.json.JSONObject mergeUpdate(Map<Object,Object> update,net.sf.json.JSONObject json) {
    	
    	for(Entry<Object,Object> entry:update.entrySet()) {
    		String k=(String) entry.getKey();
    		if(json.optJSONObject(k)==null&&json.optJSONArray(k)==null)
    			json.put(k, entry.getValue());
    			
    		if(json.optJSONObject(k)!=null) {	
    			net.sf.json.JSONObject pcs=json.optJSONObject(k);
    			@SuppressWarnings("unchecked")
				Map<Object,Object> newPcs=(Map<Object, Object>) update.get(k);
    			mergeUpdate(newPcs,pcs);
    		}
    		
    		if(json.optJSONArray(k)!=null) {
    			JSONArray lps=json.optJSONArray(k);
    	    	@SuppressWarnings("unchecked")
    	    	List<Object> newLps=(List<Object>) update.get(k);
    	    	
    	    	for(int j=0;j<newLps.size();j++) {
    	    		@SuppressWarnings("unchecked")
					Map<Object,Object> temp=(Map<Object, Object>) newLps.get(j);
    	    		mergeUpdate(temp,lps.getJSONObject(j));
    	    	}
    		}
    		
    	}
    	return json;
    }
    
    
    List<String> planId=new ArrayList<>();
   
    
    public boolean indexEachObject(JSONObject jsonObject,String uuid) {
		Map<String, String> simpleMap = new HashMap<String, String>();
		
		for(Object key:jsonObject.keySet()) {
			String attributeKey = String.valueOf(key);
			Object attributeVal = jsonObject.get(String.valueOf(key));
			
			String edge = attributeKey;
			if(attributeVal instanceof JSONObject) {
				JSONObject embdObject = (JSONObject) attributeVal;
				JSONObject joinObj = new JSONObject();
				if(edge.equals("planserviceCostShares")&&embdObject.getString("objectType").equals("membercostshare")) {
					joinObj.put("name", "planservice_membercostshare");
				}
				else
					joinObj.put("name", embdObject.getString("objectType"));
				joinObj.put("parent", uuid);
				embdObject.put("plan_service", joinObj);
			
				JSONObject json = new JSONObject();
				json.put("node", embdObject);
				json.put("parent_id", uuid);
				
				kafkaPublisher.publish("index", json.toString());
				String embd_uuid = embdObject.getString("objectId");
			}else if(attributeVal instanceof JSONArray) {
				JSONArray jsonArray = jsonObject.getJSONArray("linkedPlanServices");
				
				Iterator<Object> jsonIterator = jsonArray.iterator();
		
				while(jsonIterator.hasNext()){
					JSONObject embdObject = (JSONObject) jsonIterator.next();
					//System.out.println(embdObject);
					JSONObject json = new JSONObject();
					json.put("node", embdObject);
					json.put("parent_id", uuid);
					String embd_uuid = embdObject.getString("objectId");
					relationMap.put(embd_uuid, uuid);
					indexEachObject(embdObject,embd_uuid);
				}
				
			}else {
				simpleMap.put(attributeKey,String.valueOf(attributeVal));
				
			}
		}
		JSONObject joinObj = new JSONObject();
		joinObj.put("name", simpleMap.get("objectType"));
		JSONObject obj = new JSONObject();
		if(!simpleMap.containsKey("planType")) {
			joinObj.put("parent", relationMap.get(uuid));
			obj.put("parent_id", relationMap.get(uuid));
		}
			
		JSONObject obj1 = JSONObject.fromObject(simpleMap);
		obj1.put("plan_service", joinObj);
		obj.put("node", obj1);
		
		kafkaPublisher.publish("index", obj.toString());
		return true;
	}
    
    public List<String> getAllObjectId(JSONObject json,List<String> list){
    	list.add(String.valueOf(json.get("objectId")));
    	for(Object k:json.keySet()) {
			String str=k.toString();
			if(json.optJSONObject(str)!=null) 
			    getAllObjectId(json.optJSONObject(str),list);
			if(json.optJSONArray(str)!=null) {
				net.sf.json.JSONArray tempList=json.optJSONArray(str);
				for(int j=0;j<tempList.size();j++) {
					net.sf.json.JSONObject temp=(net.sf.json.JSONObject) tempList.get(j);
					getAllObjectId(temp,list);
				}
			}
		}
    	return list;
    }
    
    public boolean deleteIndexedObject(List<String> list) {
    	//JSONObject json =JSONObject.fromObject(findObjectById(type,uuid));
    	//List<String> list=new ArrayList<>();
    	//list=getAllObjectId(json,list);
    	for(String id:list) {
    		JSONObject obj = new JSONObject();
        	obj.put("uuid", id);
        	kafkaDeleter.publish("deleteElement",obj.toString());
    	}
    	return true;
    }
    
    
    
}
