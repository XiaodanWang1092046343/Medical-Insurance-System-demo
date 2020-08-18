package com.example.demo.controller;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.helper.*;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;

@RestController
//@RequestMapping("/")
public class Controller {
	    private static Controller controller;
	    private Validator valid=new Validator();
	    private TokenManager tokenVerifier=new TokenManager();
	    private List<String> etags=new ArrayList<>();
	    		
	    //private RedisManager redis=new RedisManager();
	    
	    private static Jedis jedis=new Jedis("127.0.0.1");
	    
	    @Autowired
	    private RedisManager redis;
	
	    @RequestMapping(value = "/{type}/{id}", method = RequestMethod.GET)
	    public ResponseEntity<String> findObject(@RequestHeader HttpHeaders header,@PathVariable("type") String type,@PathVariable("id") String id) {
	    	//verify token
	    	String token=header.getFirst("Authorization");
	    	try {
	    	    if(!tokenVerifier.verifyToken(token))
					return new ResponseEntity<>("Invalid token!", HttpStatus.BAD_REQUEST);
	    	}
	    	catch (Exception e){
	    		return new ResponseEntity<>("Invalid token!", HttpStatus.BAD_REQUEST);
	    	}
	    	
	    	//search operation
	    	String result=redis.findObjectById(type,id);
	    	if(result==null) 
	    		return ResponseEntity.badRequest()
			            .body("The object you want to find does not exist.");
	    	/*if(header.getIfNoneMatch()==null)
	    		etag.add(header.getETag());
	    	else if(etag.contains(header.getETag()))
	    		return new ResponseEntity<>("Not modified", HttpStatus.NOT_MODIFIED);
	    		*/
	    	etags.add(header.getFirst("If-None-Match"));
	    	//HttpHeaders responseHeaders = new HttpHeaders();
	    	return new ResponseEntity<>(result, HttpStatus.OK);
	    }
	    
	    /*
	    @RequestMapping(value = "/plan", method = RequestMethod.GET)
	    public ResponseEntity findAllPlan() {
	    	return new ResponseEntity<>(redis.findAllPlan(), HttpStatus.OK);
	    }
	    */
	    
	    
	    @RequestMapping(value = "/plan/{id}", method = RequestMethod.PUT)
	    public ResponseEntity<String> updatePlan(@RequestHeader HttpHeaders header,@RequestBody String jsonString,@PathVariable("id") String id) {
	    	//verify token
	    	String token=header.getFirst("Authorization");
	    	try {
	    	    if(!tokenVerifier.verifyToken(token))
					return new ResponseEntity<>("Invalid token!", HttpStatus.BAD_REQUEST);
	    	}
	    	catch (Exception e){
	    		return new ResponseEntity<>("Invalid token!", HttpStatus.BAD_REQUEST);
	    	}
	    	
	    	String lastEtag="";
	    	if(etags.size()>0)
	    	    lastEtag=etags.get(etags.size()-1);
	    	if(header.getFirst("If-Match")!=null&&(!header.getFirst("If-Match").equals(lastEtag))) {
	    		//System.out.println(lastEtag);
	    		//System.out.println(header.getFirst("If-Match"));
	    		return new ResponseEntity<>("Someone changed this plan.", HttpStatus.PRECONDITION_FAILED);
	    	}
	    	
	    	//schema validation
	    	try {
	    		if(!valid.validation(jsonString))
	    			return new ResponseEntity<>("Validation failed.\nPost failed.", HttpStatus.BAD_REQUEST);;
	    	}catch(Exception e) {
	    		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	    	}
	    	//update operation
	    	JSONObject json=JSONObject.fromObject(jsonString);
	    	String result=redis.updatePlan(id,json);
	        if(result==null)
	        	return new ResponseEntity<>("The plan you want to update does not exist.\nUpdate failed.", HttpStatus.BAD_REQUEST);
	        redis.indexEachObject(json,id);
	        return new ResponseEntity<>("Successful update.\n"+result, HttpStatus.OK);
	    	
	    }
	    
