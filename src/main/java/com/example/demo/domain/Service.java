package com.example.demo.domain;

public class Service {
	private String _org;
	private String objectId;
	private String objectType;
	private String name;
	
	
	public Service(String _org, String objectId, String objectType, String name) {
		super();
		this._org = _org;
		this.objectId = objectId;
		this.objectType = objectType;
		this.name = name;
	}
	
	
	public String get_org() {
		return _org;
	}


	public void set_org(String _org) {
		this._org = _org;
	}


	public String getObjectType() {
		return objectType;
	}


	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}


	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
