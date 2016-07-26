/*
 * Copyright (c) 2016 Google Inc. All Rights Reserved.
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

package com.google.android.vts.servlet;

import com.google.android.vts.proto.VtsReportMessage;
import com.google.android.vts.proto.VtsReportMessage.ProfilingReportMessage;
import com.google.android.vts.proto.VtsReportMessage.TestReportMessage;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet for handling requests to load individual tables.
 */
@WebServlet(name = "show_table", urlPatterns = {"/show_table"})
public class ShowTableServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ShowTableServlet.class);
    // Error message displayed on the webpage is tableName passed is null.
    private static final String TABLE_NAME_ERROR = "Error : Table name must be passed!";
    private static final String PROFILING_DATA_ERROR = "Error : No profiling data was found.";

    /**
     * Returns the table corresponding to the table name.
     * @param tableName Describes the table name which is passed as a parameter from
     *        dashboard_main.jsp, which represents the table to fetch data from.
     * @return table An instance of org.apache.hadoop.hbase.client.Table
     * @throws IOException
     */
    public Table getTable(TableName tableName) throws IOException {
        long result;
        Table table = null;

        try {
            table = BigtableHelper.getConnection().getTable(tableName);
        } catch (IOException e) {
            logger.error("Exception occurred in com.google.android.vts.servlet.DashboardServletTable."
              + "getTable()", e);
            return null;
        }
        return table;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User currentUser = userService.getCurrentUser();
        RequestDispatcher dispatcher = null;
        Table table = null;
        TableName tableName = null;
        if (request.getParameter("tableName") == null) {
            request.setAttribute("table_name", TABLE_NAME_ERROR);
            dispatcher = request.getRequestDispatcher("/show_table.jsp");
            return;
        }

        tableName = TableName.valueOf(request.getParameter("tableName"));

        if (currentUser != null) {
            response.setContentType("text/plain");
            table = getTable(tableName);
            ResultScanner scanner = table.getScanner(new Scan());

            // map to hold the the list of time taken for each test.
            Map<String, List<Double>> map = new HashMap();
            for (Result result = scanner.next(); (result != null); result = scanner.next()) {
                for (KeyValue keyValue : result.list()) {
                    TestReportMessage message = VtsReportMessage.TestReportMessage.
                        parseFrom(keyValue.getValue());
                    for (ProfilingReportMessage profilingReportMessage : message.getProfilingList()) {
                        String profilingPointName =  new String(profilingReportMessage.getName().toByteArray(), "UTF-8");
                        double timeTaken = ((double)(profilingReportMessage.getEndTimestamp() - profilingReportMessage.
                            getStartTimestamp())) / 1000;
                        if (timeTaken < 0) {
                            logger.info("Inappropriate value for time taken");
                            continue;
                        }
                        if (!map.containsKey(profilingPointName)) {
                            map.put(profilingPointName, new ArrayList<Double>());
                        }
                        map.get(profilingPointName).add(timeTaken);
                    }
                }
            }

            // Result map for jsp
            Map<String, List<String>> resultMap = new HashMap<String, List<String>>();
            for (String key : map.keySet()) {
                double[] values = new double[map.get(key).size()];
                for (int i = 0; i < values.length; i++) {
                    values[i] = map.get(key).get(i);
                }

                List<String> resultList = new ArrayList<String>();
                int[] percentiles = {99, 95, 90, 80, 75, 50, 25, 10};
                for (int percentile : percentiles) {
                    resultList.add(String.valueOf(percentile) +" percentile : " + String.valueOf((double)
                        Math.round(new Percentile().evaluate(values, percentile))));
                }
                resultMap.put(key, resultList);
            }


            request.setAttribute("map", map);
            if (resultMap.isEmpty()) {
                request.setAttribute("error", PROFILING_DATA_ERROR);
            }
            request.setAttribute("resultMap", resultMap);
            request.setAttribute("table_name", table.getName());
            dispatcher = request.getRequestDispatcher("/show_table.jsp");
            try {
                dispatcher.forward(request, response);
            } catch (ServletException e) {
                logger.error("Servlet Excpetion caught : ", e);
            }
        } else {
            response.sendRedirect(userService.createLoginURL(request.getRequestURI()));
        }
    }
}
