<!DOCTYPE html>
<html>
<head>
<title>spring3</title>
<meta charset="UTF-8">
<link href="${webRoot}/css/themes/default/easyui.css" rel="stylesheet"
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
var serviceIdToName = new Array();
<#list allService as s>
serviceIdToName[${s.id}]="${s.name}";
</#list>
//初始化
$(function(){
	searchInit("${webRoot}/userServices/search.do");     
	
	$("#search_status").html("<option value='' selected>请选择</option>");
	$("#search_status").append(getSelectList(userServices_status_map,""));   
	
	$("#search_serviceId").html("<option value='' selected>请选择</option>");
	$("#search_serviceId").append(getSelectList(serviceIdToName,""));        
})
function viewInitCombobox(obj,data){
	obj.find("#view_isDelete").html(isDelete_map[obj.find("#view_isDelete").html()]);
	obj.find("#view_serviceId").html(serviceIdToName[obj.find("#view_serviceId").html()]);
}
function newInitCombobox(obj,data){
	InitServiceType(obj,data);
}
function editInitCombobox(obj,data){
	newInitCombobox(obj,data);
}
// 初始化 
function InitServiceType(obj,data){
	obj.find("#serviceId").html("<option value='' selected>请选择</option>");
	$.ajax({
				url : '../services/search.do',
				type : 'post',
				data : {
					'method' : 'getAllQianMing',
					timestampstr : new Date().getTime()
				},
				dataType : 'json',
				success : function(data){
					var rows = data.rows;
					var length = data.rows.length;
					var path = "";
					for(var i=0;i < length;i++){
						path +=rows[i].id+rows[i].name+"|";
						obj.find("#serviceId").append("<option value="+rows[i].id+">"+rows[i].name+"("+services_type_map[rows[i].type]+", "+ rows[i].price +"元, "+services_from_flag[rows[i].platformFlag]+")</option>")
					}
				}
			});
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
		//checkOnSelect:false,
		columns:[[
			{field:'ck',checkbox:true,width:2},
			{field:'id',title:'${(cmodel.userServices_id)!''}',sortable:true,
				formatter:function(value,row,index){return row.id;} 
			},
			{field:'userId',title:'${(cmodel.userServices_userId)!''}',sortable:true,
				formatter:function(value,row,index){return row.userId;} 
			},
			{field:'serviceId',title:'${(cmodel.userServices_serviceId)!''}',sortable:true,
				formatter:function(value,row,index){return serviceIdToName[row.serviceId];} 
			},
			{field:'createTime',title:'${(cmodel.userServices_createTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.createTime;} 
			},
			{field:'status',title:'${(cmodel.userServices_status)!''}',sortable:true,
				formatter:function(value,row,index){return userServices_status_map[row.status];} 
			},
			{field:'currentMsgCount',title:'${(cmodel.userServices_currentMsgCount)!''}',sortable:true,
				formatter:function(value,row,index){return row.currentMsgCount;} 
			},
			{field:'currentCallMinutes',title:'${(cmodel.userServices_currentCallMinutes)!''}',sortable:true,
				formatter:function(value,row,index){return row.currentCallMinutes;} 
			},
			{field:'weight',title:'${(cmodel.userServices_weight)!''}',sortable:true,
				formatter:function(value,row,index){return row.weight;} 
			},
			{field:'isDelete',title:'${(cmodel.userServices_isDelete)!''}',sortable:true,
				formatter:function(value,row,index){return isDelete_map[row.isDelete];} 
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
		      iconCls:'icon-info',
		      text:'详细',
		      disabled:noPermis("userServices","view"),
		      handler:function(){
		 	     viewObj("${(cmodel.userServices)!''}","${webRoot}/userServices/view.do?id=");
		      }
		},{
		      iconCls:'icon-add',
		      text:'添加',
		      disabled:noPermis("userServices","saveByManager"),
		      handler:function(){
		 	     newObj("${(cmodel.userServices)!''}","${webRoot}/userServices/saveByManager.do");
		      }
		},{
		      iconCls:'icon-edit',
		      text:'修改',
		      disabled:noPermis("userServices","update"),
		      handler:function(){
		          editObj("${(cmodel.userServices)!''}","${webRoot}/userServices/view.do?id=","${webRoot}/userServices/update.do");
		      }
		},{
		      iconCls:'icon-remove',
		      text:'删除',
		      disabled:noPermis("userServices","delete"),
		      handler:function(){
		          removeObj("${webRoot}/userServices/delete.do");
		      }
		},{
		      iconCls:'icon-reload',
		      text:'重置状态',
		      disabled:noPermis("userServices","changeStatus"),
		      handler:function(){
		          changeObj("${webRoot}/userServices/changeStatus.do");
		      }
		}]
	});
}
var tdErr;
function validation(obj){
var err = "";
var errId = "";
var userId = $("#userId").val();
var serviceId = $("#serviceId").val();
var createTime = $("#createTime").val();
var currentMsgCount = $("#currentMsgCount").val();
var currentCallMinutes = $("#currentCallMinutes").val();
var weight = $("#weight").val();
var isDelete = $("#isDelete").val();
	if(isNull(userId)){
		err = "${(cmodel.userServices_userId)!''}不能为空！";
		errId = "userId";
	} else 
	{
		//success
		return true;
	}
	$.messager.show({title:'编辑错误提示',msg:err,timeout:5000,showType:'slide'});
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
</script>	
</head>
  
<body>
<table id="dg">
</table>
<div id="tb" style="padding:5px;height:auto">
	<form id="searchForm">
   	<div>
      开始时间: <input class="easyui-datebox" name="search.GED_createTime" id="startTime" style="width:80px">
      结束时间: <input class="easyui-datebox" name="search.LTD_createTime" id="endTime" style="width:80px">
      ${(cmodel.userServices_userId)!''}:<input type="text" name="search.EQS_userId" size="8"/>
      ${(cmodel.userServices_status)!''}:<select id="search_status" name="search.EQS_status"/>
      ${(cmodel.userServices_serviceId)!''}:<select id="search_serviceId" name="search.EQS_serviceId"/>
      <input type="button" onclick="search();" value="查询"/><input type="reset" value="清空" onclick="searchReset();"/>
      </div>
  	</form>         
</div>
<div id="editObj" style="display:none;">
    <form id="editForm" method="post">
    <table class="formTable" width="100%">
        <tbody>
			<input type="hidden" id="id" name="id" value=""/>
            <tr>
                <td>${(cmodel.userServices_userId)!''}</td><td><input type="text" id="userId" name="userId" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.userServices_serviceId)!''}</td><td><select id="serviceId" name="serviceId" value=""></selected></td>
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
    <table class="formTable" width="100%">
        <tbody>
            <tr>
                <td>${(cmodel.userServices_id)!''}</td><td id="view_id"></td>
            </tr>
            <tr>
                <td>${(cmodel.userServices_userId)!''}</td><td id="view_userId"></td>
            </tr>
            <tr>
                <td>${(cmodel.userServices_serviceId)!''}</td><td id="view_serviceId"></td>
            </tr>
            <tr>
                <td>${(cmodel.userServices_createTime)!''}</td><td id="view_createTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.userServices_currentMsgCount)!''}</td><td id="view_currentMsgCount"></td>
            </tr>
            <tr>
                <td>${(cmodel.userServices_currentCallMinutes)!''}</td><td id="view_currentCallMinutes"></td>
            </tr>
            <tr>
                <td>${(cmodel.userServices_weight)!''}</td><td id="view_weight"></td>
            </tr>
            <tr>
                <td>${(cmodel.userServices_isDelete)!''}</td><td id="view_isDelete"></td>
            </tr>
        </tbody>
    </table>
    </form>
</div>
</body>
</html>
