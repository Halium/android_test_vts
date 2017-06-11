/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef __VTS_SYSFUZZER_COMMON_UTILS_IFSPECUTIL_H__
#define __VTS_SYSFUZZER_COMMON_UTILS_IFSPECUTIL_H__

#include <string>

#include "test/vts/proto/ComponentSpecificationMessage.pb.h"

#define VTS_INTERFACE_SPECIFICATION_FUNCTION_NAME_PREFIX "vts_func_"

using namespace std;

namespace android {
namespace vts {

// Returns the function name prefix of a given interface specification.
string GetFunctionNamePrefix(const ComponentSpecificationMessage& message);

// Get HAL version string to be used to build a relevant dir path.
string GetVersionString(float version, bool for_macro=false);

// Get the full interface name for a Hidl Hal,
// e.g. ::android::hardware::nfc::V1_0::INfc.
string GetFullInterfaceName(const string& package_name, const float version,
                            const string& interface_name);
}  // namespace vts
}  // namespace android

#endif  // __VTS_SYSFUZZER_COMMON_UTILS_IFSPECUTIL_H__
