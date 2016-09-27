<!DOCTYPE html>
<html>
<head>
<title>spring3</title>
<meta charset="UTF-8">
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
#uploadFile{
	width:500px;
	height=350;
	
}
</style>
<script type="text/javascript">
//初始化
$(function(){
	searchInit("${webRoot}/insideLetter/search.do"); 
	$("#search_isDelete").html("<option value='' selected>请选择</option>");
	$("#search_isDelete").append(getSelectList(isDelete_map,""));
})
function viewInitCombobox(obj,data){
	obj.find("#view_isDelete").html(isDelete_map[obj.find("#view_isDelete").html()]);
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
		fitColumns:false,	//横向滚动条
		//checkOnSelect:false,
		columns:[[
			{field:'ck',checkbox:true,width:2},
			{field:'id',title:'${(cmodel.insideletter_id)!'ID'}',sortable:true,
				formatter:function(value,row,index){return row.id;} 
			},
			{field:'title',title:'${(cmodel.insideletter_title)!'标题'}',sortable:true,
				formatter:function(value,row,index){return row.title;} 
			},
			{field:'iconPath',title:'${(cmodel.insideletter_iconPath)!'图片路径'}',sortable:true,
				formatter:function(value,row,index){return row.iconPath;} 
			},
			{field:'createTime',title:'${(cmodel.insideletter_createTime)!'创建时间'}',sortable:true,
				formatter:function(value,row,index){return row.createTime;}
			},
			{field:'beginTime',title:'${(cmodel.insideletter_beginTime)!'活动开始'}',sortable:true,
				formatter:function(value,row,index){return row.beginTime;} 
			},
			{field:'endTime',title:'${(cmodel.insideletter_endTime)!'活动结束'}',sortable:true,
				formatter:function(value,row,index){return row.endTime;} 
			},
			{field:'intro',title:'${(cmodel.insideletter_intro)!'简介'}',sortable:true,
				formatter:function(value,row,index){return row.intro;} 
			},
			{field:'content',title:'${(cmodel.insideletter_content)!'详情'}',sortable:true,
				formatter:function(value,row,index){return row.content;} 
			},
			{field:'linkUrl',title:'${(cmodel.insideletter_linkUrl)!'活动链接'}',sortable:true,width:200,
				formatter:function(value,row,index){return row.linkUrl;} 
			},
			{field:'isDelete',title:'${(cmodel.insideletter_isDelete)!'标记删除'}',sortable:true,
				formatter:function(value,row,index){return isDelete_map[row.isDelete];} 
			},
			{field:'Confirmation',title:'操作',width:100,sortable:false,
				formatter:function(value,row,index){return '';}
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
			text:'新站内信',
			handler:function(){
				$(".easyui-datetimebox").datebox('setValue', '');
				$("#uploadSubmit").attr("disabled", false);
				uploadObj("发送${(cmodel.insideLetter)!''}","${webRoot}/insideLetter/send.do");
		    }
		},{
		      iconCls:'icon-info',
		      text:'查看详情',
		      disabled:noPermis("insideLetter","view"),
		      handler:function(){
				  viewObj("${(cmodel.insideLetter)!''}","${webRoot}/insideLetter/view.do?id=",500,330);
		      }
		},{
		      iconCls:'icon-remove',
		      text:'标记删除',
		      disabled:noPermis("insideLetter","markDelete"),
		      handler:function(){
		         dealUserStatus("${(cmodel.insideLetter)!''}标记删除","${webRoot}/insideLetter/markDelete.do?id=");
		      }
		},{
		      iconCls:'icon-edit',
		      text:'状态还原',
		      disabled:noPermis("insideLetter","markNotDelete"),
		      handler:function(){
		          dealUserStatus("${(cmodel.insideLetter)!''}状态还原","${webRoot}/insideLetter/markNotDelete.do?id=");
		      }
		},{
		      iconCls:'icon-add',
		      text:'重新发送',
		      disabled:noPermis("insideLetter","resend"),
		      handler:function(){
		 	     dealUserStatus("重发${(cmodel.insideLetter)!''}","${webRoot}/insideLetter/resend.do?id=");
		      }
		},{
		      iconCls:'icon-delete',
		      text:'真实删除',
		      disabled:noPermis("insideLetter","deleteicon"),
		      handler:function(){
	      			$.messager.confirm("警告：不可恢复", "该操作为真实删除，<strong>不可恢复</strong><br/>确定执行？", 
						function (data) {
			            	if (data) {
			                	dealUserStatus("真实删除${(cmodel.insideLetter)!''}","${webRoot}/insideLetter/deleteicon.do?id=");
			            	}
			            }
			        )
		      		
		      }
		},]
	});
}

