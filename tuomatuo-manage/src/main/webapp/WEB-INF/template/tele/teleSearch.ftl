<!DOCTYPE html>
<html>
<head>
<title>tele</title>
<meta charset="UTF-8">
	<link rel="stylesheet" type="text/css" href="${webRoot}/css/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="${webRoot}/css/themes/icon.css">
	<script type="text/javascript" src="${webRoot}/js/jquery.min.js"></script>
	<script type="text/javascript" src="${webRoot}/js/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="${webRoot}/js/easyui-lang-zh_CN.js"></script>
<script>
document.write("<s"+"cript type='text/javascript' src='${webRoot}/js/index.js?"+Math.random()+"'></scr"+"ipt>");
document.write("<s"+"cript type='text/javascript' src='${webRoot}/js/tools.js?"+Math.random()+"'></scr"+"ipt>");
</script>
<style>
body {
	margin:0px;
	padding:0px;
	height:100%;
	border:0px;
}
</style>
<script type="text/javascript">
//初始化
$(function(){
	searchInit("${webRoot}/tele/search.do");
	$("#search_tele_status").html("<option value='' selected>请选择</option>");
	$("#search_tele_status").append(getSelectList(tele_status_map,''));
})
function newInitCombobox(obj,data){
	obj.find("#display").html('');
	obj.find("#display").append(getSelectList(tele_display,data.display));
	obj.find("#needVerifyIp").html('')
	obj.find("#needVerifyIp").append(getSelectList(operator_checkIp_map,data.needVerifyIp));	
}
function viewInitCombobox(obj,data){
	obj.find("#view_teleStatus").html(tele_status_map[obj.find("#view_teleStatus").html()]);
	obj.find("#view_needVerifyIp").html(operator_checkIp_map[obj.find("#view_needVerifyIp").html()]);
	obj.find("#view_teleIsDelete").html(isDelete_map[obj.find("#view_teleIsDelete").html()]);
	obj.find("#view_display").html(tele_display[obj.find("#view_display").html()]);
}
function editInitCombobox(obj,data){
	newInitCombobox(obj,data);
}
function searchInit(url){
	$('#dg').datagrid({
		method:'post',
		iconCls:'icon-edit', //图标
		singleSelect:false, //多选
		fitColumns: true, //自动调整各列，用了这个属性，下面各列的宽度值就只是一个比例。
		fit: true,//自动大小
		striped: true, //奇偶行颜色不同
		collapsible:false,//可折叠
		url:url, //数据来源
		sortName: 'id', //排序的列
		sortOrder: 'desc', //倒序
		remoteSort: true, //服务器端排序
		idField:'id', //主键字段
		queryParams:{}, //查询条件
		pagination:true, //显示分页
		rownumbers:true, //显示行号
		pageSize:20,
		toolbar:'#tb',
		pagePosition:'both',
		border:false,
		fitColumns:false,
		//checkOnSelect:false,
		columns:[[
			{field:'ck',checkbox:true,width:2},
			{field:'id',title:'${(cmodel.tele_id)!''}',sortable:true,
				formatter:function(value,row,index){return row.id;} 
			},
			{field:'teleName',title:'${(cmodel.tele_name)!''}',sortable:true,
				formatter:function(value,row,index){return row.teleName;} 
			},
			{field:'teleKey',title:'${(cmodel.tele_key)!''}',sortable:true,
				formatter:function(value,row,index){return row.teleKey;} 
			},
			{field:'teleCreateTime',title:'${(cmodel.tele_createTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.teleCreateTime;} 
			},
			{field:'teleUpdateTime',title:'${(cmodel.tele_updateTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.teleUpdateTime;} 
			},
			{field:'teleStatus',title:'${(cmodel.tele_status)!''}',sortable:true,
				formatter:function(value,row,index){return tele_status_map[row.teleStatus];} 
			},
			{field:'display',title:'${(cmodel.tele_display)!'来显类型定义'}',sortable:true,
				formatter:function(value,row,index){return tele_display[row.display];} 
			},
			{field:'teleIsDelete',title:'${(cmodel.tele_isDelete)!''}',sortable:true,
				formatter:function(value,row,index){return isDelete_map[row.teleIsDelete];} 
			},
			{field:'teleNotifyCallUrl',title:'${(cmodel.tele_notifyCallUrl)!''}',sortable:true,width:200,
				formatter:function(value,row,index){return row.teleNotifyCallUrl;}
			},
			{field:'teleNotifyMtReceUrl',title:'${(cmodel.tele_notifyMtReceUrl)!''}',sortable:true,width:200,
				formatter:function(value,row,index){return row.teleNotifyMtReceUrl;} 
			},
			{field:'teleNotifyMoUrl',title:'${(cmodel.tele_notifyMoUrl)!''}',sortable:true,width:200,
				formatter:function(value,row,index){return row.teleNotifyMoUrl;} 
			},
			{field:'needVerifyIp',title:'${(cmodel.tele_needVerifyIp)!''}',sortable:true,
				formatter:function(value,row,index){return operator_checkIp_map[row.needVerifyIp];} 
			},
			{field:'validIp',title:'${(cmodel.tele_validIp)!''}',sortable:true,
				formatter:function(value,row,index){return row.validIp;} 
			},
			{field:'callFlow',title:'${(cmodel.tele_callFlow)!''}',sortable:true,
				formatter:function(value,row,index){return row.callFlow;} 
			},
			{field:'msgFlow',title:'${(cmodel.tele_msgFlow)!''}',sortable:true,
				formatter:function(value,row,index){return row.msgFlow;} 
			},
			{field:'generalFlow',title:'${(cmodel.tele_generalFlow)!''}',sortable:true,
				formatter:function(value,row,index){return row.generalFlow;} 
			},
			{field:'checkAuthFmTimeout',title:'${(cmodel.tele_checkAuthFmTimeout)!''}',sortable:true,
				formatter:function(value,row,index){return row.checkAuthFmTimeout;} 
			},
			{field:'authFmUrl',title:'${(cmodel.tele_authFmUrl)!'商户侧校验来电号码url'}',sortable:true,
				formatter:function(value,row,index){return row.authFmUrl;} 
			},
			{field:'callDirectionUrl',title:'${(cmodel.tele_callDirectionUrl)!'call direction url参数'}',sortable:true,
				formatter:function(value,row,index){return row.callDirectionUrl;} 
			},
			{field:'notifyMediaStreamUrl',title:'${(cmodel.tele_notifyMediaStreamUrl)!'notifyMediaStreamUrl'}',sortable:true,
				formatter:function(value,row,index){return row.notifyMediaStreamUrl;} 
			},
			{field:'dmpCallUrl',title:'${(cmodel.tele_dmpCallUrl)!'dmpCallUrl'}',sortable:true,
				formatter:function(value,row,index){return row.dmpCallUrl;} 
			},
			{field:'dmpMtReceUrl',title:'${(cmodel.tele_dmpMtReceUrl)!'dmpMtReceUrl'}',sortable:true,
				formatter:function(value,row,index){return row.dmpMtReceUrl;} 
			},
			{field:'dmpMoUrl',title:'${(cmodel.tele_dmpMoUrl)!'dmpMoUrl'}',sortable:true,
				formatter:function(value,row,index){return row.dmpMoUrl;} 
			},
			{field:'notifyAudioStatusUrl',title:'${(cmodel.tele_notifyAudioStatusUrl)!'notifyAudioStatusUrl'}',sortable:true,
				formatter:function(value,row,index){return row.notifyAudioStatusUrl;} 
			},
			{field:'dmpRecordUrl',title:'${(cmodel.tele_dmpRecordUrl)!'dmpRecordUrl'}',sortable:true,
				formatter:function(value,row,index){return row.dmpRecordUrl;} 
			},
			{field:'Confirmation',title:'操作',width:100,sortable:false,
				formatter:function(value,row,index){
					return '';
				}
			}
		]],
		loadFilter:function(data){
			if(isReturnError(data)){
			}
			return data;
		},
		onLoadSuccess:function(){
			$('#dg').datagrid('clearSelections'); 
		},
		onClickRow:function (rowIndex, rowData){
			onClickRowBySingle(rowIndex,rowData);
		}
	});
	var pager = $('#dg').datagrid('getPager');
	pager.pagination({
		pageSize: 20,
		pageList: [5,10,15,20,50,100],
		beforePageText: '第',
		afterPageText: '页    共 {pages} 页',  
		displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录',
		buttons:[{
		      iconCls:'icon-add',
		      text:'添加',
		      disabled:noPermis("tele","save"),
		      handler:function(){
		 	    newObj("${(cmodel.tele)!''}","${webRoot}/tele/save.do", 400, 410);
		 	    randomKey();
		      }
		},{
		      iconCls:'icon-info',
		      text:'详细',
		      disabled:noPermis("tele","view"),
		      handler:function(){
		 	    viewObj("${(cmodel.tele)!''}","${webRoot}/tele/view.do?id=", 600, 410);
		      }
		},{
		      iconCls:'icon-edit',
		      text:'修改',
		      disabled:noPermis("tele","update"),
		      handler:function(){
		          editObj("${(cmodel.tele)!''}","${webRoot}/tele/view.do?id=","${webRoot}/tele/update.do", 500, 410);
		      }
		},{
		      iconCls:'icon-delete',
		      text:'删除',
		      disabled:noPermis("tele","update"),
		      handler:function(){
		          removeObj("${webRoot}/tele/update.do?teleIsDelete=1");
		      }
		},{
		      iconCls:'icon-reload',
		      text:'更改状态',
		      disabled:noPermis("userServices","changeStatus"),
		      handler:function(){
		          changeObj("${webRoot}/tele/changeStatus.do");
		      }
		},{
		      iconCls:'icon-set',
		      text:'设置权限',
		      disabled:noPermis("tele","view"),
		      handler:function(){
		          telePermission("${webRoot}/telePermission/tpview2.do?id=","${webRoot}/telePermission/tpchange2.do");
		      }
		}]
	});
}
var tdErr;
function validation(obj){
var err = "";
var errId = "";
var tele_name = $("#teleName").val();
var tele_key = $("#teleKey").val();

	
	if(isNull(tele_name)){
		err = "${(cmodel.tele_name)!''}不能为空！";
		errId = "teleName";
	}else if(tele_key.length != 24){
		err = "${(cmodel.tele_key)!''}为24位！";
		errId = "teleKey";
	}else{
		return true;  
	}

	$.messager.show({title:'编辑错误提示',msg:err,timeout:5000,showType:'slide'});
	$("#"+errId).focus();
	tdErr = $("#"+errId).parent();
	tdErr.css("background-color","#ffffd0");
	setTimeout("recover()", 5000);
	return false;
}

