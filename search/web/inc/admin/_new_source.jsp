<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="/WEB-INF/tlds/securedContent.tld" prefix="scrd"%>
<%@ taglib uri="/WEB-INF/tlds/cmn.tld" prefix="view"%>

<%@ page isELIgnored="false"%>
<view:object name="ga" clz="cz.incad.Kramerius.views.virtualcollection.ListOfCDKSourcesViewObject"></view:object>




<div class="newrole">


  <ul>
    <li><a href="#basic"><view:msg>collection.administration.content.tab.basic</view:msg></a></li>
  </ul>


<div id="basic">
     <c:choose>
       <c:when test="${ga.parameterCollection != null}">
	     <input id="vc_pid" name="vc_pid" type="text" value="${ga.parameterCollection.pid}" style="display:none"/>
       </c:when>    
      <c:otherwise>
	     <input id="vc_pid" name="vc_pid" type="text" value="" style="display:none"/>
       </c:otherwise>
     </c:choose>
	

     <table style="width:100%">

     <tr><td style="width:100%"><label for="czech_text"><view:msg>collection.administration.edit.abbreviation</view:msg></label></td></td>
     <c:choose>
       <c:when test="${ga.parameterCollection != null}">
        <tr><td style="width:100%"><input style="width:100%" id="name" name="name" type="text" value="${ga.parameterCollection.label}" /></td></td>
       </c:when>    
      <c:otherwise>
	     <tr><td style="width:100%"><input style="width:100%" id="name" name="name" type="name" /></td></td>
       </c:otherwise>
     </c:choose>

     <tr><td style="width:100%"><label for="czech_text"><view:msg>collection.administration.edit.czech_lang</view:msg></label></td></td>
     <c:choose>
       <c:when test="${ga.parameterCollection != null}">
        <tr><td style="width:100%"><input style="width:100%" id="czech_text" name="czech" type="text" value="${ga.parameterCollection.descriptionsMap['cs']}" /></td></td>
       </c:when>    
      <c:otherwise>
	     <tr><td style="width:100%"><input style="width:100%" id="czech_text" name="czech" type="text" /></td></td>
       </c:otherwise>
     </c:choose>
     

     <tr><td style="width:100%"><label for="english_text"><view:msg>collection.administration.edit.eng_lang</view:msg></label></td></tr>
     <c:choose>
       <c:when test="${ga.parameterCollection != null}">
        <tr><td style="width:100%"><input  style="width:100%" id="english_text" name="english" type="text" value="${ga.parameterCollection.descriptionsMap['en']}" /></td></td>
       </c:when>    
      <c:otherwise>
	     <tr><td style="width:100%"><input style="width:100%" id="english_text" name="english" type="text" /></td></td>
       </c:otherwise>
     </c:choose>
     
     <tr><td style="width:100%"><label for="source_url">Url</label></td></tr>
     <c:choose>
       <c:when test="${ga.parameterCollection != null}">
        <tr><td style="width:100%"><input  style="width:100%" id="source_url" name="source_url" type="text" value="${ga.parameterCollection.url}" /></td></td>
       </c:when>    
      <c:otherwise>
	     <tr><td style="width:100%"><input style="width:100%" id="source_url" name="source_url" type="text" /></td></td>
       </c:otherwise>
     </c:choose>
     

	</table>
</div>


<script type="text/javascript" language="javascript">
    $(document).ready(function(){
       // tabs
       $('.newrole').tabs();
    });
    
   
</script>
</div>

