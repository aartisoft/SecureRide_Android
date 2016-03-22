LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := securide
LOCAL_SRC_FILES := tclient2-2.c connection_manager.c
LOCAL_STATIC_LIBRARIES := boost_serialization_static
LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)

