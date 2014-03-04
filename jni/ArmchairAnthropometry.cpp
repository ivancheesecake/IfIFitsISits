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

	float euclideanDistance2d(Point A, Point B);
	void midPoint(Point *midpoint, Point a, Point b);
	void measureSide(Mat & src, Mat & out, double *measurements, double actual_dimensions);
	void measureFront(Mat & src, Mat & out, double *measurements,double actual_dimensions);
	void measureBack(Mat & src, Mat & out, double *measurements,double actual_dimensions);

	JNIEXPORT jint JNICALL Java_com_cheesecake_ififitsisits_DisplayProcessActivity_DeriveData(JNIEnv *env, jobject obj, jlong src, jlong out, jdoubleArray arr, jdouble dimensions, jint flag);

	JNIEXPORT jint JNICALL Java_com_cheesecake_ififitsisits_DisplayProcessActivity_DeriveData(JNIEnv *env, jobject obj, jlong src, jlong out, jdoubleArray arr, jdouble dimensions, jint flag){

	Mat & binaryMat = *(Mat*)src;
	Mat & img = *(Mat*)out;
	//Mat & binaryMat2 = *(Mat*)src2;
	//Mat & img2 = *(Mat*)out2;

	//Mat binaryMat2;

    //binaryMat.copyTo(binaryMat2);

    jdouble *measurementsArray;
    measurementsArray = env->GetDoubleArrayElements(arr, NULL);
    
     // do some exception checking
     if (measurementsArray == NULL) {
         return -1; /* exception occurred */
     }	

     if(flag==0)
     	measureSide(binaryMat,img,measurementsArray,dimensions);
     else if(flag==1)
     	measureFront(binaryMat,img,measurementsArray,dimensions);
     else
     	measureBack(binaryMat,img,measurementsArray,dimensions);

     env->ReleaseDoubleArrayElements(arr, measurementsArray, 0);

	}

	void measureSide(Mat & src, Mat & out, double *measurements, double actual_dimensions){

	Mat & binaryMat = *(&src);
	Mat & img = *(&out);
	Mat binaryMat2;

    binaryMat.copyTo(binaryMat2);

    

    char output[50];
   	sprintf(output,"%f",actual_dimensions);
	__android_log_write(ANDROID_LOG_INFO, "Measure", "Side");//Or ANDROID_LOG_INFO, ... 

	/*
	=========== Find the Subject and the Reference Object ==============
	*/

	// Find Contours
	cout << "Detecting Subject and Reference Object..." << endl;
	vector<vector<Point> > contours;
	vector<Vec4i> hierarchy;
	findContours( binaryMat, contours, hierarchy, CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE, Point(0, 0) );

	/// Approximate contours to polygons + get bounding rects and circles

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

	/*for( int i = 0; i< contours.size(); i++ ){
		
		rectangle( img, boundRect[i].tl(), boundRect[i].br(), black, 1, 8, 0 );
	}*/

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

	rectangle( img, boundRect[biggest].tl(), boundRect[biggest].br(), black, 1, 8, 0 );
	rectangle( img, boundRect[next].tl(), boundRect[next].br(), white, 1, 8, 0 );
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

	
	refObject_dimension = boundRect[next].height;
	pixelRatio = refObject_dimension / actual_dimensions;
	cout << refObject_dimension << endl;
	cout << "There are " << pixelRatio << " pixels in 1 cm." << endl;

	//------------------------------------------------------------------>
	
	Point A,B,C,D,E,F,G,H,I,J,K,L;

	subject_x = boundRect[biggest].x;
	subject_y = boundRect[biggest].y;
	subject_width = boundRect[biggest].width + subject_x;
	subject_height = boundRect[biggest].height + subject_y;
	
	//--------------------- SitH ---------------------------------------> 
	
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

	line( img, A, B, Scalar( 255, 0, 0 ), 1, 8 );
	circle(img, A, 5, Scalar( 255, 0, 0), 1, 8,0);
	circle(img, B, 5, Scalar( 255, 0, 0 ), 1, 8,0);

	sitH = euclideanDistance2d(A,B);
	sitH /= pixelRatio;

	
	//------------------------------------------------------------------> 
	
	//--------------------- PH ----------------------------------------->

	for(x=subject_width; x > subject_x; x--){

		if(static_cast<int>(binaryMat2.at<uchar>(B.y,x))==255){
			C = Point(x,B.y);	
			break;
		}

	}

	for(y=subject_height; y> subject_y; y--){

		if(static_cast<int>(binaryMat2.at<uchar>(y,C.x))==255){
			D = Point(C.x,y);	
			break;
		}
	}

	line( img, C, D, Scalar( 255, 0, 255 ), 1, 8 );
	circle(img, C, 5, Scalar( 255, 0, 255 ), 1, 8,0);
	circle(img, D, 5, Scalar( 255, 0, 255 ), 1, 8,0);
	pH = euclideanDistance2d(C,D);
	pH /= pixelRatio;

	
	//------------------------------------------------------------------>

	//--------------------- TC ----------------------------------------->

	midPoint(&E,B,C);

	//for robustness of E

	if(binaryMat2.at<uchar>(E.y,E.x) == 0){
		
		for(y=E.y; y>subject_height; y--){
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
	
	line( img, E, F, Scalar( 0, 0, 255 ), 1, 8 );
	circle(img, E, 5, Scalar( 0, 0, 255 ), 1, 8,0);
	circle(img, F, 5, Scalar( 0, 0, 255 ), 1, 8,0);
	tC = euclideanDistance2d(E,F);
	tC /= pixelRatio;

	

	//------------------------------------------------------------------>

	//--------------------- BPL ---------------------------------------->

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

	line( img, H, I, Scalar( 255, 255, 0 ), 1, 8 );
	circle(img, H, 5, Scalar( 255, 255, 0 ), 1, 8,0);
	circle(img, H, 5, Scalar( 255, 255, 0 ), 1, 8,0);
	bpL = euclideanDistance2d(H,I);
	bpL /= pixelRatio;


	//------------------------------------------------------------------>

	//--------------------- KH ----------------------------------------->
	
	J = Point(I.x, D.y);
	line( img, J, I, Scalar( 0, 255, 255 ), 1, 8 );
	circle(img, J, 5, Scalar( 0, 255, 255 ), 1, 8,0);
	circle(img, I, 5, Scalar( 0, 255, 255 ), 1, 8,0);

	knH = euclideanDistance2d(J,I);
	knH /= pixelRatio;


	//------------------------------------------------------------------>
	/*
	//-------------------- ERH ------------------------------------------>

	for(y=F.y-1; y > subject_y; y--){
		if(binaryMat2.at<uchar>(y,F.x) == 255){
			K = Point(E.x, y+1);
			break;
		}
	}

	circle(img, K, 5, Scalar( 0, 128, 128 ), 1, 8,0);
	erH = euclideanDistance2d(E,K);
	erH /= pixelRatio;
	//------------------------------------------------------------------>

	//-------------------- SH ------------------------------------------>

	for(y=subject_y; y < subject_height; y++){
		if(binaryMat2.at<uchar>(y,F.x) == 255){
			L = Point(F.x, y-1);
			break;
		}
	}

	circle(img, L, 5, Scalar( 255, 255, 255 ), 1, 8,0);
	sH = euclideanDistance2d(E,L);
	sH /= pixelRatio;
	*/
	//------------------------------------------------------------------>

	measurements[0] = sitH;
	measurements[1] = pH;
	measurements[2] = tC;
	measurements[3] = bpL;
	measurements[4] = knH;
	//measurements[5] = erH;
	//measurements[6] = sH;

}

void measureFront(Mat & src, Mat & out, double *measurements,double actual_dimensions){

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
	/*
	for( int i = 0; i< contours.size(); i++ ){
		
		rectangle( img, boundRect[i].tl(), boundRect[i].br(), black, 1, 8, 0 );
	}*/

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

		if(boundRect[biggest].area()-boundRect[i].area() < difference){

			next = i;
			difference = boundRect[biggest].area()-boundRect[i].area();
		}

		//cout << boundRect[i].area() << endl;
	}

	/*
	==================== It's crunch time (again) =======================
	*/


	//cout << "Deriving Anthropometric Data..." << endl << endl;

	bool stop = false;
	int x,y;
	int subject_x,subject_y, subject_width, subject_height;
	int refObject_y,refObject_dimension;
	float hB,kkB;
	float pixelRatio;

	//-------------------- Reference Object ---------------------------->

	
	refObject_dimension = boundRect[next].height;
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

	//-------------------------- HB ------------------------------------>

	A = Point(subject_x,(int)(subject_y+subject_height)/2);
	B= Point(subject_x,(int)((subject_y+subject_height)/2)-(subject_height/15));
	C= Point(subject_x,(int)((subject_y+subject_height)/2)+(subject_height/15));

	
	for(int y=B.y; y<C.y; y++){

		for(x=subject_x; x<subject_width; x++)
			if(static_cast<int>(binaryMat2.at<uchar>(y,x))==255)
				break;
		temp_x1 = x;

		for(x=subject_width; x>subject_x; x--)
			if(static_cast<int>(binaryMat2.at<uchar>(y,x))==255)
				break;
		temp_x2 = x;

		hip = euclideanDistance2d(Point(temp_x1,y),Point(temp_x2,y));
		
		if(hip>maxHip){

			maxHip =hip;
			max_x1 = temp_x1;
			max_x2 = temp_x2;
			max_y = y;
		}

	}
	F = Point(max_x1,max_y);
	G = Point(max_x2,max_y);
	circle(img, F, 5, Scalar( 255, 0, 0 ), 1, 8,0);
	circle(img, G, 5, Scalar( 255, 0, 0), 1, 8,0);	
	line( img, F, G, Scalar( 255, 0,0 ), 1, 8 );
	hB = euclideanDistance2d(F,G);
	hB /= pixelRatio;

	//------------------------------------------------------------------->
	//-------------------------- KKB ------------------------------------>

	D = Point(subject_x,(int)((subject_y+subject_height)/2)+((subject_y+subject_height)/4));
	E = Point(subject_x,(int)((subject_y+subject_height)/2)+((subject_y+subject_height)/4)-(subject_height/10));


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

	line( img, H, I, Scalar( 0, 255, 255 ), 1, 8 );
	circle(img, H, 5, Scalar( 0, 255, 255 ), 1, 8,0);
	circle(img, I, 5, Scalar( 0, 255, 255), 1, 8,0);
	kkB = euclideanDistance2d(H,I);
	kkB /= pixelRatio;


	//------------------------------------------------------------------->

	measurements[7] = hB;
	measurements[8] = kkB;

 }

}


