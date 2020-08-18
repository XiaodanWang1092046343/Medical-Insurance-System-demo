package com.example.demo.domain;

import java.util.List;

public class Plan {
	private MemberCostShare planCostShares;
	private List<PlanService> linkedPlanServices;
	private String _org;
	private String objectId;
	private String objectType;
	private String planType;
	private String creationDate;
	
	
	public Plan(MemberCostShare planCostShares, List<PlanService> linkedPlanServices, String _org, String objectId,
			String objectType, String planType, String creationDate) {
		super();
		this.planCostShares = planCostShares;
		this.linkedPlanServices = linkedPlanServices;
		this._org = _org;
		this.objectId = objectId;
		this.objectType = objectType;
		this.planType = planType;
		this.creationDate = creationDate;
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

	public String getPlanType() {
		return planType;
	}

	public void setPlanType(String planType) {
		this.planType = planType;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public MemberCostShare getPlanCostShares() {
		return planCostShares;
	}

	public void setPlanCostShares(MemberCostShare planCostShares) {
		this.planCostShares = planCostShares;
	}

	public List<PlanService> getLinkedPlanServices() {
		return linkedPlanServices;
	}

	public void setLinkedPlanServices(List<PlanService> linkedPlanServices) {
		this.linkedPlanServices = linkedPlanServices;
	}

}
