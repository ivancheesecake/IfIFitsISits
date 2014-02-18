LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

include C:\Work\OpenCV4Android\OpenCV-2.4.7.1-android-sdk\sdk\native\jni\OpenCV.mk

LOCAL_MODULE    := ififits_native
LOCAL_SRC_FILES := \
Keyer.cpp \ sideAnthropometry.cpp \ Keyer2.cpp \ ArmchairAnthropometry.cpp \ Overlay.cpp
LOCAL_LDLIBS +=  -llog -ldl

include $(BUILD_SHARED_LIBRARY)
