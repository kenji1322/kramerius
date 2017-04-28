<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/tlds/securedContent.tld" prefix="scrd"%>
<%@ taglib uri="/WEB-INF/tlds/cmn.tld" prefix="view"%>

<%@ page isELIgnored="false"%>


<scrd:securedContent action="display_admin_menu" sendForbidden="true">

<script type="text/javascript">
      function collectionRightDialog(pid){
          var structs = [
             {
               'models': "monograph",
               'pid':pid
             }
          ];

          // open dialog
          var affectedDialog = findObjectsDialog();
          affectedDialog.actions = null;
          affectedDialog.openDialog(structs);
      }
      
    
    
/** collection object administration */
function CDKAdminSupport() {

	/** 
	 * Basic open dialog
	 * @private
	 */
	this.dialog = null;

	/** 
	 * Image association dialog
	 * @private
	 */
	this.imageAssocDialog = null;

}

CDKAdminSupport.prototype.waiting=function() {
	$("#collections-content").hide();
	$('#collections-waiting').show();
}
CDKAdminSupport.prototype.refresh=function() {
	var url = "inc/admin/_cdksources_actions_content.jsp";
	this.waiting();
	$("#collections-content").html("");
    $.get(url, bind(function(data) {
		$("#sources-content").html(data);
		this.content();
    },this));
}
CDKAdminSupport.prototype.content=function() {
	$("#collections-content").show();
	$('#collections-waiting').hide();
}



CDKAdminSupport.prototype.edit=function(action, canLeave, czech, english, collection, sourceurl, name) {
	//var url = "vc?action=CREATE&canLeave=" + canLeave;
    var url = "cdkmanage?action="+action+"&canLeave=" + canLeave;

	if (czech) {
        var escapedText = replaceAll(encodeURIComponent(czech), ',', '');
            escapedText = replaceAll(escapedText, '\n', '');
            escapedText = escapedText.replace(/ +(?= )/g,'');
            escapedText = escapedText.replace(/&/g,'%26');
            url = url + "&text_cs=" + escapedText;
	}
	
	if (english) {
        var escapedText = replaceAll(encodeURIComponent(english), ',', '');
            escapedText = replaceAll(escapedText, '\n', '');
            escapedText = escapedText.replace(/ +(?= )/g,'');
            escapedText = escapedText.replace(/&/g,'%26');
            url = url + "&text_en=" + escapedText;
    }
    
    if (collection) {
	    url = url + "&pid="+collection
    }
    
    if (sourceurl) {
	    url = url + "&url="+sourceurl
    }
    if (name) {
	    url = url + "&name="+name
    }
                
    this.waiting();
    $.get(url,  bind(function(pid){
        this.refresh();
    },this));
    
}



CDKAdminSupport.prototype.harvestCollections = function(pid, url) {
	showConfirmDialog(dictionary['administrator.dialogs.virtualcollectionsdeleteconfirm'], function(){
        var url = "lr?action=start&def=cdkcollectionharvest&out=text&params="+pid+","+url;
        processStarter("cdkcollectionharvest").start(url);
    });
}

CDKAdminSupport.prototype.source=function(collection) {
    var url = "inc/admin/_new_source.jsp";
    if (collection) { url = url +"?collection="+collection; }
    $.get(url, bind(function(data) {
    	if (this.dialog) {
    		this.dialog.dialog('open');
        } else {
            $(document.body).append('<div id="newsource">'+'</div>');
            this.dialog = $('#newsource').dialog({
                width:600,
                height:480,
                modal:true,
                title:'Collection',
                buttons: [
                    {
                        text: dictionary["common.ok"],
                        click: bind(function() {
                               var czech = $("#czech_text").val();
                               var english = $("#english_text").val();
                               var canLeave = $("#canLeave").is(":checked");
                               var collection = $("#vc_pid").val();
                               var action = (collection ? "CHANGESOURCE" : "CREATESOURCE");

                               
                               var czech_log_desc = $("#descs_cs_text").val();
                               var english_log_desc = $("#descs_en_text").val();

                               var url = $("#source_url").val();

                               var name = $("#name").val();
                               
                               this.edit(action,canLeave,czech,english, collection, url,name);
                               
                               this.dialog.dialog("close"); 
                        },this)
                    },
                    {
                        text: dictionary['common.close'],
                        click:function() {
                            $(this).dialog("close"); 
                        } 
                    }
                ] 
            });
        }
    	$("#newsource").html(data);
    	//$("#newsource").dialog('option','title',dictionary['rights.changepswd.title']);
    },this));

}
      
var cdkadm = new CDKAdminSupport();


$(document).ready(function(){
        cdkadm.refresh();
});
</script>

<style>
<!--
.criteriums-table {
    width:100%;
}    
.criteriums-table thead tr td:last-child {
    width: 150px;
} 
.sources-buttons {
    float: right;
}
.sources-buttons-clear {
    clear: right;
}
-->
</style>


<div>
    
<div>    
    <div class="sources-buttons">
      <a href="javascript:cdkadm.refresh();" class="ui-icon ui-icon-transferthick-e-w"></a>
    </div>
    <div class="sources-buttons">
        <a href="javascript:cdkadm.source();" class="ui-icon ui-icon-plusthick"></a>
    </div>
    
     <div class="collections-buttons-clear"></div>
</div>

<div class="sources-buttons-clear">
</div>


<div id="sources-waiting" style="display: none;"><span><view:msg>administrator.dialogs.waiting</view:msg></span>
</div>
    
<div id="sources-content"></div>

</scrd:securedContent>