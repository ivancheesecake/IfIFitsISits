#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <android/log.h>
#include <stdio.h>

using namespace std;
using namespace cv;

//Pag galing Java, RGB na.

extern "C" {

int rgb2y (int r, int g, int b);
int rgb2cb (int r, int g, int b);
int rgb2cr (int r, int g, int b);
float colorclose(int Cb_p, int Cr_p, int Cb_key, int Cr_key, int tola, int tolb);

JNIEXPORT void JNICALL Java_com_cheesecake_ififitsisits_DisplayProcessActivity_KeyerYCbCr(JNIEnv*, jobject, jlong src, jlong dst);

JNIEXPORT void JNICALL Java_com_cheesecake_ififitsisits_DisplayProcessActivity_KeyerYCbCr(JNIEnv*, jobject, jlong src, jlong dst){

	
	Mat & img = *(Mat*)src;
	Mat & keyed = *(Mat*)dst;
	Mat imgCopy;

	int Cb_key, Cr_key, Cb_p, Cr_p;
	int tola,tolb;
	float mask;
	tola = 30;
	tolb = 200;
	char string[30];

	//Mat keyed = Mat(img.size(),CV_8UC1);
	Mat bg = Mat(img.size(),CV_8UC3);
	Mat maskara = Mat(img.size(),CV_8UC1);
	bg.setTo(Scalar(0,0,0));

	img.copyTo(imgCopy);
	//Cb_key = rgb2cb(img.at<cv::Vec3b>(0,0)[0],img.at<cv::Vec3b>(0,0)[1],img.at<cv::Vec3b>(0,0)[2]);
	//Cr_key = rgb2cr(img.at<cv::Vec3b>(0,0)[0],img.at<cv::Vec3b>(0,0)[1],img.at<cv::Vec3b>(0,0)[2]);

	int redKey = img.at<cv::Vec3b>(0,0)[0];
	int greenKey = img.at<cv::Vec3b>(0,0)[1];
	int blueKey = img.at<cv::Vec3b>(0,0)[2];
	int red,green,blue;

	Cb_key =  (int)round(128 + -0.168736*redKey - 0.331264*greenKey + 0.5*blueKey);
	Cr_key = (int) round(128 + 0.5*redKey - 0.418688*greenKey - 0.081312*blueKey); 

	sprintf(string,"%d %d",Cb_key,Cr_key);
	__android_log_write(ANDROID_LOG_INFO, "Cb_key Cr_key", string);	

	int maskConverted=0;

	for(int y=0; y< img.rows; y++){
		for(int x=0; x< img.cols ; x++){

		
		int r = static_cast<int>(imgCopy.data[imgCopy.channels()*(imgCopy.cols*y + x) + 0]);    
		int g = static_cast<int>(imgCopy.data[imgCopy.channels()*(imgCopy.cols*y + x) + 1]);
		int b = static_cast<int>(imgCopy.data[imgCopy.channels()*(imgCopy.cols*y + x) + 2]);	

		Cb_p = rgb2cb(r,g,b);
		Cr_p = rgb2cr(r,g,b);
		mask = colorclose(Cb_p, Cr_p, Cb_key, Cr_key, tola, tolb); 
        mask = 1 - mask;	
		maskConverted  = (int)255*mask;

		maskara.at<uchar>(y,x)=maskConverted;



		}
	}
	maskara.copyTo(keyed);
	threshold(maskara, keyed, 0, 255,CV_THRESH_OTSU);
	erode(keyed, keyed, Mat(), Point(-1, -1), 2, 1, 1);
	dilate(keyed, keyed, Mat(), Point(-1, -1), 2, 1, 1);

	//imgCopy.copyTo(img, maskara);

}

int rgb2y (int r, int g, int b) { 
   /*a utility function to convert colors in RGB into YCbCr*/ 
   int y; 
   y = (int) round(0.299*r + 0.587*g + 0.114*b); 
   return (y); 
} 

int rgb2cb (int r, int g, int b) { 
   /*a utility function to convert colors in RGB into YCbCr*/ 
   int cb; 
   cb = (int) round(128 + -0.168736*r - 0.331264*g + 0.5*b); 
   return (cb); 
} 

int rgb2cr (int r, int g, int b) { 
   /*a utility function to convert colors in RGB into YCbCr*/ 
   int cr; 
   cr = (int) round(128 + 0.5*r - 0.418688*g - 0.081312*b); 
   return (cr); 
} 

float colorclose(int Cb_p, int Cr_p, int Cb_key, int Cr_key, int tola, int tolb){
	
	float temp;

	temp = sqrt(((Cb_key-Cb_p)*(Cb_key-Cb_p))+((Cr_key-Cr_p)*(Cr_key-Cr_p)));
	//cout << temp << endl;
	if(temp < tola)
    	return 1.0;
   	else if(temp < tolb)
     	return (temp-tola)/(tolb-tola);
  	else
     	return 0.0;

}


}