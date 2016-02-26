LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

APP_ABI := all

LOCAL_MODULE    := libkeepalive
LOCAL_SRC_FILES := keep_alive_do.c
LOCAL_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_LDLIBS += -L$(SYSROOT)/usr/lib -llog
LOCAL_CFLAGS += -DALI_LOG
include $(BUILD_EXECUTABLE)