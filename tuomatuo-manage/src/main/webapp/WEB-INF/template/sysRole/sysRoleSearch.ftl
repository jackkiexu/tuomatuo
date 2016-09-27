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
	searchInit("${webRoot}/sysRole/search.do");          
})
function viewInitCombobox(obj,data){
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
			{field:'id',title:'${(cmodel.sysRole_id)!''}',sortable:true,
				formatter:function(value,row,index){return row.id;} 
			},
			{field:'name',title:'${(cmodel.sysRole_name)!''}',sortable:true,
				formatter:function(value,row,index){return row.name;} 
			},
			{field:'details',title:'${(cmodel.sysRole_details)!''}',sortable:true,
				formatter:function(value,row,index){return row.details;} 
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
		      disabled:noPermis("sysRole","view"),
		      handler:function(){
		 	     viewObj("${(cmodel.sysRole)!''}","${webRoot}/sysRole/view.do?id=");
		      }
		},{
		      iconCls:'icon-add',
		      text:'添加',
		      disabled:noPermis("sysRole","save"),
		      handler:function(){
		 	     newObj("${(cmodel.sysRole)!''}","${webRoot}/sysRole/save.do");
		      }
		},{
		      iconCls:'icon-edit',
		      text:'修改',
		      disabled:noPermis("sysRole","update"),
		      handler:function(){
		          editObj("${(cmodel.sysRole)!''}","${webRoot}/sysRole/view.do?id=","${webRoot}/sysRole/update.do");
		      }
		},{
		      iconCls:'icon-delete',
		      text:'删除',
		      disabled:noPermis("sysRole","delete"),
		      handler:function(){
		          removeObj("${webRoot}/sysRole/delete.do");
		      }
		},{
			  iconCls:'icon-set',
		      text:'配置权限',
		      disabled:noPermis("sysRolePermission","preSetRolePermission"),
		      handler:function(){
		 	     rolePermission("${webRoot}/sysRolePermission/preSetRolePermission.do","760","280");
		      }
		}]
	});
	
	$('#selectIn').click(function(){
	    $('#selectList option:selected').each(function(){
	    	$('#selectedList').append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
	    	$(this).remove(); 
	    });
	});
	$('#selectOut').click(function(){
	    $('#selectedList option:selected').each(function(){
	    	$('#selectList').append("<option value='"+$(this).val()+"'>"+$(this).text()+"</option>");
	    	$(this).remove(); 
	    });
	});
	$('#selectFinish').click(function(){
		var ids = new Array();
		$('#selectedList option').each(function(){
		 	ids.push($(this).val());
		});
		startLoading();
		$.ajax({
			type: "POST",
			url: "${webRoot}/sysRolePermission/setRolePermission.do",
			data:{ids:ids.join(','),roleId:$("#roleId").val()},
			dataType:"JSON",
			success: function(data){
				stopLoading();
				if(data!=undefined&&data.result==true){
            		$('#setRolePermission').dialog('close');
            		$.messager.show({
            			title:'配置权限',
            			msg:'配置权限成功！',
            			timeout:5000,
            			showType:'slide'
            		});
            	}else{
            		returnError(data);
            	}
			}
		});
	});	
}
function rolePermission(url,width,height){
	var rows = $('#dg').datagrid('getSelections');
	var id = "";
	var size = 0;
	if(rows==undefined||rows==''){
		$.messager.alert('提示','请选择需要设置的记录！','info');
	    return;
	}
	$.each(rows,function(i,n){
		if(i==0){
	    	id = n.id;
	    	$("#roleId").val(n.id);
	    }
		size=i;
	});
	if(size>0){
		$.messager.alert('提示','请选择一条需要设置的记录！','info');
	    return;
	}
	url = url+"?roleId="+id;
	$('#setRolePermission').show();
	$.ajax({
        type: "post",
        dataType: "json",
        url: url,
        success: function(data){
        	if(data!=undefined&&data.result){
        		var slist = data.selectList;
        		var str = '';
        		for(var i=0;i<slist.length;i++){
    				str=str+'<option value="'+slist[i].id+'">'+slist[i].name+'['+slist[i].id+']'+'['+slist[i].path+']'+'['+slist[i].methods+']</option>';
    			}
				$("#selectList").html(str);
				
				str='';
				var sedlist = data.selectedList;
				for(var i=0;i<sedlist.length;i++){
    				str=str+'<option value="'+sedlist[i].id+'">'+sedlist[i].name+'['+sedlist[i].id+']'+'['+sedlist[i].path+']'+'['+sedlist[i].methods+']</option>';
    			}
				$("#selectedList").html(str);
				
    			$("#setRolePermission").dialog({
    				title:'配置角色对应的权限',
    				width:width,
    				height:height,
    				cache: false,
    				closed: true,
    			});
    			$('#setRolePermission').dialog('open');
    		}else{
    			if(data.msg==undefined)
    				$.messager.alert('提示','出错了！','info');
    			else
    				$.messager.alert('提示',data.msg,'info');
    		}
        }
	});
}
var tdErr;
function validation(obj){
var err = "";
var errId = "";
var name = $("#name").val();
var details = $("#details").val();
	if(isNull(name)){
		err = "${(cmodel.sysRole_name)!''}不能为空！";
		errId = "name";
	} else 
	if(!checkLength(name,4,100)){
		err = "${(cmodel.sysRole_name)!''}不能少于4个字且不能超过100个字！";
		errId = "name";
	} else
	if(isNull(details)){
		err = "${(cmodel.sysRole_details)!''}不能为空！";
		errId = "details";
	} else 
	if(!checkLength(details,0,1000)){
		err = "${(cmodel.sysRole_details)!''}不能超过1000个字！";
		errId = "details";
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
	  ${(cmodel.sysRole_name)!''}:<input type="text" name="search.LIKES_name" value="${(form.name)!''}"/>
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
                <td>${(cmodel.sysRole_name)!''}</td><td><input type="text" id="name" name="name" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.sysRole_details)!''}</td><td><input type="text" id="details" name="details" value=""/></td>
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
                <td>${(cmodel.sysRole_id)!''}</td><td id="view_id"></td>
            </tr>
            <tr>
                <td>${(cmodel.sysRole_name)!''}</td><td id="view_name"></td>
            </tr>
            <tr>
                <td>${(cmodel.sysRole_details)!''}</td><td id="view_details"></td>
            </tr>
        </tbody>
    </table>
    </form>
</div>
<div id="setRolePermission" style="display:none;">
<div id="loading" class="datagrid-mask" style="display:none;width:100%;height:100%"></div>
<div id="loading_msg" class="datagrid-mask-msg" style="display:none;position:absolute;font-size:9px;left:300px">正在处理，请稍候... ...</div>

    <form id="setRolePermissionFrom">
    <input type="hidden" id="roleId" name="roleId" value=""/>
    <table class="formTable" width="100%">
        <tbody>
            <tr>
                <td style="text-align:center">可选权限</td><td></td><td style="text-align:center">已有权限</td>
            </tr>
<tr>
<td>
<select name="selectList" id="selectList" multiple="multiple" size="12" style="width:300px;">
</select>
</td>
<td style="vertical-align:middle;text-align:center">
<input type="button" id="selectIn" value=" 选择加入>> "/>
<input type="button" id="selectOut" value=" <<选择删除 "/>
<br/><br/>
<input type="button" id="selectFinish" value="完成选择"/>
</td>
<td>
<select name="selectedList" id="selectedList" multiple="multiple" size="12" style="width:300px;">
</select>
</td>
</tr>
        </tbody>
    </table>
    </form>
</div>
</body>
</html>