function telePermission(url,action){
	var rows = $('#dg').datagrid('getSelections');
	var id = "";
	var size = 0;
	if(rows==undefined||rows==''){
		$.messager.alert('提示','请选择一条记录进行编辑！','info');
	    return;
	}
	$.each(rows,function(i,n){
		if(i==0) 
	    	id = n.id;
		size=i;
	});
	if(size>0){
		$.messager.alert('提示','该服务只允许单条操作','info');
	    return;
	}
	url = url+id;
	$('#telePermissionDiv').show();
	$.get(url,function(data){
		if(data!=undefined&&data.result!=undefined&&data.result){
			var obj = $("#telePermissionForm");
			$("#percentForRecording").val('0');
			$.each(data.row,function(id,ival){
				if(obj.find("#"+id)!=undefined){
					if(ival==true){
						obj.find("#"+id).html('<option value="true" selected>允许</option>');
						obj.find("#"+id).append('<option value="false">不允许</option>');						
					}else if(ival==false){
						obj.find("#"+id).html('<option value="false" selected>不允许</option>');
						obj.find("#"+id).append('<option value="true">允许</option>');	
					}else{
						obj.find("#"+id).val(ival);
					}
				}
			});
			$("#telePermissionDiv").dialog({
				title:'编辑商户权限,商户ID为：'+id,
				top:100,
				width:500,
				height:400,
				cache: false,
				closed: true,
			});
			$('#telePermissionDiv').dialog('open');
			$("#telePermissionSubmit").unbind();
			$("#telePermissionSubmit").click(function(){
					var rs=$('#telePermissionForm');
					if(rs==undefined||rs==false){
						return;
				}
				$.ajax({
		            type: "post",
		            dataType: "json",
		            url: action,
		            data: $('#telePermissionForm').serializeArray(),
		            contentType: 'application/x-www-form-urlencoded; charset=utf-8', 
		            success: function(data){
		            	if(data!=undefined&&data.result==true){
		            		$('#telePermissionDiv').dialog('close');
		            		$('#dg').datagrid('reload');
		            		$.messager.show({
		                		title:'编辑'+name,
		                		msg:'编辑成功！',
		                		timeout:5000,
		                		showType:'slide'
		            		});
		            	}else{
		            		returnError(data);
		            	}
		            }
				});
			});
		}else{
			returnError(data);
		}
	});
}

