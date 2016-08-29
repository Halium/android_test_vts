#
# Copyright (C) 2016 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := 28838221_poc
LOCAL_MODULE_STEM_32 := 28838221_poc32
LOCAL_MODULE_STEM_64 := 28838221_poc64

LOCAL_SRC_FILES := poc.c
LOCAL_STATIC_LIBRARIES += libc
LOCAL_FORCE_STATIC_EXECUTABLE := true

LOCAL_MULTILIB := both
LOCAL_COMPATIBILITY_SUITE := vts

include $(BUILD_EXECUTABLE)