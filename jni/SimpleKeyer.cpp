#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <android/log.h>
#include <stdio.h>

using namespace std;
using namespace cv;

//Pag galing Java, RGB na.

extern "C" {

JNIEXPORT void JNICALL Java_com_cheesecake_ififitsisits_DisplayProcessActivity_SimpleKeyer(JNIEnv*, jobject, jlong src, jlong dst);

JNIEXPORT void JNICALL Java_com_cheesecake_ififitsisits_DisplayProcessActivity_SimpleKeyer(JNIEnv*, jobject, jlong src, jlong dst){

	
	Mat & img = *(Mat*)src;
	Mat & keyed = *(Mat*)dst;
	
	int Cb_key, Cr_key, Cb_p, Cr_p;
	int thresh = 20;
	float mask;
	char string[30];

	//Mat keyed = Mat(img.size(),CV_8UC1);
	Mat bg = Mat(img.size(),CV_8UC3);
	Mat maskara = Mat(img.size(),CV_8UC1);
	bg.setTo(Scalar(0,0,0));

	int maskConverted=0;

	for(int y=0; y< img.rows; y++){
		for(int x=0; x< img.cols ; x++){

		int r = static_cast<int>(img.data[img.channels()*(img.cols*y + x) + 0]);    
		int g = static_cast<int>(img.data[img.channels()*(img.cols*y + x) + 1]);
		int b = static_cast<int>(img.data[img.channels()*(img.cols*y + x) + 2]);	

		if( (g > r + thresh) && (g > b +thresh) ) {
				maskara.at<uchar>(y,x) = 0;
			}
			else{
				maskara.at<uchar>(y,x) = 255;	
			}


		}
	}
	
	//threshold(maskara, keyed, 0, 255,CV_THRESH_OTSU);
	erode(maskara, maskara, Mat(), Point(-1, -1), 2, 1, 1);
	dilate(maskara, maskara, Mat(), Point(-1, -1), 2, 1, 1);

	maskara.copyTo(keyed);

}




}