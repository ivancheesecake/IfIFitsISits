package com.cheesecake.ififitsisits;

import java.io.Serializable;

import android.graphics.Bitmap;

public class IfIFitsExtra implements Serializable{

	private static final long serialVersionUID = 133868465534245470L;

	private int flag;

	private double refObj;
	private double[] measurements;
	
	public IfIFitsExtra(){
		this.flag = 0;

		this.refObj = 0.0;
		this.measurements = new double[9];
		
	}
	
	public int get_flag(){
		
		return this.flag;
	}
	

	public double get_refObj(){
		
		return this.refObj;
	}
	public double[] get_measurements(){
		
		return this.measurements;
	}
	public void set_flag(int flag){
		this.flag = flag;
	}
	public int next_flag(){
		return ++this.flag;
	}

	public void set_refObj(double refObj){
		this.refObj = refObj;
	}
	public void set_measurements(double[] measurements){
		this.measurements = measurements;
	}

	
}
