<%
/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Forderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/
%>
<%@page import="de.mpg.escidoc.services.framework.PropertyReader"%>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="../resources/escidoc-css/css/main.css" />
		<title>eSciDoc SearchandExport Service</title>
		<script type="text/javascript" id="script">

			// This function is called to send the request.
			function submitItem()
			{
				var queryString =  '?cqlQuery=' + document.form.cqlQuery.value;
				
				queryString += '&exportFormat=' + document.form.exportFormat.options[document.form.exportFormat.selectedIndex].value;
				queryString += '&outputFormat=' + document.form.outputFormat.options[document.form.outputFormat.selectedIndex].value;
				queryString += '&language=' + document.form.language.options[document.form.language.selectedIndex].value;
				
				var req = document.form.url.value  + queryString;
				
				document.getElementById('result').innerHTML = req;   
				
				//window.open(req,'','height=100, width=100, toolbar=no, scrollbars=yes, resizable=yes');
				location.href = req;
			}
			
		</script>
	</head>
	<body>
		<div id="col3">
			<div class="content">
		<h1 class="topSpace">
			eSciDoc SearchAndExport Service REST interface sample application
		</h1>
		<div class="topSpace">
		<form name="form" method="post" action="rest">
			<div class="editItemSingleCol">
				<label class="colLbl">This is the CQL search query:</label><br/>
				<input type="text" size="100" name="cqlQuery" value="escidoc.metadata=test"></input>
			</div>
			<div class="editItemSingleCol">
				<label class="colLbl"><strong>Info:</strong> You can find
				<a href="http://colab.mpdl.mpg.de/mediawiki/PubMan_Indexing" target="_blank" >here</a> 
				all indexes are allowed.</label><br/>
			</div>
			<div class="editItemSingleCol">
				<label class="colLbl">Choose a Database Index:</label><br/>
				<select size="1" name="language" style="width:120px">
					<option value="all">all languages</option>
					<option value="en">en</option>
					<option value="de">de</option>
				</select>
			</div>
			<div class="editItemSingleCol">
				<label class="colLbl">Choose an Export format:</label><br/>
				<select size="1" name="exportFormat" onchange="checkEndNote()" style="width:120px">
					<option value="APA">APA</option>
					<option value="ENDNOTE">EndNote</option>
				</select>
			</div>
			<div class="editItemSingleCol">
				<label class="colLbl">Choose an Output format:</label><br/>
				<select size="1" name="outputFormat" style="width:120px">
					<option value="pdf">PDF</option>
					<option value="html">HTML</option>
					<option value="rtf">RTF</option>
					<option value="odt">ODT</option>
					<option value="snippet">SNIPPET</option>
				</select>
			</div>
			<div class="editItemSingleCol">
				<label class="colLbl">This is the address where to send to:</label><br/>
				<input type="text" name="url" size="100" value="<%= (request.getProtocol().contains("HTTPS") ? "https" : "http") %>://<%= request.getServerName() %><%= (request.getServerPort() != 80 ? ":" + request.getServerPort() : "") %><%= request.getContextPath() %>/SearchAndExport"/>
			</div>
			<div class="editItemSingleCol">
				<label class="colLbl">Then submit it here:</label><br/>
				<input type="button" class="inlineButton" value="Submit" onclick="submitItem()"/>
			</div>
			<div class="editItemSingleCol">
				<label class="colLbl">The complete URI:</label><br/>
				<pre id="result" style="background-color: #EEEEEE"></pre>
			</div>
		</form>
		</div>
			</div>
		</div>
	</body>
</html>