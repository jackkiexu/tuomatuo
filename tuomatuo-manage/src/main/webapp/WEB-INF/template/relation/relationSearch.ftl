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
var splist = new Array();
<#if spList?exists>
	<#list spList as tele>
		splist["${tele.id}"]="${tele.teleName}";
	</#list>
</#if>
//初始化
$(function(){
	searchInit("${webRoot}/relation/search.do");         
	
	$("#search_status").html("<option value='' selected>请选择</option>");
	$("#search_status").append(getSelectList(relation_map,""));  
	
	$("#search_onOff").html("<option value='' selected>请选择</option>")
	$("#search_onOff").append(getSelectList(relation_onOff,"")); 
})
function newInitCombobox(obj,data){
}
function viewInitCombobox(obj,data){
	obj.find("#view_status").html(relation_map[obj.find("#view_status").html()]);
	obj.find("#view_onOff").html(relation_onOff[obj.find("#view_onOff").html()]);
	obj.find("#view_isDelete").html(isDelete_map[obj.find("#view_isDelete").html()]);
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
			{field:'id',title:'${(cmodel.relation_id)!''}',sortable:true,
				formatter:function(value,row,index){return row.id;} 
			},
			{field:'spId',title:'${(cmodel.relation_spId)!''}',sortable:true,
				formatter:function(value,row,index){return splist[row.spId];} 
			},
			{field:'bindMobile',title:'${(cmodel.relation_bindMobile)!''}',sortable:true,
				formatter:function(value,row,index){return row.bindMobile;} 
			},
			{field:'virtualMobile',title:'${(cmodel.relation_virtualMobile)!''}',sortable:true,
				formatter:function(value,row,index){return row.virtualMobile;} 
			},
			{field:'status',title:'${(cmodel.relation_status)!''}',sortable:true,
				formatter:function(value,row,index){return relation_map[row.status];} 
			},
			{field:'createTime',title:'${(cmodel.relation_createTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.createTime;} 
			},
			{field:'updateTime',title:'${(cmodel.relation_updateTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.updateTime;} 
			},
			{field:'onOff',title:'${(cmodel.relation_onOff)!''}',sortable:true,
				formatter:function(value,row,index){return relation_onOff[row.onOff];} 
			},
			{field:'correspondingID',title:'${(cmodel.relation_correspondingID)!''}',sortable:true,
				formatter:function(value,row,index){return row.correspondingID;} 
			},
			{field:'isDelete',title:'${(cmodel.relation_isDelete)!''}',sortable:true,
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
		      disabled:noPermis("relation","view"),
		      handler:function(){
		 	     viewObj("${(cmodel.relation)!''}","${webRoot}/relation/view.do?id=","","400");
		      }
		},{
		      iconCls:'icon-add',
		      text:'添加',
		      disabled:noPermis("relation","save"),
		      handler:function(){
		 	     newObj("${(cmodel.relation)!''}","${webRoot}/relation/save.do","","400");
		      }
		},{
		      iconCls:'icon-edit',
		      text:'修改',
		      disabled:noPermis("relation","update"),
		      handler:function(){
		          editObj("${(cmodel.relation)!''}","${webRoot}/relation/view.do?id=","${webRoot}/relation/update.do","","400");
		      }
		},{
		      iconCls:'icon-remove',
		      text:'删除',
		      disabled:noPermis("relation","delete"),
		      handler:function(){
		          removeObj("${webRoot}/relation/delete.do");
		      }
		}]
	});
}
var tdErr;
function validation(obj){
var err = "";
var errId = "";
var spId = $("#spId").val();
var bindMobile = $("#bindMobile").val();
var virtualMobile = $("#virtualMobile").val();
var status = $("#status").val();
var createTime = $("#createTime").val();
var updateTime = $("#updateTime").val();
var isDelete = $("#isDelete").val();
var onOff = $("#onOff").val();
	if(isNull(userId)){
		err = "${(cmodel.relation_spId)!''}不能为空！";
		errId = "spId";
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
      查询起始时间: <input class="easyui-datebox" name="search.GED_createTime" id="createTime" style="width:80px">
      查询结束时间: <input class="easyui-datebox" name="search.LTD_createTime" id="updateTime" style="width:80px">
	  ${(cmodel.relation_bindMobile)!''}:<input type="text"  id="search_bindMobile" name="search.EQS_bindMobile" value="${(form.bindMobile)!''}"/>
	  ${(cmodel.relation_virtualMobile)!''}:<input type="text"  id="search_virtualMobile" name="search.EQS_virtualMobile" value="${(form.virtualMobile)!''}"/>
	  ${(cmodel.relation_status)!''}:<select id="search_status" name="search.EQS_status"></select>
	  ${(cmodel.relation_onOff)!''}:<select id="search_onOff" name="search.EQS_onOff"></select>
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
                <td>${(cmodel.relaion_spId)!''}</td><td><input type="text" id="spId" name="spId" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.relation_bindMobile)!''}</td><td><input type="text" id="bindMobile" name="bindMobile" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.relation_virtualMobile)!''}</td><td><input type="text" id="virtualMobile" name="virtualMobile" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.relation_status)!''}</td><td><input type="text" id="status" name="status" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.relation_createTime)!''}</td><td><input type="text" id="createTime" name="createTime" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.relation_updateTime)!''}</td><td><input type="text" id="updateTime" name="updateTime" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.relation_isDelete)!''}</td><td><input type="text" id="isDelete" name="isDelete" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.relation_onOff)!''}</td><td><input type="text" id="onOff" name="onOff" value=""/></td>
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
                <td>${(cmodel.relation_id)!''}</td><td id="view_id"></td>
            </tr>
            <tr>
                <td>${(cmodel.relation_spId)!''}</td><td id="view_spId"></td>
            </tr>
            <tr>
                <td>${(cmodel.relation_bindMobile)!''}</td><td id="view_bindMobile"></td>
            </tr>
            <tr>
                <td>${(cmodel.relation_virtualMobile)!''}</td><td id="view_virtualMobile"></td>
            </tr>
            <tr>
                <td>${(cmodel.relation_status)!''}</td><td id="view_status"></td>
            </tr>
            <tr>
                <td>${(cmodel.relation_createTime)!''}</td><td id="view_createTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.relation_updateTime)!''}</td><td id="view_updateTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.relation_isDelete)!''}</td><td id="view_isDelete"></td>
            </tr>
            <tr>
                <td>${(cmodel.relation_onOff)!''}</td><td id="view_onOff"></td>
            </tr>
        </tbody>
    </table>
    </form>
</div>
</body>
</html>
