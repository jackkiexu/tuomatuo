<!DOCTYPE html>
<html>
<head>
<title>${projectName}管理平台</title>
<meta charset="UTF-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<link href="${webRoot}/css/themes/default/easyui.css" rel="stylesheet">
	<link rel="stylesheet" type="text/css" href="${webRoot}/css/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="${webRoot}/css/themes/icon.css">
	<script type="text/javascript" src="${webRoot}/js/jquery.min.js"></script>
	<script type="text/javascript" src="${webRoot}/js/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="${webRoot}/js/tools.js"></script>
	<script type="text/javascript" src="${webRoot}/js/index.js"></script>
	<script type="text/javascript" src="${webRoot}/js/easyui-lang-zh_CN.js"></script>
<style>
body {
	margin:0px;
	padding:0px;
	width:100%;
	height:100%;
	border:0px;
}
#customers {font-family:"Trebuchet MS", Arial, Helvetica, sans-serif;border-collapse:collapse;}
#customers td, #customers th {font-size:1em;border:1px solid #95b8e7;padding:3px 7px 2px 7px;}
#customers th {font-size:1.1em;text-align:left;padding-top:5px;padding-bottom:4px;background-color:#e0ecff;color:#000000;}
#customers tr.alt td{color:#000000;background-color:#fafafa;}
</style>
<script type="text/javascript"> 
var i=1;
function addTab(title, url){
	var tt = $('#main_tabs');
    if (tt.tabs('exists', title)){
        tt.tabs('select', title);
        //refreshTab({tabTitle:title,url:url});  
    } else {
    	//if(tt_size.size>10){
    	//	$.messager.alert('Warning','打开的标签页太多，请关闭不使用的标签页！');
    	//	return;
    	//}
        var content = '<iframe scrolling="auto" frameborder="0" src="'+url+'" style="width:100%;height:100%"></iframe>';
        tt.tabs('add',{
        	id:i,
            title:title,
            content:content,
            closable:true,
            cache:false,
            border:false,
            fit:true
        });
        $("#"+i).css("overflow","hidden"); 
        i++;        
    }
}
function updatePassword(){
	$("#passwordOld").val('');
	$("#passwordNew").val('');
	$("#passwordNewCheck").val('');
	$('#updatePassword').show();
	$("#updatePassword").dialog({
				title:'修改密码',
				top:100,
				width:300,
				height:200,
				cache: false,
				closed: true,
			});
	$('#updatePassword').dialog('open');
	$("#updatePasswordSubmit").unbind();
	$("#updatePasswordSubmit").click(function(){
		if ($.isFunction(window.validationPassword)){
			var rs=validationPassword();
			if(rs==undefined||rs==false){
				return;
			}
		}
		$.ajax({
	        type: "post",
	        dataType: "json",
	        url: "${webRoot}/sysOperator/updatePassword.do",
	        data: $('#updatePasswordForm').serializeArray(),
	        success: function(data){
	          	if(data!=undefined&&data.result==true){
	           		$.messager.show({
	               		title:'编辑'+name,
	               		msg:'编辑成功！',
	               		timeout:5000,
	               		showType:'slide'
	           		});
	           		$('#updatePassword').dialog('close');
	           	}else{
	           		returnError(data);
	           	}
	        }
		});
	});
}
var tdErr;
function validationPassword(){
var err = "";
var errId = "";
var passwordOld = $("#passwordOld").val();
var passwordNew = $("#passwordNew").val();
var passwordNewCheck = $("#passwordNewCheck").val();

var $system_announce_category = $('#system_announce_category').val();
var $system_announce_theme = $('#system_announce_theme');
var $system_announce_content = $('#system_announce_content');
var $system_announce_receive_category = $('#system_announce_receive_category');
var $system_announce_receive_ids = $('#system_announce_receive_ids');
var $system_announce_push_start_time = $('#system_announce_push_start_time');
var $system_announce_push_end_time = $('#system_announce_push_end_time');



	if(isNull(passwordOld)){
		err = "原密码不能为空！";
		errId = "passwordOld";
	} else 
	if(!checkLength(passwordOld,6,15)){
		err = "原密码不能少于6个字且不能超过15个字！";
		errId = "passwordOld";
	} else
	if(isNull(passwordNew)){
		err = "新密码不能为空！";
		errId = "passwordNew";
	} else 
	if(!checkLength(passwordNew,6,15)){
		err = "新密码不能少于6个字且不能超过15个字！";
		errId = "passwordNew";
	} else
	if(isNull(passwordNewCheck)){
		err = "新密码确认必须填写！";
		errId = "passwordNewCheck";
	} else 
	if(passwordNewCheck!=passwordNew){
		err = "两次输入的新密码不相同！";
		errId = "passwordNewCheck";
	} else
	if(passwordOld==passwordNew){
		err = "原密码与新密码相同！";
		errId = "passwordNew";
	} else
	{
		//success
		return true;
	}
	$.messager.show({title:'修改密码错误提示',msg:err,timeout:5000,showType:'slide'});
	$("#"+errId).focus();
	tdErr = $("#"+errId).parent();
	tdErr.css("background-color","#ffffd0");
	setTimeout("recover()", 5000);
	return false;
}
function recover(){
	if(tdErr!=undefined){
		tdErr.css("background-color","#FFFFFF");
	}
}
function refreshTab(cfg){  
    var refresh_tab = cfg.tabTitle?$('#main_tabs').tabs('getTab',cfg.tabTitle):$('#main_tabs').tabs('getSelected');  
    if(refresh_tab && refresh_tab.find('iframe').length > 0){  
	    var _refresh_ifram = refresh_tab.find('iframe')[0];  
	    var refresh_url = cfg.url?cfg.url:_refresh_ifram.src;  
	    _refresh_ifram.contentWindow.location.href=refresh_url;  
    }  
}



