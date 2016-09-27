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
	searchInit("${webRoot}/userServicesMonitor/search.do");
})
function newInitCombobox(obj,data){
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
			{field:'userId',title:'${(cmodel.userServicesRepeatVO_userId)!''}',sortable:true,
				formatter:function(value,row,index){return row.userId;} 
			},
			{field:'loginName',title:'${(cmodel.userServicesRepeatVO_loginName)!''}',sortable:true,
				formatter:function(value,row,index){return row.loginName;} 
			},
			{field:'repeatCount',title:'${(cmodel.userServicesRepeatVO_repeatCount)!''}',sortable:true,
				formatter:function(value,row,index){return row.repeatCount;} 
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
		      iconCls:'icon-lock',
		      text:'锁定帐号',
		      disabled:noPermis("userServiceMonitor","lock"),
		      handler:function(){
		 	     dealUserStatus("${(cmodel.userServicesMonitor)!''}锁定","${webRoot}/userServiceMonitor/lock.do?id=");
		      }
		},{
		      iconCls:'icon-unlock',
		      text:'解锁帐号',
		      disabled:noPermis("userServiceMonitor","unlock"),
		      handler:function(){
		          dealUserStatus("${(cmodel.userServicesMonitor)!''}解锁","${webRoot}/userServiceMonitor/unlock.do?id=");
		      }
		}]
	});
}
function dealUserStatus(name,url){
	var rows = $('#dg').datagrid('getChecked');
	var userId = "";
	var size = 0;
	if(rows==undefined||rows==''){
		$.messager.alert('提示','请选择需要处理的记录！','info');
	    return;
	}
	$.each(rows,function(i,n){
		if(i==0) 
	    	userId = n.userId;
		size=i;
	});
 if(size>0){
			$.messager.alert('提示','该服务不支持多选','info');;
	    return;
	}
	url = url+userId;
	$.ajax({
        type: "post",
        dataType: "json",
        url: url,
        success: function(data){
        	if(data!=undefined&&data.result){
        		$('#dg').datagrid('reload');
    			$.messager.show({
		        	title:name,
		            msg:name+'成功！',
		            timeout:5000,
		            showType:'slide'
		        });
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
	      	开始时间: <input class="easyui-datetimebox" name="startTime" style="width:80px" id="startTime">
		    ${(cmodel.userServicesRepeatVO_userId)!''}:<input type="text" name="userId"/><br/>
			  结束时间: <input class="easyui-datetimebox" name="endTime" style="width:80px" id="endTime">
		  	${(cmodel.userServicesRepeatVO_repeatCount)!''}:<input type="text" name="repeatCount"/>
		    ${(cmodel.userServicesRepeatVO_loginName)!''}:<input type="text" name="loginName"/>
	      	<input type="button" onclick="search();" value="查询"/><input type="reset" onclick="searchReset();" value="清空"/>
	    </div>
  	</form>    
</div>
</body>
</html>
