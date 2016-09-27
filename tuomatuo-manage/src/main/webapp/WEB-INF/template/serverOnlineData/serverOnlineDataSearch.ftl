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


//初始化
$(function(){
	searchInit("${webRoot}/serverOnlineData/search.do");     
	
})
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
		//生成表
		columns:[[
		{field:'ck',checkbox:true,width:2},
		{field:'id',title:'${(cmodel.serverOnlineData_id)!''}',sortable:true,
				formatter:function(value,row,index){return row.id;} 
			},
			{field:'serverIP',title:'${(cmodel.serverOnlineData_serverIP)!''}',sortable:true,
				formatter:function(value,row,index){return row.serverIP;} 
			},
			{field:'serverName',title:'${(cmodel.serverOnlineData_serverName)!''}',sortable:true,
				formatter:function(value,row,index){return row.serverName;} 
			},
			{field:'cpuOne',title:'${(cmodel.serverOnlineData_cpuOne)!''}',sortable:true,
				formatter:function(value,row,index){return row.cpuOne;} 
			},
			{field:'cpuTwo',title:'${(cmodel.serverOnlineData_cpuTwo)!''}',sortable:true,
				formatter:function(value,row,index){return row.cpuTwo;} 
			},
			{field:'cpuThree',title:'${(cmodel.serverOnlineData_cpuThree)!''}',sortable:true,
				formatter:function(value,row,index){return row.cpuThree;} 
			},
			{field:'serverMen',title:'${(cmodel.serverOnlineData_serverMen)!''}',sortable:true,
				formatter:function(value,row,index){return row.serverMen;}
			},
			{field:'serverIO',title:'${(cmodel.serverOnlineData_serverIO)!''}',sortable:true,
				formatter:function(value,row,index){return row.serverIO;} 
			},
			{field:'serverDisk',title:'${(cmodel.serverOnlineData_serverDisk)!''}',sortable:true,
				formatter:function(value,row,index){return row.serverDisk;} 
			},
			{field:'createTime',title:'${(cmodel.serverOnlineData_createTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.createTime;} 
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
		}]
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
      创建时间: <input class="easyui-datebox" name="search.GED_createTime" id="createTime" style="width:80px">
	  ${(cmodel.serverOnlineData_serverIP)!''} : <input type="text" name="search.EQI_serverIP"/>
      <input type="button" onclick="search();" value="查询"/><input type="reset" onclick="searchReset();" value="清空"/>
      </div>
  	</form>         
</div>
</body>
</html>
