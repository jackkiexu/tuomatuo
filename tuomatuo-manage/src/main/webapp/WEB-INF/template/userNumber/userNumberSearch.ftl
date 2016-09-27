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
	searchInit("${webRoot}/userNumber/search.do");    
	$("#search_status").html("<option value=''  selected>请选择</option>")
	$("#search_status").append(getSelectList(userNumber_status_map,""));
	$("#search_currStatus").html("<option value=''  selected>请选择</option>")
	$("#search_currStatus").append(getSelectList(userNumber_currStatus_map,""));
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
			{field:'endTime',title:'${(cmodel.userNumber_endTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.endTime;} 
			},
			{field:'overDue',title:'${(cmodel.userNumber_overDue)!''}',sortable:true,
				formatter:function(value,row,index){return row.overDue;} 
			},
			{field:'currStatus',title:'${(cmodel.userNumber_currStatus)!''}',sortable:true,
				formatter:function(value,row,index){return userNumber_currStatus_map[row.currStatus];} 
			},
			{field:'bootTime',title:'${(cmodel.userNumber_bootTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.bootTime;} 
			},
			{field:'shutTime',title:'${(cmodel.userNumber_shutTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.shutTime;} 
			},
			{field:'isDelete',title:'${(cmodel.userNumber_isDelete)!''}',sortable:true,
				formatter:function(value,row,index){return isDelete_map[row.isDelete];} 
			},
			{field:'isRedirect',title:'是否设置转接',sortable:true,
				formatter:function(value,row,index){return userNumber_isRedirect_map[row.isRedirect];} 
			},
			{field:'sendDelayMsgFlag',title:'发送延期续费短信通知标记',sortable:true,
				formatter:function(value,row,index){return user_sendDelayMsgFlag_map[row.sendDelayMsgFlag];} 
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
		      disabled:noPermis("userNumber","view"),
		      handler:function(){
		 	     viewObj("${(cmodel.userNumber)!''}","${webRoot}/userNumber/view.do?id=","","360");
		      }
		},{
		      iconCls:'icon-add',
		      text:'添加',
		      disabled:noPermis("userNumber","save"),
		      handler:function(){
		 	     newObj("${(cmodel.userNumber)!''}","${webRoot}/userNumber/save.do");
		      }
		},{
		      iconCls:'icon-edit',
		      text:'修改',
		      disabled:noPermis("userNumber","update"),
		      handler:function(){
		          editObj("${(cmodel.userNumber)!''}","${webRoot}/userNumber/view.do?id=","${webRoot}/userNumber/update.do");
		      }
		},{
		      iconCls:'icon-remove',
		      text:'删除',
		      disabled:noPermis("userNumber","delete"),
		      handler:function(){
		          removeObj("${webRoot}/userNumber/delete.do");
		      }
		},{
		      iconCls:'icon-remove',
		      text:'管理员解绑',
		      disabled:noPermis("userNumber","unbind"),
		      handler:function(){
		          unbindObj("${webRoot}/userNumber/unbind.do?id=");
		      }
		},{
		      iconCls:'icon-reload',
		      text:'开关or关机',
		      disabled:noPermis("userNumber","switch"),
		      handler:function(){
		          switchObj("${webRoot}/userNumber/switch.do");
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
function unbindObj(url){
	if(window.confirm('此操作确保是该用户需要释放号码！！！')){ 
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
			$.messager.alert('提示','请选择一条记录进行编辑操作！','info');
	   	 return;
		}
		url = url+id;

		$.ajax({
   	     	type: "post",
   	     	dataType: "json",
    	    url: url,
    	    contentType: 'application/x-www-form-urlencoded; charset=utf-8', 
    	    success: function(data){
    	      	if(data!=undefined&&data.success==true){
    	      		$('#dg').datagrid('reload');
   	        		$.messager.show({
    	       			title:name,
   	        			msg:'解绑成功！',
   	        			timeout:5000,
   	        			showType:'slide'
   	        		});
   	        	}else{
   	        		returnError(data);
    	       	}
   	    	}
		});
	}else{ 
		//alert("取消"); 
		return; 
	} 
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
   ${(cmodel.userNumber_mobile)!''}:<input type="text" name="search.LIKES_mobile"  size="8" />
   ${(cmodel.userNumber_redirectMobile)!''}:<input type="text" name="search.LIKES_redirectMobile"  size="8" />
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
                <td>${(cmodel.userNumber_userId)!''}</td><td><input type="text" id="userId" name="userId" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.userNumber_mobile)!''}</td><td><input type="text" id="mobile" name="mobile" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.userNumber_redirectMobile)!''}</td><td><input type="text" id="redirectMobile" name="redirectMobile" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.userNumber_status)!''}</td><td><input type="text" id="status" name="status" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.userNumber_createTime)!''}</td><td><input type="text" id="createTime" name="createTime" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.userNumber_bootTime)!''}</td><td><input type="text" id="bootTime" name="bootTime" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.userNumber_shutTime)!''}</td><td><input type="text" id="shutTime" name="shutTime" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.userNumber_isDelete)!''}</td><td><input type="text" id="isDelete" name="isDelete" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.userNumber_msgStartTime)!''}</td><td><input type="text" id="msgStartTime" name="msgStartTime" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.userNumber_msgEndTime)!''}</td><td><input type="text" id="msgEndTime" name="msgEndTime" value=""/></td>
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
                <td>${(cmodel.userNumber_id)!''}</td><td id="view_id"></td>
            </tr>
            <tr>
                <td>${(cmodel.userNumber_userId)!''}</td><td id="view_userId"></td>
            </tr>
            <tr>
                <td>${(cmodel.userNumber_mobile)!''}</td><td id="view_mobile"></td>
            </tr>
            <tr>
                <td>${(cmodel.userNumber_redirectMobile)!''}</td><td id="view_redirectMobile"></td>
            </tr>
            <tr>
                <td>${(cmodel.userNumber_status)!''}</td><td id="view_status"></td>
            </tr>
            <tr>
                <td>${(cmodel.userNumber_createTime)!''}</td><td id="view_createTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.userNumber_endTime)!''}</td><td id="view_endTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.userNumber_currStatus)!''}</td><td id="view_currStatus"></td>
            </tr>
            <tr>
                <td>${(cmodel.userNumber_bootTime)!''}</td><td id="view_bootTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.userNumber_shutTime)!''}</td><td id="view_shutTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.userNumber_isDelete)!''}</td><td id="view_isDelete"></td>
            </tr>
            <tr>
                <td>${(cmodel.userNumber_msgStartTime)!''}</td><td id="view_msgStartTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.userNumber_msgEndTime)!''}</td><td id="view_msgEndTime"></td>
            </tr>
        </tbody>
    </table>
    </form>
</div>
</body>
</html>
