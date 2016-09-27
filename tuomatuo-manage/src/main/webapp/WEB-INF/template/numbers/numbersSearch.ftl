<!DOCTYPE html>
<html>
<head>
<title>search</title>
<meta charset="UTF-8">
<link href="${webRoot}/css/themes/default/easyui.css" rel="stylesheet"
	<link rel="stylesheet" type="text/css" href="${webRoot}/css/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="${webRoot}/css/themes/icon.css">
	<script type="text/javascript" src="${webRoot}/js/jquery.min.js"></script>
	<script type="text/javascript" src="${webRoot}/js/jquery.form.js"></script>
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
$(function(){
	searchInit("${webRoot}/numbers/search.do");  
	$("#search_status").html("<option value='' selected>请选择</option>");
	$("#search_status").append(getSelectList(numbers_status_map,""));         

	$("#search_flag").html("<option value='' selected>请选择</option>");
	$("#search_flag").append(getSelectList(numbers_flag_map,""));   
	
	$("#search_type").html("<option value='' selected>请选择</option>");
	$("#search_type").append(getSelectList(numbers_type_map,""));
	
	$("#search_callChannel").html("<option value='' selected>请选择</option>");
	$("#search_callChannel").append(getSelectList(numbers_call_channel_map, ""));
	
	$("#search_smsChannel").html("<option value='' selected>请选择</option>");
	$("#search_smsChannel").append(getSelectList(numbers_sms_channel_map, ""));
	
	$("#search_sort").html("<option value='' selected>请选择</option>");
	$("#search_sort").append(getSelectList(mobile_pool_map, ""));
	
	$("#search_spId").html("<option value='' selected>请选择</option>");
	$("#search_spId").append(getSelectList(splist, ""));
	
	$("#search_inCache").html("<option value='' selected>请选择</option>");
	$("#search_inCache").append(getSelectList(numbers_in_cache_map, ""));
	
	if(noPermis('numbers','updates')){
		$("#updates").attr({"disabled":"disabled"});
	}
})
function viewInitCombobox(obj,data){
	obj.find("#view_status").html(numbers_status_map[obj.find("#view_status").html()]);
	obj.find("#view_operator").html(numbers_operator_map[obj.find("#view_operator").html()]);
	obj.find("#view_area").html(numbers_area_map[obj.find("#view_area").html()]);
	obj.find("#view_effect").html(numbers_effect_map[obj.find("#view_effect").html()]);
	obj.find("#view_type").html(numbers_status_map[obj.find("#view_type").html()]);
	obj.find("#view_flag").html(numbers_flag_map[obj.find("#view_flag").html()]);
}
function newInitCombobox(obj,data){
	obj.find("#status").html("<option value='' selected>请选择</option>");
	obj.find("#status").append(getSelectList(numbers_status_map,"1"));
	obj.find("#operator").html("<option value='' selected>请选择</option>");
	obj.find("#operator").append(getSelectList(numbers_operator_map,"1"));
	obj.find("#area").html("<option value='' selected>请选择</option>");
	obj.find("#area").append(getSelectList(numbers_area_map,"1"));
	obj.find("#effect").html("<option value='' selected>请选择</option>");
	obj.find("#effect").append(getSelectList(numbers_effect_map,"1"));
	obj.find("#type").html("<option value='' selected>请选择</option>");
	obj.find("#type").append(getSelectList(numbers_type_map,"1"));
	obj.find("#flag").html("<option value='' selected>请选择</option>");
	obj.find("#flag").append(getSelectList(numbers_flag_map,"3"));
	obj.find("#callChannel").html("<option value='' selected>请选择</option>");
	obj.find("#callChannel").append(getSelectList(numbers_call_channel_map, "2"));
	obj.find("#smsChannel").html("<opstion value='' selected>请选择</option>");
	obj.find("#smsChannel").append(getSelectList(numbers_sms_channel_map, "0"));
	obj.find("#sort").html("<option value='0' selected>请选择</option>");
	obj.find("#sort").append(getSelectList(mobile_pool_map, "0"));
	obj.find("#spId").html("<option value='0' selected>请选择</option>");
	obj.find("#spId").append(getSelectList(splist, "2"));
}
function editInitCombobox(obj,data){
	obj.find("#status").html("<option value='' selected>请选择</option>");
	obj.find("#status").append(getSelectList(numbers_status_map,data.status));
	obj.find("#operator").html("<option value='' selected>请选择</option>");
	obj.find("#operator").append(getSelectList(numbers_operator_map,data.operator));
	obj.find("#area").html("<option value='' selected>请选择</option>");
	obj.find("#area").append(getSelectList(numbers_area_map,data.area));
	obj.find("#effect").html("<option value='' selected>请选择</option>");
	obj.find("#effect").append(getSelectList(numbers_effect_map,data.effect));
	obj.find("#type").html("<option value='' selected>请选择</option>");
	obj.find("#type").append(getSelectList(numbers_type_map,data.type));
	obj.find("#flag").html("<option value='' selected>请选择</option>");
	obj.find("#flag").append(getSelectList(numbers_flag_map,data.flag));
	obj.find("#inCache").html("<option value='' selected>请选择</option>");
	obj.find("#inCache").append(getSelectList(numbers_in_cache_map,data.inCache));
	
	//ims渠道勿修改
	if(data.callChannel=='2'){
		obj.find("#callChannel").html("");
		obj.find("#callChannel").append("<option value='"+data.callChannel+"' selected>"+numbers_call_channel_map[data.callChannel]+"</option>");
	}else{
		obj.find("#callChannel").html("<option value='' selected>请选择</option>");
		obj.find("#callChannel").append(getSelectList(numbers_call_channel_map, data.callChannel));
	}
	obj.find("#smsChannel").html("<option value='' selected>请选择</option>");
	obj.find("#smsChannel").append(getSelectList(numbers_sms_channel_map, data.smsChannel));
	obj.find("#sort").html("<option value='0' selected>请选择</option>");
	obj.find("#sort").append(getSelectList(mobile_pool_map, data.sort));
	//滴滴和关系密号不能修改
	if(data.spId=='3'||data.spId=='5'){
		obj.find("#spId").html("");
		obj.find("#spId").append("<option value='"+data.spId+"' selected>"+splist[data.spId]+"</option>");
	}else{
		if(data.spId==''){
			obj.find("#spId").html("<option value='0' selected>请选择</option>");
			obj.find("#spId").append(getSelectList(splist, '6'));
		}else{
			obj.find("#spId").html("<option value='0' selected>请选择</option>");
			obj.find("#spId").append(getSelectList(splist, data.spId));
		}
	}
		
	
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
			{field:'id',title:'${(cmodel.numbers_id)!''}',sortable:true,
				formatter:function(value,row,index){return row.id;} 
			},
			{field:'mobile',title:'${(cmodel.numbers_mobile)!''}',sortable:true,width:fixWidth(0.2),
				formatter:function(value,row,index){return row.mobile;} 
			},
			{field:'status',title:'${(cmodel.numbers_status)!''}',sortable:true,
				formatter:function(value,row,index){return numbers_status_map[row.status];} 
			},
			{field:'count',title:'被申请次数',sortable:true,
				formatter:function(value,row,index){return row.count;} 
			},
			{field:'effect',title:'${(cmodel.numbers_effect)!''}',sortable:true,
				formatter:function(value,row,index){return numbers_effect_map[row.effect];} 
			},
			{field:'area',title:'${(cmodel.numbers_area)!''}',sortable:true,
				formatter:function(value,row,index){return numbers_area_map[row.area];} 
			},
			{field:'weight',title:'${(cmodel.numbers_weight)!''}',sortable:true,
				formatter:function(value,row,index){return row.weight;} 
			},
			//{field:'effect',title:'${(cmodel.numbers_effect)!''}',sortable:true,
			//	formatter:function(value,row,index){return numbers_effect_map[row.effect];} 
			//},
			{field:'flag',title:'${(cmodel.numbers_flag)!''}',sortable:true,
				formatter:function(value,row,index){return numbers_flag_map[row.flag];} 
			},
			{field:'type',title:'${(cmodel.numbers_type)!''}',sortable:true,
				formatter:function(value,row,index){return numbers_type_map[row.type];} 
			},
			{field:'createTime',title:'${(cmodel.numbers_createTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.createTime;} 
			},
			{field:'callChannel',title:'${(cmodel.numbers_callChannel)!''}',sortable:true,
				formatter:function(value,row,index){return numbers_call_channel_map[row.callChannel];} 
			},
			{field:'smsChannel',title:'${(cmodel.numbers_smsChannel)!''}',sortable:true,
				formatter:function(value,row,index){return numbers_sms_channel_map[row.smsChannel];} 
			},
			{field:'spId',title:'${(cmodel.numbers_spId)!''}',sortable:true,
				formatter:function(value,row,index){return splist[row.spId];} 
			},
			{field:'inCache',title:'${(cmodel.numbers_inCache)!''}',sortable:true,
				formatter:function(value,row,index){return numbers_in_cache_map[row.inCache];} 
			},
			{field:'Confirmation',title:'操作',sortable:false,width:100,
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
		      disabled:noPermis("numbers","view"),
		      handler:function(){
		 	     viewObj("${(cmodel.numbers)!''}","${webRoot}/numbers/view.do?id=","","360");
		      }
		},{
		      iconCls:'icon-add',
		      text:'添加',
		      disabled:noPermis("numbers","save"),
		      handler:function(){
		 	     newObj("${(cmodel.numbers)!''}","${webRoot}/numbers/save.do", 500, 450);
		      }
		},{
		      iconCls:'icon-edit',
		      text:'修改',
		      disabled:noPermis("numbers","update"),
		      handler:function(){
		          editObj("${(cmodel.numbers)!''}","${webRoot}/numbers/view.do?id=","${webRoot}/numbers/update.do", 500, 450);
		      }
		},{
		      iconCls:'icon-lock',
		      text:'锁定禁用',
		      disabled:noPermis("numbers","lock"),
		      handler:function(){
		          dealObj("锁定号码","${webRoot}/numbers/lock.do?id=");
		      }
		},{
			  iconCls:'icon-unlock',
		      text:'解除锁定',
		      disabled:noPermis("numbers","unlock"),
		      handler:function(){
		          dealObj("解锁号码","${webRoot}/numbers/unlock.do?id=");
		      }
		},{
		      iconCls:'icon-set',
		      text:'测试绑定',
		      disabled:noPermis("numbers","testBindMobile"),
		      handler:function(){
		 	     testBind("测试绑定","${webRoot}/numbers/testBindMobile.do?id=");
		      }
		},{
		      iconCls:'icon-tack',
		      text:'测试转正式',
		      disabled:noPermis("numbers","testToFormal"),
		      handler:function(){
		 	     dealObj("测试号码转正式号码","${webRoot}/numbers/testToFormal.do?id=");
		      }
		},{
		      iconCls:'icon-print',
		      text:'批量导入号码',
		      disabled:noPermis("numbers","uploadNumbers"),
		      handler:function(){
		 	     uploadObj("批量导入号码","${webRoot}/numbers/uploadNumbers.do", 500, 500);
		      }
		}]
	});
}
//批量更新号码
function updateAll(name,action,width,height){
	if(width==undefined||width==''){
		width=500;
	}
	if(height==undefined||height==''){
		height=300;
	}
	var rows = $('#dg').datagrid('getSelections');
	var id = "";
	var size = 0;
	if(rows==undefined||rows==''){
		$.messager.alert('提示','请选择需要查看的记录！','info');
	    return;
	}
	$.each(rows,function(i,n){
		if(i==0) 
	    	id = n.id;
	    else
	    	id = id + "," + n.id;
		size=i;
	});
	$('#updateAllDiv').show();
	$("#updateAllDiv").dialog({
		title:name,
		top:100,
		width:width,
		height:height,
		cache: false,
		closed: true,
	});
	$('#updateForm')[0].reset();
	
	$("#updateForm").find("#update_status").html("<option value='' selected>请选择</option>");
	$("#updateForm").find("#update_status").append(getSelectList(numbers_status_map,""));
	$("#updateForm").find("#update_operator").html("<option value='' selected>请选择</option>");
	$("#updateForm").find("#update_operator").append(getSelectList(numbers_operator_map, ""));
	$("#updateForm").find("#update_area").html("<option value='' selected>请选择</option>");
	$("#updateForm").find("#update_area").append(getSelectList(numbers_area_map, ""));
	$("#updateForm").find("#update_effect").html("<option value='' selected>请选择</option>");
	$("#updateForm").find("#update_effect").append(getSelectList(numbers_effect_map, ""));
	$("#updateForm").find("#update_type").html("<option value='' selected>请选择</option>");
	$("#updateForm").find("#update_type").append(getSelectList(numbers_type_map, ""));
	$("#updateForm").find("#update_flag").html("<option value='' selected>请选择</option>");
	$("#updateForm").find("#update_flag").append(getSelectList(numbers_flag_map, ""));
	$("#updateForm").find("#update_callChannel").html("<option value='' selected>请选择</option>");
	$("#updateForm").find("#update_callChannel").append(getSelectList(numbers_call_channel_map, ""));
	$("#updateForm").find("#update_smsChannel").html("<option value='' selected>请选择</option>");
	$("#updateForm").find("#update_smsChannel").append(getSelectList(numbers_sms_channel_map, ""));
	$("#updateForm").find("#update_spId").html("<option value='' selected>请选择</option>");
	$("#updateForm").find("#update_spId").append(getSelectList(splist, ""));
	$("#updateForm").find("#update_inCache").html("<option value='' selected>请选择</option>");
	$("#updateForm").find("#update_inCache").append(getSelectList(numbers_in_cache_map, ""));
	
	$("#updateForm").find("#showIds").html(id);
	$("#updateAllSubmit").unbind();
	$("#updateAllSubmit").click(function(){
		x=$("#updateForm").serializeArray();
		d = "ids="+id;
		$.each(x, function(i, field){
		   d=d+"&"+field.name+"="+field.value;
		});
		var options = {
			url : action+"?"+d,
			type : "POST",
			success : function(data) {
				if(data!=undefined&&data.result){
	    			$('#updateAllDiv').dialog('close');
	    			$('#dg').datagrid('reload');
	            	$.messager.show({
	            		title:name,
	            		msg:'批量修改数据成功！',
	            		timeout:5000,
	            		showType:'slide'
	            	});
	            }else{
	            	returnError(data);
	            }
			}
		};
		$("#updateAllSubmit").ajaxSubmit(options);
		return false;
	});
	$('#updateAllDiv').dialog('open');
}

