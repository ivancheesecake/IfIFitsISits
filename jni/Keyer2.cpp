#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <android/log.h>
#include <stdio.h>

using namespace std;
using namespace cv;

extern "C" {


JNIEXPORT void JNICALL Java_com_cheesecake_ififitsisits_DisplayProcessActivity_Keyer(JNIEnv*, jobject, jlong src, jlong dst);

JNIEXPORT void JNICALL Java_com_cheesecake_ififitsisits_DisplayProcessActivity_Keyer(JNIEnv*, jobject, jlong src, jlong dst){

	int backgroundRed,backgroundGreen,backgroundBlue,redTemp,greenTemp,blueTemp;

	int background_x =0;
	int background_y=0;
	
	Mat & img = *(Mat*)src;
	Mat & thresh = *(Mat*)dst;

	Mat combined = Mat(img.size(),CV_8UC1);
	Mat combinedContrast = Mat(img.size(),CV_8UC1);
	Mat greenInverted;
	
	vector<Mat> rgb;
	//split into 3 channels
	split(img,rgb);

	//invert green channel
	bitwise_not ( rgb[1], greenInverted );

	backgroundRed = rgb[2].at<cv::Vec3b>(background_y,background_x)[0];
	backgroundGreen = greenInverted.at<cv::Vec3b>(background_y,background_x)[0];
	backgroundBlue = rgb[0].at<cv::Vec3b>(background_y,background_x)[0];

	for(int y=0; y<img.rows; y++){
		for(int x=0; x<img.cols; x++){
			redTemp = max((rgb[2].at<uchar>(y,x) - backgroundRed),0);
			greenTemp = max((greenInverted.at<uchar>(y,x) - backgroundGreen),0);
			blueTemp = max((rgb[0].at<uchar>(y,x) - backgroundBlue),0);	

			combined.at<uchar>(y,x) = min((redTemp+greenTemp+blueTemp),255);
			
		}	
	}

	combined.convertTo(combinedContrast, -1, 2, 0); 
	threshold(combinedContrast, thresh, 0, 255, CV_THRESH_BINARY | CV_THRESH_OTSU);

}


}