<!DOCTYPE html>
<html>
<head>
<title>search</title>
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
var permission_parentId_map = new Array();
<#if parentId?exists>
	<#list parentId as pl>
permission_parentId_map[${pl.id}]="${pl.name}";
	</#list>
</#if>
$(function(){
	searchInit("${webRoot}/sysPermission/search.do");     
	$("#search_menu").html("<option value='' selected>请选择</option>");
	$("#search_menu").append(getSelectList(permission_menu_map,""));
})
function viewInitCombobox(obj,data){
	obj.find("#view_menu").html(permission_menu_map[obj.find("#view_menu").html()]);
	
	obj.find("#view_parentId").html(permission_parentId_map[obj.find("#view_parentId").html()]);
}
function newInitCombobox(obj,data){
	obj.find("#menu").html("<option value='' selected>请选择</option>");
	obj.find("#menu").append(getSelectList(permission_menu_map,data.menu));
	
	obj.find("#parentId").html("<option value='-1' selected>请选择</option>");
	obj.find("#parentId").append(getSelectList(permission_parentId_map,data.parentId));
}
function editInitCombobox(obj,data){
	obj.find("#menu").html("<option value='' selected>请选择</option>");
	obj.find("#menu").append(getSelectList(permission_menu_map,data.menu));
	
	obj.find("#parentId").html("<option value='-1' selected>请选择</option>");
	if(data.menu=="1"){
		obj.find("#parentId").append(getSelectList(permission_parentId_map,data.parentId));
	}
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
			{field:'id',title:'${(cmodel.sysPermission_id)!''}',sortable:true,
				formatter:function(value,row,index){return row.id;} 
			},
			{field:'name',title:'${(cmodel.sysPermission_name)!''}',sortable:true,
				formatter:function(value,row,index){return row.name;} 
			},
			{field:'path',title:'${(cmodel.sysPermission_path)!''}',sortable:true,
				formatter:function(value,row,index){return row.path;} 
			},
			{field:'methods',title:'${(cmodel.sysPermission_methods)!''}',sortable:true,
				formatter:function(value,row,index){return row.methods;} 
			},
			{field:'menu',title:'${(cmodel.sysPermission_menu)!''}',sortable:true,
				formatter:function(value,row,index){return permission_menu_map[row.menu];} 
			},
			{field:'parentId',title:'${(cmodel.sysPermission_parentId)!''}',sortable:true,
				formatter:function(value,row,index){return row.parentId;} 
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
		      disabled:noPermis("sysPermission","edit"),
		      handler:function(){
		 	     viewObj("${(cmodel.sysPermission)!''}","${webRoot}/sysPermission/view.do?id=");
		      }
		},{
		      iconCls:'icon-add',
		      text:'添加',
		      disabled:noPermis("sysPermission","save"),
		      handler:function(){
		 	     newObj("${(cmodel.sysPermission)!''}","${webRoot}/sysPermission/save.do");
		      }
		},{
		      iconCls:'icon-edit',
		      text:'修改',
		      disabled:noPermis("sysPermission","update"),
		      handler:function(){
		          editObj("${(cmodel.sysPermission)!''}","${webRoot}/sysPermission/view.do?id=","${webRoot}/sysPermission/update.do");
		      }
		},{
		      iconCls:'icon-delete',
		      text:'删除',
		      disabled:noPermis("sysPermission","delete"),
		      handler:function(){
		          removeObj("${webRoot}/sysPermission/delete.do");
		      }
		}]
	});
}
var tdErr;
function validation(obj){
	var name = $("#name").val();
	var err = "";
	var errId = "";
	if(isNull(name)){
		err = "${(cmodel.sysPermission_name)!''}不能为空！";
		errId = "name";
	} else 
	if(!checkLength(name,3,100)){
		err = "${(cmodel.sysPermission_name)!''}不能少于3个字且不能超过100个字！";
		errId = "name";
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
	  ${(cmodel.sysPermission_name)!''}:<input type="text" name="search.LIKES_name" value=""/>
	  ${(cmodel.sysPermission_path)!''}:<input type="text" name="search.LIKES_path" value=""/>
	  ${(cmodel.sysPermission_methods)!''}:<input type="text" name="search.LIKES_methods" value=""/>
	  ${(cmodel.sysPermission_menu)!''}:<select id="search_menu" name="search.EQS_menu"/>
      <input type="button" onclick="search();" value="查询"/><input type="reset" value="清空"/>
      </div>
  	</form>         
</div>
<div id="editObj" style="display:none;">
    <form id="editForm" method="post"><input type="hidden" id="id" name="id" value=""/>
    <table class="formTable" width="100%">
            <tr>
                <td>${(cmodel.sysPermission_name)!''}</td><td><input type="text" id="name" name="name" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.sysPermission_path)!''}</td><td><input type="text" id="path" name="path" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.sysPermission_methods)!''}</td><td><input type="text" id="methods" name="methods" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.sysPermission_menu)!''}</td>
                <td>
               		<select id="menu" name="menu"/>
                </td>
            </tr>
            <tr>
                <td>${(cmodel.sysPermission_parentId)!''}</td>
                <td>
               		<select id="parentId" name="parentId"/>
                </td>
            </tr>
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
                <td>${(cmodel.sysPermission_id)!''}</td><td id="view_id"></td>
            </tr>
            <tr>
                <td>${(cmodel.sysPermission_name)!''}</td><td id="view_name"></td>
            </tr>
            <tr>
                <td>${(cmodel.sysPermission_path)!''}</td><td id="view_path"></td>
            </tr>
            <tr>
                <td>${(cmodel.sysPermission_methods)!''}</td><td id="view_methods"></td>
            </tr>
            <tr>
                <td>${(cmodel.sysPermission_menu)!''}</td><td id="view_menu"></td>
            </tr>
            <tr>
                <td>${(cmodel.sysPermission_parentId)!''}</td><td id="view_parentId"></td>
            </tr>
        </tbody>
    </table>
    </form>
</div>
</body>
</html>