		@RequestMapping(value = "/plan/{id}", method = RequestMethod.PATCH)
	    public ResponseEntity<String> mergeUpdatePlan(@RequestHeader HttpHeaders header,@RequestBody Map<Object,Object> update,@PathVariable("id") String id) throws GeneralSecurityException, IOException {
			//verify token
	    	String token=header.getFirst("Authorization");
	    	try {
	    	    if(!tokenVerifier.verifyToken(token))
					return new ResponseEntity<>("Invalid token!", HttpStatus.BAD_REQUEST);
	    	}
	    	catch (Exception e){
	    		return new ResponseEntity<>("Invalid token!", HttpStatus.BAD_REQUEST);
	    	}
	    	
	    	String lastEtag="";
	    	if(etags.size()>0)
	    	    lastEtag=etags.get(etags.size()-1);
	    	if(header.getFirst("If-Match")!=null&&(!header.getFirst("If-Match").equals(lastEtag))) {
	    		//System.out.println(lastEtag);
	    		//System.out.println(header.getFirst("If-Match"));
	    		return new ResponseEntity<>("Someone changed this plan.", HttpStatus.PRECONDITION_FAILED);
	    	}
	    	
	    	//check planID
	    	String targetPlan=redis.findObjectById("plan",id);
	    	if(targetPlan==null) 
	    		return ResponseEntity.badRequest().body("The plan you want to update does not exist.\nUpdate failed.");
	    	else if(update==null)
	    		return new ResponseEntity<>("Empty payground!",HttpStatus.NOT_MODIFIED);
	    	//update operation
	    	JSONObject json=JSONObject.fromObject(targetPlan);
	    	redis.mergeUpdate(update, json);
	    	redis.savePlan(json);
	    	redis.indexEachObject(json,id);
	    	String key = "{\r\n \"objectType\":"+"\""+"plan"+ "\""+",\r\n \"objectId\":"+"\""+id+"\""+"\r\n}";
	        return new ResponseEntity<>("Successful update.\n"+key, HttpStatus.OK);
	    	
	    }

	    @RequestMapping(value = "/plan", method = RequestMethod.POST)
	    public ResponseEntity<String> createPlan(@RequestHeader HttpHeaders header,@RequestBody String jsonString) {
	    //public ResponseEntity<String> createPlan(@RequestBody Plan plan) {
	    	/*if(plan==null)
	    		return new ResponseEntity<>("Empty document. Post failed.", HttpStatus.BAD_REQUEST);
	    	if(plan.getObjectId()==null)
	    		return new ResponseEntity<>("Bad request. OjectId cannot be empty.", HttpStatus.BAD_REQUEST);
	    	
	    	JSONObject json=JSONObject.fromObject(plan);
	    	*/
	    	//String jsonString=json.toString();
	    	
	    	//verify token
	    	String token=header.getFirst("Authorization");
	    	try {
	    	    if(!tokenVerifier.verifyToken(token))
					return new ResponseEntity<>("Invalid token!", HttpStatus.BAD_REQUEST);
	    	}
	    	catch (Exception e){
	    		return new ResponseEntity<>("Invalid token!", HttpStatus.BAD_REQUEST);
	    	}
	    	
	    	//schema validation
	    	try {
	    		if(!valid.validation(jsonString))
	    			return new ResponseEntity<>("Validation failed.\nPost failed.", HttpStatus.BAD_REQUEST);;
	    	}catch(Exception e) {
	    		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	    	}
	    	//String validationResult=valid.validation(json);
	    	//if(validationResult==null)
	    	//	return new ResponseEntity<>("Validation failed.\nPost failed.", HttpStatus.BAD_REQUEST);;

	    	//insert operation
	    	JSONObject json=JSONObject.fromObject(jsonString);
	    	
	        String result=redis.insertObject(json);
	        if(result==null)
	        	return new ResponseEntity<>("Replicate plan id.\nPost failed.", HttpStatus.BAD_REQUEST);
	        redis.indexEachObject(json, String.valueOf(json.get("objectId")));
	        //redis.indexObject(String.valueOf(json.get("objectId")), json);
	        return new ResponseEntity<>("Successful post.\n"+result, HttpStatus.CREATED);
	    }

	    @RequestMapping(value = "/{type}/{id}", method = RequestMethod.DELETE)
	    public ResponseEntity<String> modifyPlan(@RequestHeader HttpHeaders header,@PathVariable("type") String type,@PathVariable("id") String id) {
	    	//verify token
	    	String token=header.getFirst("Authorization");
	    	try {
	    	    if(!tokenVerifier.verifyToken(token))
					return new ResponseEntity<>("Invalid token!", HttpStatus.BAD_REQUEST);
	    	}
	    	catch (Exception e){
	    		return new ResponseEntity<>("Invalid token!", HttpStatus.BAD_REQUEST);
	    	}
	    	
	    	//delete operation
	    	JSONObject json =JSONObject.fromObject(redis.findObjectById(type,id));
	    	List<String> list=new ArrayList<>();
	    	list=redis.getAllObjectId(json,list);
	    	String result=redis.deleteObject(type, id);
	    	redis.deleteIndexedObject(list);
	    	//redis.deleteIndexedObject(type,id);
	    	
	    	
	    	if(result=="success")
	    		return new ResponseEntity<String>(result, HttpStatus.NO_CONTENT);
	    	else
	    		return new ResponseEntity<>("Object id does not exist.", HttpStatus.BAD_REQUEST);
	    	//String result=redis.deletePlan(id);
	    	//if(result==null)
	    	//	return new ResponseEntity<>("Plan id does not exist.", HttpStatus.BAD_REQUEST);
	    	//return new ResponseEntity<>("Successfully delete.\n"+result, HttpStatus.NO_CONTENT);
	    }
	    
	    
	    
}
