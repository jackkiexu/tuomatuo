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
	searchInit("${webRoot}/messageRecordDwh/search.do");   
	$("#search_sendStatus").html("<option value='' selected>请选择</option>");
	$("#search_sendStatus").append(getSelectList(messageRecord_sendStatus_map,""));   
	$("#search_receiveStatus").html("<option value='' selected>请选择</option>");
	$("#search_receiveStatus").append(getSelectList(messageRecord_receiveStatus_map,""));      
	$("#search_messageType").html("<option value='' selected>请选择</option>");
	$("#search_messageType").append(getSelectList(messageRecord_messageType_map,""));      
	$("#search_smsChannel").html("<option value='' selected>请选择</option>");
	$("#search_smsChannel").append(getSelectList(numbers_sms_channel_map,""));    
	
	$("#search_spId").html("<option value='' selected>请选择</option>");
	$("#search_spId").append(getSelectList(splist, ""));  
})
function newInitCombobox(obj,data){
}
function viewInitCombobox(obj,data){
	obj.find("#view_sendStatus").html(messageRecord_sendStatus_map[obj.find("#view_sendStatus").html()]);
	obj.find("#view_receiveStatus").html(messageRecord_receiveStatus_map[obj.find("#view_receiveStatus").html()]);
	obj.find("#view_isDelete").html(isDelete_map[obj.find("#view_isDelete").html()]);
	obj.find("#view_messageType").html(messageRecord_messageType_map[obj.find("#view_messageType").html()]);
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
			{field:'id',title:'${(cmodel.messageRecord_id)!''}',sortable:true,
				formatter:function(value,row,index){return row.id;} 
			},
			{field:'userId',title:'${(cmodel.messageRecord_userId)!''}',sortable:true,
				formatter:function(value,row,index){return row.userId;} 
			},
			{field:'spId',title:'${(cmodel.callRecord_spId)!''}',sortable:true,
				formatter:function(value,row,index){return splist[row.spId];} 
			},
			{field:'fromNumber',title:'${(cmodel.messageRecord_fromNumber)!''}',sortable:true,
				formatter:function(value,row,index){return row.fromNumber;} 
			},
			{field:'toNumber',title:'${(cmodel.messageRecord_toNumber)!''}',sortable:true,
				formatter:function(value,row,index){return row.toNumber;} 
			},
			{field:'content',title:'${(cmodel.messageRecord_content)!''}',sortable:true,width:200,
				formatter:function(value,row,index){return row.content;} 
			},
			{field:'createTime',title:'${(cmodel.messageRecord_createTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.createTime;} 
			},
			{field:'messageType',title:'${(cmodel.messageRecord_messageType)!''}',sortable:true,
				formatter:function(value,row,index){return messageRecord_messageType_map[row.messageType];} 
			},
			{field:'sendStatus',title:'${(cmodel.messageRecord_sendStatus)!''}',sortable:true,
				formatter:function(value,row,index){return messageRecord_sendStatus_map[row.sendStatus];} 
			},
			{field:'receiveStatus',title:'${(cmodel.messageRecord_receiveStatus)!''}',sortable:true,
				formatter:function(value,row,index){return messageRecord_receiveStatus_map[row.receiveStatus];} 
			},
			{field:'errorMsg',title:'${(cmodel.messageRecord_errorMsg)!''}',sortable:true,
				formatter:function(value,row,index){return row.errorMsg;} 
			},
			{field:'smsChannel',title:'${(cmodel.messageRecord_smsChannel)!''}',sortable:true,
				formatter:function(value,row,index){return numbers_sms_channel_map[row.smsChannel];} 
			},
			{field:'isDelete',title:'${(cmodel.messageRecord_isDelete)!''}',sortable:true,
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
		      disabled:noPermis("messageRecord","view"),
		      handler:function(){
		 	     viewObj("${(cmodel.messageRecord)!''}","${webRoot}/messageRecordDwh/view.do?id=","","400");
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
	if(isNull(userId)){
		err = "${(cmodel.messageRecord_userId)!''}不能为空！";
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
      开始时间: <input class="easyui-datebox" name="search.GED_createTime" style="width:80px" id="startTime">
	  ${(cmodel.messageRecord_userId)!''}:<input type="text" name="search.LIKES_userId"/>
	  ${(cmodel.messageRecord_fromNumber)!''}:<input type="text" name="search.LIKES_fromNumber"/>
	  ${(cmodel.messageRecord_sendStatus)!''}:<select id="search_sendStatus" name="search.EQI_sendStatus"></select>
	  ${(cmodel.messageRecord_messageType)!''}:<select id="search_messageType" name="search.EQI_messageType"></select>
	  <br/>
	  结束时间: <input class="easyui-datebox" name="search.LTD_createTime" style="width:80px" id="endTime">
	  ${(cmodel.messageRecord_content)!''}:<input type="text" name="search.LIKES_content"/>
	  ${(cmodel.messageRecord_toNumber)!''}:<input type="text" name="search.LIKES_toNumber"/>
	  ${(cmodel.messageRecord_receiveStatus)!''}:<select id="search_receiveStatus" name="search.EQS_receiveStatus"></select>
	  ${(cmodel.messageRecord_smsChannel)!''}:<select id="search_smsChannel" name="search.EQS_smsChannel"></select>
	  ${(cmodel.callRecord_spId)!''}:<select id="search_spId" name="search.EQS_spId"></select>
      <input type="button" onclick="do_search();" value="查询"/><input type="reset" onclick="searchReset();" value="清空"/>
      </div>
  	</form>         
</div>
<div id="editObj" style="display:none;">
    <form id="editForm" method="post">
    <table class="formTable" width="100%" style="padding:3px">
        <tbody>
			<input type="hidden" id="id" name="id" value=""/>
            <tr>
                <td width="120px">${(cmodel.messageRecord_userId)!''}</td><td><input type="text" id="userId" name="userId" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.messageRecord_fromNumber)!''}</td><td><input type="text" id="fromNumber" name="fromNumber" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.messageRecord_toNumber)!''}</td><td><input type="text" id="toNumber" name="toNumber" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.messageRecord_content)!''}</td><td><input type="text" id="content" name="content" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.messageRecord_sendStatus)!''}</td><td><input type="text" id="sendStatus" name="sendStatus" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.messageRecord_receiveStatus)!''}</td><td><input type="text" id="receiveStatus" name="receiveStatus" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.messageRecord_errorMsg)!''}</td><td><input type="text" id="errorMsg" name="errorMsg" value=""/></td>
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
                <td width="120px">${(cmodel.messageRecord_id)!''}</td><td id="view_id"></td>
            </tr>
            <tr>
                <td>${(cmodel.messageRecord_userId)!''}</td><td id="view_userId"></td>
            </tr>
            <tr>
                <td>${(cmodel.messageRecord_fromNumber)!''}</td><td id="view_fromNumber"></td>
            </tr>
            <tr>
                <td>${(cmodel.messageRecord_toNumber)!''}</td><td id="view_toNumber"></td>
            </tr>
            <tr>
                <td>${(cmodel.messageRecord_content)!''}</td><td id="view_content"></td>
            </tr>
            <tr>
                <td>${(cmodel.messageRecord_createTime)!''}</td><td id="view_createTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.messageRecord_updateTime)!''}</td><td id="view_updateTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.messageRecord_messageType)!''}</td><td id="view_messageType"></td>
            </tr>
            <tr>
                <td>${(cmodel.messageRecord_sendStatus)!''}</td><td id="view_sendStatus"></td>
            </tr>
            <tr>
                <td>${(cmodel.messageRecord_receiveStatus)!''}</td><td id="view_receiveStatus"></td>
            </tr>
            <tr>
                <td>${(cmodel.messageRecord_isDelete)!''}</td><td id="view_isDelete"></td>
            </tr>
            <tr>
                <td>${(cmodel.messageRecord_errorMsg)!''}</td><td id="view_errorMsg"></td>
            </tr>
        </tbody>
    </table>
    </form>
</div>
</body>
</html>
