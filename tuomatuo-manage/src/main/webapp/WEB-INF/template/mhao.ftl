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
	<div region="center" border="true" title="" style="margin:0px;padding:10px">
				<p>欢迎访问${projectName}管理平台！您好！</p>
				<table id="customers" style="float:left; margin-right:20px;width:200px;">  
					<tr>  
						<th width="140px">对应项</th>  
						<th>数据</th>  
					</tr>
					<tr>  
						<td>用户总数</td>  
						<td>${map.user_count}</td>
					</tr>
					<tr class="alt">  
						<td>今日新增用户总数</td>  
						<td>${map.user_count_today}</td>  
					</tr>  
					<tr>  
						<td>有效用户总数</td>  
						<td>${map.user}(${map.user_distinct})</td>
					</tr>
					<tr>  
						<td>用户号码</td>  
						<td>${map.user_number}(${map.user_number_distinct})</td>  
					</tr>
					<tr>  
						<td>正在使用的号码</td>  
						<td>${map.number}(${map.number_distinct})</td>  
					</tr>
					<tr>  
						<td>基础服务</td>  
						<td>${map.user_services}(${map.user_services_distinct})</td>  
					</tr>
					<tr>  
						<td>短信上行总数</td>  
						<td>${map.message_send}</td>  
					</tr>
					<tr class="alt">  
						<td>短信下行总数</td>  
						<td>${map.message_receive}</td>  
					</tr>
					<tr>  
						<td>近一小时内短信上行</td>  
						<td>${map.message_send_hour}</td>  
					</tr>
					<tr class="alt">  
						<td>近一小时内短信下行</td>  
						<td>${map.message_receive_hour}</td>  
					</tr>
					<tr>  
						<td>空闲有效密号数</td>  
						<td>${map.number_idle}</td>  
					</tr>
					<tr class="alt">  
						<td>今日成功订单数</td>  
						<td>${map.today_order_count}</td>  
					</tr>
					<tr class="alt">  
						<td>用户占用多号码</td>  
						<td>${map.user_multiple_mobile}</td>  
					</tr>
					<tr class="alt">  
						<td>号码被多用户占用</td>  
						<td>${map.mobile_multiple_user}</td>  
					</tr>
					<tr class="alt">  
						<td>使用但空闲的号码</td>  
						<td>${map.active_number_but_used}</td>  
					</tr>
					<tr class="alt">  
						<td>免费号码池</td>  
						<td>${map.number_pool_free_count}</td>  
					</tr>
					<tr class="alt">  
						<td>收费号码池</td>  
						<td>${map.number_pool_pay_count}</td>  
					</tr>
					<tr class="alt">  
						<td>管理员手工投放池</td>  
						<td>${map.number_pool_manager_count}</td>  
					</tr>
					<tr class="alt">  
						<td>短信密号号码池</td>  
						<td>${map.number_pool_sms_count}</td>  
					</tr>
					<tr class="alt">  
						<td>滴滴业务号码池</td>  
						<td>${map.didi_number_pool_count}</td>  
					</tr>
					<tr class="alt">  
						<td>滴滴专车号码池</td>  
						<td>${map.didiZhuanche_number_pool_count}</td>  
					</tr>
					<tr class="alt">  
						<td>珍爱网号码池</td>  
						<td>${map.zhenai_number_pool_count}</td>  
					</tr>
					<tr class="alt">  
						<td>近一天短信量异常</td>  
						<td>${map.message_monitor_pool_count}</td>  
					</tr>
					<tr class="alt">  
						<td>近一天通话量异常</td>  
						<td>${map.call_monitor_pool_count}</td>  
					</tr>
					<tr class="alt">  
						<td>近一天非法短信</td>  
						<td>${map.message_key_word_pool_count}</td>  
					</tr>
				</table>
				<table id="customers" style="float:left;width:600px;">  
					<tr>  
						<th width="140px">系统公告</th>  
						<th>参数(此操作只有root有权限)</th>  
					</tr>
					<tr>  
						<th>类别</th>
						<td><select id="system_announce_category"/></td>  
					</tr>
					<tr>
						<th>主题</th>
						<td><input type="text" id="system_announce_theme" style="width:95%"></td>  
					</tr>
					<tr>
						<th>内容</th>
						<td><input type="text" id="system_announce_content" style="width:95%"></td>
					</tr>
					<tr>
						<th>接收类别</th>
						<td><select id="system_announce_receive_category"></td>
					</tr>
					<tr>
						<th>用户Id</th>
						<td><input type="text" id="system_announce_receive_ids"></td>
					</tr>
					<tr>
						<th>消息推送时间</th>
						<td><input class="easyui-datetimebox" name="endTime" id="system_announce_push_start_time" style="width:150px"> </td>
					</tr>
					<tr>
						<th>消息截止时间</th>
						<td><input class="easyui-datetimebox" name="endTime" id="system_announce_push_end_time" style="width:150px"> </td>
					</tr>
					<tr>
						<th>操作</th>
						<td><input type="button" value="确定" id="system_announce_submit" style="margin-left:380px" /></td>
					</tr>
				</table>
				<table style="width:600px;height:25px;border=0px;">
				<tr><td></td></tr>
				</table>
				<table id="customers" style="float:left;width:600px;">  
					<tr>  
						<th width="140px">系统维护</th>  
						<th>操作(此操作只有root有权限)</th>  
					</tr>
					<tr>  
						<th>mhao_weixin</th>
						<td>
							<#if mapPermissions.mhaoWeiXinStatus=="1">
								<input type="button" value="关闭系统维护" id="mhao_wei_xin_operation" class="0" style="margin-left:380px" />
							<#else>
								<input type="button" value="开启系统维护" id="mhao_wei_xin_operation" class="1" style="margin-left:380px" />
							</#if> 
						</td>  
					</tr>
					<tr>
						<th>mhao_server</th>
						<td>
							<#if mapPermissions.mhaoServerStatus=="1">
								<input type="button" value="关闭系统维护" id="mhao_server_operation" class="0" style="margin-left:380px" />
							<#else>
								<input type="button" value="开启系统维护" id="mhao_server_operation" class="1" style="margin-left:380px" />
							</#if> 
						</td>
					</tr>
				</table>
				<div style="clear:both;"></div>
				<br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/><br/>
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