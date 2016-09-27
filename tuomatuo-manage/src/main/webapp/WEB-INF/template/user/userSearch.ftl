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
	searchInit("${webRoot}/user/search.do");     
	$("#search_status").html("<option value='' selected>请选择</option>");
	$("#search_status").append(getSelectList(user_status_map,""));   
	
	$("#search_fromType").html("<option value='' selected>请选择</option>");
	$("#search_fromType").append(getSelectList(user_fromType_map,""));      
})
function viewInitCombobox(obj,data){
	obj.find("#view_status").html(user_status_map[obj.find("#view_status").html()]);
	obj.find("#view_fromType").html(user_fromType_map[obj.find("#view_fromType").html()]);
}
function newInitCombobox(obj,data){
	obj.find("#status").html("<option value='' selected>请选择</option>");
	obj.find("#status").append(getSelectList(user_status_map,data.status));
	obj.find("#fromType").html("<option value='' selected>请选择</option>");
	obj.find("#fromType").append(getSelectList(user_fromType_map,data.fromType));
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
			{field:'id',title:'${(cmodel.user_id)!''}',sortable:true,
				formatter:function(value,row,index){return row.id;} 
			},
			{field:'loginName',title:'${(cmodel.user_loginName)!''}',sortable:true,
				formatter:function(value,row,index){return row.loginName;} 
			},
			{field:'experienceFlag',title:'${(cmodel.user_countNumber)!''}',sortable:true,
				formatter:function(value,row,index){return row.experienceFlag;}
			},
			{field:'openId',title:'${(cmodel.user_openId)!''}',sortable:true,
				formatter:function(value,row,index){return row.openId;} 
			},
			{field:'status',title:'${(cmodel.user_status)!''}',sortable:true,
				formatter:function(value,row,index){return user_status_map[row.status];} 
			},
			{field:'fromType',title:'${(cmodel.user_fromType)!''}',sortable:true,
				formatter:function(value,row,index){return user_fromType_map[row.fromType];} 
			},
			{field:'createTime',title:'${(cmodel.user_createTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.createTime;} 
			},
			{field:'endTime',title:'${(cmodel.user_endTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.endTime;} 
			},
			{field:'lastLoginTime',title:'${(cmodel.user_lastLoginTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.lastLoginTime;} 
			},
			{field:'code',title:'验证码',sortable:true,
				formatter:function(value,row,index){return row.code;} 
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
		      disabled:noPermis("user","view"),
		      handler:function(){
		 	     viewObj("${(cmodel.user)!''}","${webRoot}/user/view.do?id=","","500");
		      }
		},{
		      iconCls:'icon-add',
		      text:'添加',
		      disabled:noPermis("user","save"),
		      handler:function(){
		 	     newObj("${(cmodel.user)!''}","${webRoot}/user/save.do");
		 	     $(".passwordRow").show();
		 	     $(".status").hide();
		 	     $(".fromType").hide();
		      }
		},{
		      iconCls:'icon-edit',
		      text:'修改',
		      disabled:noPermis("user","update"),
		      handler:function(){
		     	 $(".passwordRow").hide();
		     	 $(".status").show();
		 	     $(".fromType").show();
		          editObj("${(cmodel.user)!''}","${webRoot}/user/view.do?id=","${webRoot}/user/update.do");
		      }
		},{
		      iconCls:'icon-lock',
		      text:'锁定帐号',
		      disabled:noPermis("user","lock"),
		      handler:function(){
		 	     dealUserStatus("${(cmodel.user)!''}锁定","${webRoot}/user/lock.do?id=");
		      }
		},{
		      iconCls:'icon-unlock',
		      text:'解锁帐号',
		      disabled:noPermis("user","unlock"),
		      handler:function(){
		          dealUserStatus("${(cmodel.user)!''}解锁","${webRoot}/user/unlock.do?id=");
		      }
		},{
		      iconCls:'icon-delete',
		      text:'释放号码',
		      disabled:noPermis("user","releaseMobile"),
		      handler:function(){
		          dealUserStatus("${(cmodel.user)!''}号码释放","${webRoot}/user/releaseMobile.do?id=");
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
		if(i==0) 
	    	id = n.id;
		size=i;
	});
 if(size>0){
			$.messager.alert('提示','该服务不支持多选','info');;
	    return;
	}
	url = url+id;
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
var loginName = $("#loginName").val();
var password = $("#password").val();
var mail = $("#mail").val();
var openId = $("#openId").val();
var status = $("#status").val();
var fromType = $("#fromType").val();
	if(isNull(loginName)){
		err = "${(cmodel.user_loginName)!''}不能为空！";
		errId = "loginName";
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
      结束时间: <input class="easyui-datebox" name="search.LTD_createTime" id="endTime" style="width:80px">
	  ${(cmodel.user_id)!''}:<input type="text" name="search.EQI_id"/>
	  ${(cmodel.user_loginName)!''}:<input type="text" name="search.LIKES_loginName"/>
	  ${(cmodel.user_status)!''}:<select id="search_status" name="search.EQS_status"/>
	  ${(cmodel.user_fromType)!''}:<select id="search_fromType" name="search.EQS_fromType"/>
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
                <td>${(cmodel.user_loginName)!''}</td><td><input type="text" id="loginName" name="loginName" value=""/></td>
            </tr>
            <tr class="passwordRow">
                <td>${(cmodel.user_password)!''}</td><td><input type="text" id="password" name="password" value="" /></td>
            </tr>
            <tr class="status">
                <td>${(cmodel.user_status)!''}</td><td><select id="status" name="status"/></td>
            </tr>
            <tr class="fromType">
                <td>${(cmodel.user_fromType)!''}</td><td><select id="fromType" name="fromType"/></td>
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
                <td>${(cmodel.user_id)!''}</td><td id="view_id"></td>
            </tr>
            <tr>
                <td>${(cmodel.user_loginName)!''}</td><td id="view_loginName"></td>
            </tr>
            <tr>
                <td>${(cmodel.user_countNumber)!''}</td><td id="view_countNumber"></td>
            </tr>
            <tr>
                <td>${(cmodel.user_password)!''}</td><td id="view_password"></td>
            </tr>
            <tr>
                <td>${(cmodel.user_fromType)!''}</td><td id="view_fromType"></td>
            </tr>
            <tr>
                <td>${(cmodel.user_openId)!''}</td><td id="view_openId"></td>
            </tr>
            <tr>
                <td>${(cmodel.user_status)!''}</td><td id="view_status"></td>
            </tr>
            <tr>
                <td>${(cmodel.user_createTime)!''}</td><td id="view_createTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.user_lastLoginTime)!''}</td><td id="view_lastLoginTime"></td>
            </tr>
        </tbody>
    </table>
    </form>
</div>
</body>
</html>
