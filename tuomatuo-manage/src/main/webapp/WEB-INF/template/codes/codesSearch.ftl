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
	searchInit("${webRoot}/codes/search.do");
	$("#search_status").html("<option value='' selected>请选择</option>");
	$("#search_status").append(getSelectList(codes_status_map,''));
	$("#search_serviceId, #new_serviceId").html("<option value='' selected>请选择</option>");
	$("#search_serviceId, #new_serviceId").append(getSelectList(codes_serviceId_map,''));
	
})
function newInitCombobox(obj,data){
	
}
function viewInitCombobox(obj,data){
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
		queryParams:{'search.EQI_ctype':'0'}, //查询条件
		pagination:true, //显示分页
		rownumbers:true, //显示行号
		pageSize:20,
		toolbar:'#tb',
		pagePosition:'both',
		border:false,
		//checkOnSelect:false,
		columns:[[
			{field:'ck',checkbox:true,width:2},
			{field:'id',title:'${(cmodel.codes_id)!''}',sortable:true,
				formatter:function(value,row,index){return row.id;} 
			},
			{field:'userId',title:'${(cmodel.codes_codes)!''}',sortable:true,
				formatter:function(value,row,index){return row.codes;} 
			},
			{field:'fromNumber',title:'${(cmodel.codes_status)!''}',sortable:true,
				formatter:function(value,row,index){return codes_status_map[row.status];} 
			},
			{field:'toNumber',title:'${(cmodel.codes_serviceId)!''}',sortable:true,
				formatter:function(value,row,index){return codes_serviceId_map[row.serviceId];} 
			},
			{field:'content',title:'${(cmodel.codes_userId)!''}',sortable:true,width:200,
				formatter:function(value,row,index){return row.userId;} 
			},
			{field:'createTime',title:'${(cmodel.codes_useTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.useTime;} 
			},
			{field:'messageType',title:'${(cmodel.codes_createTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.createTime;} 
			},
			{field:'sendStatus',title:'${(cmodel.codes_endTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.endTime;} 
			},
			{field:'receiveStatus',title:'${(cmodel.codes_isDelete)!''}',sortable:true,
				formatter:function(value,row,index){return isDelete_map[row.isDelete];} 
			},
			{field:'errorMsg',title:'${(cmodel.codes_codesDes)!''}',sortable:true,
				formatter:function(value,row,index){return row.codesDes;} 
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
		      disabled:noPermis("codes","save"),
		      handler:function(){
		      	randomCodes();	//生成优惠码
		 	    newObj("${(cmodel.codes)!''}","${webRoot}/codes/save.do");
		      }
		}]
	});
}
var tdErr;
function validation(obj){
var err = "";
var errId = "";
var userId = $("#userId").val();
var fromNumber = $("#fromNumber").val();
var toNumber = $("#toNumber").val();
var content = $("#content").val();
var createTime = $("#createTime").val();
var updateTime = $("#updateTime").val();
var sendStatus = $("#sendStatus").val();
var receiveStatus = $("#receiveStatus").val();
var isDelete = $("#isDelete").val();
var errorMsg = $("#errorMsg").val();


	var codes = $("#new_codes").val();
	var serviceId = $("#new_serviceId").val();
	var endTime = $("#new_endTime").datebox("getValue");
	
	if(isNull(codes)){
		err = "${(cmodel.codes_codes)!''}不能为空！";
		errId = "codes";
	}else if(codes.length != 8){
		err = "${(cmodel.codes_codes)!''}为8位！";
		errId = "name";
	}else  if(isNull(serviceId)){
		err = "${(cmodel.codes_serviceId)!''}不能为空！";
		errId = "serviceId";
	}else if(isNull(endTime)){
		err = "${(cmodel.codes_endTime)!''}不能为空！";
		errId = "endTime";
	}else{
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

//从服务器获取一个唯一的优惠码
function randomCodes(){
	$.ajax({
        type: "post",
        dataType: "json",
        url: "${webRoot}/codes/randomCodes.do",
        data: {length:"8"},
        contentType: 'application/x-www-form-urlencoded; charset=utf-8', 
        success: function(data){
        	if(data!=undefined&&data.result==true){
        		$("#new_codes").val(data.rc);
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
	      ${(cmodel.codes_createTime)!''}: <input class="easyui-datebox" name="search.GED_createTime" style="width:100px" id="startTime">
	      ${(cmodel.codes_endTime)!''}: <input class="easyui-datebox" name="search.LTD_endTime" style="width:100px" id="endTime">
		  ${(cmodel.codes_codes)!''}:<input type="text" name="search.LIKES_codes"/>
		  ${(cmodel.codes_userId)!''}:<input type="text" name="search.LIKES_userId"/>
		  ${(cmodel.codes_status)!''}:<select id="search_status" name="search.EQI_status"></select>
		  ${(cmodel.codes_serviceId)!''}:<select id="search_serviceId" name="search.EQI_serviceId"></select>
	      <input type="button" onclick="search();" value="查询"/><input type="reset" onclick="searchReset();" value="清空"/>
        </div>
  	</form>         
</div>
<div id="editObj" style="display:none;">
    <form id="editForm" method="post">
    <!-- 分类0表示优惠码 -->
    <input type="hidden" name="ctype" value="0" />
    <table class="formTable" width="100%" style="padding:3px">
        <tbody>
            <tr>
                <td width="120px">${(cmodel.codes_codes)!''}</td><td><input type="text" id="new_codes" readOnly="true" name="codes" value=""/>   <input type="button" onclick="randomCodes()" value="生成优惠码" /></td>
            </tr>
            <tr>
                <td>${(cmodel.codes_serviceId)!''}</td><td><select id="new_serviceId" name="serviceId" value=""></select></td>
            </tr>
            <tr>
                <td>${(cmodel.codes_endTime)!''}</td><td><input class="easyui-datebox" name="endTime" id="new_endTime"  style="width:90px"/></td>
            </tr>
            <tr>
                <td width="120px">${(cmodel.codes_codesDes)!''}</td><td><input type="text" id="new_codesDes" name="codesDes" value=""/></td>
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
    <table class="formTable" width="100%" style="padding:3px">
        <tbody>
            <tr>
                <td width="120px">${(cmodel.codes_id)!''}</td><td id="view_id"></td>
            </tr>
            <tr>
                <td>${(cmodel.codes_codes)!''}</td><td id="view_codes"></td>
            </tr>
            <tr>
                <td>${(cmodel.codes_status)!''}</td><td id="view_status"></td>
            </tr>
            <tr>
                <td>${(cmodel.codes_serviceId)!''}</td><td id="view_serviceId"></td>
            </tr>
            <tr>
                <td>${(cmodel.codes_userId)!''}</td><td id="view_userId"></td>
            </tr>
            <tr>
                <td>${(cmodel.codes_useTime)!''}</td><td id="view_useTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.codes_createTime)!''}</td><td id="view_createTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.codes_endTime)!''}</td><td id="view_endTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.codes_isDelete)!''}</td><td id="view_isDelete"></td>
            </tr>
            <tr>
                <td>${(cmodel.codes_codesDes)!''}</td><td id="view_codesDes"></td>
            </tr>
        </tbody>
    </table>
    </form>
</div>
</body>
</html>
