/*
 * 
 *	DatabaseHelper.java
 *	Description: This class allows basic CR(U)D operations on the local SQLite database. 
 *  Author: Escamos, Ivan Marc H. 
 *  Date last modified: 04/10/14
 * 	
 * 	Received help from: 
 * 	http://hmkcode.com/android-simple-sqlite-database-tutorial/
 * 	http://stackoverflow.com/questions/7575166/android-sqlite-get-last-insert-row-id
 * */

package com.cheesecake.ififitsisits;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {		//Start of class DatabaseHelper


    private static final String TABLE_RECORD = "record";					//Table names
    private static final String TABLE_PROJECT = "project";
    private static final String TABLE_UPLOADQUEUE = "upload_queue";
    
    private static final String KEY_ID = "id";				//Column names
    private static final String KEY_HEIGHT = "height";
    private static final String KEY_WEIGHT = "weight";
    private static final String KEY_AGE = "age";
    private static final String KEY_SEX = "sex";
    private static final String KEY_REGION = "region";
    private static final String KEY_CITYPROV = "cityprov";
    private static final String KEY_SITH = "sitH";
    private static final String KEY_SH = "sH";
    private static final String KEY_ERH = "erH";
    private static final String KEY_TC = "tC";
    private static final String KEY_PH = "pH";
    private static final String KEY_KH = "kH";
    private static final String KEY_BPL = "bpL";
    private static final String KEY_HB = "hB";
    private static final String KEY_KKB = "kkB";
    private static final String KEY_SIDEIMG = "side_img";
    private static final String KEY_FRONTIMG = "front_img";
    private static final String KEY_BACKIMG = "back_img";
    private static final String KEY_PROJECTID = "project_id";
    private static final String KEY_PROJECTNAME = "project_name";
    private static final String KEY_OTHERFIELDS = "other_fields";
    
    private LinkedList<Integer> queue;
  
    private static final int DATABASE_VERSION = 5;

    private static final String DATABASE_NAME = "RecordsDB";

    public DatabaseHelper(Context context) {				//constructor for DatabaseHelper
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }
    
    
    /*
     * Creation of the database
     * 
     * */
    public void onCreate(SQLiteDatabase db) {
        
        String CREATE_RECORD_TABLE = "CREATE TABLE record ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
                "height DOUBLE, "+
                "weight DOUBLE, "+ 
                "age INTEGER, "+
                "sex TEXT, "+
                "region TEXT, "+
                "cityprov TEXT, "+
                "sitH DOUBLE, "+
                "sH DOUBLE, "+
                "erH DOUBLE, "+
                "tC DOUBLE, "+
                "pH DOUBLE, "+
                "kH DOUBLE, "+
                "bpL DOUBLE, "+
                "hB DOUBLE, "+
                "kkB DOUBLE, "+
                "side_img TEXT, "+
                "front_img TEXT, "+
                "back_img TEXT, "+
                "project_id TEXT, "+
                "other_fields TEXT, "+
                "date DATETIME DEFAULT CURRENT_DATE"+
                ")";
        String CREATE_UPLOADQUEUE_TABLE = "CREATE TABLE upload_queue (id INTEGER, FOREIGN KEY(id) REFERENCES record(id))";
        String CREATE_PROJECT_TABLE = "CREATE TABLE project (project_id TEXT, project_name TEXT, other_fields TEXT)";
        db.execSQL(CREATE_RECORD_TABLE);
        db.execSQL(CREATE_UPLOADQUEUE_TABLE);
        db.execSQL(CREATE_PROJECT_TABLE);
        
        
    }
    
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS record");
        db.execSQL("DROP TABLE IF EXISTS upload_queue");
        db.execSQL("DROP TABLE IF EXISTS project");
        this.onCreate(db);
    }
    
    /*
     * Function that adds a record to the table "record"
     * 
     * */
    public void addRecord(Record record){
    
    	SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_HEIGHT, record.get_height());
        values.put(KEY_WEIGHT, record.get_weight());
        values.put(KEY_AGE, record.get_age());
        values.put(KEY_SEX, record.get_sex());
        values.put(KEY_REGION, record.get_region());
        values.put(KEY_CITYPROV, record.get_cityprov());
        values.put(KEY_SITH, record.get_sitH());
        values.put(KEY_SH, record.get_sH());
        values.put(KEY_ERH, record.get_erH());
        values.put(KEY_TC, record.get_tC());
        values.put(KEY_PH, record.get_pH());
        values.put(KEY_KH, record.get_kH());
        values.put(KEY_BPL, record.get_bpL());
        values.put(KEY_HB, record.get_hB());
        values.put(KEY_KKB, record.get_kkB());
        values.put(KEY_SIDEIMG, record.get_sideImg());
        values.put(KEY_FRONTIMG, record.get_frontImg());
        values.put(KEY_BACKIMG, record.get_backImg());
        values.put(KEY_PROJECTID, record.get_projectId());
        values.put(KEY_OTHERFIELDS, record.get_otherFields());
       
        db.insert(TABLE_RECORD, null, values); 
 
        db.close(); 
    }
  
    /*
     * Function for retrieving all rows from the table "record" that has a specific projectId
     * 
     * */
    public List<Record> getAllRecords(String projectId) {
    	Log.d("Current Records","");
        List<Record> records = new LinkedList<Record>();
  
  
        String query = "SELECT  * FROM " + TABLE_RECORD + " WHERE project_id='" +projectId + "' ORDER BY id DESC" ;
  
   
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
  
        Record record = null;
        if (cursor.moveToFirst()) {
        	
            do {
                record = new Record();
                record.set_id(Integer.parseInt(cursor.getString(0)));
                record.set_height(Double.parseDouble(cursor.getString(1)));
                record.set_weight(Double.parseDouble(cursor.getString(2)));
                record.set_age(Integer.parseInt(cursor.getString(3)));
                record.set_sex(cursor.getString(4));
                record.set_region(cursor.getString(5));
                record.set_cityprov(cursor.getString(6));
                record.set_sitH(Double.parseDouble(cursor.getString(7)));
                record.set_sH(Double.parseDouble(cursor.getString(8)));
                record.set_erH(Double.parseDouble(cursor.getString(9)));
                record.set_tC(Double.parseDouble(cursor.getString(10)));
                record.set_pH(Double.parseDouble(cursor.getString(11)));
                record.set_kH(Double.parseDouble(cursor.getString(12)));
                record.set_bpL(Double.parseDouble(cursor.getString(13)));
                record.set_hB(Double.parseDouble(cursor.getString(14)));
                record.set_kkB(Double.parseDouble(cursor.getString(15)));
                record.set_sideImg(cursor.getString(16));
                record.set_frontImg(cursor.getString(17));
                record.set_backImg(cursor.getString(18));
                record.set_projectId(cursor.getString(19));
                record.set_otherFields(cursor.getString(20));
                record.set_date(cursor.getString(21));
                Log.d(record.get_date(),""+record.get_id());
                records.add(record);
            } while (cursor.moveToNext());
        }
        db.close(); 
        return records;
    }
    
    /*
     * Function for retrieving a row with a specified id from the table "record"
     * 
     * */ 
    public Record getRecord(int id) {

        String query = "SELECT  * FROM " + TABLE_RECORD + " WHERE id="+id+ " ORDER BY id DESC";
  
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
  
        Record record = null;
        if (cursor.moveToFirst()) {
        	
            do {
                record = new Record();
                record.set_id(Integer.parseInt(cursor.getString(0)));
                record.set_height(Double.parseDouble(cursor.getString(1)));
                record.set_weight(Double.parseDouble(cursor.getString(2)));
                record.set_age(Integer.parseInt(cursor.getString(3)));
                record.set_sex(cursor.getString(4));
                record.set_region(cursor.getString(5));
                record.set_cityprov(cursor.getString(6));
                record.set_sitH(Double.parseDouble(cursor.getString(7)));
                record.set_sH(Double.parseDouble(cursor.getString(8)));
                record.set_erH(Double.parseDouble(cursor.getString(9)));
                record.set_tC(Double.parseDouble(cursor.getString(10)));
                record.set_pH(Double.parseDouble(cursor.getString(11)));
                record.set_kH(Double.parseDouble(cursor.getString(12)));
                record.set_bpL(Double.parseDouble(cursor.getString(13)));
                record.set_hB(Double.parseDouble(cursor.getString(14)));
                record.set_kkB(Double.parseDouble(cursor.getString(15)));
                record.set_sideImg(cursor.getString(16));
                record.set_frontImg(cursor.getString(17));
                record.set_backImg(cursor.getString(18));
                record.set_projectId(cursor.getString(19));
                record.set_otherFields(cursor.getString(20));
                record.set_date(cursor.getString(21));
                Log.d(record.get_date(),""+record.get_id());

            } while (cursor.moveToNext());
        }
        db.close(); 
        return record;
    }
    
    /*
     * Function that inserts ids to the upload queue
     * 
     * */
    public void enqueueUpload(){
    	
    	long lastId=0;
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	String query = "SELECT id from record order by id DESC limit 1";
    	Cursor c = db.rawQuery(query,null);
    	if (c != null && c.moveToFirst()) {
    	   lastId = c.getLong(0);
    	}
    	
        ContentValues values = new ContentValues();
        values.put(KEY_ID, (int)lastId);
        
        db.insert(TABLE_UPLOADQUEUE,null,values); 
        db.close(); 
        
    }
    
    /*
     * Function that retrieves the most recent id 
     * 
     * */
    public int getLastId(){
    	
    	long lastId=0;
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	String query = "SELECT id from record order by id DESC limit 1";
    	Cursor c = db.rawQuery(query,null);
    	if (c != null && c.moveToFirst()) {
    	   lastId = c.getLong(0);
    	}
    	
    	db.close();
    	
    	return (int) lastId;
    		
    }
    
    /*
     * Function that retrieves the current upload queue
     * 
     * */
    public LinkedList<Integer> getQueue(){

        String query = "SELECT  * FROM " + TABLE_UPLOADQUEUE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        queue = new LinkedList<Integer>();
        
        if (cursor.moveToFirst()) {
            do {
            	queue.add(Integer.parseInt(cursor.getString(0)));
            	
            } while (cursor.moveToNext());
        }

        db.close(); 
        
        return queue;
    }
    
    /*
     * Function that removes items form the upload queue
     * 
     * */
    public void dequeue(int id){
    	
        SQLiteDatabase db = this.getWritableDatabase();
       
        db.delete(TABLE_UPLOADQUEUE, KEY_ID+" = ?", new String[] { id+""}); 
        db.close();

        Log.d("Dequeued: ", id+"");
    }
    
    /*
     * Function that deletes a row from the table "project" and deletes associated records
     * 
     * */
    public void deleteProject(String projectId){
    	
    	SQLiteDatabase db = this.getWritableDatabase();
    	
    	List<Record> records = getAllRecords(projectId);
    	
    	for(Record r: records){
    		db = this.getWritableDatabase();
    		//add deletion for img3
    		File img = new File(Environment.getExternalStorageDirectory() + "/ififits/"+r.get_sideImg());
    		File img2 = new File(Environment.getExternalStorageDirectory() + "/ififits/"+r.get_backImg());
    		File img3 = new File(Environment.getExternalStorageDirectory() + "/ififits/"+r.get_frontImg());
    		img.delete();
    		img2.delete();
    		img3.delete();
    		db.delete(TABLE_UPLOADQUEUE, KEY_ID+" = ?", new String[] { r.get_id()+""});  //dequeue
    		db.delete(TABLE_RECORD, KEY_ID+" = ?", new String[] { r.get_id()+""});  //dequeue
    		db.close();
 
    	}
    	db = this.getWritableDatabase();
    	db.delete(TABLE_PROJECT, KEY_PROJECTID+" = ?", new String[] {projectId});  //dequeue
    	db.close();
  
    }
    
    
    /*
     * Function that adds data to the table "project"
     * 
     * */   
    public void addProject(Project project){
    
        
    	SQLiteDatabase db = this.getWritableDatabase();
 

        ContentValues values = new ContentValues();
        values.put(KEY_PROJECTID, project.get_projectId());
        values.put(KEY_PROJECTNAME, project.get_projectName());
        values.put(KEY_OTHERFIELDS, project.get_otherFields());
      
        db.insert(TABLE_PROJECT, null, values); 
 

        db.close(); 
    }
 
    /*
     * Function that retrieves all data from the table "projects"
     * 
     * */   
    public List<Project> getAllProjects() {
 	 
	 Log.d("Current Records","");
     List<Project> projects = new LinkedList<Project>();


     String query = "SELECT  * FROM " + TABLE_PROJECT;


     SQLiteDatabase db = this.getWritableDatabase();
        
     Cursor cursor = db.rawQuery(query, null);

     Project project = null;
     if (cursor.moveToFirst()) {
     	
         do {
             project = new Project();
             project.set_projectId(cursor.getString(0));
             project.set_projectName(cursor.getString(1));
             project.set_otherFields(cursor.getString(2));
             projects.add(project);
             
         } while (cursor.moveToNext());
     }
     db.close(); 
     return projects;
 }
    
}
