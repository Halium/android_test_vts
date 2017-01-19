<%--
  ~ Copyright (c) 2016 Google Inc. All Rights Reserved.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License"); you
  ~ may not use this file except in compliance with the License. You may
  ~ obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
  ~ implied. See the License for the specific language governing
  ~ permissions and limitations under the License.
  --%>
<%@ page contentType='text/html;charset=UTF-8' language='java' %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>
<%@ taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core'%>

<html>
  <head>
    <title>VTS Table</title>
    <link rel='icon' href='//www.gstatic.com/images/branding/googleg/1x/googleg_standard_color_32dp.png' sizes='32x32'>
    <link rel='stylesheet' href='https://fonts.googleapis.com/icon?family=Material+Icons'>
    <link rel='stylesheet' href='https://fonts.googleapis.com/css?family=Roboto:100,300,400,500,700'>
    <link rel='stylesheet' href='https://www.gstatic.com/external_hosted/materialize/all_styles-bundle.css'>
    <link rel="stylesheet" href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.12.0/jquery-ui.css">
    <link type='text/css' href='/css/navbar.css' rel='stylesheet'>
    <link type="text/css" href="/css/datepicker.css" rel="stylesheet">
    <link type='text/css' href='/css/show_performance_digest.css' rel='stylesheet'>
    <script src='/js/analytics.js' type='text/javascript'></script>
    <script type='text/javascript' src='https://ajax.googleapis.com/ajax/libs/jquery/3.1.0/jquery.min.js'></script>
    <script src='https://www.gstatic.com/external_hosted/materialize/materialize.min.js'></script>
    <script src='https://www.gstatic.com/external_hosted/moment/min/moment-with-locales.min.js'></script>
    <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.js"></script>
    <script type='text/javascript'>
      if (${analytics_id}) analytics_init(${analytics_id});

      ONE_DAY = 86400000000;
      MICRO_PER_MILLI = 1000;

      function load() {
          var time = $('#date').datepicker('getDate').getTime() - 1;
          time = time * MICRO_PER_MILLI + ONE_DAY;  // end of day
          var ctx = '${pageContext.request.contextPath}';
          var link = ctx + '/show_performance_digest?profilingPoint=${profilingPointName}' +
              '&testName=${testName}' +
              '&startTime=' + time;
          if ($('#device-select').prop('selectedIndex') > 1) {
              link += '&device=' + $('#device-select').val();
          }
          window.open(link,'_self');
      }

      $(function() {
          var date = $('#date').datepicker({
              showAnim: "slideDown",
              maxDate: new Date()
          });
          date.datepicker('setDate', new Date(${startTime} / MICRO_PER_MILLI));
          $('#load').click(load);

          $('.date-label').each(function(i) {
              var label = $(this);
              label.html(moment(parseInt(label.html())).format('M/D/YY'));
          });
          $('select').material_select();
      });

    </script>

    <nav id='navbar'>
      <div class='nav-wrapper'>
        <span>
          <a href='${pageContext.request.contextPath}/' class='breadcrumb'>VTS Dashboard Home</a>
          <a href='${pageContext.request.contextPath}/show_table?testName=${testName}' class='breadcrumb'>${testName}</a>
          <a href="#!" class="breadcrumb">Performance Digest</a>
        </span>
        <ul class='right'><li>
          <a id='dropdown-button' class='dropdown-button btn red lighten-3' href='#' data-activates='dropdown'>
            ${email}
          </a>
        </li></ul>
        <ul id='dropdown' class='dropdown-content'>
          <li><a href='${logoutURL}'>Log out</a></li>
        </ul>
      </div>
    </nav>
  </head>

  <body>
    <div class='container'>
      <div class='row card'>
        <div id='header-container' class='col s12'>
          <div class='col s12'>
            <h4>Daily Performance Digest</h4>
          </div>
          <div id='device-select-wrapper' class='input-field col s6 m3 offset-m6'>
            <select id='device-select'>
              <option value='' disabled>Select device</option>
              <option value='0' ${empty selectedDevice ? 'selected' : ''}>All Devices</option>
              <c:forEach items='${devices}' var='device' varStatus='loop'>
                <option value=${device} ${selectedDevice eq device ? 'selected' : ''}>${device}</option>
              </c:forEach>
            </select>
          </div>
          <input type='text' id='date' name='date' class='col s5 m2'>
          <a id='load' class='btn-floating btn-medium red right waves-effect waves-light'>
            <i class='medium material-icons'>cached</i>
          </a>
        </div>
      </div>
      <div class='row'>
        <c:forEach items='${tables}' var='table' varStatus='loop'>
          <div class='col s12 card summary'>
            <div class='col s3 valign'>
              <h5>Profiling Point:</h5>
            </div>
            <div class='col s9 right-align valign'>
              <h5 class="profiling-name truncate">${tableTitles[loop.index]}</h5>
            </div>
            ${table}
            <span class='profiling-subtitle'>
              ${tableSubtitles[loop.index]}
            </span>
          </div>
        </c:forEach>
      </div>
    </div>

    <footer class='page-footer'>
      <div class='footer-copyright'>
          <div class='container'>© 2016 - The Android Open Source Project
          </div>
      </div>
    </footer>
  </body>
</html>
