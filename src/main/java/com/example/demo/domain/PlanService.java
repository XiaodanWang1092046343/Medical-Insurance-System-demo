package com.example.demo.domain;

public class PlanService {
	private Service linkedService;
	private MemberCostShare planserviceCostShares;
	private String _org;
	private String objectId;
	private String objectType;

	public PlanService(Service linkedService, MemberCostShare planserviceCostShares, String _org, String objectId,
			String objectType) {
		this.linkedService = linkedService;
		this.planserviceCostShares = planserviceCostShares;
		this._org = _org;
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



	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public Service getLinkedService() {
		return linkedService;
	}
	public void setLinkedService(Service linkedServide) {
		this.linkedService = linkedServide;
	}
	public MemberCostShare getPlanserviceCostShares() {
		return planserviceCostShares;
	}
	public void setPlanserviceCostShares(MemberCostShare planserviceCostShares) {
		this.planserviceCostShares = planserviceCostShares;
	}
	
}