//初始化
$(function(){
	$("#system_announce_category").html("<option value='' selected>请选择</option>");
	$("#system_announce_category").append(getSelectList(mhao_prodtct,""));  
	
	$("#system_maintenance_category").html("<option value='' selected>请选择</option>");
	$("#system_maintenance_category").append(getSelectList(mhao_prodtct_real,"")); 
	
	$("#system_announce_receive_category").html("<option value='' selected>请选择</option>");
	$("#system_announce_receive_category").append(getSelectList(system_annouce_category,""));
	
	$('#system_announce_submit').click(function(){
		var system_announce_category = $('#system_announce_category').val();
		var system_announce_theme = $('#system_announce_theme').val();
		var system_announce_content = $('#system_announce_content').val();
		var system_announce_receive_category = $('#system_announce_receive_category').val();
		var system_announce_receive_ids = $('#system_announce_receive_ids').val();
		var system_announce_push_start_time = $('#system_announce_push_start_time').datetimebox('getValue');
		var system_announce_push_end_time = $('#system_announce_push_end_time').datetimebox('getValue');
		
		if($.trim(system_announce_category) == '' || system_announce_category == null){
				alert("请选择类别!");
				return false;
		}
		if($.trim(system_announce_theme) == '' || system_announce_theme == null){
				alert("请填写主题!");
				return false;
		}
		if($.trim(system_announce_content) == '' || system_announce_content == null){
				alert("请填写内容!");
				return false;
		}
		if($.trim(system_announce_receive_category) == '' || system_announce_receive_category == null){
				alert("请选择接收类别!");
				return false;
		}
		$.ajax({
				url : '${webRoot}/upSystemAnnounce.do',
				type : 'post',
				data : {
					'system_announce_category' : system_announce_category,
					'system_announce_theme' : system_announce_theme,
					'system_announce_content' : system_announce_content,
					'system_announce_receive_category' : system_announce_receive_category,
					'system_announce_receive_ids' : system_announce_receive_ids,
					'system_announce_push_start_time' : system_announce_push_start_time,
					'system_announce_push_end_time' : system_announce_push_end_time,
					timestampstr : new Date().getTime()
				},
				dataType : 'json',
				success : function(data){
					if(data.status==0){
						$('#editObj').dialog('close');
		            		$('#dg').datagrid('reload');
		            		$.messager.show({
		                		title:'提交',
		                		msg:'提交成功！',
		                		timeout:5000,
		                		showType:'slide'
		            		});
		            		 $('#system_announce_category').val('');
							 $('#system_announce_theme').val('');
							 $('#system_announce_content').val('');
							 $('#system_announce_receive_category').val('');
							 $('#system_announce_receive_ids').val('');
					} else {
						alert(data.msg);
					}
				}
			});
		
	});
	
	$('#mhao_wei_xin_operation').click(function(){
	
		var mhao_weixin_status = $('#mhao_wei_xin_operation').attr("class");
		if(!confirm("警告 mhao_weixin 服务的状态将变成:"+mhao_weixin_status)){return false;}
		$.ajax({
				url : '${webRoot}/updateMhaoWeiXinStatus.do',
				type : 'post',
				data : {
					'mhao_weixin_status' : mhao_weixin_status,
					timestampstr : new Date().getTime()
				},
				dataType : 'json',
				success : function(data){
					if(data.status==0){
						alert("success");
						location.reload() 
					} else {
						alert(data.msg);
					}
				}
			});
			return;
	});
	
	$('#mhao_server_operation').click(function(){
		var mhao_server_status = $('#mhao_server_operation').attr("class");
		if(!confirm("警告 mhao_weixin 服务的状态将变成:"+mhao_server_status)){return false;}
		$.ajax({
				url : '${webRoot}/updateMhaoServerStatus.do',
				type : 'post',
				data : {
					'mhao_server_status' : mhao_server_status,
					timestampstr : new Date().getTime()
				},
				dataType : 'json',
				success : function(data){
					if(data.status==0){
						alert("success");
						location.reload() 
					} else {
						alert(data.msg);
					}
				}
			});
			return;
	});
})

