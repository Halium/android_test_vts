LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := NfcHidlBasicTest
VTS_CONFIG_SRC_DIR := testcases/hal/nfc/hidl/host
include test/vts/tools/build/Android.host_config.mk