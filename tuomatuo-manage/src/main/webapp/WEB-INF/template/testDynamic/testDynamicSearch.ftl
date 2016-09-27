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
var testUserList = new Array();
<#if testUserList?exists>
	<#list testUserList as pl>
testUserList[${pl.id}]="${pl.loginName}";
	</#list>
</#if>
var testServiceList = new Array();
<#if testServiceList?exists>
	<#list testServiceList as pl>
testServiceList[${pl.id}]="${pl.name}";
	</#list>
</#if>

//初始化
$(function(){

	searchInit("${webRoot}/testDynamic/search.do");    
	$("#search_status").html("<option value=''  selected>请选择</option>")
	$("#search_status").append(getSelectList(userNumber_status_map,""));
	$("#search_currStatus").html("<option value=''  selected>请选择</option>")
	$("#search_currStatus").append(getSelectList(userNumber_currStatus_map,""));
	
	
	$("#testUserId").html("<option value=''  selected>请选择</option>")
	$("#testUserId").append(getSelectList(testUserList,""));
	
	$("#testServiceId").html("<option value=''  selected>请选择</option>")
	$("#testServiceId").append(getSelectList(testServiceList,""));
})
function viewInitCombobox(obj,data){
	obj.find("#view_status").html(userNumber_status_map[obj.find("#view_status").html()]);
	obj.find("#view_currStatus").html(userNumber_currStatus_map[obj.find("#view_currStatus").html()]);
	obj.find("#view_isDelete").html(isDelete_map[obj.find("#view_isDelete").html()]);
}
function newInitCombobox(obj,data){
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
		//checkOnSelect:false,
		columns:[[
			{field:'ck',checkbox:true,width:2},
			{field:'id',title:'${(cmodel.userNumber_id)!''}',sortable:true,
				formatter:function(value,row,index){return row.id;} 
			},
			{field:'userId',title:'${(cmodel.userNumber_userId)!''}',sortable:true,
				formatter:function(value,row,index){return row.userId;} 
			},
			{field:'mobile',title:'${(cmodel.userNumber_mobile)!''}',sortable:true,
				formatter:function(value,row,index){return row.mobile;} 
			},
			{field:'redirectMobile',title:'${(cmodel.userNumber_redirectMobile)!''}',sortable:true,
				formatter:function(value,row,index){return row.redirectMobile;} 
			},
			{field:'status',title:'${(cmodel.userNumber_status)!''}',sortable:true,
				formatter:function(value,row,index){return userNumber_status_map[row.status];} 
			},
			{field:'createTime',title:'${(cmodel.userNumber_createTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.createTime;} 
			},
			{field:'currStatus',title:'${(cmodel.userNumber_currStatus)!''}',sortable:true,
				formatter:function(value,row,index){return userNumber_currStatus_map[row.currStatus];} 
			},
			{field:'isRedirect',title:'是否设置转接',sortable:true,
				formatter:function(value,row,index){return userNumber_isRedirect_map[row.isRedirect];} 
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
		      disabled:noPermis("testDynamic","addDynamic"),
		      handler:function(){
		 	     addDynamic("${(cmodel.userNumber)!''}","${webRoot}/testDynamic/addDynamic.do");
		      }
		}]
	});
}
var tdErr;
function validation(obj){
var err = "";
var errId = "";
var userId = $("#userId").val();
var mobile = $("#mobile").val();
var redirectMobile = $("#redirectMobile").val();
var status = $("#status").val();
var createTime = $("#createTime").val();
var bootTime = $("#bootTime").val();
var shutTime = $("#shutTime").val();
var isDelete = $("#isDelete").val();
var msgStartTime = $("#msgStartTime").val();
var msgEndTime = $("#msgEndTime").val();
	if(isNull(userId)){
		err = "${(cmodel.userNumber_userId)!''}不能为空！";
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


function addDynamic(name,action,width,height){
	if(width==undefined||width==''){
		width=500;
	}
	if(height==undefined||height==''){
		height=300;
	}
	$('#editObj').show();
	$("#editObj").dialog({
		title:'添加'+name,
		top:100,
		width:width,
		height:height,
		cache: false,
		closed: true
	});
	$('#editForm')[0].reset();
	if ($.isFunction(window.newInitCombobox))
		newInitCombobox($('#editForm'),"");
	$("#editSubmit").unbind();
	$("#editSubmit").click(function(){
		if ($.isFunction(window.validation)){
			var rs=validation($('#editForm'));
			if(rs==undefined||rs==false){
				return;
			}
		}
		$.ajax({
            type: "post",
            dataType: "json",
            url: action,
            data: $('#editForm').serializeArray(),
            contentType: 'application/x-www-form-urlencoded; charset=utf-8', 
            success: function(data){
            	if(data!=undefined&&data.result==true){
            		$('#editObj').dialog('close');
            		$("#returnInfo").show();
            		$("#returnInfo").dialog({
						title:'操作完成',
						top:100,
						width:500,
						height:300,
						cache: false,
						closed: true
					});					
            		$("#testNumber").html(data.testNumber);
            		$("#testBindNumber").html(data.testBindNumber);
            		$('#returnInfo').dialog('open');	
            	}else{
            		returnError(data);
            	}
            }
		});
	});
	$('#editObj').dialog('open');	
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
   ${(cmodel.userNumber_mobile)!''}:<input type="text" name="search.LIKES_mobile"  size="8" />
   ${(cmodel.userNumber_status)!''}:<select id="search_status" name="search.EQS_status"></select>
   ${(cmodel.userNumber_currStatus)!''}:<select id="search_currStatus" name="search.EQS_currStatus"></select>
      <input type="button" onclick="search();" value="查询"/><input type="reset" value="清空"/>
      </div>
  	</form>         
</div>
<div id="editObj" style="display:none;">
    <form id="editForm" method="post">
    <table class="formTable" width="100%">
        <tbody>
			<input type="hidden" id="id" name="id" value=""/>
            <tr>
                <td>测试用户：</td><td><select id="testUserId" name="userId"></select></td>
            </tr>
            <tr>
                <td>测试服务：</td><td><select id="testServiceId" name="servicesId"></select></td>
            </tr>
        </tbody>
    </table>
    <table border="0" width="100%" id="formOperate">
    	<tr><td align="center"><input type="button" id="editSubmit" value="确定"/><input type="reset"/></td></tr>
    </table>
    </form>
</div>
<div id="returnInfo" style="display:none;">
    <table class="formTable" width="100%">
        <tbody>
            <tr>
                <td>测试用户：</td><td><div id="testNumber"></div></td>
            </tr>
            <tr>
                <td>绑定的临时号码：</td><td><div id="testBindNumber"></div></td>
            </tr>
        </tbody>
    </table>
</div>
</body>
</html>
