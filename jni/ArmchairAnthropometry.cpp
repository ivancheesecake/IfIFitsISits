/*

ArmchairAnthropometry.cpp
Descripiton: Contains native functions for deriving anthropometric data using digital image processing.
Author: Escamos, Ivan Marc H.
Date last modified: 04/10/2014

*/

#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <android/log.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <iostream>

using namespace std;
using namespace cv;

extern "C" {

	float euclideanDistance2d(Point A, Point B);			//function prototypes
	void midPoint(Point *midpoint, Point a, Point b);
	void measureSide(Mat & src, Mat & out, double *measurements, double actual_dimensions);
	void measureFront(Mat & src, Mat & out, double *measurements,double actual_dimensions);
	void measureBack(Mat & src, Mat & out, double *measurements,double actual_dimensions);

	JNIEXPORT jint JNICALL Java_com_cheesecake_ififitsisits_DisplayProcessActivity_DeriveData(JNIEnv *env, jobject obj, jlong src, jlong out, jdoubleArray arr, jdouble dimensions, jint flag);

	JNIEXPORT jint JNICALL Java_com_cheesecake_ififitsisits_DisplayProcessActivity_DeriveData(JNIEnv *env, jobject obj, jlong src, jlong out, jdoubleArray arr, jdouble dimensions, jint flag){

	Mat & binaryMat = *(Mat*)src;
	Mat & img = *(Mat*)out;

    jdouble *measurementsArray;
    measurementsArray = env->GetDoubleArrayElements(arr, NULL);
    
     // do some exception checking
     if (measurementsArray == NULL) {
         return -1; /* exception occurred */
     }	

     if(flag==0)			//call appropriate function based on current flag
     	measureSide(binaryMat,img,measurementsArray,dimensions);
     else if(flag==1)
     	measureBack(binaryMat,img,measurementsArray,dimensions);
     else
     	measureFront(binaryMat,img,measurementsArray,dimensions);	

     env->ReleaseDoubleArrayElements(arr, measurementsArray, 0);		//return the float array to java

	}

	void measureSide(Mat & src, Mat & out, double *measurements, double actual_dimensions){		//function for deriving anthropometric data in the side image

	Mat & binaryMat = *(&src);
	Mat & img = *(&out);
	Mat binaryMat2;

    binaryMat.copyTo(binaryMat2);

    char output[50];
   	sprintf(output,"%f",actual_dimensions);
	__android_log_write(ANDROID_LOG_INFO, "Measure", "Side");

	/*
	=========== Find the Subject and the Reference Object ==============
	*/

	// Find Contours
	cout << "Detecting Subject and Reference Object..." << endl;
	vector<vector<Point> > contours;
	vector<Vec4i> hierarchy;
	findContours( binaryMat, contours, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE, Point(0, 0) );

	/// Approximate contours to polygons + get bounding rects and circles

	if(contours.size()!=0){
		vector<vector<Point> > contours_poly( contours.size() );
		vector<Rect> boundRect( contours.size() );
		vector<Point2f>center( contours.size() );
		vector<float>radius( contours.size() );

		// Detect and draw bounding boxes

		for( int i = 0; i < contours.size(); i++ ){ 
			approxPolyDP( Mat(contours[i]), contours_poly[i], 3, true );
			boundRect[i] = boundingRect( Mat(contours_poly[i]) );
		}

		Mat drawing = Mat::zeros( binaryMat.size(), CV_8UC3 );
		
		Scalar black = Scalar( 0,0,0);
		Scalar white = Scalar( 255,255,255);

		
		// Identify subject and reference object
		int maxarea =0, biggest =0,next=0;
		for(int i=0; i<contours.size(); i++){

			if(boundRect[i].area() > maxarea){

				biggest = i;
				maxarea = boundRect[i].area();
			}

		}

		int  difference = maxarea;

		for(int i=0; i<contours.size(); i++){

			if(i==biggest)
					continue;
			if(boundRect[biggest].area()-boundRect[i].area() < difference){

				next = i;
				difference = boundRect[biggest].area()-boundRect[i].area();
			}

			//cout << boundRect[i].area() << endl;
		}

		rectangle( img, boundRect[biggest].tl(), boundRect[biggest].br(), black, 3, 8, 0 );
		rectangle( img, boundRect[next].tl(), boundRect[next].br(), white, 3, 8, 0 );
		/*
		==================== It's crunch time (again) =======================
		*/

		bool stop = false;
		int x,y;
		int subject_x,subject_y, subject_width, subject_height;
		int refObject_y,refObject_dimension;
		float sitH,pH,tC,bpL,knH,erH,sH;
		float pixelRatio;

		//-------------------- Reference Object ---------------------------->

		
		refObject_dimension = boundRect[next].width;
		pixelRatio = refObject_dimension / actual_dimensions;
		cout << refObject_dimension << endl;
		cout << "There are " << pixelRatio << " pixels in 1 cm." << endl;

		//------------------------------------------------------------------>
		
		Point A,B,C,D,E,F,G,H,I,J,K,L;

		subject_x = boundRect[biggest].x;
		subject_y = boundRect[biggest].y;
		subject_width = boundRect[biggest].width + subject_x;
		subject_height = boundRect[biggest].height + subject_y;
		
		//Sitting Height 
		
		for(y=subject_y; y < subject_height && !stop ; y++){
			for(x=subject_x; x< subject_width; x++){
				if(static_cast<int>(binaryMat2.at<uchar>(y,x))==255){
					
					A = Point(x,y);
					stop = true;
					break;

				}
			}
		}

		for(y=subject_height; y> subject_y; y--){

			if(static_cast<int>(binaryMat2.at<uchar>(y,A.x))==255){
				B = Point(A.x,y);	
				break;
			}
		}

		//line( img, A, B, Scalar( 255, 0, 0 ), 1, 8 );
		circle(img, A, 10, Scalar( 255, 0, 0), -1, 8,0);
		circle(img, B, 10, Scalar( 255, 0, 0 ), -1, 8,0);

		sitH = euclideanDistance2d(A,B);
		sitH /= pixelRatio;
		
		//Popliteal Height

		for(x=subject_width; x > subject_x; x--){

			if(static_cast<int>(binaryMat2.at<uchar>(B.y,x))==255){
				C = Point(x,B.y);	
				break;
			}

		}

		D = Point(C.x,subject_height);	

		circle(img, C, 10, Scalar( 255, 0, 255 ), -1, 8,0);
		circle(img, D, 10, Scalar( 255, 0, 255 ), -1, 8,0);
		pH = euclideanDistance2d(C,D);
		pH /= pixelRatio;

		
		//Thigh Clearance

		midPoint(&E,B,C);


		//for robustness of E

		if(binaryMat2.at<uchar>(E.y,E.x) == 0){
			
			for(y=E.y; y>subject_y; y--){			//dapat subject_y hindi subject_height
				if(binaryMat2.at<uchar>(y,E.x) != 0){
					E = Point(E.x, y);
					break;
				}
			}
		}

		else if(binaryMat2.at<uchar>(E.y,E.x) == 255){
			
			for(y=E.y; y<subject_height ; y++){
				if(binaryMat2.at<uchar>(y,E.x) != 255){
					E = Point(E.x, y-1);
					break;
				}
			}
		}

		for(y=E.y; y > subject_y; y--){
			if(binaryMat2.at<uchar>(y,E.x) == 0){
				F = Point(E.x, y+1);
				break;
			}
		}
		
		//line( img, E, F, Scalar( 0, 0, 255 ), 1, 8 );
		circle(img, E, 10, Scalar( 0, 0, 255 ), -1, 8,0);
		circle(img, F, 10, Scalar( 0, 0, 255 ), -1, 8,0);
		tC = euclideanDistance2d(E,F);
		tC /= pixelRatio;

		//Buttock Popliteal Length

		midPoint(&G, E, F);

		for(x=subject_x; x<subject_width; x++){
			
			if(binaryMat2.at<uchar>(G.y,x)==255){
				H = Point(x,G.y);
				break; 
				}
		}

		for(x=subject_width; x>subject_x; x--){
			
			if(binaryMat2.at<uchar>(G.y,x)==255){
				I = Point(x,G.y);
				break; 
				}
		}

		circle(img, H, 10, Scalar( 255, 255, 0 ), -1, 8,0);
		circle(img, I, 10, Scalar( 255, 255, 0 ), -1, 8,0);
		bpL = euclideanDistance2d(H,I);
		bpL /= pixelRatio;

		//Knee Height
		
		J = Point(I.x, D.y);
		K = Point(I.x,F.y);
		circle(img, J, 10, Scalar( 255, 255, 255 ), -1, 8,0);
		circle(img, K, 10, Scalar( 0, 255, 255 ), -1, 8,0);

		knH = euclideanDistance2d(J,K);
		knH /= pixelRatio;

		//Store Measurements
		measurements[1] = 0.6339*pH +11.896;
		measurements[2] = 0.4868*tC +6.9836;
		measurements[3] = 0.6024*bpL + 19.855;
		measurements[4] = 0.6302*knH + 17.425;
	}
	else{
		measurements[0] =0.0;
		measurements[1] =0.0;
		measurements[2] =0.0;
		measurements[3] =0.0;
		measurements[4] =0.0;
	}
	
}

void measureFront(Mat & src, Mat & out, double *measurements,double actual_dimensions){		//function for deriving anthropometric data in the front image

	__android_log_write(ANDROID_LOG_INFO, "Measure", "Front");//Or ANDROID_LOG_INFO, ...

	Mat & binaryMat = *(&src);
	Mat & img = *(&out);
	Mat binaryMat2;

    binaryMat.copyTo(binaryMat2);

    /*
	=========== Find the Subject and the Reference Object ==============
	*/

	// Find Contours
	cout << "Detecting Subject and Reference Object..." << endl;
	vector<vector<Point> > contours;
	vector<Vec4i> hierarchy;
	findContours( binaryMat, contours, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE, Point(0, 0) );

	/// Approximate contours to polygons + get bounding rects and circles
	if(contours.size()!=0){
		vector<vector<Point> > contours_poly( contours.size() );
		vector<Rect> boundRect( contours.size() );
		vector<Point2f>center( contours.size() );
		vector<float>radius( contours.size() );

		// Detect and draw bounding boxes

		for( int i = 0; i < contours.size(); i++ ){ 
			approxPolyDP( Mat(contours[i]), contours_poly[i], 3, true );
			boundRect[i] = boundingRect( Mat(contours_poly[i]) );
		}

		Mat drawing = Mat::zeros( binaryMat.size(), CV_8UC3 );
		
		Scalar black = Scalar( 0,0,0);

		// Identify subject and reference object
		int maxarea =0, biggest =0,next=0;
		for(int i=0; i<contours.size(); i++){

			if(boundRect[i].area() > maxarea){

				biggest = i;
				maxarea = boundRect[i].area();
			}

		}

		int  difference = maxarea;

		for(int i=0; i<contours.size(); i++){
			if(i==biggest)
				continue;

			if(boundRect[biggest].area()-boundRect[i].area() < difference){

				next = i;
				difference = boundRect[biggest].area()-boundRect[i].area();
			}

			//cout << boundRect[i].area() << endl;
		}

		rectangle( img, boundRect[biggest].tl(), boundRect[biggest].br(), black, 3, 8, 0 );
		rectangle( img, boundRect[next].tl(), boundRect[next].br(), black, 3, 8, 0 );

		/*
		==================== It's crunch time (again) =======================
		*/


		cout << "Deriving Anthropometric Data..." << endl << endl;

		bool stop = false;
		int x,y;
		int subject_x,subject_y, subject_width, subject_height;
		int refObject_y,refObject_dimension;
		float hB,kkB;
		float pixelRatio;

		//-------------------- Reference Object ---------------------------->

		
		refObject_dimension = boundRect[next].width;
		pixelRatio = refObject_dimension / actual_dimensions;
		cout << refObject_dimension << endl;
		cout << "There are " << pixelRatio << " pixels in 1 cm." << endl;

		//------------------------------------------------------------------>
		
		Point A,B,C,D,E,F,G,H,I;

		subject_x = boundRect[biggest].x;
		subject_y = boundRect[biggest].y;
		subject_width = boundRect[biggest].width + subject_x;
		subject_height = boundRect[biggest].height + subject_y;

		float hip,maxHip=0,knee,minKnee=subject_width,temp_x1,temp_x2,max_x1=0,max_x2=0,max_y=0,min_x1=0,min_x2=0,min_y=0;
		
		D = Point(subject_x, subject_height - (int)subject_height/4.5);
		E = Point(subject_x, subject_height - (int)subject_height/3);

		circle(img, D, 10, Scalar( 255, 0, 255 ), -1, 8,0);
		circle(img, E, 10, Scalar( 0, 255, 255 ), -1, 8,0);

		for(int y=D.y; y>E.y; y--){

			for(x=subject_x; x<subject_width; x++)
				if(static_cast<int>(binaryMat2.at<uchar>(y,x))==255)
					break;
			temp_x1 = x;

			for(x=subject_width; x>subject_x; x--)
				if(static_cast<int>(binaryMat2.at<uchar>(y,x))==255)
					break;
			temp_x2 = x;

			knee = euclideanDistance2d(Point(temp_x1,y),Point(temp_x2,y));
			
			if(knee<minKnee){

				minKnee =knee;
				min_x1 = temp_x1;
				min_x2 = temp_x2;
				min_y = y;
			}

		}	
		H = Point(min_x1,min_y);
		I = Point(min_x2,min_y);

		circle(img, H, 10, Scalar( 0, 255, 255 ), -1, 8,0);
		circle(img, I, 10, Scalar( 0, 255, 255), -1, 8,0);
		kkB = euclideanDistance2d(H,I);
		kkB /= pixelRatio;

		//------------------------------------------------------------------->

		measurements[8] = kkB;
	}
	else{
		measurements[8] = 0.0;	
	}
 }




void measureBack(Mat & src, Mat & out, double *measurements,double actual_dimensions){		//function for deriving anthropometric data in the back image

	__android_log_write(ANDROID_LOG_INFO, "Measure", "Back");

	Mat & binaryMat = *(&src);
	Mat & img = *(&out);
	Mat binaryMat2;

    binaryMat.copyTo(binaryMat2);

     /*
	=========== Find the Subject and the Reference Object ==============
	*/

	// Find Contours
	cout << "Detecting Subject and Reference Object..." << endl;
	vector<vector<Point> > contours;
	vector<Vec4i> hierarchy;
	findContours( binaryMat, contours, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE, Point(0, 0) );

	/// Approximate contours to polygons + get bounding rects and circles
	if(contours.size()!=0){
		vector<vector<Point> > contours_poly( contours.size() );
		vector<Rect> boundRect( contours.size() );
		vector<Point2f>center( contours.size() );
		vector<float>radius( contours.size() );

		// Detect and draw bounding boxes

		for( int i = 0; i < contours.size(); i++ ){ 
			approxPolyDP( Mat(contours[i]), contours_poly[i], 3, true );
			boundRect[i] = boundingRect( Mat(contours_poly[i]) );
		}
	
		Mat drawing = Mat::zeros( binaryMat.size(), CV_8UC3 );
		
		Scalar black = Scalar( 0,0,0);

		// Identify subject and reference object
		int maxarea =0, biggest =0,next=0;
		for(int i=0; i<contours.size(); i++){

			if(boundRect[i].area() > maxarea){

				biggest = i;
				maxarea = boundRect[i].area();
			}

		}

		int  difference = maxarea;

		for(int i=0; i<contours.size(); i++){
			if(i==biggest)
				continue;

			if(boundRect[biggest].area()-boundRect[i].area() < difference){

				next = i;
				difference = boundRect[biggest].area()-boundRect[i].area();
			}

		}

		rectangle( img, boundRect[biggest].tl(), boundRect[biggest].br(), black, 3, 8, 0 );
		rectangle( img, boundRect[next].tl(), boundRect[next].br(), black, 3, 8, 0 );

		/*
		==================== It's crunch time (again) =======================
		*/


		cout << "Deriving Anthropometric Data..." << endl << endl;

		bool stop = false;
		int x,y,pixcount=0,pixcount_prev=0;
		int subject_x,subject_y, subject_width, subject_height;
		int refObject_y,refObject_dimension;
		float sH=0,erH=0,hB=0,sitH=0;
		float pixelRatio;
		int mincount, min_x=0, min_y=0,min;
		int hip_y;

		//-------------------- Reference Object ---------------------------->

		
		refObject_dimension = boundRect[next].width;
		pixelRatio = refObject_dimension / actual_dimensions;
		cout << refObject_dimension << endl;
		cout << "There are " << pixelRatio << " pixels in 1 cm." << endl;

		//------------------------------------------------------------------>
		
		
		Point A,B,C,D,E,F,G,H,I,J;

		subject_x = boundRect[biggest].x;
		subject_y = boundRect[biggest].y;
		subject_width = boundRect[biggest].width + subject_x;
		subject_height = boundRect[biggest].height + subject_y;

		//sitting height
		H = Point((boundRect[biggest].width/2)+subject_x,subject_y);
		I = Point((boundRect[biggest].width/2)+subject_x,subject_height);

		circle(img, H, 10, Scalar( 255, 128, 0 ), -1, 8,0);
		circle(img, I, 10, Scalar( 255, 128, 0 ), -1, 8,0);

		sitH = (float)(euclideanDistance2d(H,I)/pixelRatio);


		//Hip Breadth
		hip_y = subject_height-(subject_height*1/15);

		for(x=subject_x; x<subject_width; x++)
			if(binaryMat2.at<uchar>(hip_y,x)==255)
				break;

		E = Point(x,hip_y);	

		for(x = subject_width; x>subject_x; x--)
			if(binaryMat2.at<uchar>(hip_y,x)==255)
				break;

		F = Point(x,hip_y);

		circle(img, E, 10, Scalar( 0, 255, 255 ), -1, 8,0);
		circle(img, F, 10, Scalar( 0, 255, 255 ), -1, 8,0);
		

		hB = (float) euclideanDistance2d(E,F)/pixelRatio;

		//Elbow Rest Height

		min = boundRect[biggest].height;
		pixcount = 0;
		pixcount_prev = 0;

		for(x=subject_x; x<(E.x*0.9); x++){

			pixcount = 0;
			
			for(y=subject_height; y>0; y--){
				pixcount++;
				if(binaryMat2.at<uchar>(y,x)==255)
					break;

			}
			
			if((float)pixcount < (float)(pixcount_prev*0.2))
				break;

			if(pixcount < min){
				min = pixcount;
				min_x = x;
				min_y = y;
			}

			pixcount_prev = pixcount;

		}

		A = Point(min_x,min_y);
		B = Point(min_x,subject_height);
		circle(img, A, 10, Scalar( 255, 0, 255 ), -1, 8,0);
		circle(img, B, 10, Scalar( 255, 0, 255 ), -1, 8,0);
		
		
		erH = euclideanDistance2d(A,B)/pixelRatio;

		//Shoulder Height

		C = Point(subject_x+(boundRect[biggest].width*0.15),subject_y);


		for(y=subject_y; y<subject_height; y++){
			if(binaryMat2.at<uchar>(y,C.x)==255){
					D = Point(C.x,y);					
					break;
				}
		}

		J=Point(C.x,subject_height);
		
		circle(img, C, 10, Scalar( 255, 255, 255 ), -1, 8,0);
		circle(img, D, 10, Scalar( 255, 255, 255 ), -1, 8,0);
		circle(img, J, 10, Scalar( 255, 255, 255 ), -1, 8,0);
		//line( img, C, D, Scalar( 0, 128, 255 ), 1, 8 );

		sH = euclideanDistance2d(D,J)/pixelRatio;
		//sH /= pixelRatio;

		

		measurements[0] = (float)boundRect[biggest].height/pixelRatio;
		measurements[5] = 0.8086* erH + 2.8667;
		measurements[6] = sH;
		measurements[7] = hB;
	
		cout << "height:  " << (float)boundRect[biggest].height/pixelRatio << endl;
	}
	else{
		measurements[0] = 0.0;
		measurements[5] = 0.0;
		measurements[6] = 0.0;
		measurements[7] = 0.0;	
	}
 }

 float euclideanDistance2d(Point A, Point B) {		//function for computing the distance between two points

 		int distdist = (A.x-B.x)*(A.x-B.x) + (A.y-B.y)*(A.y-B.y);
  		
  		return (float)(sqrt(distdist));  				


	}

void midPoint(Point *midpoint, Point a, Point b){	//function for locating the midpoint between two points
		
		(*midpoint).x = (int)(a.x +b.x)/2;
		(*midpoint).y = (int)(a.y +b.y)/2;

	}
}