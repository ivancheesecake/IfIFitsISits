#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <android/log.h>
#include <stdio.h>

using namespace std;
using namespace cv;

extern "C" {


JNIEXPORT void JNICALL Java_com_cheesecake_ififitsisits_CaptureActivity_Overlay(JNIEnv*, jobject, jlong src, jlong ovr);

JNIEXPORT void JNICALL Java_com_cheesecake_ififitsisits_CaptureActivity_Overlay(JNIEnv*, jobject, jlong src, jlong ovr){

	Mat & img = *(Mat*)src;
	Mat & overlay = *(Mat*)ovr;

	//rectangle(img, Point(img.cols/2,1), Point(img.cols-1,img.rows-1),Scalar(0,128,255),1);	//Modify camera feed to show rectangles
    rectangle(img, Point(img.cols/2,1), Point(img.cols/2+21,21), Scalar(0,255,40),1);

	for(int y=0; y<img.rows; y++){
		for(int x=0; x<(int)img.cols/1.5; x++){
			 img.at<cv::Vec3b>(y,x)[0] = overlay.at<cv::Vec3b>(y,x)[0];
			 img.at<cv::Vec3b>(y,x)[1] = overlay.at<cv::Vec3b>(y,x)[1];
			 img.at<cv::Vec3b>(y,x)[2] = overlay.at<cv::Vec3b>(y,x)[2];

			 //img.at<cv::Vec3b>(y,x)[0] = 255;
			 //img.at<cv::Vec3b>(y,x)[1] = 255;
			 //img.at<cv::Vec3b>(y,x)[2] = 255;
		}
	}

}


}