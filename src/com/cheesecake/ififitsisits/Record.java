package com.cheesecake.ififitsisits;

import java.io.Serializable;


public class Record implements Serializable{
	
	private static final long serialVersionUID = 6325661285945116981L;
	private int id;
	private double height;
	private double weight;
	private int age;
	private String sex;
	private String region;
	private String cityprov;
	private double sitH;
	private double sH;
	private double erH;
	private double tC;
	private double pH;
	private double kH;
	private double bpL;
	private double hB;
	private double kkB;
	private String date;
	private String side_img;
	private String front_img;
	private String back_img;
	
	public Record(){
		
		this.height = 0.0;
	    this.weight = 0.0;
	    this.age = 0;
	    this.sex = "Male";
	    this.region = "Region IV-A";
	    this.cityprov = "Los Banos, Laguna";
	    this.sitH = 0.0;
	    this.sH = 0.0;
	    this.erH = 0.0;
	    this.tC = 0.0;
	    this.pH = 0.0;
	    this.kH = 0.0;
	    this.bpL = 0.0;
	    this.hB = 0.0;
	    this.kkB = 0.0;
	    this.side_img = "NULL";
	    this.front_img = "NULL";
	    this.back_img = "NULL";
		
	}
	
	public Record(int id, double height, double weight, int age, String sex, String region, String cityprov, 
			double sitH, double sH, double erH, double tC, double pH, double kH, double bpL,
			double hB, double kkB,String date, String side_img, String front_img, String back_img) {
		
		super();
	    this.id = id;
	    this.height = height;
	    this.weight = weight;
	    this.age = age;
	    this.sex = sex;
	    this.region = region;
	    this.cityprov = cityprov;
	    this.sitH = sitH;
	    this.sH = sH;
	    this.erH = erH;
	    this.tC = tC;
	    this.pH = pH;
	    this.kH = kH;
	    this.bpL = bpL;
	    this.hB = hB;
	    this.kkB = kkB;
	    this.date = date;
	    this.side_img = side_img;
	    this.front_img = front_img;
	    this.back_img = back_img;
	    }
	
	public Record(double height, double weight, int age, String sex, String region, String cityprov,
			double sitH, double sH, double erH, double tC, double pH, double kH, double bpL,
			double hB, double kkB, String side_img, String front_img, String back_img) {
		
		super();
	    
	    this.height = height;
	    this.weight = weight;
	    this.age = age;
	    this.sex = sex;
	    this.region = region;
	    this.cityprov = cityprov;
	    this.sitH = sitH;
	    this.sH = sH;
	    this.erH = erH;
	    this.tC = tC;
	    this.pH = pH;
	    this.kH = kH;
	    this.bpL = bpL;
	    this.hB = hB;
	    this.kkB = kkB;
	    this.side_img = side_img;
	    this.front_img = front_img;
	    this.back_img = back_img;
	    }
	
	//Getter methods
	
	public int get_id(){
		return this.id;
	}
	public double get_height(){
		return this.height;
	}
	public double get_weight(){
		return this.weight;
	}
	public int get_age(){
		return this.age;
	}
	public String get_sex(){
		return this.sex;
	}
	public String get_region(){
		return this.region;
	}
	public String get_cityprov(){
		return this.cityprov;
	}
	public double get_sitH(){
		return this.sitH;
	}
	public double get_sH(){
		return this.sH;
	}
	public double get_erH(){
		return this.erH;
	}
	public double get_tC(){
		return this.tC;
	}
	public double get_pH(){
		return this.pH;
	}
	public double get_kH(){
		return this.kH;
	}
	public double get_bpL(){
		return this.bpL;
	}
	public double get_hB(){
		return this.hB;
	}
	public double get_kkB(){
		return this.kkB;
	}
	public String get_date(){
		return this.date;
	}
	public String get_sideImg(){
		return this.side_img;
	}
	public String get_frontImg(){
		return this.front_img;
	}
	public String get_backImg(){
		return this.back_img;
	}
	
	//Setter Methods
	public void set_id(int id){
		this.id = id;
	}
	public void set_height(double height){
		this.height = height;
	}
	public void set_weight(double weight){
		this.weight = weight;
	}
	public void set_age(int age){
		this.age = age;
	}
	public void set_sex(String sex){
		this.sex = sex;
	}
	public void set_region(String region){
		this.region = region;
	}
	public void set_cityprov(String cityprov){
		this.cityprov = cityprov;
	}
	public void set_sitH(double sitH){
		this.sitH = sitH;
	}
	public void set_sH(double sH){
		this.sH = sH;
	}
	public void set_erH(double erH){
		this.erH = erH;
	}
	public void set_tC(double tC){
		this.tC = tC;
	}
	public void set_pH(double pH){
		this.pH = pH;
	}
	public void set_kH(double kH){
		this.kH = kH;
	}
	public void set_bpL(double bpL){
		this.bpL = bpL;
	}
	public void set_hB(double hB){
		this.hB = hB;
	}
	public void set_kkB(double kkB){
		this.kkB = kkB;
	}
	public void set_date(String date){
		this.date = date;
	}
	public void set_sideImg(String side_img){
		this.side_img = side_img;
	}
	public void set_frontImg(String front_img){
		this.front_img = front_img;
	}
	public void set_backImg(String back_img){
		this.back_img = back_img;
	}
	
	@Override
    public String toString() {
        return  this.id+"";
    }
	
}
