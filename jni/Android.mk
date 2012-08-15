LOCAL_PATH:= $(call my-dir)
local_c_includes := \
	$(LOCAL_PATH) \
	$(LOCAL_PATH)/libcurl/curl

local_src_files:= \
	com_xingcloud_analytic_xnative_XCNative.cpp\

include $(CLEAR_VARS)
LOCAL_CXXFLAGS:=-g
LOCAL_SRC_FILES := $(local_src_files)
LOCAL_C_INCLUDES := $(local_c_includes)
LOCAL_MODULE    :=  libanalyticglue


LOCAL_MODULE_TAGS := optional
#LOCAL_STATIC_LIBRARIES := libxml2
#LOCAL_STATIC_LIBRARIES :=  $(LOCAL_PATH)/curl $(LOCAL_PATH)/tinyxml
#LOCAL_LDLIBS := /libcurl.a /libtinyxml.a
LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog 
LOCAL_LDLIBS += -L$(call host-path, $(LOCAL_PATH))	\
               -lcurl
include $(BUILD_SHARED_LIBRARY)
