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
	searchInit("${webRoot}/keyWord/search.do");     
	$("#search_type").html("<option value='' selected>请选择</option>");
	$("#search_type").append(getSelectList(keyWord_type_map,"")); 
	
	
})
function viewInitCombobox(obj,data){
	obj.find("#view_type").html(keyWord_type_map[obj.find("#view_type").html()]);
}
function newInitCombobox(obj,data){
	
	obj.find("#type").html("<option value='' selected>请选择</option>");
	obj.find("#type").append(getSelectList(keyWord_type_map,data.status));
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
		{field:'id',title:'${(cmodel.keyWord_id)!''}',sortable:true,
				formatter:function(value,row,index){return row.id;} 
			},
			{field:'keyName',title:'${(cmodel.keyWord_keyName)!''}',sortable:true,
				formatter:function(value,row,index){return row.keyName;}
			},
			{field:'createTime',title:'${(cmodel.keyWord_createTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.createTime;} 
			},
			{field:'createUser',title:'${(cmodel.keyWord_createUser)!''}',sortable:true,
				formatter:function(value,row,index){return row.createUser;} 
			},
			{field:'type',title:'${(cmodel.keyWord_type)!''}',sortable:true,
				formatter:function(value,row,index){return keyWord_type_map[row.type];}
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
		      disabled:noPermis("keyWord","view"),
		      handler:function(){
		 	     viewObj("${(cmodel.keyWord)!''}","${webRoot}/keyWord/view.do?id=","","500");
		      }
		},{
		      iconCls:'icon-add',
		      text:'添加',
		      disabled:noPermis("keyWord","save"),
		      handler:function(){
		 	     newObj("${(cmodel.keyWord)!''}","${webRoot}/keyWord/save.do","","");
		      }
		},{
		      iconCls:'icon-edit',
		      text:'修改',
		      disabled:noPermis("keyWord","update"),
		      handler:function(){
		          editObj("${(cmodel.keyWord)!''}","${webRoot}/keyWord/view.do?id=","${webRoot}/keyWord/update.do","","");
		      }
		},{
		      iconCls:'icon-delete',
		      text:'删除',
		      disabled:noPermis("keyWord","delete"),
		      handler:function(){
		          removeObj("${webRoot}/keyWord/delete.do");
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
	var keyName = $("#keyName").val();
	var type = $("#type").val();
	var createUser = $("#createUser").val();
		 if(isNull(keyName)){
			err = "${(cmodel.keyWord_keyName)!''}不能为空！";
			errId = "keyName";
		}else if(isNull(type)){
			err = "${(cmodel.keyWord_type)!''}不能为空！";
			errId = "type";
		}else if(isNull(createUser)){
			err = "${(cmodel.keyWord_createUser)!''}不能为空！";
			errId = "createUser";
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
	  ${(cmodel.keyWord_id)!''}:<input type="text" name="search.EQI_id"/>
	  ${(cmodel.keyWord_type)!''}:<select id="search_type" name="search.EQS_type"/>
	  ${(cmodel.keyWord_keyName)!''}:<select id="search_keyName" name="search.EQS_keyName"/>
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
                <td>${(cmodel.keyWord_keyName)!''}</td><td><input type="text" id="keyName" name="keyName"   value="" /></td>
            </tr>
            <tr class="vmNumber">
                <td>${(cmodel.keyWord_createUser)!''}</td><td><input type="text" id="createUser" name="createUser" value="" /></td>
            </tr>
             <tr class="createTime">
                <td>${(cmodel.keyWord_createTime)!''}</td><td><input class="easyui-datetimebox" type="text" id="createTime" name="createTime"  value="" /></td>
            </tr>
             <tr class="isDelete">
                <td>${(cmodel.keyWord_type)!''}</td><td><select id="type" name="type"/></td>
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
                <td>${(cmodel.keyWord_id)!''}</td><td id="view_id"></td>
            </tr>
            <tr>
                <td>${(cmodel.keyWord_keyName)!''}</td><td id="view_keyName"></td>
            </tr>
            <tr>
                <td>${(cmodel.keyWord_createTime)!''}</td><td id="view_createTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.keyWord_type)!''}</td><td id="view_type"></td>
            </tr>
            <tr>
                <td>${(cmodel.keyWord_createUser)!''}</td><td id="view_createUser"></td>
            </tr>
            </tr>
        </tbody>
    </table>
    </form>
</div>
</body>
</html>
