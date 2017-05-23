/*
 * Copyright (c) 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.android.vts.util;

import com.android.vts.entity.TestCaseRunEntity;
import com.android.vts.proto.VtsReportMessage.TestCaseResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Helper object for describing test results data. */
public class TestRunDetails {
    private static final String NAME_KEY = "name";
    private static final String DATA_KEY = "data";

    private class ResultColumn {
        private final String name;
        private final List<String> testCases;

        public ResultColumn(String name) {
            this.name = name;
            this.testCases = new ArrayList<>();
        }

        public void add(String testCase) {
            this.testCases.add(testCase);
        }

        public int size() {
            return this.testCases.size();
        }

        public JsonObject toJson() {
            Collections.sort(testCases);
            JsonObject json = new JsonObject();
            json.add(NAME_KEY, new JsonPrimitive(name));
            json.add(DATA_KEY, new Gson().toJsonTree(testCases));
            return json;
        }
    }

    private final ResultColumn[] columns;
    public final int[] resultCounts = new int[TestCaseResult.values().length];

    public TestRunDetails() {
        columns = new ResultColumn[TestCaseResult.values().length];
        for (TestCaseResult r : TestCaseResult.values()) {
            columns[r.getNumber()] = new ResultColumn(r.name());
        }
    }

    /**
     * Add a test case to the details object.
     * @param testCase The TestCaseRunEntity object storing a test case result.
     */
    public void addTestCase(TestCaseRunEntity testCase) {
        if (testCase.result > resultCounts.length)
            return;
        ++resultCounts[testCase.result];
        ResultColumn column = columns[testCase.result];
        column.add(testCase.testCaseName);
    }

    /**
     * Serializes the test run details to json format.
     *
     * @return A JsonObject object representing the details object.
     */
    public JsonElement toJson() {
        List<JsonObject> jsonColumns = new ArrayList<>();
        for (ResultColumn column : columns) {
            if (column.size() > 0) {
                jsonColumns.add(column.toJson());
            }
        }
        return new Gson().toJsonTree(jsonColumns);
    }
}
