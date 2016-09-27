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
	searchInit("${webRoot}/blockMobile/search.do");     
	$("#search_status").html("<option value='' selected>请选择</option>");
	$("#search_status").append(getSelectList(blockMobile_status_map,"")); 
	
	$("#search_callStrategy").html("<option value='' selected>请选择</option>");
	$("#search_callStrategy").append(getSelectList(blockMobile_callStrategy_map,""));   
	
	$("#search_msgStrategy").html("<option value='' selected>请选择</option>");
	$("#search_msgStrategy").append(getSelectList(blockMobile_msgStrategy_map,""));   
	
	$("#search_isDelete").html("<option value='' selected>请选择</option>");
	$("#search_isDelete").append(getSelectList(isDelete_map,""));     
	
})
function viewInitCombobox(obj,data){
	obj.find("#view_status").html(blockMobile_status_map[obj.find("#view_status").html()]);
	obj.find("#view_msgStrategy").html(blockMobile_msgStrategy_map[obj.find("#view_msgStrategy").html()]);
	obj.find("#view_callStrategy").html(blockMobile_callStrategy_map[obj.find("#view_callStrategy").html()]);
	obj.find("#view_isDelete").html(isDelete_map[obj.find("#view_isDelete").html()]);
}
function newInitCombobox(obj,data){
	obj.find("#status").html("<option value='' selected>请选择</option>");
	obj.find("#status").append(getSelectList(blockMobile_status_map,data.status));
	
	obj.find("#msgStrategy").html("<option value='' selected>请选择</option>");
	obj.find("#msgStrategy").append(getSelectList(blockMobile_msgStrategy_map,data.status));
	
	obj.find("#callStrategy").html("<option value='' selected>请选择</option>");
	obj.find("#callStrategy").append(getSelectList(blockMobile_callStrategy_map,data.status));
	
	obj.find("#isDelete").html("<option value='' selected>请选择</option>");
	obj.find("#isDelete").append(getSelectList(isDelete_map,data.status));
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
		//生成表
		columns:[[
		{field:'ck',checkbox:true,width:2},
		{field:'id',title:'${(cmodel.blockMobile_id)!''}',sortable:true,
				formatter:function(value,row,index){return row.id;} 
			},
			{field:'userId',title:'${(cmodel.blockMobile_userId)!''}',sortable:true,
				formatter:function(value,row,index){return row.userId;} 
			},
			{field:'fromNumber',title:'${(cmodel.blockMobile_fromNumber)!''}',sortable:true,
				formatter:function(value,row,index){return row.fromNumber;}
			},
			{field:'vmNumber',title:'${(cmodel.blockMobile_vmNumber)!''}',sortable:true,
				formatter:function(value,row,index){return row.vmNumber;} 
			},
			{field:'createTime',title:'${(cmodel.blockMobile_createTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.createTime;} 
			},
			{field:'updateTime',title:'${(cmodel.blockMobile_updateTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.updateTime;} 
			},
			{field:'isDelete',title:'${(cmodel.blockMobile_isDelete)!''}',sortable:true,
				formatter:function(value,row,index){return isDelete_map[row.isDelete];} 
			},
			{field:'status',title:'${(cmodel.blockMobile_status)!''}',sortable:true,
				formatter:function(value,row,index){return blockMobile_status_map[row.status];}
			},
			{field:'callStrategy',title:'${(cmodel.blockMobile_callStrategy)!''}',sortable:true,
				formatter:function(value,row,index){return blockMobile_callStrategy_map[row.callStrategy];}
			},
			{field:'msgStrategy',title:'${(cmodel.blockMobile_msgStrategy)!''}',sortable:true,
				formatter:function(value,row,index){return blockMobile_msgStrategy_map[row.msgStrategy];}
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
		      iconCls:'icon-info',
		      text:'详细',
		      disabled:noPermis("blockMobile","view"),
		      handler:function(){
		 	     viewObj("${(cmodel.blockMobile)!''}","${webRoot}/blockMobile/view.do?id=","","500");
		      }
		},{
		      iconCls:'icon-tack',
		      text:'添加全局',
		      disabled:noPermis("blockMobile","addglobal"),
		      handler:function(){
		          dealUserStatus("${(cmodel.blockMobile)!''}添加全局黑号","${webRoot}/blockMobile/addglobal.do?id=");
		      }
		},{
		      iconCls:'icon-delete',
		      text:'删除',
		      disabled:noPermis("blockMobile","delete"),
		      handler:function(){
		          removeObj("${webRoot}/blockMobile/delete.do");
		      }
		},{
		      iconCls:'icon-edit',
		      text:'修改',
		      disabled:noPermis("blockMobile","update"),
		      handler:function(){
		          editObj("${(cmodel.blockMobile)!''}","${webRoot}/blockMobile/view.do?id=","${webRoot}/blockMobile/update.do");
		      }
		}]
	});
}
//此方法获取页面上选中项的ID并传给后台
function dealUserStatus(name,url){
	var rows = $('#dg').datagrid('getChecked');
	var id = "";
	var size = 0;
	if(rows==undefined||rows==''){
		$.messager.alert('提示','请选择需要处理的记录！','info');
	    return;
	}
	$.each(rows,function(i,n){
		id += n.id + ",";
	});
	if(id == "")return;
	id = id.substring(0,id.length-1);
	url =url+id;
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
var tdErr;
function validation(obj){
	var err = "";
	var errId = "";
	var userId = $("#userId").val();
	var fromNumber = $("#fromNumber").val();
	var vmNumber = $("#vmNumber").val();
	var status = $("#status").val();
	var isDelete = $("#isDelete").val();
	var createTime = $("#createTime").val();
	var updateTime = $("#updateTime").val();
	var callStrategy = $("#callStrategy").val();
	var msgStrategy = $("#msgStrategy").val();
		 if(isNull(fromNumber)||isNaN(fromNumber) || !checkMobile(fromNumber)){
			err = "${(cmodel.blockMobile_fromNumber)!''}不能为空或非数字！";
			errId = "fromNumber";
		}else if(isNull(vmNumber)||isNaN(vmNumber) || !checkMobile(vmNumber)){
			err = "${(cmodel.blockMobile_vmNumber)!''}不能为空或非数字！";
			errId = "vmNumber";
		}else if(isNull(isDelete)){
			err = "${(cmodel.blockMobile_isDelete)!''}不能为空！";
			errId = "isDelete";
		}else if(isNull(status)){
			err = "${(cmodel.blockMobile_status)!''}不能为空！";
			errId = "status";
		}else 
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
      创建时间: <input class="easyui-datebox" name="search.GED_createTime" id="createTime" style="width:80px">
      更新时间: <input class="easyui-datebox" name="search.LTD_createTime" id="updateTime" style="width:80px">
	  ${(cmodel.blockMobile_id)!''}:<input type="text" name="search.EQI_id"/>
	  ${(cmodel.blockMobile_userId)!''}:<input type="text" name="search.LIKES_userId"/>
	  ${(cmodel.blockMobile_status)!''}:<select id="search_status" name="search.EQS_status"/>
	  ${(cmodel.blockMobile_fromNumber)!''}:<select id="search_fromNumber" name="search.EQS_fromNumber"/>
      <input type="button" onclick="search();" value="查询"/><input type="reset" onclick="searchReset();" value="清空"/>
      </div>
  	</form>         
</div>
<div id="editObj" style="display:none;">
    <form id="editForm" method="post">
    <table class="formTable" width="100%">
        <tbody>
			<input type="hidden" id="id" name="id" value=""/>
            <tr class="fromNumber">
                <td>${(cmodel.blockMobile_fromNumber)!''}</td><td><input type="text" id="fromNumber" name="fromNumber"   value="" /></td>
            </tr>
            <tr class="vmNumber">
                <td>${(cmodel.blockMobile_vmNumber)!''}</td><td><input type="text" id="vmNumber" name="vmNumber" value="" /></td>
            </tr>
             <tr class="isDelete">
                <td>${(cmodel.blockMobile_isDelete)!''}</td><td><select id="isDelete" name="isDelete"/></td>
            </tr>
             <tr class="callStrategy">
                <td>${(cmodel.blockMobile_callStrategy)!''}</td><td><select id="callStrategy" name="callStrategy"/></td>
            </tr>
             <tr class="msgStrategy">
                <td>${(cmodel.blockMobile_msgStrategy)!''}</td><td><select id="msgStrategy" name="msgStrategy"/></td>
            </tr>
            <tr class="status">
                <td>${(cmodel.blockMobile_status)!''}</td><td><select id="status" name="status"/></td>
            </tr>
        </tbody>
    </table>
    <table border="0" width="100%" id="formOperate">
    	<tr><td align="center"><input type="button" id="editSubmit" value="确定"/><input type="reset" onclick="searchReset();" value="清空"/></td></tr>
    </table>
    </form>
</div>
<div id="viewObj" style="display:none;">
    <form id="viewForm">
    <table class="formTable" width="100%">
        <tbody>
            <tr>
                <td>${(cmodel.blockMobile_id)!''}</td><td id="view_id"></td>
            </tr>
            <tr>
                <td>${(cmodel.blockMobile_userId)!''}</td><td id="view_userId"></td>
            </tr>
            <tr>
                <td>${(cmodel.blockMobile_fromNumber)!''}</td><td id="view_fromNumber"></td>
            </tr>
            <tr>
                <td>${(cmodel.blockMobile_vmNumber)!''}</td><td id="view_vmNumber"></td>
            </tr>
            <tr>
                <td>${(cmodel.blockMobile_isDelete)!''}</td><td id="view_isDelete"></td>
            </tr>
            <tr>
                <td>${(cmodel.blockMobile_status)!''}</td><td id="view_status"></td>
            </tr>
            <tr>
                <td>${(cmodel.blockMobile_createTime)!''}</td><td id="view_createTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.blockMobile_updateTime)!''}</td><td id="view_updateTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.blockMobile_callStrategy)!''}</td><td id="view_callStrategy"></td>
            </tr>
            <tr>
                <td>${(cmodel.blockMobile_msgStrategy)!''}</td><td id="view_msgStrategy"></td>
            </tr>
        </tbody>
    </table>
    </form>
</div>
</body>
</html>
