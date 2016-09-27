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
var now = new Date();
var nowDayOfWeek = now.getDay();
var nowDay = now.getDate();
var nowMonth = now.getMonth();
var nowYear = now.getFullYear();
var lastMonthDate = new Date();
lastMonthDate.setDate(1);
lastMonthDate.setMonth(lastMonthDate.getMonth()-2);  
var lastYear = lastMonthDate.getFullYear();  
var lastMonth = lastMonthDate.getMonth();

var time2 = formatDate(new Date(lastYear,lastMonth,1),"yyyy-MM-dd");
$(function(){
    $('#search_startTime').val(time2);
	$('#startTime').datebox('setValue', time2);	
	searchInit("${webRoot}/userLogDwh/search.do");          
})
function viewInitCombobox(obj,data){
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
		queryParams:{"search.GED_createTime":time2,"search_startTime":time2}, //查询条件
		pagination:true, //显示分页
		rownumbers:true, //显示行号
		pageSize:20,
		toolbar:'#tb',
		pagePosition:'both',
		border:false,
		//checkOnSelect:false,
		columns:[[
			{field:'ck',checkbox:true,width:2},
			{field:'id',title:'${(cmodel.userLog_id)!''}',sortable:true,
				formatter:function(value,row,index){return row.id;} 
			},
			{field:'userId',title:'${(cmodel.userLog_userId)!''}',sortable:true,
				formatter:function(value,row,index){return row.userId;} 
			},
			{field:'userName',title:'${(cmodel.userLog_userName)!''}',sortable:true,
				formatter:function(value,row,index){return row.userName;} 
			},
			{field:'createTime',title:'${(cmodel.userLog_createTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.createTime;} 
			},
			{field:'active',title:'${(cmodel.userLog_active)!''}',sortable:true,
				formatter:function(value,row,index){return row.active;} 
			},
			{field:'activeDes',title:'${(cmodel.userLog_activeDes)!''}',sortable:true,
				formatter:function(value,row,index){return row.activeDes;}
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
		      disabled:noPermis("userLog","view"),
		      handler:function(){
		 	     viewObj("${(cmodel.userLog)!''}","${webRoot}/userLogDwh/view.do?id=");
		      }
		}]
	});
}
var tdErr;
function validation(obj){
var err = "";
var errId = "";
var userId = $("#userId").val();
var userName = $("#userName").val();
var createTime = $("#createTime").val();
var active = $("#active").val();
var isDelete = $("#isDelete").val();
var deleteDes = $("#deleteDes").val();
	if(isNull(userId)){
		err = "${(cmodel.userLog_userId)!''}不能为空！";
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
   	  <input type="hidden" name="search_startTime" id="search_startTime"/>
      开始时间: <input class="easyui-datebox" name="search.GED_createTime" id="startTime" style="width:80px">
      结束时间: <input class="easyui-datebox" name="search.LTD_createTime" id="endTime" style="width:80px">
	  ${(cmodel.userLog_userName)!''}:<input type="text" name="search.LIKES_userName" value="${(form.userName)!''}"/>
	  ${(cmodel.userLog_active)!''}:<input type="text" name="search.LIKES_active" value="${(form.active)!''}"/>
      <input type="button" onclick="do_search();" value="查询"/><input type="reset" onclick="searchReset();" value="清空"/>
      </div>
  	</form>         
</div>
<div id="editObj" style="display:none;">
    <form id="editForm" method="post">
    <table class="formTable" width="100%">
        <tbody>
			<input type="hidden" id="id" name="id" value=""/>
            <tr>
                <td>${(cmodel.userLog_userId)!''}</td><td><input type="text" id="userId" name="userId" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.userLog_userName)!''}</td><td><input type="text" id="userName" name="userName" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.userLog_createTime)!''}</td><td><input type="text" id="createTime" name="createTime" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.userLog_active)!''}</td><td><input type="text" id="active" name="active" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.userLog_isDelete)!''}</td><td><input type="text" id="isDelete" name="isDelete" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.userLog_deleteDes)!''}</td><td><input type="text" id="deleteDes" name="deleteDes" value=""/></td>
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
                <td>${(cmodel.userLog_id)!''}</td><td id="view_id"></td>
            </tr>
            <tr>
                <td>${(cmodel.userLog_userId)!''}</td><td id="view_userId"></td>
            </tr>
            <tr>
                <td>${(cmodel.userLog_userName)!''}</td><td id="view_userName"></td>
            </tr>
            <tr>
                <td>${(cmodel.userLog_createTime)!''}</td><td id="view_createTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.userLog_active)!''}</td><td id="view_active"></td>
            </tr>
            <tr>
                <td>${(cmodel.userLog_isDelete)!''}</td><td id="view_isDelete"></td>
            </tr>
            <tr>
                <td>${(cmodel.userLog_deleteDes)!''}</td><td id="view_deleteDes"></td>
            </tr>
            <tr>
                <td>${(cmodel.userLog_activeDes)!''}</td><td id="view_activeDes"></td>
            </tr>
        </tbody>
    </table>
    </form>
</div>
</body>
</html>