function uploadObj(name,action,width,height){
	if (width==undefined||width==''){
		width=500;
	}
	if (height==undefined||width==''){
		height=340;
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
	$("#uploadSubmit").unbind();
	$("#uploadSubmit").click(function(){
		$("#uploadSubmit").attr("disabled", true);
		//判空
		var txtFile = $("#txtFile").val();
		if (txtFile ==""){
			$.messager.alert('提示', '请先选择需要上传的文件', 'info');
			$("#uploadSubmit").attr("disabled", false);
			return ;
		}
		var options = {
			url : action,
			type : "script",
			dataType : "json",
			success : function(data){}
		};
		$("#uploadForm").ajaxSubmit(options);
		return false;
	});
	$('#uploadObj').dialog('open');
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
			$.messager.alert('提示','只能操作一条数据','info');
	    return;
	}	
	url =url+id;
	$.ajax({
        type: "POST",
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

function unlock(){
			$("#uploadSubmit").attr("disabled", false);
}

</script>

</head>
<body> 
<table id="dg">
</table>
<div id="tb" style="padding:5px;height:auto">
	<form id="searchForm">
   	<div>
   		创建时间: <input class="easyui-datetimebox" data-options="showSeconds:true,editable:false" name="search.GED_createTime" style="width:150px" id="startTime">
   		至 <input class="easyui-datetimebox" data-options="showSeconds:true,editable:false" name="search.LED_createTime" style="width:150px" id="endTime">
   		标记：<select id="search_isDelete" name="search.EQI_isDelete"></select>
		<input type="button" onclick="search();" value="查询"/><input type="reset" onclick="searchReset();" value="清空"/>
	</div>
  	</form>         
</div>

<div id="viewObj" style="display:none;">
    <form id="viewForm">
    <table class="formTable" width="100%" style="padding:3px">
        <tbody>
            <tr>
                <td width="120px">${(cmodel.insideletter_id)!'ID'}</td>
                <td id="view_id"></td>
            </tr>
            <tr>
                <td>${(cmodel.insideletter_title)!'标题'}</td>
                <td id="view_title"></td>
            </tr>
            <tr>
                <td>${(cmodel.insideletter_iconPath)!'图片路径'}</td>
                <td id="view_iconPath"></td>
            </tr>
            <tr>
                <td>${(cmodel.insideletter_createTime)!'创建时间'}</td>
                <td id="view_createTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.insideletter_beginTime)!'活动开始'}</td>
                <td id="view_beginTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.insideletter_endTime)!'活动结束'}</td>
                <td id="view_endTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.insideletter_intro)!'简介'}</td>
                <td id="view_intro"></td>
            </tr>
            <tr>
                <td>${(cmodel.insideletter_content)!'详情'}</td>
                <td id="view_content"></td>
            </tr>
            <tr>
                <td>${(cmodel.insideletter_isDelete)!'删除标记'}</td>
                <td id="view_isDelete"></td>
            </tr>
            <tr >
                <td>${(cmodel.insideletter_linkUrl)!'活动链接'}</td>
                <td id="view_linkUrl"></td>
            </tr>
        </tbody>
    </table>
    </form>
</div>

<div id="uploadObj" style="display:none;">
    <form id="uploadForm" method="post" ENCTYPE= "multipart/form-data">
    <table class="formTable" width="100%" style="padding:3px">
        <tbody>
            <tr>
                <td>图片</td>
                <td>
                	<input type="file" id="txtFile" name="txtFile" />
                </td>
            </tr>
             <tr>
                <td>${(cmodel.insideletter_title)!'标题'}</td>
                <td><input type="text"  id="upload_title" name="title"  value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.insideletter_pushTime)!'推送开始'}</td>
                <td><input type="text" id="upload_pushTime" name="pushTime"  class="easyui-datetimebox" data-options="showSeconds:true,editable:false" /></td>
            </tr>
            <tr>
                <td>${(cmodel.insideletter_stopTime)!'推送结束'}</td>
                <td><input type="text" id="upload_stopTime" name="stopTime"  class="easyui-datetimebox" data-options="showSeconds:true,editable:false" /></td>
            </tr>
            <tr>
                <td>${(cmodel.insideletter_beginTime)!'活动开始'}</td>
                <td><input type="text" id="upload_beginTime" name="beginTime" class="easyui-datetimebox" data-options="showSeconds:true,editable:false" /></td>
            </tr>
            <tr>
                <td>${(cmodel.insideletter_endTime)!'活动结束'}</td>
                <td><input type="text" id="upload_endTime" name="endTime"  class="easyui-datetimebox" data-options="showSeconds:true,editable:false" /></td>
            </tr>
            <tr>
                <td>${(cmodel.insideletter_intro)!'简介'}</td>
                <td><input type="text" id="upload_intro" name="intro" style="width:400px"/></td>
            </tr>
            <tr>
                <td>${(cmodel.insideletter_content)!'详情'}</td>
                <td><input type="text" id="upload_content" name="content"  style="width:400px"/></td>
            </tr>
            <tr>
                <td>${(cmodel.insideletter_linkUrl)!'页面链接'}</td>
                <td><input type="text" id="upload_linkUrl" name="linkUrl"  style="width:400px"/></td>
            </tr>
        </tbody>
    </table>
    <table border="0" width="100%" id="formOperate">
    	<tr><td align="center"><input type="button" onclick="unlock()" value = "解锁"/><input type="button" id="uploadSubmit" value="确定"/><input type="reset"/></td></tr>
    	<tr><td align="center"><font color="red">※点击解锁可恢复确认键功能※</font></td></tr>
    </table>
    </form>
</div>

</body>
</html>