function removeObj(action){
	var rows = $('#dg').datagrid('getSelections');
	var size=0;
    $.each(rows,function(i,n){
    	size++;
    });
    if(size<=0){
    	$.messager.alert('提示','未选择所需删除的项！','info');
    	return;
    }
    if(size>1){
    	$.messager.alert('提示','只能单条删除！','info');
    	return;
    }
	$.messager.confirm('提示','确定要删除该项信息吗?',function(result){
		if (result){
	        var ps = "";
	        $.each(rows,function(i,n){
	        	ps += "&id="+n.id;
	        });
	        $.post(action+ps,function(data){
	        	if(data!=undefined&&data.result==true){
			       	$('#dg').datagrid('reload'); 
	        		$.messager.show({
	            		title:'提示信息：',
	            		msg:size+' 项信息删除成功！',
	            		timeout:5000,
	            		showType:'slide'
	        		});
	        	}else{
	        		returnError(data);
	        	}
	        });
	    }
	});
}
function recover(){
	if(tdErr!=undefined){
		tdErr.css("background-color","#FFFFFF");
	}
}
//从服务器获取一个唯一的密钥
function randomKey(){
	$.ajax({
        type: "post",
        dataType: "json",
        url: "${webRoot}/tele/randomKey.do",
        data: {length:"24"},	//这里可以不传密匙的长度，服务端默认为24位
        contentType: 'application/x-www-form-urlencoded; charset=utf-8', 
        success: function(data){
        	if(data!=undefined&&data.status==0){
        		$("#teleKey").val(data.value);
        	}else{
        		returnError(data);
        	}
        }
	});
}
</script>	
</head>
  