//批量导入号码
function uploadObj(name,action,width,height){
	if (width==undefined||width==''){
		width=500;
	}
	if (height==undefined||width==''){
		height=300;
	}
	$('#uploadObj').show();
	$("#uploadObj").dialog({
		title:name,
		top:100,
		width:width,
		height:height,
		cache: false,
		closed: true,
	});
	$('#uploadForm')[0].reset();
	//初始化
	$("#uploadForm").find("#upload_status").html("<option value='' selected>请选择</option>");
	$("#uploadForm").find("#upload_status").append(getSelectList(numbers_status_map,""));
	$("#uploadForm").find("#upload_operator").html("<option value='' selected>请选择</option>");
	$("#uploadForm").find("#upload_operator").append(getSelectList(numbers_operator_map, ""));
	$("#uploadForm").find("#upload_area").html("<option value='' selected>请选择</option>");
	$("#uploadForm").find("#upload_area").append(getSelectList(numbers_area_map, ""));
	$("#uploadForm").find("#upload_effect").html("<option value='' selected>请选择</option>");
	$("#uploadForm").find("#upload_effect").append(getSelectList(numbers_effect_map, ""));
	$("#uploadForm").find("#upload_type").html("<option value='' selected>请选择</option>");
	$("#uploadForm").find("#upload_type").append(getSelectList(numbers_type_map, ""));
	$("#uploadForm").find("#upload_flag").html("<option value='' selected>请选择</option>");
	$("#uploadForm").find("#upload_flag").append(getSelectList(numbers_flag_map, ""));
	$("#uploadForm").find("#upload_callChannel").html("<option value='' selected>请选择</option>");
	$("#uploadForm").find("#upload_callChannel").append(getSelectList(numbers_call_channel_map, ""));
	$("#uploadForm").find("#upload_smsChannel").html("<option value='' selected>请选择</option>");
	$("#uploadForm").find("#upload_smsChannel").append(getSelectList(numbers_sms_channel_map, ""));
	$("#uploadForm").find("#upload_sort").html("<option value='' selected>请选择</option>");
	$("#uploadForm").find("#upload_sort").append(getSelectList(mobile_pool_map, ""));
	$("#uploadForm").find("#upload_spId").html("<option value='' selected>请选择</option>");
	$("#uploadForm").find("#upload_spId").append(getSelectList(splist, ""));
	
	$("#uploadSubmit").unbind();
	$("#uploadSubmit").click(function(){
		//判空
		var txtFile = $("#txtFile").val();
		if (txtFile ==""){
			$.messager.alert('提示', '请先选择需要上传的文件', 'info');
			return ;
		}
		var options = {
			url : action,
			type : "POST",
			dataType : "script",
			success : function(msg) {
				//在tools.js中
			}
		};
		$("#uploadForm").ajaxSubmit(options);
		return false;
	});
	$('#uploadObj').dialog('open');
}
function testBind(name,url,width,height){
	if(width==undefined||width==''){
		width=500;
	}
	if(height==undefined||height==''){
		height=300;
	}
	$('#testBindObj').show();
	$("#testBindObj").dialog({
		title:name,
		top:100,
		width:width,
		height:height,
		cache: false,
		closed: true,
	});
	var rows = $('#dg').datagrid('getSelections');
	var id = "";
	var size = 0;
	if(rows==undefined||rows==''){
		$.messager.alert('提示','请选择需要查看的记录！','info');
	    return;
	}
	$.each(rows,function(i,n){
		if(i==0) 
	    	id = n.id;
		size=i;
	});
	if(size>0){
		$.messager.alert('提示','请选择一条需要查看的记录！','info');
	    return;
	}
	url = url+id;
	//$('#testBindForm')[0].reset();
	$("#testBindSubmit").unbind();
	$("#testBindSubmit").click(function(){
		$.ajax({
            type: "post",
            dataType: "json",
            url: url,
            data: $('#testBindForm').serializeArray(),
            contentType: 'application/x-www-form-urlencoded; charset=utf-8', 
            success: function(data){
            	if(data!=undefined&&data.result==true){
            		$('#testBindObj').dialog('close');
            		$.messager.show({
            			title:name,
            			msg:'测试绑定成功，请在一分钟内拨测检查测试结果！',
            			timeout:5000,
            			showType:'slide'
            		});
            	}else{
            		returnError(data);
            	}
            }
		});
	});
	$('#testBindObj').dialog('open');	
}
var tdErr;
function validation(obj){
var err = "";
var errId = "";
var mobile = $("#mobile").val();
var status = $("#status").val();
var operator = $("#operator").val();
var area = $("#area").val();
var effect = $("#effect").val();
var type = $("#type").val();
var callChannel = $("#callChannel").val();
	if(isNull(mobile)){
		err = "${(cmodel.numbers_mobile)!''}不能为空！";
		errId = "mobile";
	} else 
	if(!checkLength(mobile,8,20)){
		err = "${(cmodel.numbers_mobile)!''}不能少于8个字且不能超过20个字！";
		errId = "mobile";
	} else
	if(isNull(status)){
		err = "${(cmodel.numbers_status)!''}不能为空！";
		errId = "status";
	} else 
	if(isNull(operator)){
		err = "${(cmodel.numbers_operator)!''}不能为空！";
		errId = "operator";
	} else 
	if(isNull(area)){
		err = "${(cmodel.numbers_area)!''}不能为空！";
		errId = "area";
	} else 
	if(isNull(type)){
		err = "${(cmodel.numbers_type)!''}不能为空！";
		errId = "type";
	} else 
	if(isNull(effect)){
		err = "${(cmodel.numbers_effect)!''}不能为空！";
		errId = "effect";
	} else
	if (isNull(callChannel)){
		err = "${(cmodel.numbers_callChannel)!''}不能为空";
		errId = "callChannel";
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
                   开始时间: <input class="easyui-datebox" name="search.GED_createTime" id="startTime" style="width:80px" />
      ${(cmodel.numbers_mobile)!''}:<input type="text" name="search.LIKES_mobile" />
      ${(cmodel.numbers_callChannel)!''}<select id="search_callChannel" name="search.EQS_callChannel"></select>
      ${(cmodel.numbers_smsChannel)!''}<select id="search_smsChannel" name="search.EQS_smsChannel"></select>
      ${(cmodel.numbers_sort)!''}<select id="search_sort" name="search.EQS_sort"></select>
	  <br />
	       结束时间: <input class="easyui-datebox" name="search.LTD_createTime" id="endTime" style="width:80px" />
	  ${(cmodel.numbers_status)!''}:<select id="search_status" name="search.EQS_status"></select>
	  ${(cmodel.numbers_flag)!''}:<select id="search_flag" name="search.EQS_flag"></select>
	  ${(cmodel.numbers_type)!''}:<select id="search_type" name="search.EQS_type"></select>
	  ${(cmodel.numbers_spId)!''}:<select id="search_spId" name="search.EQS_spId"></select>
	  ${(cmodel.numbers_inCache)!''}:<select id="search_inCache" name="search.EQS_inCache"></select>
      <input type="button" onclick="search();" value="查询"/>
      <input type="reset" value="清空" onclick="searchReset();"/>
      <input type="button" value="批量修改号码属性" id="updates" onclick="updateAll('批量修改号码属性','${webRoot}/numbers/updates.do', 500, 400);"/>
      </div>
  	</form>         
</div>
<div id="editObj" style="display:none;">
    <form id="editForm" method="post">
    <table class="formTable" width="100%">
        <tbody>
			<input type="hidden" id="id" name="id" value=""/>
            <tr>
                <td>${(cmodel.numbers_mobile)!''}</td><td><input type="text" id="mobile" name="mobile" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_status)!''}</td><td><select id="status" name="status" /></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_operator)!''}</td><td><select id="operator" name="operator"/></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_area)!''}</td><td><select id="area" name="area"/></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_type)!''}</td><td><select id="type" name="type"/></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_effect)!''}</td><td><select id="effect" name="effect"/></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_flag)!''}</td><td><select id="flag" name="flag"/></td>
            </tr>
            <tr>
            	<td>${(cmodel.numbers_callChannel)!''}</td><td><select id="callChannel" name="callChannel" onchange="javascript:alert('修改存在业务风险，请和产品确认沟通，明确后再操作！');"/></td>
            </tr>
            <tr>
            	<td>${(cmodel.numbers_smsChannel)!''}</td><td><select id="smsChannel" name="smsChannel" /></td>
            </tr>
            <tr>
            	<td>${(cmodel.numbers_sort)!''}</td><td><select id="sort" name="sort" /></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_weight)!''}</td><td><input type="text" id="weight" name="weight" value="0"/></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_spId)!''}</td><td><select id="spId" name="spId" onchange="javascript:alert('修改存在业务风险，请和产品确认沟通，明确后再操作！');"/></td>
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
                <td>${(cmodel.numbers_id)!''}</td><td id="view_id"></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_mobile)!''}</td><td id="view_mobile"></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_status)!''}</td><td id="view_status"></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_operator)!''}</td><td id="view_operator"></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_area)!''}</td><td id="view_area"></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_type)!''}</td><td id="view_type"></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_effect)!''}</td><td id="view_effect"></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_weight)!''}</td><td id="view_weight"></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_flag)!''}</td><td id="view_flag"></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_createTime)!''}</td><td id="view_createTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_spId)!''}</td><td id="view_spId"></td>
            </tr>
        </tbody>
    </table>
    </form>