void measureBack(Mat & src, Mat & out, double *measurements,double actual_dimensions){

	__android_log_write(ANDROID_LOG_INFO, "Measure", "Back");//Or ANDROID_LOG_INFO, ...

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

	/*
	for( int i = 0; i< contours.size(); i++ ){
		
		rectangle( img, boundRect[i].tl(), boundRect[i].br(), black, 1, 8, 0 );
	}*/

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

		if(boundRect[biggest].area()-boundRect[i].area() < difference){

			next = i;
			difference = boundRect[biggest].area()-boundRect[i].area();
		}

		//cout << boundRect[i].area() << endl;
	}

	/*
	==================== It's crunch time (again) =======================
	*/


	cout << "Deriving Anthropometric Data..." << endl << endl;

	bool stop = false;
	int x,y;
	int subject_x,subject_y, subject_width, subject_height;
	int refObject_y,refObject_dimension;
	float sH,erH;
	float pixelRatio;

	//-------------------- Reference Object ---------------------------->

	
	refObject_dimension = boundRect[next].height;
	pixelRatio = refObject_dimension / actual_dimensions;
	cout << refObject_dimension << endl;
	cout << "There are " << pixelRatio << " pixels in 1 cm." << endl;

	//------------------------------------------------------------------>
	
	
	Point A,B,C,D;

	subject_x = boundRect[biggest].x;
	subject_y = boundRect[biggest].y;
	subject_width = boundRect[biggest].width + subject_x;
	subject_height = boundRect[biggest].height + subject_y;

	//float hip,maxHip=0,knee,minKnee=subject_width,temp_x1,temp_x2,max_x1=0,max_x2=0,max_y=0,min_x1=0,min_x2=0,min_y=0;

	
	//-------------------------- SH ------------------------------------>

	A = Point(subject_width/7,subject_y);

	
	for(int y=0; y<subject_height; y++)
		if(static_cast<int>(binaryMat2.at<uchar>(y,A.x))==255){
			B = Point(A.x,y);
			break;		
		}
				
	C = Point(subject_width/7,subject_height);
	circle(img, B, 5, Scalar( 255, 0, 255 ), -1, 8,0);
	circle(img, C, 5, Scalar( 255, 0, 255), -1, 8,0);	
	line( img, B, C, Scalar( 255, 0,255 ), 1, 8 );
	sH = euclideanDistance2d(B,C);
	sH /= pixelRatio;
	
	//------------------------------------------------------------------->
	//-------------------------- ERH ------------------------------------>

	for(int y=B.y+1; y<C.y; y++){

		if(static_cast<int>(binaryMat2.at<uchar>(y,B.x))==0){
			D = Point(B.x,y);
			break;
		}
	}	

	line( img, C, D, Scalar( 255, 255, 0 ), 1, 8 );
	circle(img, D, 5, Scalar( 255, 255, 0 ), -1, 8,0);
	erH = euclideanDistance2d(C,D);
	erH /= pixelRatio;


	//------------------------------------------------------------------->

	measurements[5] = erH;
	measurements[6] = sH;
	
 }