<body>
<table id="dg">
</table>
<div id="tb" style="padding:5px;height:auto">
	<form id="searchForm">
	   <div>
	    <input class="easyui-datebox" data-options="editable:false" name="search.GED_teleCreateTime" style="width:150px" id="startTime">
   		≤${(cmodel.tele_createTime)!''}＜ <input class="easyui-datebox" data-options="editable:false" name="search.LED_teleCreateTime" style="width:150px" id="endTime">
	    ${(cmodel.tele_id)!''}:<input type="text" name="search.EQI_id" />
		${(cmodel.tele_name)!''}:<input type="text" name="search.LIKES_teleName"/>
		${(cmodel.tele_status)!''}:<select id="search_tele_status" name="search.EQI_teleStatus"></select>
	    <input type="button" onclick="search();" value="查询"/><input type="reset" onclick="searchReset();" value="清空"/>
       </div>
  	</form>         
</div>
<div id="editObj" style="display:none;">
    <form id="editForm" method="post">
    <input type="hidden" id="id" name="id" value=""/>
    <table class="formTable" width="100%" style="padding:3px">
        <tbody>
        	<tr>
                <td width="120px">${(cmodel.tele_name)!''}</td><td><input type="text" id="teleName" name="teleName" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.tele_key)!''}</td><td><input type="text" id="teleKey" name="teleKey" readOnly="true" /> <input type="button" onclick="randomKey()" value="重置密钥" /></td>
            </tr>
            <tr>
                <td>${(cmodel.tele_notifyCallUrl)!''}</td><td><input type="text" id="teleNotifyCallUrl" name="teleNotifyCallUrl" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.tele_notifyMtReceUrl)!''}</td><td><input type="text" id="teleNotifyMtReceUrl" name="teleNotifyMtReceUrl" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.tele_notifyMoUrl)!''}</td><td><input type="text" id="teleNotifyMoUrl" name="teleNotifyMoUrl" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.tele_needVerifyIp)!''}</td><td><select id="needVerifyIp" name="needVerifyIp"/></td>
            </tr>
            <tr>
                <td>${(cmodel.tele_validIp)!''}</td><td><input type="text" id="validIp" name="validIp" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.tele_callFlow)!''}</td><td><input type="text" id="callFlow" name="callFlow" value="5"/></td>
            </tr>
            <tr>
                <td>${(cmodel.tele_msgFlow)!''}</td><td><input type="text" id="msgFlow" name="msgFlow" value="5"/></td>
            </tr>
            <tr>
                <td>${(cmodel.tele_generalFlow)!''}</td><td><input type="text" id="generalFlow" name="generalFlow" value="5"/></td>
            </tr>
            <tr>
                <td>${(cmodel.tele_checkAuthFmTimeout)!''}</td><td><input type="text" id="checkAuthFmTimeout" name="checkAuthFmTimeout" value="1000"/></td>
            </tr>
            <tr>
                <td>${(cmodel.tele_display)!'来显类型定义'}</td><td><select id="display" name="display"/></td>
            </tr>
            <tr>
                <td>${(cmodel.tele_authFmUrl)!'商户侧校验来电号码url'}</td><td><input type="text" value=""  id="authFmUrl" name="authFmUrl"/></td>
            </tr>
            <tr>
                <td>${(cmodel.tele_callDirectionUrl)!'call direction url参数'}</td><td><input type="text" value=""  id="callDirectionUrl" name="callDirectionUrl"/></td>
            </tr>
            <tr>
                <td>${(cmodel.tele_notifyMediaStreamUrl)!'notifyMediaStreamUrl'}</td><td><input type="text" value=""  id="notifyMediaStreamUrl" name="notifyMediaStreamUrl"/></td>
            </tr>
            <tr>
                <td>${(cmodel.tele_dmpCallUrl)!'dmpCallUrl'}</td><td><input type="text" value=""  id="dmpCallUrl" name="dmpCallUrl"/></td>
            </tr>
            <tr>
                <td>${(cmodel.tele_dmpMtReceUrl)!'dmpMtReceUrl'}</td><td><input type="text" value=""  id="dmpMtReceUrl" name="dmpMtReceUrl"/></td>
            </tr>
            <tr>
                <td>${(cmodel.tele_dmpMoUrl)!'dmpMoUrl'}</td><td><input type="text" value=""  id="dmpMoUrl" name="dmpMoUrl"/></td>
            </tr>
            <tr>
                <td>${(cmodel.tele_notifyAudioStatusUrl)!'notifyAudioStatusUrl'}</td><td><input type="text" value=""  id="notifyAudioStatusUrl" name="notifyAudioStatusUrl"/></td>
            </tr>
            <tr>
                <td>${(cmodel.tele_dmpRecordUrl)!'dmpRecordUrl'}</td><td><input type="text" value=""  id="dmpRecordUrl" name="dmpRecordUrl"/></td>
            </tr>
   
        </tbody>
    </table>
    <table border="0" width="100%" id="formOperate">
    	<tr><td align="center"><input type="button" id="editSubmit" value="确定"/><input type="reset"/></td></tr>
    </table>
    </form>
