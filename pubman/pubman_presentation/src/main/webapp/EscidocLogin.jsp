<!DOCTYPE html>
<!--

 CDDL HEADER START

 The contents of this file are subject to the terms of the
 Common Development and Distribution License, Version 1.0 only
 (the "License"). You may not use this file except in compliance
 with the License.

 You can obtain a copy of the license at license/ESCIDOC.LICENSE
 or http://www.escidoc.org/license.
 See the License for the specific language governing permissions
 and limitations under the License.

 When distributing Covered Code, include this CDDL HEADER in each
 file and include the License file at license/ESCIDOC.LICENSE.
 If applicable, add the following below this CDDL HEADER, with the
 fields enclosed by brackets "[]" replaced with your own identifying
 information: Portions Copyright [yyyy] [name of copyright owner]

 CDDL HEADER END


 Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 für wissenschaftlich-technische Information mbH and Max-Planck-
 Gesellschaft zur Förderung der Wissenschaft e.V.
 All rights reserved. Use is subject to license terms.
-->



	 

	
	<f:view encoding="UTF-8" locale="#{InternationalizationHelper.userLocale}" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html" xmlns:ui="http://java.sun.com/jsf/facelets" xmlns:p="http://primefaces.org/ui">
			<f:loadBundle var="lbl" basename="Label"/>
			<f:loadBundle var="msg" basename="Messages"/>
			<f:loadBundle var="tip" basename="Tooltip"/>
		<html xmlns="http://www.w3.org/1999/xhtml">
			<h:head>

				<title><h:outputText value="#{ApplicationBean.appTitle}"/></title>
				<link href="/common/resources/cssFramework/main.css" type="text/css" rel="stylesheet" />
				<h:outputText value="#{ApplicationBean.pubmanStyleTags}" escape="false"/>
				<style type="text/css">
					/*.headerLogo {background-image: url("<h:outputText value='#{ApplicationBean.logoUrl}'/>") !important; background-repeat: norepeat; background-position: top left;}*/
					.headerLogo {background-image: none; <h:outputText value='#{ApplicationBean.additionalLogoCss}'/>}
				</style>
				<style type="text/css">
					.fa { line-height: inherit; margin-right: 0.454545em; color: #004465;}
				</style>

			</h:head>
			<body lang="${InternationalizationHelper.locale}">
			
			
			<div class="full wrapper">
			<h:inputHidden id="offset"></h:inputHidden>
			
				<div class="full_area0 header clear">
					<!-- begin: header section (including meta menu, logo, searchMenu and main menu)-->
						<!-- import meta menu here -->
						<span id="metaMenuSkipLinkAnchor" class="full_area0 metaMenu">
						</span>
						<div class="full_area0 LogoNSearch">
					
							<h:outputLink id="lnkStartPage" title="#{tip.navigation_lblStartpage}" value="#{ApplicationBean.instanceContextPath}/HomePage.jsp">
								<h:graphicImage styleClass="tiny_marginLExcl headerLogo" style="border:none;" url="#{ApplicationBean.logoUrl}"></h:graphicImage>
								<h:panelGroup styleClass="tiny_marginLExcl xDouble_area0 themePark #{Header.serverLogo}"></h:panelGroup>
							</h:outputLink>
							
					
							<!-- import search here-->
							
						</div>
						
					</div>
					<!-- import main menu here -->
					<div id="mainMenuSkipLinkAnchor" class="full_area0 mainMenu" >
					</div>
				<h:form >
				<div id="content" class="full_area0 clear">
				<!-- begin: content section (including elements that visualy belong to the header (breadcrumb, headline, subheader and content menu)) -->
					<div class="clear">
						<div class="headerSection">
				
							<div id="contentSkipLinkAnchor" class="clear headLine">
								<!-- Headline starts here -->
								<h1><h:outputText value="#{lbl.login_btLogin}"/></h1>
								<!-- Headline ends here -->
							</div>
						</div>
						<!-- MessageArea starts here -->
							<div class="subHeader">
								<!-- Subheadline starts here -->
								<h:messages styleClass="singleMessage" errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{EscidocLogin.numberOfMessages == 1}"/>
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea errorMessageArea" rendered="#{EscidocLogin.hasErrorMessages and EscidocLogin.numberOfMessages != 1}">
									<h2><h:outputText value="#{lbl.warning_lblMessageHeader}"/></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{EscidocLogin.hasMessages}"/>
								</h:panelGroup>
								<h:panelGroup layout="block" styleClass="half_area2_p6 messageArea infoMessageArea" rendered="#{EscidocLogin.hasMessages and !EscidocLogin.hasErrorMessages and EscidocLogin.numberOfMessages != 1}">
									<h2><h:outputText value="#{lbl.info_lblMessageHeader}"/></h2>
									<h:messages errorClass="messageError" warnClass="messageWarn" fatalClass="messageFatal" infoClass="messageStatus" layout="list" globalOnly="true" showDetail="false" showSummary="true" rendered="#{EscidocLogin.hasMessages}"/>
								</h:panelGroup>
								<h:outputText value="&#160;" rendered="#{!EscidocLogin.hasErrorMessages}" />
								<!-- Subheadline ends here -->
							</div>
						<!-- MessageArea ends here -->
					</div>
					<div class="full_area0">
						<div class="full_area0 fullItem">
								<h:panelGroup layout="block" styleClass="third_area0 tiny_marginRExcl small_marginLExcl tile_category borderDarkTurquoise" >
									<!-- Citation title and icon -->
									<h:panelGroup layout="block" styleClass="third_area0_p6">
										<h5 class="tile_citation_title">
											<img src="../resources/images/overviewPage/MPG_authors_64.png" class="big_imgBtn" align="right"/>
											<h:outputText value="#{lbl.login_btLogin}" />
										</h5>
									</h:panelGroup>
									<h:panelGroup layout="block" styleClass="third_area0_p6">
										<h:panelGroup styleClass="quad_area0_p8 xTiny_marginLExcl endline">
											<h:outputText value="#{lbl.userID}" styleClass="double_label"/>
											<h:inputText value="#{EscidocLogin.username}" styleClass="double_txtInput username"/>
										</h:panelGroup>
										<!-- Reenter password -->
										<h:panelGroup styleClass="quad_area0_p8 xTiny_marginLExcl endline">
											<h:outputText value="#{lbl.userPW}" styleClass="double_label"/>
											<h:inputSecret value="#{EscidocLogin.password}" styleClass="double_txtInput password"/>
										</h:panelGroup>
										<!-- Update password -->
										<h:panelGroup styleClass="quad_area0_p8 xTiny_marginLExcl endline">
											<h:commandButton styleClass="free_area1_p8 activeButton" value="#{lbl.login_btLogin}" action="#{EscidocLogin.login}" />
										</h:panelGroup>
										
										<h:panelGroup styleClass="quad_area0_p8 xTiny_marginLExcl endline">
										</h:panelGroup>

									</h:panelGroup>
									
								</h:panelGroup>
						</div>
					</div>	
				</div>
				<!-- end: content section -->
				</h:form>
			</div>
			<ui:include src="footer/Footer.jspf" />
			
			<script type="text/javascript">
				var passArea = $('.passArea'); 
				passArea.find("input[type=password]").keyup(function(keyEvent) {
					var key = keyEvent.keyCode;
					if(key == '13') {
						passArea.find('.activeButton').trigger("click");
					};
				});
				
				$("input[id$='offset']").submit(function() {
					$(this).val($(window).scrollTop());
				});
				$(document).ready(function () {
					$(window).scrollTop($("input[id$='offset']").val());
					$(window).scroll(function(){$("input[id$='offset']").val($(window).scrollTop());});
				});
			</script>
			</body>
		</html>
	</f:view>
