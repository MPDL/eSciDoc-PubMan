<!--
	CDDL HEADER START The contents of this file are subject to the terms
	of the Common Development and Distribution License, Version 1.0 only
	(the "License"). You may not use this file except in compliance with
	the License. You can obtain a copy of the license at
	license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the
	License for the specific language governing permissions and
	limitations under the License. When distributing Covered Code, include
	this CDDL HEADER in each file and include the License file at
	license/ESCIDOC.LICENSE. If applicable, add the following below this
	CDDL HEADER, with the fields enclosed by brackets "[]" replaced with
	your own identifying information: Portions Copyright [yyyy] [name of
	copyright owner] CDDL HEADER END Copyright 2006-2010
	Fachinformationszentrum Karlsruhe Gesellschaft für
	wissenschaftlich-technische Information mbH and Max-Planck-
	Gesellschaft zur Förderung der Wissenschaft e.V. All rights reserved.
	Use is subject to license terms.
-->
<!--
	Transformation from eSciDoc PubItem metadata to OAI DC (OpenAire compliant):
	Author: Matthias Walter (initial creation) $Author: $ (last changed)
	$Revision: $ $LastChangedDate: $
-->
<xsl:stylesheet version="2.0" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:eterms="http://purl.org/escidoc/metadata/terms/0.1/" xmlns:event="http://purl.org/escidoc/metadata/profiles/0.1/event" xmlns:file="http://purl.org/escidoc/metadata/profiles/0.1/file" xmlns:foxml="info:fedora/fedora-system:def/foxml#" xmlns:organization="http://purl.org/escidoc/metadata/profiles/0.1/organization" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:person="http://purl.org/escidoc/metadata/profiles/0.1/person" xmlns:publication="http://purl.org/escidoc/metadata/profiles/0.1/publication" xmlns:project="http://purl.org/escidoc/metadata/profiles/0.1/project" xmlns:prop="http://escidoc.de/core/01/properties/" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:source="http://purl.org/escidoc/metadata/profiles/0.1/source" xmlns:xalan="http://xml.apache.org/xalan" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:javaUtil="xalan://de.escidoc.core.common.util.xml.transformer.XmlUtil">

    <xsl:output method="xml" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>
    <xsl:preserve-space elements="*"/>

    <xsl:param name="handle-service-url" select="'http://hdl.handle.net/'"/>
    <xsl:param name="PID" select="''" />
    <xsl:param name="COMPONENT_IDS" select="''" />
    <xsl:param name="VISIBILITY" select="''" />

    <xsl:variable name="genreMappingEscidocToOpenAire">
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/article">info:eu-repo/semantics/article</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/newspaper-article">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/eprint/type/Book">info:eu-repo/semantics/book</genre>
        <genre type="http://purl.org/eprint/type/BookItem">info:eu-repo/semantics/bookPart</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/proceedings">info:eu-repo/semantics/conferenceObject</genre>
        <genre type="http://purl.org/eprint/type/ConferencePaper">info:eu-repo/semantics/conferenceObject</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/meeting-abstract">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/talk-at-event">info:eu-repo/semantics/lecture</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/conference-report">info:eu-repo/semantics/conferenceObject</genre>
        <genre type="http://purl.org/eprint/type/ConferencePoster">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/courseware-lecture">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/paper">info:eu-repo/semantics/workingPaper</genre>
        <genre type="http://purl.org/eprint/type/Report">info:eu-repo/semantics/report</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/issue">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/journal">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/manuscript">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/series">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/other">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/editorial">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-handbook">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-encyclopedia">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-festschrift">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-commentary">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/contribution-to-collected-edition">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/book-review">info:eu-repo/semantics/review</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/opinion">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/case-study">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/case-note">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/monograph">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/newspaper">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/encyclopedia">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/multi-volume">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/commentary">info:eu-repo/semantics/annotation</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/handbook">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/collected-edition">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/festschrift">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/manual">info:eu-repo/semantics/other</genre>
        <genre type="http://purl.org/eprint/type/Patent">info:eu-repo/semantics/patent</genre>
        <genre type="http://purl.org/escidoc/metadata/ves/publication-types/film">info:eu-repo/semantics/other</genre>
    </xsl:variable>

    <xsl:variable name="genreMapping" select="xalan:nodeset($genreMappingEscidocToOpenAire)"/>

    <xsl:variable name="visibilityMappingEscidocToOpenAire">
        <visibility type="public">info:eu-repo/semantics/openAccess</visibility>
        <visibility type="audience">info:eu-repo/semantics/restrictedAccess</visibility>
        <visibility type="private">info:eu-repo/semantics/closedAccess</visibility>
    </xsl:variable>

    <xsl:variable name="visibilityMapping" select="xalan:nodeset($visibilityMappingEscidocToOpenAire)"/>

    <xsl:variable name="filesTreeFrag">
        <xsl:if test="$COMPONENT_IDS">
            <xsl:for-each select="xalan:tokenize($COMPONENT_IDS, ',')">
                <xsl:variable name="fedora-item" select="javaUtil:getXmlDocumentWithAuthentication(.)"/>
                <file>
                    <xsl:copy-of select="xalan:nodeset($fedora-item)"/>
                </file>
            </xsl:for-each>
        </xsl:if>
    </xsl:variable>

    <xsl:variable name="files" select="xalan:nodeset($filesTreeFrag)"/>

    <xsl:template match="node()|@*">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="publication:publication">
        <oai_dc:dc xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
            <!-- dc:type -->
            <dc:type>
                <xsl:variable name="publication-type">
                    <xsl:value-of select="./@type"/>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="@type = 'http://purl.org/eprint/type/Thesis'">
                        <xsl:choose>
                            <xsl:when test="eterms:degree = 'http://purl.org/escidoc/metadata/ves/academic-degrees/master'">
                                <xsl:value-of select="'info:eu-repo/semantics/masterThesis'"/>
                            </xsl:when>
                            <xsl:when test="eterms:degree = 'http://purl.org/escidoc/metadata/ves/academic-degrees/diploma'">
                                <xsl:value-of select="'info:eu-repo/semantics/other'"/>
                            </xsl:when>
                            <xsl:when test="eterms:degree = 'http://purl.org/escidoc/metadata/ves/academic-degrees/magister'">
                                <xsl:value-of select="'info:eu-repo/semantics/other'"/>
                            </xsl:when>
                            <xsl:when test="eterms:degree = 'http://purl.org/escidoc/metadata/ves/academic-degrees/phd'">
                                <xsl:value-of select="'info:eu-repo/semantics/doctoralThesis'"/>
                            </xsl:when>
                            <xsl:when test="eterms:degree = 'http://purl.org/escidoc/metadata/ves/academic-degrees/staatsexamen'">
                                <xsl:value-of select="'info:eu-repo/semantics/other'"/>
                            </xsl:when>
                            <xsl:when test="eterms:degree = 'http://purl.org/escidoc/metadata/ves/academic-degrees/habilitation'">
                                <xsl:value-of select="'info:eu-repo/semantics/other'"/>
                            </xsl:when>
                            <xsl:when test="eterms:degree = 'http://purl.org/escidoc/metadata/ves/academic-degrees/bachelor'">
                                <xsl:value-of select="'info:eu-repo/semantics/bachelorThesis'"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="'info:eu-repo/semantics/other'"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:when test="$genreMapping/genre[@type=$publication-type]">
                        <xsl:value-of select="$genreMapping/genre[@type=$publication-type]"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="@type" />
                    </xsl:otherwise>
                </xsl:choose>
            </dc:type>

            
            <!-- CREATORS -->
            <xsl:for-each select="./eterms:creator">

                <xsl:choose>
                    <xsl:when test="@role = 'http://www.loc.gov/loc.terms/relators/AUT' and not(./organization:organization)">
                        <dc:creator>
                            <xsl:call-template name="person">
                                <xsl:with-param name="person" select="person:person"/>
                            </xsl:call-template>
                        </dc:creator>
                    </xsl:when>
                    <xsl:when test="@role = 'http://www.loc.gov/loc.terms/relators/EDT' or ./organization:organization">
                        <dc:contributor>
                            <xsl:choose>
                                <xsl:when test="./person:person">
                                    <xsl:call-template name="person">
                                        <xsl:with-param name="person" select="./person:person"/>
                                    </xsl:call-template>
                                </xsl:when>
                                <xsl:when test="./organization:organization">
                                    <xsl:call-template name="organization">
                                        <xsl:with-param name="organization" select="./organization:organization"/>
                                    </xsl:call-template>
                                </xsl:when>
                            </xsl:choose>
                        </dc:contributor>
                    </xsl:when>
                </xsl:choose>

            </xsl:for-each>

            <!-- dc:title -->
            <xsl:variable name="title" select="normalize-space(./dc:title)"/>
            <xsl:if test="$title != ''">
                <dc:title>
                    <xsl:value-of select="$title"/>
                </dc:title>
            </xsl:if>

            <!-- dc:language -->
            <xsl:for-each select="./dc:language">
                <xsl:if test=". != ''">
                    <dc:language>
                        <xsl:value-of select="normalize-space(.)"/>
                    </dc:language>
                </xsl:if>
            </xsl:for-each>

            <!-- dcterms:alternative -> dc:title -->
            <xsl:for-each select="./dcterms:alternative">
                <dc:title>
                    <xsl:value-of select="." />
                </dc:title>
            </xsl:for-each>

            <!-- dc:identifiers -->
            <xsl:if test="$PID != ''">
                <dc:identifier>
                    <xsl:value-of select="concat($handle-service-url, substring-after($PID, 'hdl:'))"/>
                </dc:identifier>
            </xsl:if>

            <xsl:for-each select="$files/file">
                <xsl:if test="normalize-space(./foxml:digitalObject/foxml:datastream[@ID = 'RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description/prop:pid) != ''">
                    <dc:identifier>
                        <xsl:value-of select="concat($handle-service-url, substring-after(./foxml:digitalObject/foxml:datastream[@ID = 'RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description/prop:pid, 'hdl:'))"/>
                    </dc:identifier>
                </xsl:if>
            </xsl:for-each>
            
            <!-- dc:publisher -->
            <xsl:variable name="publisher" select="normalize-space(./eterms:publishing-info/dc:publisher)"/>
            <xsl:if test="$publisher != ''">
                <dc:publisher>
                    <xsl:value-of select="$publisher"/>
                </dc:publisher>
            </xsl:if>

            <!-- dc:date -->
            <xsl:variable name="date">
                <xsl:choose>
                    <xsl:when test="./dcterms:issued != ''">
                        <xsl:value-of select="./dcterms:issued"/>
                    </xsl:when>
                    <xsl:when test="./eterms:published-online != ''">
                        <xsl:value-of select="./eterms:published-online"/>
                    </xsl:when>
                    <xsl:when test="./dcterms:dateAccepted != ''">
                        <xsl:value-of select="./dcterms:dateAccepted"/>
                    </xsl:when>
                    <xsl:when test="./dcterms:dateSubmitted != ''">
                        <xsl:value-of select="./dcterms:dateSubmitted"/>
                    </xsl:when>
                    <xsl:when test="./dcterms:modified != ''">
                        <xsl:value-of select="./dcterms:modified"/>
                    </xsl:when>
                    <xsl:when test="./dcterms:created != ''">
                        <xsl:value-of select="./dcterms:created"/>
                    </xsl:when>
                    <xsl:when test="./event:event/eterms:start-date != ''">
                        <xsl:value-of select="./event:event/eterms:start-date"/>
                    </xsl:when>
                </xsl:choose>
            </xsl:variable>

            <xsl:if test="$date != ''">
                <dc:date>
                    <xsl:value-of select="$date" />
                </dc:date>
            </xsl:if>

            <!-- dc:sources -->
            <!--
            <xsl:variable name="source">
                <xsl:value-of select="./eterms:publishing-info/eterms:place" />
                <xsl:value-of select="concat(' ', ./eterms:publishing-info/eterms:edition)" />
                <xsl:value-of select="concat(' ', ./source:source[1]/dc:title)" />
                <xsl:for-each select="./source:source[1][@type = 'http://purl.org/eprint/type/Book' or @type = 'http://purl.org/escidoc/metadata/ves/publication-types/proceedings' or @type = 'http://purl.org/escidoc/metadata/ves/publication-types/issue' or @type = 'http://purl.org/escidoc/metadata/ves/publication-types/other']/eterms:creator">
                    <xsl:if test="./person:person!=''">
                        <xsl:value-of select="concat(' ', ./person:person/eterms:family-name, ', ', ./person:person/eterms:given-name)" />
                    </xsl:if>
                    <xsl:if test="./organization:organization/dc:title!=''">
                        <xsl:value-of select="concat(' ', ./organization:organization/dc:title)" />
                    </xsl:if>
                </xsl:for-each>
                <xsl:value-of select="concat(' ', ./source:source[1]/eterms:volume)" />
                <xsl:value-of select="concat(' ', ./source:source[1]/eterms:issue)" />
                <xsl:for-each select="./source:source[1][@type = 'http://purl.org/escidoc/metadata/ves/publication-types/journal' or @type = 'http://purl.org/escidoc/metadata/ves/publication-types/journal']/eterms:publishing-info">
                    <xsl:value-of select="concat(' ', ./dc:publisher, ' ', ./eterms:place, ' ', ./eterms:edition)" />
                </xsl:for-each>
            </xsl:variable>
            
            <xsl:if test="$source != ''">
                <dc:source>
                    <xsl:value-of select="normalize-space($source)" />
                </dc:source>
            </xsl:if>
            -->

            <!-- dc:format -->
            <xsl:for-each select="$files/file">
                <xsl:if test="normalize-space(./foxml:digitalObject/foxml:datastream[@ID = 'escidoc']/foxml:datastreamVersion[last()]/foxml:xmlContent/file:file/dc:format[1]) != ''">
                    <dc:format>
                        <xsl:value-of select="./foxml:digitalObject/foxml:datastream[@ID = 'escidoc']/foxml:datastreamVersion[last()]/foxml:xmlContent/file:file/dc:format[1]"/>
                    </dc:format>
                </xsl:if>
            </xsl:for-each>
            <!--
            <xsl:variable name="format">
                <xsl:choose>
                    <xsl:when test="./source:source[1]/eterms:start-page and source:source[1]/eterms:end-page">
                        <xsl:value-of select="normalize-space(concat(./source:source[1]/eterms:start-page, '-', source:source[1]/eterms:end-page, ' ', source:source[1]/eterms:total-number-of-pages ))" />
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="normalize-space(./source:source[1]/eterms:total-number-of-pages)" />
                    </xsl:otherwise>
                </xsl:choose>    
            </xsl:variable>

            <xsl:if test="$format != ''">
                <dc:format>
                    <xsl:value-of select="$format" />
                </dc:format>
            </xsl:if>
            -->

            <!-- dc:relation -->
            <xsl:for-each select="./project:project-info">
				<dc:relation>
					<xsl:variable name="relationString" select="concat('info:eu-repo/grantAgreement/', 
						./project:funding-info/project:funding-organization/dc:identifier,
						'/',
						./project:funding-info/project:funding-program/dc:identifier,
						'/',
						./dc:identifier
						)" />
					<xsl:value-of select="$relationString" />
				</dc:relation>
            </xsl:for-each>

            <xsl:for-each select="./dc:identifier">
                <dc:relation>
                    <xsl:choose>
                        <xsl:when test="@xsi:type = 'eterms:ISSN'">
                            <xsl:value-of select="concat('info:eu-repo/semantics/altIdentifier/pissn/', normalize-space(.))" />
                        </xsl:when>
                        <xsl:when test="@xsi:type = 'eterms:ISBN'">
                            <xsl:value-of select="concat('info:eu-repo/semantics/altIdentifier/isbn/', normalize-space(.))" />
                        </xsl:when>
                        <xsl:when test="@xsi:type = 'eterms:DOI'">
                            <xsl:value-of select="concat('info:eu-repo/semantics/altIdentifier/doi/', normalize-space(.))" />
                        </xsl:when>
                        <xsl:when test="@xsi:type = 'eterms:ARXIV'">
                            <xsl:value-of select="concat('info:eu-repo/semantics/altIdentifier/arxiv/', normalize-space(.))" />
                        </xsl:when>
                        <xsl:when test="@xsi:type = 'eterms:PMID'">
                            <xsl:value-of select="concat('info:eu-repo/semantics/altIdentifier/pmid/', normalize-space(.))" />
                        </xsl:when>
                        <xsl:when test="@xsi:type = 'eterms:URN'">
                            <xsl:value-of select="concat('info:eu-repo/semantics/altIdentifier/urn/', normalize-space(.))" />
                        </xsl:when>
                        <xsl:when test="@xsi:type = 'eterms:URI'">
                            <xsl:value-of select="concat('info:eu-repo/semantics/altIdentifier/urn/', normalize-space(.))" />
                        </xsl:when>
                    </xsl:choose>
                </dc:relation>
            </xsl:for-each>

            <!-- dc:description -->
            <xsl:for-each select="./dcterms:abstract">
                <xsl:if test=". != ''">
                    <dc:description>
                        <xsl:value-of select="normalize-space(.)" />
                    </dc:description>
                </xsl:if>
            </xsl:for-each>
            <xsl:variable name="table-of-contents" select="normalize-space(./dcterms:tableOfContents)"/>
            <xsl:if test="$table-of-contents!=''">
                <dc:description>
                    <xsl:value-of select="$table-of-contents" />
                </dc:description>
            </xsl:if>
            
            <!-- dc:rights -->
            <dc:rights>
                <xsl:choose>
                    <xsl:when test="$files/file/foxml:digitalObject/foxml:datastream[@ID = 'content']/foxml:datastreamVersion[last()]/foxml:contentLocation[@TYPE='INTERNAL_ID']/../../../foxml:datastream[@ID = 'RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description[prop:visibility = 'public']/prop:content-category = 'http://purl.org/escidoc/metadata/ves/content-categories/any-fulltext' 
                                        or $files/file/foxml:digitalObject/foxml:datastream[@ID = 'content']/foxml:datastreamVersion[last()]/foxml:contentLocation[@TYPE='INTERNAL_ID']/../../../foxml:datastream[@ID = 'RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description[prop:visibility = 'public']/prop:content-category = 'http://purl.org/escidoc/metadata/ves/content-categories/publisher-version'
                                        or $files/file/foxml:digitalObject/foxml:datastream[@ID = 'content']/foxml:datastreamVersion[last()]/foxml:contentLocation[@TYPE='INTERNAL_ID']/../../../foxml:datastream[@ID = 'RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description[prop:visibility = 'public']/prop:content-category = 'http://purl.org/escidoc/metadata/ves/content-categories/post-print'
                                        or $files/file/foxml:digitalObject/foxml:datastream[@ID = 'content']/foxml:datastreamVersion[last()]/foxml:contentLocation[@TYPE='INTERNAL_ID']/../../../foxml:datastream[@ID = 'RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description[prop:visibility = 'public']/prop:content-category = 'http://purl.org/escidoc/metadata/ves/content-categories/pre-print'">
                        <xsl:value-of select="'info:eu-repo/semantics/openAccess'"/>
                    </xsl:when>
                    <xsl:when test="$files/file/foxml:digitalObject/foxml:datastream[@ID = 'content']/foxml:datastreamVersion[last()]/foxml:contentLocation[@TYPE='INTERNAL_ID']/../../../foxml:datastream[@ID = 'RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description[prop:visibility = 'audience']/prop:content-category = 'http://purl.org/escidoc/metadata/ves/content-categories/any-fulltext' 
                                        or $files/file/foxml:digitalObject/foxml:datastream[@ID = 'content']/foxml:datastreamVersion[last()]/foxml:contentLocation[@TYPE='INTERNAL_ID']/../../../foxml:datastream[@ID = 'RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description[prop:visibility = 'audience']/prop:content-category = 'http://purl.org/escidoc/metadata/ves/content-categories/publisher-version'
                                        or $files/file/foxml:digitalObject/foxml:datastream[@ID = 'content']/foxml:datastreamVersion[last()]/foxml:contentLocation[@TYPE='INTERNAL_ID']/../../../foxml:datastream[@ID = 'RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description[prop:visibility = 'audience']/prop:content-category = 'http://purl.org/escidoc/metadata/ves/content-categories/post-print'
                                        or $files/file/foxml:digitalObject/foxml:datastream[@ID = 'content']/foxml:datastreamVersion[last()]/foxml:contentLocation[@TYPE='INTERNAL_ID']/../../../foxml:datastream[@ID = 'RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description[prop:visibility = 'audience']/prop:content-category = 'http://purl.org/escidoc/metadata/ves/content-categories/pre-print'">
                        <xsl:value-of select="'info:eu-repo/semantics/restrictedAccess'"/>
                    </xsl:when>
                    <xsl:when test="$files/file/foxml:digitalObject/foxml:datastream[@ID = 'content']/foxml:datastreamVersion[last()]/foxml:contentLocation[@TYPE='INTERNAL_ID']/../../../foxml:datastream[@ID = 'RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description[prop:visibility = 'private']/prop:content-category = 'http://purl.org/escidoc/metadata/ves/content-categories/any-fulltext' 
                                        or $files/file/foxml:digitalObject/foxml:datastream[@ID = 'content']/foxml:datastreamVersion[last()]/foxml:contentLocation[@TYPE='INTERNAL_ID']/../../../foxml:datastream[@ID = 'RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description[prop:visibility = 'private']/prop:content-category = 'http://purl.org/escidoc/metadata/ves/content-categories/publisher-version'
                                        or $files/file/foxml:digitalObject/foxml:datastream[@ID = 'content']/foxml:datastreamVersion[last()]/foxml:contentLocation[@TYPE='INTERNAL_ID']/../../../foxml:datastream[@ID = 'RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description[prop:visibility = 'private']/prop:content-category = 'http://purl.org/escidoc/metadata/ves/content-categories/post-print'
                                        or $files/file/foxml:digitalObject/foxml:datastream[@ID = 'content']/foxml:datastreamVersion[last()]/foxml:contentLocation[@TYPE='INTERNAL_ID']/../../../foxml:datastream[@ID = 'RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description[prop:visibility = 'private']/prop:content-category = 'http://purl.org/escidoc/metadata/ves/content-categories/pre-print'">
                        <xsl:value-of select="'info:eu-repo/semantics/closedAccess'"/>
                    </xsl:when>
                    <xsl:otherwise/>
                </xsl:choose>
                <!-- <xsl:value-of select="$visibilityMapping/visibility[@type=$VISIBILITY]" /> -->
            </dc:rights>
            
            <!-- dc:subject -->
            <xsl:for-each select="./dc:subject">
                <xsl:if test=". != ''">
                    <dc:subject>
                        <xsl:value-of select="normalize-space(.)" />
                    </dc:subject>
                </xsl:if>
            </xsl:for-each>

        </oai_dc:dc>
    </xsl:template>

    <xsl:template name="person">
        <xsl:param name="person"/>
        <xsl:value-of select="$person/eterms:family-name"/>
        <xsl:text>, </xsl:text>
        <xsl:if test="$person/eterms:given-name != ''">
            <xsl:value-of select="concat(substring($person/eterms:given-name, 1, 1), '.')"/>
        </xsl:if>
    </xsl:template>

    <xsl:template name="organization">
        <xsl:param name="organization" />
        <xsl:value-of select="$organization/dc:title" />
    </xsl:template>

</xsl:stylesheet>
