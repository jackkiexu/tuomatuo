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
//初始化
$(function(){
	searchInit("${webRoot}/services/search.do");     
	
	$("#search_statue").html("<option value='' selected>请选择</option>");
	$("#search_statue").append(getSelectList(services_status_map,""));
	     
})
function viewInitCombobox(obj,data){
	obj.find("#view_type").html(services_type_map[obj.find("#view_type").html()]);
	obj.find("#view_status").html(services_status_map[obj.find("#view_status").html()]);
	obj.find("#view_price").html(obj.find("#view_price").html()+"元");
	obj.find("#view_maxMsgCount").html(obj.find("#view_maxMsgCount").html()+"条");
	obj.find("#view_maxCallMinutes").html(obj.find("#view_maxCallMinutes").html()+"分钟");
}
function newInitCombobox(obj,data){
	obj.find("#status").html("<option value='' selected>请选择</option>");
	obj.find("#status").append(getSelectList(services_status_map,data.status));
	obj.find("#type").html("<option value='' select>请选择</option>");
	obj.find("#type").append(getSelectList(services_type_map,data.type));
	obj.find("#personIssuedFlag").html("<option value='' select>请选择</option>");
	obj.find("#personIssuedFlag").append(getSelectList(person_Issued_Flag_map,data.type));
	obj.find("#platformFlag").html("<option value='' select>请选择</option>");
	obj.find("#platformFlag").append(getSelectList(services_platfrom_flag,data.type));
}
function editInitCombobox(obj,data){
	$('#startTime').datebox('setValue',data.startTime);
	$('#endTime').datebox('setValue',data.endTime); 
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
			{field:'id',title:'${(cmodel.services_id)!''}',sortable:true,
				formatter:function(value,row,index){return row.id;} 
			},
			{field:'name',title:'${(cmodel.services_name)!''}',sortable:true,
				formatter:function(value,row,index){return row.name;} 
			},
			{field:'price',title:'${(cmodel.services_price)!''}',sortable:true,
				formatter:function(value,row,index){return row.price+'元';} 
			},
			{field:'type',title:'${(cmodel.services_type)!''}',sortable:true,
				formatter:function(value,row,index){return services_type_map[row.type];} 
			},
			{field:'status',title:'${(cmodel.services_status)!''}',sortable:true,
				formatter:function(value,row,index){return services_status_map[row.status];} 
			},
			{field:'createTime',title:'${(cmodel.services_createTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.createTime;} 
			},
			{field:'effect',title:'服务功能',sortable:true,
				formatter:function(value,row,index){return service_effect_map[row.effect];} 
			},
			{field:'flag',title:'服务标记',sortable:true,
				formatter:function(value,row,index){return services_flag_map[row.flag];} 
			},
			{field:'delayDays',title:'${(cmodel.services_delayDays)!''}',sortable:true,
				formatter:function(value,row,index){return row.delayDays+"天";} 
			},
			{field:'delayMinutes',title:'${(cmodel.services_delayMinutes)!''}',sortable:true,
				formatter:function(value,row,index){return row.delayMinutes+"分钟";} 
			},
			{field:'personIssuedCount',title:'${(cmodel.services_personIssuedCount)!''}',sortable:true,
				formatter:function(value,row,index){return row.personIssuedCount;} 
			},
			{field:'personIssuedFlag',title:'${(cmodel.services_personIssuedFlag)!''}',sortable:true,
				formatter:function(value,row,index){return person_Issued_Flag_map[row.personIssuedFlag];} 
			},
			{field:'platformFlag',title:'${(cmodel.services_platformFlag)!''}',sortable:true,
				formatter:function(value,row,index){return services_platfrom_flag[row.platformFlag];} 
			},
			{field:'mobilePool',title:'${(cmodel.services_mobilePool)!''}',sortable:true,
				formatter:function(value,row,index){return mobile_pool_map[row.mobilePool];} 
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
		      disabled:noPermis("services","view"),
		      handler:function(){
		 	     viewObj("${(cmodel.services)!''}","${webRoot}/services/view.do?id=");
		      }
		},{
		      iconCls:'icon-add',
		      text:'添加',
		      disabled:noPermis("services","save"),
		      handler:function(){
		 	     newObj("${(cmodel.services)!''}","${webRoot}/services/save.do","",400);
		      }
		},{
		      iconCls:'icon-edit',
		      text:'修改',
		      disabled:noPermis("services","update"),
		      handler:function(){
		          editObj("${(cmodel.services)!''}","${webRoot}/services/view.do?id=","${webRoot}/services/update.do");
		      }
		},{
		      iconCls:'icon-delete',
		      text:'删除',
		      disabled:noPermis("services","delete"),
		      handler:function(){
		          removeObj("${webRoot}/services/delete.do");
		      }
		},{
		      iconCls:'icon-set',
		      text:'配置号码池',
		      disabled:noPermis("services","mobilePoolSet"),
		      handler:function(){
		          mobilePoolSet("${webRoot}/services/mobilePoolSet.do");
		      }
		}]
	});
}
var tdErr;
function validation(obj){
var err = "";
var errId = "";
var name = $("#name").val();
var price = $("#price").val();
var type = $("#type").val();
var status = $("#status").val();
var startTime = $("#startTime").datebox("getValue");
var endTime = $("#endTime").datebox("getValue");
var maxMsgCount = $("#maxMsgCount").val();
var maxCallMinutes = $("#maxCallMinutes").val();
	if(isNull(name)){
		err = "${(cmodel.services_name)!''}不能为空！";
		errId = "name";
	} else 
	if(!checkLength(name,3,200)){
		err = "${(cmodel.services_name)!''}不能少于3个字且不能超过200个字！";
		errId = "name";
	} else
	if(isNull(price)){
		err = "${(cmodel.services_price)!''}不能为空！";
		errId = "price";
	} else 
	if(!checkLength(price,1,10)){
		err = "${(cmodel.services_price)!''}不能少于1个字且不能超过10个字！";
		errId = "price";
	} else
	if(isNull(type)){
		err = "${(cmodel.services_type)!''}不能为空！";
		errId = "type";
	} else 
	if(isNull(status)){
		err = "${(cmodel.services_status)!''}不能为空！";
		errId = "status";
	} else 
	if(isNull(startTime)){
		err = "${(cmodel.services_startTime)!''}不能为空！";
		errId = "startTime";
	} else 
	if(isNull(endTime)){
		err = "${(cmodel.services_endTime)!''}不能为空！";
		errId = "endTime";
	} else 
	if(isNull(maxMsgCount)){
		err = "${(cmodel.services_maxMsgCount)!''}不能为空！";
		errId = "maxMsgCount";
	} else 
	if(isNull(maxCallMinutes)){
		err = "${(cmodel.services_maxCallMinutes)!''}不能为空！";
		errId = "maxCallMinutes";
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
	  ${(cmodel.services_name)!''}:<input type="text" name="search.LIKES_name"/>
	  ${(cmodel.services_type)!''}:<input type="text" name="search.EQS_type"/>
	  ${(cmodel.services_status)!''}:<select id="search_statue" name="search.EQS_status"/>
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
                <td>${(cmodel.services_name)!''}</td><td><input type="text" id="name" name="name" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.services_price)!''}</td><td><input type="text" id="price" name="price" value=""/>元</td>
            </tr>
            <tr>
                <td>${(cmodel.services_type)!''}</td><td><select id="type" name="type" value=""></select></td>
            </tr>
            <tr>
                <td>${(cmodel.services_status)!''}</td><td><select id="status" name="status" value=""></select></td>
            </tr>
            <tr>
                <td>${(cmodel.services_startTime)!''}</td><td><input class="easyui-datebox" name="startTime" id="startTime"  style="width:90px"/></td>
            </tr>
            <tr>
                <td>${(cmodel.services_endTime)!''}</td><td><input class="easyui-datebox" name="endTime" id="endTime"  style="width:90px"/></td>
            </tr>
            <tr>
                <td>${(cmodel.services_maxMsgCount)!''}</td><td><input type="text" id="maxMsgCount" name="maxMsgCount" value=""/>条</td>
            </tr>
            <tr>
                <td>${(cmodel.services_maxCallMinutes)!''}</td><td><input type="text" id="maxCallMinutes" name="maxCallMinutes" value=""/>分钟</td>
            </tr>
            <tr>
                <td>${(cmodel.services_personIssuedCount)!''}</td><td><input type="text" id="personIssuedCount" name="personIssuedCount" value=""/>次</td>
            </tr>
            <tr>
                <td>${(cmodel.services_personIssuedFlag)!''}</td><td><select id="personIssuedFlag" name="personIssuedFlag" value=""></td>
            </tr>
            <tr>
                <td>${(cmodel.services_platformFlag)!''}</td><td><select id="platformFlag" name="platformFlag" value=""></select></td>
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
                <td>${(cmodel.services_id)!''}</td><td id="view_id"></td>
            </tr>
            <tr>
                <td>${(cmodel.services_name)!''}</td><td id="view_name"></td>
            </tr>
            <tr>
                <td>${(cmodel.services_price)!''}</td><td id="view_price"></td>
            </tr>
            <tr>
                <td>${(cmodel.services_type)!''}</td><td id="view_type"></td>
            </tr>
            <tr>
                <td>${(cmodel.services_status)!''}</td><td id="view_status"></td>
            </tr>
            <tr>
                <td>${(cmodel.services_createTime)!''}</td><td id="view_createTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.services_startTime)!''}</td><td id="view_startTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.services_endTime)!''}</td><td id="view_endTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.services_maxMsgCount)!''}</td><td id="view_maxMsgCount"></td>
            </tr>
            <tr>
                <td>${(cmodel.services_maxCallMinutes)!''}</td><td id="view_maxCallMinutes"></td>
            </tr>
            <tr>
                <td>${(cmodel.services_personIssuedCount)!''}</td><td><td id="personIssuedCount"></td>
            </tr>
            <tr>
                <td>${(cmodel.services_personIssuedFlag)!''}</td><td><td id="personIssuedFlag"></td>
            </tr>
        </tbody>
    </table>
    </form>
</div>
<div id="setMobilePoolObj" style="display:none;">
	<form id="setMobilePoolForm">
		<table class="formTable" width="100%">
	        <tbody>
				<tr>
					<td>${(cmodel.services_id)!''}</td><td><input id='service_view_id' name='id' readonly="readonly" /></td>
				</tr>
	            <tr>
	                <td>${(cmodel.services_name)!''}</td><td id='service_view_name'></td>
	            </tr>
	            <tr>
	                <td>${(cmodel.services_mobilePool)!''}</td><td><select id="service_view_sort" name='mobilePool'></select></td>
	            </tr>
	        </tbody>
	    </table>
	    <table border="0" width="100%" id="formOperate">
	    	<tr><td align="center"><input type="button" id="setMobilePoolSubmit" value="确定"/></td></tr>
	    </table>
	</form>
</div>
	
</body>
</html>