</div>
<div id="viewObj" style="display:none;">
    <form id="viewForm">
    <table class="formTable" width="100%" style="padding:3px">
        <tbody>
            <tr height="28px;">
                <td width="120px">${(cmodel.tele_id)!''}</td><td id="view_id" width="400px;"></td>
            </tr>
            <tr height="28px;">
                <td>${(cmodel.tele_name)!''}</td><td id="view_teleName"></td>
            </tr>
            <tr height="28px;">
                <td>${(cmodel.tele_key)!''}</td><td id="view_teleKey"></td>
            </tr>
            <tr height="28px;">
                <td>${(cmodel.tele_createTime)!''}</td><td id="view_teleCreateTime"></td>
            </tr>
            <tr height="28px;">
                <td>${(cmodel.tele_updateTime)!''}</td><td id="view_teleUpdateTime"></td>
            </tr>
            <tr height="28px;">
                <td>${(cmodel.tele_status)!''}</td><td id="view_teleStatus"></td>
            </tr>
            <tr height="28px;">
                <td>${(cmodel.tele_notifyCallUrl)!''}</td><td id="view_teleNotifyCallUrl" width="465px;" style="word-WRAP:break-word;"></td>
            </tr>
            <tr height="28px;">
                <td>${(cmodel.tele_notifyMtReceUrl)!''}</td><td id="view_teleNotifyMtReceUrl" style="word-WRAP:break-word;"></td>
            </tr>
            <tr height="28px;">
                <td>${(cmodel.tele_notifyMoUrl)!''}</td><td id="view_teleNotifyMoUrl" style="word-WRAP:break-word;"></td>
            </tr>
            <tr height="28px;">
                <td>${(cmodel.tele_needVerifyIp)!''}</td><td id="view_needVerifyIp"></td>
            </tr>
            <tr height="28px;">
                <td>${(cmodel.tele_validIp)!''}</td><td id="view_validIp"></td>
            </tr>
            <tr height="28px;">
                <td>${(cmodel.tele_callFlow)!''}</td><td id="view_callFlow"></td>
            </tr>
            <tr height="28px;">
                <td>${(cmodel.tele_msgFlow)!''}</td><td id="view_msgFlow"></td>
            </tr>
            <tr height="28px;">
                <td>${(cmodel.tele_generalFlow)!''}</td><td id="view_generalFlow"></td>
            </tr>
            <tr height="28px;">
                <td>${(cmodel.tele_isDelete)!''}</td><td id="view_teleIsDelete"></td>
            </tr>
            <tr height="28px;">
                <td>${(cmodel.tele_display)!'来显类型定义'}</td><td id="view_display"></td>
            </tr>
            <tr>
                <td>${(cmodel.tele_tele_display)!'商户侧校验来电号码url'}</td><td id="view_authFmUrl"/>
            </tr>
            <tr>
                <td>${(cmodel.tele_callDirectionUrl)!'call direction url参数'}</td><td id="view_callDirectionUrl"/>
            </tr>
            <tr>
                <td>${(cmodel.tele_notifyMediaStreamUrl)!'notifyMediaStreamUrl'}</td><td id="view_notifyMediaStreamUrl"/>
            </tr>
            <tr>
                <td>${(cmodel.tele_dmpCallUrl)!'dmpCallUrl'}</td><td id="view_dmpCallUrl"/>
            </tr>
            <tr>
                <td>${(cmodel.tele_dmpMtReceUrl)!'dmpMtReceUrl'}</td><td id="view_dmpMtReceUrl"/>
            </tr>
            <tr>
                <td>${(cmodel.tele_dmpMoUrl)!'dmpMoUrl'}</td><td id="view_dmpMoUrl"/>
            </tr>
            <tr>
                <td>${(cmodel.tele_notifyAudioStatusUrl)!'notifyAudioStatusUrl'}</td><td id="view_notifyAudioStatusUrl"/>
            </tr>
            <tr>
                <td>${(cmodel.tele_dmpRecordUrl)!'dmpRecordUrl'}</td><td id="view_dmpRecordUrl"/>
            </tr>
            
        </tbody>
    </table>
    </form>