</div>
<div id="testBindObj" style="display:none;">
    <form id="testBindForm" method="post">
    <table class="formTable" width="100%">
        <tbody>
            <tr>
                <td>${(cmodel.numbers_status)!''}</td>
                <td>
                	<select id="test_userId" name="test_userId">
                	<option value=''>请选择</option>
                	<option value='2102'>李惠超</option>
                	<option value='23'>贾中元</option>
                	<option value='14'>高翔</option>
                	<option value='88'>晓燕</option>
                	</select>
                </td>
            </tr>
        </tbody>
    </table>
    <table border="0" width="100%" id="formOperate">
    	<tr><td align="center"><input type="button" id="testBindSubmit" value="确定"/><input type="reset"/></td></tr>
    </table>
    </form>
</div>
<div id="uploadObj" style="display:none;">
    <form id="uploadForm" method="post">
    <table class="formTable" width="100%">
        <tbody>
            <tr>
                <td>文件地址</td>
                <td>
                	<input type="file" id="txtFile" name="txtFile" />
                </td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_status)!''}</td><td><select id="upload_status" name="status" /></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_operator)!''}</td><td><select id="upload_operator" name="operator"/></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_area)!''}</td><td><select id="upload_area" name="area"/></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_type)!''}</td><td><select id="upload_type" name="type"/></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_effect)!''}</td><td><select id="upload_effect" name="effect"/></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_flag)!''}</td><td><select id="upload_flag" name="flag"/></td>
            </tr>
            <tr>
            	<td>${(cmodel.numbers_callChannel)!''}</td><td><select id="upload_callChannel" name="callChannel"/></td>
            </tr>
            <tr>
            	<td>${(cmodel.numbers_smsChannel)!''}</td><td><select id="upload_smsChannel" name="smsChannel" /></td>
            </tr>
            <tr>
            	<td>${(cmodel.numbers_sort)!''}</td><td><select id="upload_sort" name="sort" /></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_weight)!''}</td><td><input type="text" id="upload_weight" name="weight" value="0"/></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_spId)!''}</td><td><select id="upload_spId" name="spId"/></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_sort)!''}</td><td><input type="text" id="upload_sort" name="sort" value="0"/></td>
            </tr>
        </tbody>
    </table>
    <table border="0" width="100%" id="formOperate">
    	<tr><td align="center"><input type="button" id="uploadSubmit" value="确定"/><input type="reset"/></td></tr>
    </table>
    </form>
