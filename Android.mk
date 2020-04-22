LOCAL_PATH := $(call my-dir)


include $(CLEAR_VARS)
LOCAL_PACKAGE_NAME := BluetoothApp
LOCAL_MODULE_TAGS := optional
LOCAL_CERTIFICATE := platform
LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res
LOCAL_PROGUARD_ENABLED := disabled
LOCAL_PRIVILEGED_MODULE := true





#LOCAL_JAVA_LIBRARIES := javax.obex   

LOCAL_STATIC_JAVA_LIBRARIES := javax.obexstatic  com.android.vcard bluetooth.mapsapi sap-api-java-static android-support-v4 services.net android.bluetooth.client.map

LOCAL_MULTILIB := both

include $(BUILD_PACKAGE)