var permissionList = new Array();
<#if Session["permissionList"]?exists>
	<#list Session["permissionList"] as pl>
permissionList.push("{\"path\":\"${pl.path}\",\"method\":\"${pl.methods}\"}");
	</#list>
</#if>
</script>

</head>
<body class="easyui-layout" style="border:0px" border="false">
	<div region="north" style="height:30px;background:#e0ecff;padding:0px;border:0px">
		<table width="100%" style="padding:0px;font-size:12px;padding:5px">
			<tr><td width="200px">${projectName}管理平台</td><td></td><td width="200px" style="text-align:right"><a href="javascript:void(0);" onclick="updatePassword();" target="_top">[修改密码]</a> <a href="${webRoot}/logout.do" target="_top">[登出]</a></td></tr>
		</table>
	</div>
	<div region="west" title="导航条" split="true" style="width:120px;padding:0px;">
		<div id="menuDiv" class="easyui-accordion" fit="true" border="false" animate="true">
<#if menu?exists>
<#list menu as m>
	<div title="${m.name}" style="overflow:hidden;">
	<#list m.menuList?keys as ikey>
		<div class="menuClass" onmouseover="this.className='menuClassOver'" onmouseout="this.className='menuClass'" onclick="addTab('${ikey}','${webRoot}${m.menuList[ikey]}')">
			<a href="javascript:void(0);" >${ikey}</a>
		</div>
	</#list>
	</div>
</#list>
</#if>
		</div>
	</div>
	<div region="center" border="true" title="" style="margin:0px;padding:0px">
		<div id="main_tabs" class="easyui-tabs" fit="true" border="false" style="width:100%;height:100%">
			<div title="主页" style="padding:10px;overflow:hidden">
				<p>欢迎访问${projectName}管理平台！您好！</p>
<#if Session["admin"].login=="root">
				<table id="customers" style="float:left; margin-right:20px;width:200px;">  

				</table>
</#if>
				<div style="clear:both;"></div>
				<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
			</div>	
		</div>
	</div>
<div id="updatePassword" style="display:none;">
    <form id="updatePasswordForm">
    <table class="formTable" width="100%">
        <tbody>
            <tr>
                <td>原密码：</td><td><input type="password" id="passwordOld" name="passwordOld" value=""/></td>
            </tr>
            <tr>
                <td>新密码：</td><td><input type="password" id="passwordNew" name="passwordNew" value=""/></td>
            </tr>
            <tr>
                <td>新密码确认：</td><td><input type="password" id="passwordNewCheck" name="passwordNewCheck" value=""/></td>
            </tr>
        </tbody>
    </table>
    <table border="0" width="100%" id="formOperate">
    	<tr><td align="center"><input type="button" id="updatePasswordSubmit" value="确定"/><input type="reset"/></td></tr>
    </table>
    </form>
</div>
</body>
</html>