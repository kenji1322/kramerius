<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/tlds/securedContent.tld" prefix="scrd"%>
<%@ taglib uri="/WEB-INF/tlds/cmn.tld" prefix="view"%>

<%@ page isELIgnored="false"%>

<view:object name="ga" clz="cz.incad.Kramerius.views.virtualcollection.ListOfCDKSourcesViewObject"></view:object>
<scrd:securedContent action="display_admin_menu" sendForbidden="true">

<view:kconfig var="harvestlibrary" key="cdk.harvestlibrary.button.show" defaultValue="true"></view:kconfig>
<view:kconfig var="harvesttitle" key="cdk.harvesttitle.button.show" defaultValue="true"></view:kconfig>
<view:kconfig var="harvestcollections" key="cdk.harvestcollections.button.show" defaultValue="true"></view:kconfig>
<view:kconfig var="publishing" key="cdk.publishing.button.show" defaultValue="true"></view:kconfig>

<div id="collections-content">


    <table  style="width:100%">
        <thead>
            <tr> 
                <td title="Displayable name" style="text-overflow: ellipsis;"><strong>Library</strong></td>
                <td width="100px" title="Unique identifier" style="text-overflow: ellipsis;"><strong><view:msg>common.pid</view:msg></strong></td>
                <td width="20%" title="URL of the source" style="text-overflow: ellipsis;"><strong>Url</strong></td>
                <td width="15%" title="The last harvesting timestamp" style="text-overflow: ellipsis;"><strong>Harvesting timestamp</strong></td>
                <td width="15%" title="The publishing timestamp" style="text-overflow: ellipsis;"><strong>Publishing timestamp</strong></td>
                <td width="5%" title="Edit properties" style="text-overflow: ellipsis;"><strong>Edit</strong></td>
                
            <c:if test="${harvestlibrary == 'true'}">
                <td width="5%" title="Start harvesting library" style="text-overflow: ellipsis;"><strong>Library harvesting</strong></td>
            </c:if>
                
            <c:if test="${harvesttitle == 'true'}">
                <td width="5%" title="Start title harvesting" style="text-overflow: ellipsis;"><strong>Start title harvesting</strong></td>
            </c:if>
                
            <c:if test="${harvestcollections == 'true'}">
                <td width="5%" title="Start collection harvesting" style="text-overflow: ellipsis;"><strong>Start library collection harvesting</strong></td>
            </c:if>

            <c:if test="${publishing == 'true'}">
                <td width="5%" title="Start publishing"  style="text-overflow: ellipsis;"><strong>Start publishing</strong></td>
            </c:if>
                
             </tr>
        </thead>
        <tbody>
        
	            <c:forEach var="itm" items="${ga.sourcesLocale}" varStatus="i">
                    <tr class="${(i.index mod 2 == 0) ? 'result ui-state-default': 'result '}">
	                  <td title="${itm.descriptionsMap[ga.localeLang]}">${itm.descriptionsMap[ga.localeLang]}</td>
	                  <td title="${itm.pid}">${itm.pid}</td>
	                  <td title="${itm.url}">${itm.url}</td>

	                  <td title="${itm.harvestingTimestamp}">${itm.harvestingTimestamp}</td>
	                  <td title="${itm.publishingTimestamp}">${itm.publishingTimestamp}</td>

	                  <td><button onclick="cdkadm.source('${itm.pid}');">Edit</button></td>

                  <c:if test="${harvestlibrary == 'true'}">
	                  <td><button onclick="parametrizedProcess.open('cdksourceharvest',{'source':'${itm.url}'.escapeChars([':',';','}','{','\\\\'])});">start</button></td>
                  </c:if>

                  <c:if test="${harvesttitle == 'true'}">
	                  <td><button onclick="parametrizedProcess.open('cdksourcetitleharvest',{'source':'${itm.url}'.escapeChars([':',';','}','{','\\\\'])});">start</button></td>
                  </c:if>
	                  
                  <c:if test="${harvestcollections == 'true'}">
	                  <td><button onclick="cdkadm.harvestCollections('${itm.pid}','${itm.url}');" >start</button></td>
                  </c:if>
	                  
                  <c:if test="${publishing == 'true'}">
	                  <td><button onclick="void" >start</button></td>
                  </c:if>

	            </c:forEach>
        </tbody>
    </table>
    </div>
    
</div>

</scrd:securedContent>