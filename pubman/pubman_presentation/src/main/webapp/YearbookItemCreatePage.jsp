<?xml version="1.0" encoding="UTF-8"?>
<!--

 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License"). You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.de/license.
 See the License for the specific language governing permissions
 and limitations under the License.

 When distributing Covered Code, include this CDDL HEADER in each
 file and include the License file at license/ESCIDOC.LICENSE.
 If applicable, add the following below this CDDL HEADER, with the
 fields enclosed by brackets "[]" replaced with your own identifying
 information: Portions Copyright [yyyy] [name of copyright owner]

 CDDL HEADER END


 Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->

<jsp:root version="2.1" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:rich="http://richfaces.org/rich" xmlns:a4j="http://richfaces.org/a4j" >

	<jsp:output doctype-root-element="html"
	       doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
	       doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" /> 

	<jsp:directive.page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"/>
	<f:view locale="#{InternationalizationHelper.userLocale}">
			<f:loadBundle var="lbl" basename="Label"/>
			<f:loadBundle var="msg" basename="Messages"/>
			<f:loadBundle var="tip" basename="Tooltip"/>
		<html xmlns="http://www.w3.org/1999/xhtml">
			<head>

				<title><h:outputText value="#{ApplicationBean.appTitle}"/></title>

				<jsp:directive.include file="header/ui/StandardImports.jspf" />
				<script src="./resources/commonJavaScript/jquery/jquery.jdialog.min.js" language="JavaScript" type="text/javascript">;</script>


			</head>
			<body lang="#{InternationalizationHelper.locale}">
			<h:outputText value="#{YearbookItemCreateBean.beanName}" styleClass="noDisplay" />
			<h:form >
			<div class="full wrapper">
			<h:inputHidden id="offset"></h:inputHidden>
			
				<jsp:directive.include file="header/Header.jspf" />

				<div id="content" class="full_area0 clear">
				<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
					<div class="clear">
						<div class="headerSection">
							
						<jsp:directive.include file="header/Breadcrumb.jspf" />
				
							<div id="contentSkipLinkAnchor" class="clear headLine">
								<!-- Headline starts here -->
								<h1><h:outputText value="#{lbl.YearbookItemCreatePage}" /></h1>
								<!-- Headline ends here -->
							</div>
						</div>
						<div class="small_marginLIncl subHeaderSection">

							<h:panelGroup layout="block" styleClass="contentMenu">
							<!-- content menu starts here -->
								<h:panelGroup layout="block" styleClass="free_area0 sub">
									<h:outputLink id="lnkMenuQAWorkspace" title="#{tip.chooseWorkspace_QAWorkspace}"  value="#{ApplicationBean.appContext}QAWSPage.jsp" rendered="#{LoginHelper.isModerator and ContextListSessionBean.moderatorContextListSize>0}">
										<h:outputText value="#{lbl.chooseWorkspace_optMenuQAWorkspace}" rendered="#{LoginHelper.isModerator and ContextListSessionBean.moderatorContextListSize>0}"/>
									</h:outputLink>
									
									<h:outputText styleClass="seperator void" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}" />
									<h:outputLink id="lnkSubmission_lnkImportWorkspaceMenu" title="#{tip.chooseWorkspace_ImportWorkspace}" value="#{ApplicationBean.appContext}ImportWorkspace.jsp" rendered="#{DepositorWSSessionBean.newSubmission and ContextListSessionBean.depositorContextListSize>0}">
										<h:outputText value="#{lbl.chooseWorkspace_optMenuImportWorkspace}"/>
									</h:outputLink>  
									
									<h:panelGroup id="txtMenuYearbookWorkspace" rendered="#{LoginHelper.isYearbookEditor}">
										<h:outputText styleClass="seperator void" />
										<h:outputText value="#{lbl.chooseWorkspace_optMenuYearbookWorkspace}"/>
									</h:panelGroup>
									
									
									<h:outputText styleClass="seperator void" rendered="#{BreadcrumbItemHistorySessionBean.lastPageIdentifier != 'ReportWorkspacePage' and LoginHelper.isReporter and ContextListSessionBean.moderatorContextListSize>0}" />
									<h:outputLink id="lnkMenuReportWorkspace" title="#{tip.chooseWorkspace_ReportWorkspace}" value="#{ApplicationBean.appContext}ReportWorkspacePage.jsp" rendered="#{BreadcrumbItemHistorySessionBean.lastPageIdentifier != 'ReportWorkspacePage' and LoginHelper.isReporter and ContextListSessionBean.moderatorContextListSize>0}">
										<h:outputText value="#{lbl.chooseWorkspace_optMenuReportWorkspace}"/>
									</h:outputLink>
								</h:panelGroup>
							<!-- content menu ends here -->
							</h:panelGroup>
							
							<div class="subHeader">
								<!-- Subheadline starts here -->
								<h:messages styleClass="singleMessage" errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{YearbookItemCreateBean.numberOfMessages == 1}"/>
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea" rendered="#{YearbookItemCreateBean.hasErrorMessages and YearbookItemCreateBean.numberOfMessages != 1}">
									<h2><h:outputText value="#{lbl.warning_lblMessageHeader}"/></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{YearbookItemCreateBean.hasMessages}"/>
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea" rendered="#{YearbookItemCreateBean.hasMessages and !YearbookItemCreateBean.hasErrorMessages and YearbookItemCreateBean.numberOfMessages != 1}">
									<h2><h:outputText value="#{lbl.info_lblMessageHeader}"/></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{YearbookItemCreateBean.hasMessages}"/>
								</h:panelGroup>
								<!-- Subheadline ends here -->
							</div>
						</div>
					</div>
					<div class="full_area0">
						<div id="fullItem" class="full_area0 fullItem">	
							<jsp:directive.include file="yearbook/CreateYearbookItem.jspf" />
						</div>
							
						<div class="full_area0 formButtonArea">
								<h:commandLink styleClass="free_area1_p8 activeButton" id="lnkSave" value="Save" action="#{YearbookItemCreateBean.save}" onclick="fullItemReloadAjax();"/>
						</div>
						
						
					</div>
				<!-- end: content section -->
				</div>
			</div>
			<jsp:directive.include file="footer/Footer.jspf" />
			</h:form>
			<script type="text/javascript">
			<![CDATA[	
				$pb("input[id$='offset']").submit(function() {
					$pb(this).val($pb(window).scrollTop());
				});
				$pb(document).ready(function () {
					$pb(window).scrollTop($pb("input[id$='offset']").val());
					$pb(window).scroll(function(){$pb("input[id$='offset']").val($pb(window).scrollTop());});
					var element = document.getElementById('selSelectedOrgUnit');
					if (element.options !=null && element.options.length == 2)
					{
						throb();
						$pb.getJSON('AffiliationsAsJSON.jsp', loadAffiliations);
					}
				});
				]]>
			</script>
			<script type="text/javascript">
				function fullItemReload()
				{
					document.getElementById('fullItem').style.opacity='0.4';
					document.getElementById('fullItem').style.bg='FFF';
					$pb('#fullItem :input :text').attr('readonly', true);
				    $pb('#fullItem :textarea').attr('readonly', true);
				}
			</script>
			</body>
		</html>
	</f:view>
</jsp:root>