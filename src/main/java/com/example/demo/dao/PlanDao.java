package com.example.demo.dao;

import java.util.List;

import com.example.demo.domain.Plan;

public class PlanDao {
	//private static Map<String,Plan> plans;
	private static List<Plan> plans;
	
	/*static {
		plans=new ArrayList<Plan>();
		MemberCostShare mcs1=new MemberCostShare(2000,23,"1234vxc2324sdf-501");
		MemberCostShare mcs2=new MemberCostShare(10,0,"1234512xvc1314asdfs-503");
		MemberCostShare mcs3=new MemberCostShare(10,175,"1234512xvc1314sdfsd-506");
		Service s1=new Service("1234520xvc30asdf-502","Yearly physical");
		Service s2=new Service("1234520xvc30sfs-505","well baby");
		PlanService ps1=new PlanService("27283xvx9asdff-504",s1,mcs2);
		PlanService ps2=new PlanService("27283xvx9sdf-507",s2,mcs3);
		List<PlanService> lps=new ArrayList<PlanService>();
		lps.add(ps1);
		lps.add(ps2);
		Plan p=new Plan("12xvxc345ssdsds-508","inNetwork","12-12-2017",mcs1,lps);	
		plans.add(p);
	}
	*/
	

	public Plan findPlanById(String id) {
		for(Plan plan:plans) {
			if(plan.getObjectId().equals(id))
				return plan;
		}
		return null;
	}
	public List<Plan> findAllPlan(){
		return plans;
	}
	
    public String savePlan(Plan plan) {
    	plans.add(plan);
    	//return plan.toString();
    	return plan.getObjectId();
    }
    
    public Plan deletePlan(String id) {
    	for(Plan plan:plans) {
			if(plan.getObjectId().equals(id)) {
				plans.remove(plan);
				return plan;
			}
		}
    	return null;
    }
	
	
}
