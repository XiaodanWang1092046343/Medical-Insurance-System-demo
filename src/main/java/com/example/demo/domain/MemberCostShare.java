package com.example.demo.domain;

public class MemberCostShare {

	private int deductible;
	private String _org;
	private int copay;
	private String objectId;
	private String objectType;
	
	
	public MemberCostShare(int deductible, String _org, int copay, String objectId, String objectType) {
		this.deductible = deductible;
		this._org = _org;
		this.copay = copay;
		this.objectId = objectId;
		this.objectType = objectType;
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



	public int getDeductible() {
		return deductible;
	}
	public void setDeductible(int deductible) {
		this.deductible = deductible;
	}
	public int getCopay() {
		return copay;
	}
	public void setCopay(int copay) {
		this.copay = copay;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	

}
