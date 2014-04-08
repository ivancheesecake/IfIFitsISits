package com.cheesecake.ififitsisits;

public class Project {

	private String projectId;
	private String projectName;
	private String otherFields;
	
	public Project(){
		
		this.projectId = "";
		this.projectName = "";
		this.otherFields = "";
		
	}
	
	public Project(String projectId,String projectName,String otherFields){
		
		this.projectId = projectId;
		this.projectName = projectName;
		this.otherFields = otherFields;
		
	}
	
	public String get_projectId(){
		return this.projectId;
	}
	
	public String get_projectName(){
		return this.projectName;
	}
	
	public String get_otherFields(){
		return this.otherFields;
	}
	
	public void set_projectId(String projectId){
		this.projectId = projectId;
	}
	
	public void set_projectName(String projectName){
		this.projectName = projectName;
	}
	
	public void set_otherFields(String otherFields){
		this.otherFields = otherFields;
	}
	
}
