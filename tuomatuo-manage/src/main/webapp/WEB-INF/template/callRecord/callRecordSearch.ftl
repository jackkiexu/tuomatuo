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
function formatTime(value){
 var v1 = Math.floor((value)/60);
 var v2 = Math.round(value - v1*60);
 var v3 = '';
 if(v1!='0')
 	v3 = v1 + '分';
 if(v2!='0')
    v3 = v3 + v2 + '秒';
 if(v3!='')
 	v3 = '('+ v3 +')';
 return v3;  
}
var time2 = formatDate(new Date(),"yyyy-MM-dd");
$(function(){
	$('#startTime').datebox('setValue', time2);
	searchInit("${webRoot}/callRecord/search.do");         
	
	$("#search_status").html("<option value='' selected>请选择</option>");
	$("#search_status").append(getSelectList(call_record_map,""));   
	
	$("#search_spId").html("<option value='' selected>请选择</option>");
	$("#search_spId").append(getSelectList(splist, ""));
	
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
		queryParams:{"search.GED_startTime":time2}, //查询条件
		pagination:true, //显示分页
		rownumbers:true, //显示行号
		pageSize:20,
		toolbar:'#tb',
		pagePosition:'both',
		border:false,
		//checkOnSelect:false,
		columns:[[
			{field:'ck',checkbox:true,width:2},
			{field:'id',title:'${(cmodel.callRecord_id)!''}',sortable:true,
				formatter:function(value,row,index){return row.id;} 
			},
			{field:'userId',title:'${(cmodel.callRecord_userId)!''}',sortable:true,
				formatter:function(value,row,index){return row.userId;} 
			},
			{field:'spId',title:'${(cmodel.callRecord_spId)!''}',sortable:true,
				formatter:function(value,row,index){return splist[row.spId];} 
			},
			{field:'fromNumber',title:'${(cmodel.callRecord_fromNumber)!''}',sortable:true,
				formatter:function(value,row,index){return row.fromNumber;} 
			},
			{field:'vm',title:'${(cmodel.callRecord_vm)!''}',sortable:true,
				formatter:function(value,row,index){return row.vm;} 
			},
			{field:'toNumber',title:'${(cmodel.callRecord_toNumber)!''}',sortable:true,
				formatter:function(value,row,index){return row.toNumber;} 
			},
			{field:'status',title:'${(cmodel.callRecord_status)!''}',sortable:true,
				formatter:function(value,row,index){return call_record_map[row.status];} 
			},
			{field:'startTime',title:'${(cmodel.callRecord_startTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.startTime;} 
			},
			{field:'endTime',title:'${(cmodel.callRecord_endTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.endTime;} 
			},
			{field:'callMinutes',title:'${(cmodel.callRecord_callMinutes)!''}',sortable:true,
				formatter:function(value,row,index){return row.callMinutes+formatTime(row.callMinutes);} 
			},
			{field:'spSeqId',title:'${(cmodel.callRecord_spSeqId)!''}',sortable:true,
				formatter:function(value,row,index){return row.spSeqId;} 
			},
			{field:'errorMsg',title:'${(cmodel.callRecord_errorMsg)!''}',sortable:true,
				formatter:function(value,row,index){return row.errorMsg;} 
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
		      disabled:noPermis("callRecord","view"),
		      handler:function(){
		 	     viewObj("${(cmodel.callRecord)!''}","${webRoot}/callRecord/view.do?id=","","400");
		      }
		},{
		      iconCls:'icon-edit',
		      text:'添加黑名单',
		      disabled:noPermis("callRecord","addBlock"),
		      handler:function(){
		          dealUserStatus("${(cmodel.callRecord)!''}添加黑名单","${webRoot}/callRecord/addBlock.do?id=");
		      }
		},{
		      iconCls:'icon-tack',
		      text:'添加全局黑名单',
		      disabled:noPermis("callRecord","addGlobalBlack"),
		      handler:function(){
		          dealUserStatus("${(cmodel.callRecord)!''}添加黑名单","${webRoot}/callRecord/addGlobalBlack.do?id=");
		      }
		},{
		      iconCls:'icon-tack',
		      text:'添加号码黑名单',
		      disabled:noPermis("callRecord","addBlockKeyNumber"),
		      handler:function(){
		          dealUserStatus("${(cmodel.callRecord)!''}添加黑名单","${webRoot}/callRecord/addBlockKeyNumber.do?id=");
		      }
		},{
		      iconCls:'icon-add',
		      text:'添加',
		      disabled:noPermis("callRecord","save"),
		      handler:function(){
		 	     newObj("${(cmodel.callRecord)!''}","${webRoot}/callRecord/save.do","","400");
		      }
		},{
		      iconCls:'icon-remove',
		      text:'删除',
		      disabled:noPermis("callRecord","delete"),
		      handler:function(){
		          removeObj("${webRoot}/callRecord/delete.do");
		      }
		}]
	});
}
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
var toNumber = $("#toNumber").val();
var status = $("#status").val();
var startTime = $("#startTime").val();
var endTime = $("#endTime").val();
var isDelete = $("#isDelete").val();
var callMinutes = $("#callMinutes").val();
var errorMsg = $("#errorMsg").val();
	if(isNull(userId)){
		err = "${(cmodel.callRecord_userId)!''}不能为空！";
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
      开始时间: <input class="easyui-datebox" name="search.GED_startTime" id="startTime" style="width:80px">
      ${(cmodel.callRecord_fromNumber)!''}:<input type="text"  id="search_fromNumber" name="search.EQS_fromNumber" value="${(form.fromNumber)!''}"/>
	  ${(cmodel.callRecord_vm)!''}:<input type="text"  id="search_vm" name="search.EQS_vm" value="${(form.vm)!''}"/>
	  ${(cmodel.callRecord_toNumber)!''}:<input type="text" id="search_toNumber" name="search.EQS_toNumber" value="${(form.toNumber)!''}"/>
	  
      <br />
      结束时间: <input class="easyui-datebox" name="search.LTD_startTime" id="endTime" style="width:80px">
      ${(cmodel.callRecord_userId)!''}:<input type="text"  id="search_userId" name="search.EQS_userId" value="${(form.callRecord_userId)!''}"/>
	  ${(cmodel.callRecord_status)!''}:<select id="search_status" name="search.EQS_status"></select>
	  ${(cmodel.callRecord_spId)!''}:<select id="search_spId" name="search.EQS_spId"></select>
      <input type="button" onclick="search();" value="查询"/><input type="reset" value="清空" onclick="searchReset();"/>
      </div>
  	</form>         
</div>
<div id="editObj" style="display:none;">
    <form id="editForm" method="post">
    <table class="formTable" width="100%">
        <tbody>
			<input type="hidden" id="id" name="id" value=""/>
            <tr>
                <td>${(cmodel.callRecord_userId)!''}</td><td><input type="text" id="userId" name="userId" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.callRecord_fromNumber)!''}</td><td><input type="text" id="fromNumber" name="fromNumber" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.callRecord_toNumber)!''}</td><td><input type="text" id="toNumber" name="toNumber" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.callRecord_status)!''}</td><td><input type="text" id="status" name="status" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.callRecord_startTime)!''}</td><td><input type="text" id="startTime" name="startTime" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.callRecord_endTime)!''}</td><td><input type="text" id="endTime" name="endTime" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.callRecord_isDelete)!''}</td><td><input type="text" id="isDelete" name="isDelete" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.callRecord_callMinutes)!''}</td><td><input type="text" id="callMinutes" name="callMinutes" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.callRecord_errorMsg)!''}</td><td><input type="text" id="errorMsg" name="errorMsg" value=""/></td>
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
                <td>${(cmodel.callRecord_id)!''}</td><td id="view_id"></td>
            </tr>
            <tr>
                <td>${(cmodel.callRecord_userId)!''}</td><td id="view_userId"></td>
            </tr>
            <tr>
                <td>${(cmodel.callRecord_fromNumber)!''}</td><td id="view_fromNumber"></td>
            </tr>
            <tr>
                <td>${(cmodel.callRecord_toNumber)!''}</td><td id="view_toNumber"></td>
            </tr>
            <tr>
                <td>${(cmodel.callRecord_status)!''}</td><td id="view_status"></td>
            </tr>
            <tr>
                <td>${(cmodel.callRecord_startTime)!''}</td><td id="view_startTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.callRecord_endTime)!''}</td><td id="view_endTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.callRecord_isDelete)!''}</td><td id="view_isDelete"></td>
            </tr>
            <tr>
                <td>${(cmodel.callRecord_callMinutes)!''}</td><td id="view_callMinutes"></td>
            </tr>
            <tr>
                <td>${(cmodel.callRecord_errorMsg)!''}</td><td id="view_errorMsg"></td>
            </tr>
        </tbody>
    </table>
    </form>
</div>
</body>
</html>
