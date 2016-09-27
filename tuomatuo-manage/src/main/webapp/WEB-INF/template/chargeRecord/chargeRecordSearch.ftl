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
serviceIdToName[${s.id}]="${s.name}" + "|" + services_type_map[${s.type}] + "|" + services_from_flag[${s.platformFlag}] + "|" + "${s.price} 元";
</#list>
//初始化
$(function(){
	searchInit("${webRoot}/chargeRecord/search.do");     
	$("#search_status").html("<option value='' selected>请选择</option>");
	$("#search_status").append(getSelectList(chargeRecord_status_map,""));    
	
	$("#search_type").html("<option value='' selected>请选择</option>");
	$("#search_type").append(getSelectList(chargeRecord_type_map,""));   
	
	$("#search_serviceIds").html("<option value='' selected>请选择</option>");
	$("#search_serviceIds").append(getSelectList(serviceIdToName,""));    
})
function newInitCombobox(obj,data){
}
function viewInitCombobox(obj,data){
	obj.find("#view_status").html(chargeRecord_status_map[obj.find("#view_status").html()]);
	obj.find("#view_type").html(chargeRecord_type_map[obj.find("#view_type").html()]);
	obj.find("#view_serviceIds").html(serviceIdToName[obj.find("#view_serviceIds").html()]);
	obj.find("#view_isDelete").html(isDelete_map[obj.find("#view_isDelete").html()]);
	
	obj.find("#view_price").html(obj.find("#view_price").html()+"元");
}
function editInitCombobox(obj,data){
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
			{field:'id',title:'${(cmodel.chargeRecord_id)!""}',sortable:true,width:80,
				formatter:function(value,row,index){return row.id;} 
			},
			{field:'userId',title:'${(cmodel.chargeRecord_userId)!''}',sortable:true,
				formatter:function(value,row,index){return row.userId;} 
			},
			{field:'payAccount',title:'${(cmodel.chargeRecord_payAccount)!''}',sortable:true,
				formatter:function(value,row,index){return row.payAccount;} 
			},
			{field:'mobile',title:'${(cmodel.chargeRecord_mobile)!''}',sortable:true,
				formatter:function(value,row,index){return row.mobile;} 
			},
			{field:'status',title:'${(cmodel.chargeRecord_status)!''}',sortable:true,
				formatter:function(value,row,index){return chargeRecord_status_map[row.status];} 
			},
			{field:'type',title:'${(cmodel.chargeRecord_type)!''}',sortable:true,
				formatter:function(value,row,index){return chargeRecord_type_map[row.type];} 
			},
			{field:'createTime',title:'${(cmodel.chargeRecord_createTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.createTime;} 
			},
			{field:'price',title:'${(cmodel.chargeRecord_price)!''}',sortable:true,
				formatter:function(value,row,index){return row.price+'元';} 
			},
			{field:'openId',title:'${(cmodel.chargeRecord_openId)!''}',sortable:true,width:100,
				formatter:function(value,row,index){return row.openId;} 
			},
			{field:'updateTime',title:'${(cmodel.chargeRecord_updateTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.updateTime;} 
			},
			{field:'errorMsg',title:'${(cmodel.chargeRecord_errorMsg)!''}',sortable:true,width:80,
				formatter:function(value,row,index){return row.errorMsg;} 
			},
			{field:'serviceIds',title:'${(cmodel.chargeRecord_serviceIds)!''}',sortable:true,
				formatter:function(value,row,index){return serviceIdToName[row.serviceIds];} 
			},
			{field:'isDelete',title:'${(cmodel.chargeRecord_isDelete)!''}',sortable:true,
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
		      disabled:noPermis("chargeRecord","view"),
		      handler:function(){
		 	     viewObj("${(cmodel.chargeRecord)!''}","${webRoot}/chargeRecord/view.do?id=","","450");
		      }
		}]
	});
}
var tdErr;
function validation(obj){
var err = "";
var errId = "";
var userId = $("#userId").val();
var payAccount = $("#payAccount").val();
var mobile = $("#mobile").val();
var status = $("#status").val();
var type = $("#type").val();
var price = $("#price").val();
var openId = $("#openId").val();
var errorMsg = $("#errorMsg").val();
var serviceIds = $("#serviceIds").val();
var isDelete = $("#isDelete").val();
	if(isNull(userId)){
		err = "${(cmodel.chargeRecord_userId)!''}不能为空！";
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
      
	  ${(cmodel.chargeRecord_userId)!''}:<input type="text" name="search.EQS_userId" size="8"/>
	  ${(cmodel.chargeRecord_payAccount)!''}:<input type="text" name="search.LIKES_payAccount" size="8"/>
	  ${(cmodel.chargeRecord_mobile)!''}:<input type="text" name="search.LIKES_mobile" size="8"/>
	  <br />
	  结束时间: <input class="easyui-datebox" name="search.LTD_createTime" id="endTime" style="width:80px">
	  ${(cmodel.chargeRecord_status)!''}:<select id="search_status" name="search.EQS_status"></select>
	  ${(cmodel.chargeRecord_type)!''}:<select id="search_type" name="search.EQS_type"></select>
	  ${(cmodel.chargeRecord_serviceIds)!''}:<select id="search_serviceIds" name="search.EQS_serviceIds"></select>
      <input type="button" onclick="search();" value="查询"/><input type="reset" onclick="searchReset();" value="清空"/>
      </div>
  	</form>         
</div>
<div id="editObj" style="display:none;">
    <form id="editForm" method="post">
    <table class="formTable" width="100%">
        <tbody>
			<input type="hidden" id="id" name="id" value=""/>
            <tr>
                <td>${(cmodel.chargeRecord_userId)!''}</td><td><input type="text" id="userId" name="userId" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.chargeRecord_payAccount)!''}</td><td><input type="text" id="payAccount" name="payAccount" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.chargeRecord_mobile)!''}</td><td><input type="text" id="mobile" name="mobile" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.chargeRecord_status)!''}</td><td><input type="text" id="status" name="status" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.chargeRecord_type)!''}</td><td><input type="text" id="type" name="type" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.chargeRecord_price)!''}</td><td><input type="text" id="price" name="price" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.chargeRecord_openId)!''}</td><td><input type="text" id="openId" name="openId" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.chargeRecord_errorMsg)!''}</td><td><input type="text" id="errorMsg" name="errorMsg" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.chargeRecord_serviceIds)!''}</td><td><input type="text" id="serviceIds" name="serviceIds" value=""/></td>
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
                <td>${(cmodel.chargeRecord_id)!''}</td><td id="view_id"></td>
            </tr>
            <tr>
                <td>${(cmodel.chargeRecord_userId)!''}</td><td id="view_userId"></td>
            </tr>
            <tr>
                <td>${(cmodel.chargeRecord_payAccount)!''}</td><td id="view_payAccount"></td>
            </tr>
            <tr>
                <td>${(cmodel.chargeRecord_mobile)!''}</td><td id="view_mobile"></td>
            </tr>
            <tr>
                <td>${(cmodel.chargeRecord_status)!''}</td><td id="view_status"></td>
            </tr>
            <tr>
                <td>${(cmodel.chargeRecord_type)!''}</td><td id="view_type"></td>
            </tr>
            <tr>
                <td>${(cmodel.chargeRecord_createTime)!''}</td><td id="view_createTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.chargeRecord_price)!''}</td><td id="view_price"></td>
            </tr>
            <tr>
                <td>${(cmodel.chargeRecord_openId)!''}</td><td id="view_openId"></td>
            </tr>
            <tr>
                <td>${(cmodel.chargeRecord_updateTime)!''}</td><td id="view_updateTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.chargeRecord_errorMsg)!''}</td><td id="view_errorMsg"></td>
            </tr>
            <tr>
                <td>${(cmodel.chargeRecord_serviceIds)!''}</td><td id="view_serviceIds"></td>
            </tr>
            <tr>
                <td>${(cmodel.chargeRecord_isDelete)!''}</td><td id="view_isDelete"></td>
            </tr>
        </tbody>
    </table>
    </form>
</div>
</body>
</html>
