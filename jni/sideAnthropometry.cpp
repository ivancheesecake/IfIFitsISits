/*

	Ito yung C++ code ko sa pag-derive ng data na pwedeng gamitin ng JNI.

*/

#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <android/log.h>
#include <stdio.h>

using namespace std;
using namespace cv;

extern "C" {

	float euclideanDistance2d(Point A, Point B);
	void midPoint(Point *midpoint, Point a, Point b);

	/*
		Yung string after JNICALL (yung Java_com_...), Java_packagename_filename(kung saan tatawagin)_functionname

		Yung first 2 params, (JNIEnv *env, jobject obj), wag mo galawin, mahalaga yun (yata),
		tapos yung mga params after ay depende na sayo

		Yung sakin, merong src,out, tsaka arr. Yung arr yung magtatago ng result para maaccess siya sa app.

		Ganito ko siya tinawag
		double [] measurements = new double[5];
		MeasureSide(keyedMat.getNativeObjAddr(),sideMat.getNativeObjAddr(), measurements);

		basta dapat declared to sa labas

		public native void MeasureSide(long src, long dst,double [] sideArray);

		tapos dapat tinawag to sa oncreate()

		System.loadLibrary("library_name");		
	*/

	JNIEXPORT jint JNICALL Java_com_cheesecake_ififitsisits_DisplayActivity_MeasureSide(JNIEnv *env, jobject obj, jlong src, jlong out, jdoubleArray arr);

	JNIEXPORT jint JNICALL Java_com_cheesecake_ififitsisits_DisplayActivity_MeasureSide(JNIEnv *env, jobject obj, jlong src, jlong out, jdoubleArray arr){

	Mat & binaryMat = *(Mat*)src;
	Mat & img = *(Mat*)out;
	Mat binaryMat2;

    binaryMat.copyTo(binaryMat2);

    jdouble *measurementsArray;
    measurementsArray = env->GetDoubleArrayElements(arr, NULL);
    
     // do some exception checking
     if (measurementsArray == NULL) {
         return -1; /* exception occurred */
     }
	/*
	=========== Find the Subject and the Reference Object ==============
	*/

	// Find Contours
	//cout << "Detecting Subject and Reference Object..." << endl;
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

	for( int i = 0; i< contours.size(); i++ ){
		
		rectangle( img, boundRect[i].tl(), boundRect[i].br(), black, 1, 8, 0 );
	}

	// Identify subject and reference object
	int maxarea =0, biggest =0,next=0;
	for(int i=0; i<contours.size(); i++){

		if(boundRect[i].area() > maxarea){
			next = biggest;
			biggest = i;
			maxarea = boundRect[i].area();
		}

	}

	/*
	==================== It's crunch time (again) =======================
	*/

	bool stop = false;
	int x,y;
	int subject_x,subject_y, subject_width, subject_height;
	int refObject_y,refObject_height;
	float sitH,pH,tC,bpL,knH;
	float pixelRatio;

	//-------------------- Reference Object ---------------------------->

	
	refObject_height = boundRect[next].height;
	pixelRatio = refObject_height / 2.0;
	//cout << "There are " << pixelRatio << " pixels in 1 cm." << endl;

	//------------------------------------------------------------------>
	
	Point A,B,C,D,E,F,G,H,I,J;

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

	line( img, C, D, Scalar( 0, 255, 0 ), 1, 8 );

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

	bpL = euclideanDistance2d(H,I);
	bpL /= pixelRatio;


	//------------------------------------------------------------------>

	//--------------------- KH ----------------------------------------->
	
	J = Point(I.x, D.y);
	line( img, J, I, Scalar( 0, 255, 255 ), 1, 8 );

	knH = euclideanDistance2d(J,I);
	knH /= pixelRatio;


	//------------------------------------------------------------------>

	//char str[30];
	//sprintf(str,"sitH = %f pH = %f tC=%f bpL = %f knH = %f",sitH,pH,tC,bpL,knH);

	//__android_log_write(ANDROID_LOG_INFO, "Measurements: ", str);



	measurementsArray[0] = sitH;
	measurementsArray[1] = pH;
	measurementsArray[2] = tC;
	measurementsArray[3] = bpL;
	measurementsArray[4] = knH;

	// release the memory so java can have it again
     env->ReleaseDoubleArrayElements(arr, measurementsArray, 0);

     // return something, or not.. it's up to you
     return 0;
}




	float euclideanDistance2d(Point A, Point B) {

 		int distdist = (A.x-B.x)*(A.x-B.x) + (A.y-B.y)*(A.y-B.y);
  		return (float)(sqrt(distdist) + 0.5);  
	}

	void midPoint(Point *midpoint, Point a, Point b){
	
		(*midpoint).x = (int)(a.x +b.x)/2;
		(*midpoint).y = (int)(a.y +b.y)/2;

	}
}