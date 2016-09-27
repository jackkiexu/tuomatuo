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
	searchInit("${webRoot}/sysOperator/search.do");   
	
	$("#search_status").html("<option value='' selected>请选择</option>");
	$("#search_status").append(getSelectList(operator_status_map,""));          
})
function viewInitCombobox(obj,data){
}
function newInitCombobox(obj,data){
	obj.find("#status").html("<option value='' selected>请选择</option>");
	obj.find("#status").append(getSelectList(operator_status_map,data.status));
	//obj.find("#checkIp").html("<option value='' selected>请选择</option>");
	obj.find("#checkIp").append(getSelectList(operator_checkIp_map,'0'));
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
			{field:'id',title:'${(cmodel.sysOperator_id)!''}',sortable:true,
				formatter:function(value,row,index){return row.id;} 
			},
			{field:'login',title:'${(cmodel.sysOperator_login)!''}',sortable:true,
				formatter:function(value,row,index){return row.login;} 
			},
			{field:'name',title:'${(cmodel.sysOperator_name)!''}',sortable:true,
				formatter:function(value,row,index){return row.name;} 
			},
			{field:'telephone',title:'${(cmodel.sysOperator_telephone)!''}',sortable:true,
				formatter:function(value,row,index){return row.telephone;} 
			},
			{field:'status',title:'${(cmodel.sysOperator_status)!''}',sortable:true,
				formatter:function(value,row,index){return operator_status_map[row.status];} 
			},
			{field:'createTime',title:'${(cmodel.sysOperator_createTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.createTime;} 
			},
			{field:'updateTime',title:'${(cmodel.sysOperator_updateTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.updateTime;} 
			},
			{field:'lastLoginTime',title:'${(cmodel.sysOperator_lastLoginTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.lastLoginTime;} 
			},
			{field:'checkIp',title:'${(cmodel.sysOperator_checkIp)!''}',sortable:true,
				formatter:function(value,row,index){return operator_checkIp_map[row.checkIp];} 
			},
			{field:'ip',title:'${(cmodel.sysOperator_ip)!''}',sortable:true,
				formatter:function(value,row,index){return row.ip;} 
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
		      disabled:noPermis("sysOperator","view"),
		      handler:function(){
		 	     viewObj("${(cmodel.sysOperator)!''}","${webRoot}/sysOperator/view.do?id=");
		      }
		},{
		      iconCls:'icon-add',
		      text:'添加',
		      disabled:noPermis("sysOperator","save"),
		      handler:function(){
		 	     newObj("${(cmodel.sysOperator)!''}","${webRoot}/sysOperator/save.do");
		      }
		},{
		      iconCls:'icon-edit',
		      text:'修改',
		      disabled:noPermis("sysOperator","update"),
		      handler:function(){
		          editObj("${(cmodel.sysOperator)!''}","${webRoot}/sysOperator/view.do?id=","${webRoot}/sysOperator/update.do");
		      }
		},{
			  iconCls:'icon-set',
		      text:'配置角色',
		      disabled:noPermis("sysOperatorRole","preSetOperatorRole"),
		      handler:function(){
		 	     operatorRole("${webRoot}/sysOperatorRole/preSetOperatorRole.do","760","280");
		      }
		},{
		      iconCls:'icon-lock',
		      text:'锁定帐号',
		      disabled:noPermis("sysOperator","lock"),
		      handler:function(){
		 	     dealObj("${(cmodel.sysOperator)!''}锁定","${webRoot}/sysOperator/lock.do?id=");
		      }
		},{
		      iconCls:'icon-unlock',
		      text:'解锁帐号',
		      disabled:noPermis("sysOperator","unlock"),
		      handler:function(){
		          dealObj("${(cmodel.sysOperator)!''}解锁","${webRoot}/sysOperator/unlock.do?id=");
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
			url: "${webRoot}/sysOperatorRole/setOperatorRole.do",
			data:{ids:ids.join(','),operatorId:$("#operatorId").val()},
			dataType:"JSON",
			success: function(data){
				stopLoading();
				if(data!=undefined&&data.result==true){
            		$('#setOperatorRole').dialog('close');
            		$.messager.show({
            			title:'配置角色',
            			msg:'配置角色成功！',
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

function operatorRole(url,width,height){
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
	    	$("#operatorId").val(n.id);
	    }
		size=i;
	});
	if(size>0){
		$.messager.alert('提示','请选择一条需要设置的记录！','info');
	    return;
	}
	url = url+"?operatorId="+id;
	$('#setOperatorRole').show();
	$.ajax({
        type: "post",
        dataType: "json",
        url: url,
        success: function(data){
        	if(data!=undefined&&data.result){
        		var slist = data.selectList;
        		var str = '';
        		for(var i=0;i<slist.length;i++){
    				str=str+'<option value="'+slist[i].id+'">'+slist[i].name+'['+slist[i].details+']</option>';
    			}
				$("#selectList").html(str);
				
				str='';
				var sedlist = data.selectedList;
				for(var i=0;i<sedlist.length;i++){
    				str=str+'<option value="'+sedlist[i].id+'">'+sedlist[i].name+'['+sedlist[i].details+']</option>';
    			}
				$("#selectedList").html(str);
				
    			$("#setOperatorRole").dialog({
    				title:'配置操作员对应的角色',
    				width:width,
    				height:height,
    				cache: false,
    				closed: true,
    			});
    			$('#setOperatorRole').dialog('open');
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
var login = $("#login").val();
var password = $("#password").val();
var name = $("#name").val();
var telephone = $("#telephone").val();
	if(isNull(login)){
		err = "${(cmodel.sysOperator_login)!''}不能为空！";
		errId = "login";
	} else 
	if(!checkLength(login,2,100)){
		err = "${(cmodel.sysOperator_login)!''}不能少于2个字且不能超过100个字！";
		errId = "login";
	} else
	if(isNull(password)){
		err = "${(cmodel.sysOperator_password)!''}不能为空！";
		errId = "password";
	} else 
	if(!checkLength(password,5,100)){
		err = "${(cmodel.sysOperator_password)!''}不能少于5个字且不能超过100个字！";
		errId = "password";
	} else
	if(isNull(name)){
		err = "${(cmodel.sysOperator_name)!''}不能为空！";
		errId = "name";
	} else 
	if(!checkLength(name,2,100)){
		err = "${(cmodel.sysOperator_name)!''}不能少于2个字且不能超过100个字！";
		errId = "name";
	} else
	if(isNull(telephone)){
		err = "${(cmodel.sysOperator_telephone)!''}不能为空！";
		errId = "telephone";
	} else 
	if(!checkLength(telephone,11,11)){
		err = "${(cmodel.sysOperator_telephone)!''}必须是11位手机号码";
		errId = "telephone";
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
	  ${(cmodel.sysOperator_login)!''}:<input type="text" name="search.LIKES_login" value="${(form.login)!''}"/>
	  ${(cmodel.sysOperator_name)!''}:<input type="text" name="search.LIKES_name" value="${(form.name)!''}"/>
	  ${(cmodel.sysOperator_status)!''}:<select id="search_status" name="search.EQS_status"/>
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
                <td>${(cmodel.sysOperator_login)!''}</td><td><input type="text" id="login" name="login" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.sysOperator_password)!''}</td><td><input type="text" id="password" name="password" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.sysOperator_name)!''}</td><td><input type="text" id="name" name="name" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.sysOperator_telephone)!''}</td><td><input type="text" id="telephone" name="telephone" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.sysOperator_status)!''}</td><td><select id="status" name="status" /></td>
            </tr>
            <tr>
                <td>${(cmodel.sysOperator_checkIp)!''}</td><td><select id="checkIp" name="checkIp" /></td>
            </tr>
            <tr>
                <td>${(cmodel.sysOperator_ip)!''}</td><td><input type="text" id="ip" name="ip" value=""/>(多个ip用;分开)</td>
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
                <td>${(cmodel.sysOperator_id)!''}</td><td id="view_id"></td>
            </tr>
            <tr>
                <td>${(cmodel.sysOperator_login)!''}</td><td id="view_login"></td>
            </tr>
            <tr>
                <td>${(cmodel.sysOperator_password)!''}</td><td id="view_password"></td>
            </tr>
            <tr>
                <td>${(cmodel.sysOperator_name)!''}</td><td id="view_name"></td>
            </tr>
            <tr>
                <td>${(cmodel.sysOperator_status)!''}</td><td id="view_status"></td>
            </tr>
            <tr>
                <td>${(cmodel.sysOperator_telephone)!''}</td><td id="view_telephone"></td>
            </tr>
            <tr>
                <td>${(cmodel.sysOperator_createTime)!''}</td><td id="view_createTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.sysOperator_updateTime)!''}</td><td id="view_updateTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.sysOperator_lastLoginTime)!''}</td><td id="view_lastLoginTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.sysOperator_checkIp)!''}</td><td id="view_checkIp"></td>
            </tr>
            <tr>
                <td>${(cmodel.sysOperator_ip)!''}</td><td id="view_ip"></td>
            </tr>
        </tbody>
    </table>
    </form>
</div>
<div id="setOperatorRole" style="display:none;">
<div id="loading" class="datagrid-mask" style="display:none;width:100%;height:100%"></div>
<div id="loading_msg" class="datagrid-mask-msg" style="display:none;position:absolute;font-size:9px;left:300px">正在处理，请稍候... ...</div>

    <form id="setOperatorRoleFrom">
    <input type="hidden" id="operatorId" name="operatorId" value=""/>
    <table class="formTable" width="100%">
        <tbody>
            <tr>
                <td style="text-align:center">可选角色</td><td></td><td style="text-align:center">已有角色</td>
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