</div>

<div id="telePermissionDiv" style="display:none;">
    <form id="telePermissionForm" method="post">
	<input type="hidden" id="teleID" name="teleID" value=""/>
    <table class="formTable" width="100%" style="padding:3px">
        <tbody>
            <tr>
                <td>${(cmodel.staticMobile)!'是否允许调用固定密号接口'}</td>
                <td>
                <select class="tpc" id="staticMobile" name="staticMobile"/>
                </td>
            </tr>
         	<tr>
                <td>${(cmodel.relationMobile)!'是否允许调用关系密号接口'}</td>
                <td>
                <select class="tpc" id="relationMobile" name="relationMobile"/>
                </td>
            </tr>
            <tr>
                <td>${(cmodel.searchStaticMobile)!'是否允许调用查询密号绑定接口'}</td>
                <td>
                <select class="tpc" id="searchStaticMobile" name="searchStaticMobile"/>
                </td>
            </tr>
            <tr>
                <td>${(cmodel.controlMobileStatus)!'是否允许调用查询密号开关机状态接口'}</td>
                <td>
                <select class="tpc" id="controlMobileStatus" name="controlMobileStatus"/>
                </td>
            </tr>
            <tr>
                <td>${(cmodel.controlBlockMobile)!'是否允许调用黑白名单控制接口'}</td>
                <td>
                <select class="tpc" id="controlBlockMobile" name="controlBlockMobile"/>
                </td>
            </tr>
            <tr>
                <td>${(cmodel.notifyCall)!'是否允许使用话单通知接口'}</td>
                <td>
                <select class="tpc" id="notifyCall" name="notifyCall"/>
                </td>
            </tr>
            <tr>
                <td>${(cmodel.realTimeNotifyCalling)!'是否允许使用实时来电通知接口'}</td>
                <td>
                <select class="tpc" id="realTimeNotifyCalling" name="realTimeNotifyCalling"/>
                </td>
            </tr>
            <tr>
                <td>${(cmodel.realTimeNotifyCallAnswer)!'是否允许使用实时来电接听通知接口'}</td>
                <td>
                <select class="tpc" id="realTimeNotifyCallAnswer" name="realTimeNotifyCallAnswer"/>
                </td>
            </tr>
            <tr>
                <td>${(cmodel.controlMt)!'是否允许控制虚号码短信下发接口'}</td>
                <td>
                <select class="tpc" id="controlMt" name="controlMt"/>
                </td>
            </tr>
            <tr>
                <td>${(cmodel.notifyMo)!'是否允许使用短信上线通知接口'}</td>
                <td>
                <select class="tpc" id="notifyMo" name="notifyMo"/>
                </td>
            </tr>
            <tr>
                <td>${(cmodel.controlCallDirection)!'是否允许来电通知商户且由商户控制呼转接口'}</td>
                <td>
                <select class="tpc" id="controlCallDirection" name="controlCallDirection"/>
                </td>
            </tr>
            <tr>
                <td>${(cmodel.controlCallBack)!'是否允许使用双呼接口'}</td>
                <td>
                <select class="tpc" id="controlCallBack" name="controlCallBack"/>
                </td>
            </tr>
            <tr>
                <td>${(cmodel.uploadMedia)!'是否允许媒体上传接口'}</td>
                <td>
                <select class="tpc" id="uploadMedia" name="uploadMedia"/>
                </td>
            </tr>
            <tr>
                <td>${(cmodel.pushMedia)!'是否允许媒体推送接口'}</td>
                <td>
              	<select class="tpc" id="pushMedia" name="pushMedia"/>
                </td>
            </tr>
            <tr>
                <td>${(cmodel.hasLeaveMessage)!'是否有留言的权限'}</td>
                <td>
              	<select class="tpc" id="hasLeaveMessage" name="hasLeaveMessage"/>
                </td>
            </tr>
            <tr>
                <td>${(cmodel.flashSms)!'是否有flashSms的权限'}</td>
                <td>
              	<select class="tpc" id="flashSms" name="flashSms"/>
                </td>
            </tr>
            <tr>
                <td>${(cmodel.hasRecording)!'是否有录音的权限'}</td>
                <td>
              	<select class="tpc" id="hasRecording" name="hasRecording"/>
                </td>
            </tr>
            <tr>
                <td>${(cmodel.percentForRecording)!'录音抽取百分比（0~100）'}</td>
                <td>
              	<input type="text" id="percentForRecording" name="percentForRecording" value="2"/>
                </td>
            </tr>
        </tbody>
    </table>
    <table border="0" width="100%" id="formOperate">
    	<tr><td align="center">
    		<input type="button" id="telePermissionSubmit" value="确定"/>
    		<input type="reset"/>
    	</td></tr>
    </table>
    </form>
</div>

</body>
</html>
