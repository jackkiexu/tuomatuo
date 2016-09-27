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
var splist = new Array();
<#if spList?exists>
	<#list spList as tele>
		splist["${tele.id}"]="${tele.teleName}";
	</#list>
</#if>
function formatTime(value){
 var v1 = Math.floor((value)/(24*60));
 var v2 = Math.floor(Math.round(value - v1*24*60)/60);
 var v3 = '';
 if(v1!='0')
 	v3 = v1 + '天';
 if(v2!='0')
    v3 = v3 + v2 + '小时';
 if(v3!='')
 	v3 = '('+ v3 +')';
 return v3;  
}
//初始化
var time2 = formatDate(new Date(),"yyyy-MM-dd");
$(function(){
	$('#startTime').datebox('setValue', time2);
	searchInit("${webRoot}/bsBindRelation/search.do");   
	
	
	$("#search_spId").html("<option value='' selected>请选择</option>");
	$("#search_spId").append(getSelectList(splist, ""));
	$("#search_status").html("<option value='' selected>请选择</option>");
	$("#search_status").append(getSelectList(bsBindRelation_status_map, ""));
	$("#search_type").html("<option value='' selected>请选择</option>");
	$("#search_type").append(getSelectList(bsBindRelation_type_map, ""));
	
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
		queryParams:{"search.GED_createTime":time2}, //查询条件
		pagination:true, //显示分页
		rownumbers:true, //显示行号
		pageSize:20,
		toolbar:'#tb',
		pagePosition:'both',
		border:false,
		//checkOnSelect:false,
		columns:[[
			{field:'ck',checkbox:true,width:2},
			{field:'spId',title:'${(cmodel.bsBindRelation_spId)!''}',sortable:true,
				formatter:function(value,row,index){return splist[row.spId];} 
			},
			{field:'vm',title:'${(cmodel.bsBindRelation_vm)!''}',sortable:true, 
				formatter:function(value,row,index){return row.vm;} 
			},
			{field:'fm',title:'${(cmodel.bsBindRelation_fm)!''}',sortable:true,
				formatter:function(value,row,index){return row.fm;} 
			},
			{field:'tm',title:'${(cmodel.bsBindRelation_tm)!''}',sortable:true,
				formatter:function(value,row,index){return row.tm;} 
			},
			{field:'status',title:'${(cmodel.bsBindRelation_status)!''}',sortable:true,
				formatter:function(value,row,index){return bsBindRelation_status_map[row.status];} 
			},
			{field:'onOff',title:'${(cmodel.bsBindRelation_onOff)!''}',sortable:true,
				formatter:function(value,row,index){return row.onOff;} 
			},
			{field:'createTime',title:'${(cmodel.bsBindRelation_createTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.createTime;} 
			},
			{field:'updateTime',title:'${(cmodel.bsBindRelation_updateTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.updateTime;} 
			},
			{field:'bindTime',title:'${(cmodel.bsBindRelation_bindTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.bindTime+formatTime(row.bindTime);} 
			},
			{field:'type',title:'${(cmodel.bsBindRelation_type)!'类型'}',sortable:true,
				formatter:function(value,row,index){return bsBindRelation_type_map[row.type];} 
			},
			{field:'spSeqId',title:'${(cmodel.bsBindRelation_spSeqId)!''}',sortable:true,
				formatter:function(value,row,index){return row.spSeqId;} 
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
		      iconCls:'icon-search',
		      text:'详细',
		      disabled:noPermis("bsBindRelation","view"),
		      handler:function(){
		 	     viewObj("${(cmodel.bsBindRelation)!''}","${webRoot}/bsBindRelation/view.do?id=","","");
		      }
		},{
		      iconCls:'icon-add',
		      text:'添加',
		      disabled:noPermis("bsBindRelation","save"),
		      handler:function(){
		 	     newObj("${(cmodel.bsBindRelation)!''}","${webRoot}/bsBindRelation/save.do","","");
		      }
		},{
		      iconCls:'icon-edit',
		      text:'修改',
		      disabled:noPermis("bsBindRelation","update"),
		      handler:function(){
		          editObj("${(cmodel.bsBindRelation)!''}","${webRoot}/bsBindRelation/view.do?id=","${webRoot}/bsBindRelation/update.do","","");
		      }
		},{
		      iconCls:'icon-delete',
		      text:'删除',
		      disabled:noPermis("bsBindRelation","delete"),
		      handler:function(){
		          removeObj("${webRoot}/bsBindRelation/delete.do");
		      }
		}]
	});
}
var tdErr;
function validation(obj){
var err = "";
var errId = "";
var spId = $("#spId").val();
var vm = $("#vm").val();
var fm = $("#fm").val();
var tm = $("#tm").val();
var status = $("#status").val();
var onOff = $("#onOff").val();
var isDelete = $("#isDelete").val();
var createTime = $("#createTime").val();
var updateTime = $("#updateTime").val();
var bindTime = $("#bindTime").val();
	if(isNull(spId)){
		err = "${(cmodel.bsBindRelation_spId)!''}不能为空！";
		errId = "spId";
	} else 
	if(!checkLength(spId,5,100)){
		err = "${(cmodel.bsBindRelation_spId)!''}不能少于5个字且不能超过100个字！";
		errId = "spId";
	} else
	if(isNull(vm)){
		err = "${(cmodel.bsBindRelation_vm)!''}不能为空！";
		errId = "vm";
	} else 
	if(!checkLength(vm,5,100)){
		err = "${(cmodel.bsBindRelation_vm)!''}不能少于5个字且不能超过100个字！";
		errId = "vm";
	} else
	if(isNull(fm)){
		err = "${(cmodel.bsBindRelation_fm)!''}不能为空！";
		errId = "fm";
	} else 
	if(!checkLength(fm,5,100)){
		err = "${(cmodel.bsBindRelation_fm)!''}不能少于5个字且不能超过100个字！";
		errId = "fm";
	} else
	if(isNull(tm)){
		err = "${(cmodel.bsBindRelation_tm)!''}不能为空！";
		errId = "tm";
	} else 
	if(!checkLength(tm,5,100)){
		err = "${(cmodel.bsBindRelation_tm)!''}不能少于5个字且不能超过100个字！";
		errId = "tm";
	} else
	if(isNull(status)){
		err = "${(cmodel.bsBindRelation_status)!''}不能为空！";
		errId = "status";
	} else 
	if(!checkLength(status,5,100)){
		err = "${(cmodel.bsBindRelation_status)!''}不能少于5个字且不能超过100个字！";
		errId = "status";
	} else
	if(isNull(onOff)){
		err = "${(cmodel.bsBindRelation_onOff)!''}不能为空！";
		errId = "onOff";
	} else 
	if(!checkLength(onOff,5,100)){
		err = "${(cmodel.bsBindRelation_onOff)!''}不能少于5个字且不能超过100个字！";
		errId = "onOff";
	} else
	if(isNull(isDelete)){
		err = "${(cmodel.bsBindRelation_isDelete)!''}不能为空！";
		errId = "isDelete";
	} else 
	if(!checkLength(isDelete,5,100)){
		err = "${(cmodel.bsBindRelation_isDelete)!''}不能少于5个字且不能超过100个字！";
		errId = "isDelete";
	} else
	if(isNull(createTime)){
		err = "${(cmodel.bsBindRelation_createTime)!''}不能为空！";
		errId = "createTime";
	} else 
	if(!checkLength(createTime,5,100)){
		err = "${(cmodel.bsBindRelation_createTime)!''}不能少于5个字且不能超过100个字！";
		errId = "createTime";
	} else
	if(isNull(updateTime)){
		err = "${(cmodel.bsBindRelation_updateTime)!''}不能为空！";
		errId = "updateTime";
	} else 
	if(!checkLength(updateTime,5,100)){
		err = "${(cmodel.bsBindRelation_updateTime)!''}不能少于5个字且不能超过100个字！";
		errId = "updateTime";
	} else
	if(isNull(bindTime)){
		err = "${(cmodel.bsBindRelation_bindTime)!''}不能为空！";
		errId = "bindTime";
	} else 
	if(!checkLength(bindTime,5,100)){
		err = "${(cmodel.bsBindRelation_bindTime)!''}不能少于5个字且不能超过100个字！";
		errId = "bindTime";
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
      ${(cmodel.bsBindRelation_spId)!''}:<select id="search_spId" name="search.EQS_spId"></select>
      ${(cmodel.bsBindRelation_status)!''}:<select id="search_status" name="search.EQS_status"></select>
      ${(cmodel.bsBindRelation_type)!'类型'}:<select id="search_type" name="search.EQS_type"></select>
      <br />
      结束时间: <input class="easyui-datebox" name="search.LTD_createTime" id="endTime" style="width:80px">
	  ${(cmodel.bsBindRelation_vm)!''}:<input type="text" name="search.LIKES_vm"/>
	  ${(cmodel.bsBindRelation_fm)!''}:<input type="text" name="search.LIKES_fm"/>
	  ${(cmodel.bsBindRelation_tm)!''}:<input type="text" name="search.LIKES_tm"/>

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
                <td>${(cmodel.bsBindRelation_spId)!''}</td><td><input type="text" id="spId" name="spId" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.bsBindRelation_vm)!''}</td><td><input type="text" id="vm" name="vm" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.bsBindRelation_fm)!''}</td><td><input type="text" id="fm" name="fm" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.bsBindRelation_tm)!''}</td><td><input type="text" id="tm" name="tm" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.bsBindRelation_status)!''}</td><td><input type="text" id="status" name="status" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.bsBindRelation_onOff)!''}</td><td><input type="text" id="onOff" name="onOff" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.bsBindRelation_isDelete)!''}</td><td><input type="text" id="isDelete" name="isDelete" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.bsBindRelation_createTime)!''}</td><td><input type="text" id="createTime" name="createTime" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.bsBindRelation_updateTime)!''}</td><td><input type="text" id="updateTime" name="updateTime" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.bsBindRelation_bindTime)!''}</td><td><input type="text" id="bindTime" name="bindTime" value=""/></td>
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
                <td>${(cmodel.bsBindRelation_id)!''}</td><td id="view_id"></td>
            </tr>
            <tr>
                <td>${(cmodel.bsBindRelation_spId)!''}</td><td id="view_spId"></td>
            </tr>
            <tr>
                <td>${(cmodel.bsBindRelation_vm)!''}</td><td id="view_vm"></td>
            </tr>
            <tr>
                <td>${(cmodel.bsBindRelation_fm)!''}</td><td id="view_fm"></td>
            </tr>
            <tr>
                <td>${(cmodel.bsBindRelation_tm)!''}</td><td id="view_tm"></td>
            </tr>
            <tr>
                <td>${(cmodel.bsBindRelation_status)!''}</td><td id="view_status"></td>
            </tr>
            <tr>
                <td>${(cmodel.bsBindRelation_onOff)!''}</td><td id="view_onOff"></td>
            </tr>
            <tr>
                <td>${(cmodel.bsBindRelation_isDelete)!''}</td><td id="view_isDelete"></td>
            </tr>
            <tr>
                <td>${(cmodel.bsBindRelation_createTime)!''}</td><td id="view_createTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.bsBindRelation_updateTime)!''}</td><td id="view_updateTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.bsBindRelation_bindTime)!''}</td><td id="view_bindTime"></td>
            </tr>
        </tbody>
    </table>
    </form>
</div>
</body>
</html>
