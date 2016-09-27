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
//初始化
$(function(){
	searchInit("${webRoot}/systemConfig/search.do");   
})
function viewInitCombobox(obj,data){
	obj.find("#view_status").html(numbers_status_map[obj.find("#view_status").html()]);
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
		idField:'id', //主键字段
		sortOrder: 'desc', //倒序
		remoteSort: true, //服务器端排序
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
			{field:'name',title:'${(cmodel.systemConfig_name)!''}',sortable:true,
				formatter:function(value,row,index){return row.name;} 
			},
			{field:'details',title:'${(cmodel.systemConfig_details)!''}',sortable:true,
				formatter:function(value,row,index){return row.details;} 
			},
			{field:'value',title:'${(cmodel.systemConfig_value)!''}',sortable:true,
				formatter:function(value,row,index){return row.value;} 
			},
			{field:'Confirmation',title:'操作',width:100,sortable:false,
				formatter:function(value,row,index){
					return '';
				}
			}
		]],
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
		      disabled:noPermis("systemConfig","save"),
		      handler:function(){
		 	     newObj("${(cmodel.systemConfig)!''}","${webRoot}/systemConfig/save.do","","");
		      }
		},{
		      iconCls:'icon-edit',
		      text:'修改',
		      disabled:noPermis("systemConfig","update"),
		      handler:function(){
		          editObj("${(cmodel.systemConfig)!''}","${webRoot}/systemConfig/view.do?id=","${webRoot}/systemConfig/update.do","","");
		      }
		}]
	});
}
var tdErr;
function validation(obj){
var err = "";
var errId = "";
var name = $("#name").val();
var details = $("#details").val();
var value = $("#value").val();
	if(isNull(name)){
		err = "${(cmodel.systemConfig_name)!''}不能为空！";
		errId = "name";
	} else 
	if(!checkLength(name,5,100)){
		err = "${(cmodel.systemConfig_name)!''}不能少于5个字且不能超过100个字！";
		errId = "name";
	} else
	if(!checkLength(details,5,1000)){
		err = "${(cmodel.systemConfig_details)!''}不能少于5个字且不能超过1000个字！";
		errId = "details";
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
	  ${(cmodel.systemConfig_name)!''}:<input type="text" name="search.LIKES_name"/>
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
                <td>${(cmodel.systemConfig_name)!''}</td><td><input type="text" id="name" name="name" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.systemConfig_details)!''}</td><td><input type="text" id="details" name="details" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.systemConfig_value)!''}</td><td><input type="text" id="value" name="value" value=""/></td>
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
                <td>${(cmodel.systemConfig_id)!''}</td><td id="view_id"></td>
            </tr>
            <tr>
                <td>${(cmodel.systemConfig_name)!''}</td><td id="view_name"></td>
            </tr>
            <tr>
                <td>${(cmodel.systemConfig_details)!''}</td><td id="view_details"></td>
            </tr>
            <tr>
                <td>${(cmodel.systemConfig_value)!''}</td><td id="view_value"></td>
            </tr>
        </tbody>
    </table>
    </form>
</div>
</body>
</html>