</div>
<div id="updateAllDiv" style="display:none;">
    <form id="updateForm" method="post">
    <table class="formTable" width="100%">
        <tbody>
        	<tr>
                <td width="120px"></td>
                <td id="showIds" style="word-break:break-all; word-wrap:break-word;">                	
                </td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_status)!''}</td><td><select id="update_status" name="status" /></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_operator)!''}</td><td><select id="update_operator" name="operator"/></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_area)!''}</td><td><select id="update_area" name="area"/></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_effect)!''}</td><td><select id="update_effect" name="effect"/></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_type)!''}</td><td><select id="update_type" name="type"/></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_flag)!''}</td><td><select id="update_flag" name="flag"/></td>
            </tr>
            <tr>
            	<td>${(cmodel.numbers_callChannel)!''}</td><td><select id="update_callChannel" name="callChannel"/></td>
            </tr>
            <tr>
            	<td>${(cmodel.numbers_smsChannel)!''}</td><td><select id="update_smsChannel" name="smsChannel" /></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_spId)!''}</td><td><select id="update_spId" name="spId"/></td>
            </tr>
            <tr>
                <td>${(cmodel.numbers_inCache)!''}</td><td><select id="update_inCache" name="inCache"/></td>
            </tr>
        </tbody>
    </table>
    <table border="0" width="100%" id="formOperate">
    	<tr><td align="center"><input type="button" id="updateAllSubmit" value="确定"/><input type="reset"/></td></tr>
    </table>
    </form>
</div>
</body>
</html>
