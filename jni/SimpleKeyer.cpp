/*

SimpleKeyer.cpp
Descripiton: Contains a native function for foreground extraction via chroma keying.
Author: Escamos, Ivan Marc H.
Date last modified: 04/10/2014

Got help from: https://home.cc.gatech.edu/icegt/uploads/30/Chromakey.ppt
*/

#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <android/log.h>
#include <stdio.h>

using namespace std;
using namespace cv;

extern "C" {

JNIEXPORT void JNICALL Java_com_cheesecake_ififitsisits_DisplayProcessActivity_SimpleKeyer(JNIEnv*, jobject, jlong src, jlong dst);

JNIEXPORT void JNICALL Java_com_cheesecake_ififitsisits_DisplayProcessActivity_SimpleKeyer(JNIEnv*, jobject, jlong src, jlong dst){

	
	Mat & img = *(Mat*)src;
	Mat & keyed = *(Mat*)dst;
	
	int Cb_key, Cr_key, Cb_p, Cr_p;
	int thresh = 20;
	float mask;

	Mat bg = Mat(img.size(),CV_8UC3);
	Mat maskara = Mat(img.size(),CV_8UC1);
	bg.setTo(Scalar(0,0,0));

	int maskConverted=0;

	for(int y=0; y< img.rows; y++){			//run through every pixel 
		for(int x=0; x< img.cols ; x++){

		int r = static_cast<int>(img.data[img.channels()*(img.cols*y + x) + 0]);  	//get the RGB values  
		int g = static_cast<int>(img.data[img.channels()*(img.cols*y + x) + 1]);
		int b = static_cast<int>(img.data[img.channels()*(img.cols*y + x) + 2]);	

		if( (g > r + thresh) && (g > b +thresh) ) {		//if the green value is significantly larger than the red and blue values
				maskara.at<uchar>(y,x) = 0;				//consider the pixel as background
			}
			else{
				maskara.at<uchar>(y,x) = 255;		//else, consider the pixel as foreground
			}


		}
	}
	
	erode(maskara, maskara, Mat(), Point(-1, -1), 2, 1, 1);		//perform binary image cleaning
	dilate(maskara, maskara, Mat(), Point(-1, -1), 2, 1, 1);

	maskara.copyTo(keyed);

}




}