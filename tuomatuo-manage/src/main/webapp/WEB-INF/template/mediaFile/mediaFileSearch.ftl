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
	searchInit("${webRoot}/mediaFile/search.do");     
	$("#search_createUser").html("<option value='' selected>请选择</option>");
	$("#search_createUser").append(getSelectList(whiteKeyWord_createUser_map,""));   
	
	$("#search_isDelete").html("<option value='' selected>请选择</option>");
	$("#search_isDelete").append(getSelectList(isDelete_map,""));     
	
})
function viewInitCombobox(obj,data){
	obj.find("#view_createUser").html(whiteKeyWord_createUser_map[obj.find("#view_createUser").html()]);
	obj.find("#view_isDelete").html(isDelete_map[obj.find("#view_isDelete").html()]);
}
function newInitCombobox(obj,data){
	obj.find("#createUser").html("<option value='' selected>请选择</option>");
	obj.find("#createUser").append(getSelectList(whiteKeyWord_createUser_map,data.status));
	
	obj.find("#isDelete").html("<option value='' selected>请选择</option>");
	obj.find("#isDelete").append(getSelectList(isDelete_map,data.status));
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
		{field:'id',title:'${(cmodel.mediaFile_id)!''}',sortable:true,
				formatter:function(value,row,index){return row.id;} 
			},
			{field:'fm',title:'${(cmodel.mediaFile_fm)!''}',sortable:true,
				formatter:function(value,row,index){return row.fm;} 
			},
			{field:'tm',title:'${(cmodel.mediaFile_tm)!''}',sortable:true,
				formatter:function(value,row,index){return row.tm;}
			},
			{field:'vm',title:'${(cmodel.mediaFile_vm)!''}',sortable:true,
				formatter:function(value,row,index){return row.vm;} 
			},
			{field:'readUrl',title:'${(cmodel.mediaFile_readUrl)!''}',sortable:true,
				formatter:function(value,row,index){return row.readUrl;}
			},
			{field:'callRecordId',title:'${(cmodel.mediaFile_callRecordId)!''}',sortable:true,
				formatter:function(value,row,index){return row.callRecordId;}
			},
			{field:'spId',title:'${(cmodel.mediaFile_spId)!''}',sortable:true,
				formatter:function(value,row,index){return row.spId;}
			},
			{field:'startTime',title:'${(cmodel.mediaFile_startTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.startTime;} 
			},
			{field:'endTime',title:'${(cmodel.mediaFile_endTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.endTime;}
			},
			{field:'tag',title:'${(cmodel.mediaFile_tag)!''}',sortable:true,
				formatter:function(value,row,index){return row.tag;} 
			},
			{field:'isDelete',title:'${(cmodel.mediaFile_isDelete)!''}',sortable:true,
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
		      disabled:noPermis("mediaFile","view"),
		      handler:function(){
		 	     viewObj("${(cmodel.mediaFile)!''}","${webRoot}/mediaFile/view.do?id=","","500");
		      }
		},{
		      iconCls:'icon-edit',
		      text:'标记删除',
		      disabled:noPermis("mediaFile","markDelete"),
		      handler:function(){
		         dealUserStatus("${(cmodel.mediaFile)!''}标记删除","${webRoot}/mediaFile/numberMarkDelete.do?id=");
		      }
		},{
		      iconCls:'icon-delete',
		      text:'删除',
		      disabled:noPermis("mediaFile","delete"),
		      handler:function(){
		 	     removeObj("${(cmodel.mediaFile)!''}白名单","${webRoot}/mediaFile/delete.do");
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

	
</script>	
</head>
  
<body>
<table id="dg">
</table>
<div id="tb" style="padding:5px;height:auto">
	<form id="searchForm">
   	<div>
      开始时间: <input class="easyui-datebox" name="search.GED_createTime" id="createTime" style="width:80px">
      结束时间: <input class="easyui-datebox" name="search.LTD_createTime" id="createTime" style="width:80px">
	  ${(cmodel.mediaFile_id)!''}:<input type="text" name="search.EQI_id"/>
	  ${(cmodel.mediaFile_spId)!''}:<input type="text" name="search.LIKES_spId"/>
      <input type="button" onclick="search();" value="查询"/><input type="reset" onclick="searchReset();" value="清空"/>
      </div>
  	</form>         
</div>
<div id="viewObj" style="display:none;">
    <form id="viewForm">
    <table class="formTable" width="100%">
        <tbody>
            <tr>
                <td>${(cmodel.mediaFile_id)!''}</td><td id="view_id"></td>
            </tr>
            <tr>
                <td>${(cmodel.mediaFile_fm)!''}</td><td id="view_fm"></td>
            </tr>
            <tr>
                <td>${(cmodel.mediaFile_tm)!''}</td><td id="view_tm"></td>
            </tr>
            <tr>
                <td>${(cmodel.mediaFile_vm)!''}</td><td id="view_vm"></td>
            </tr>
            <tr>
                <td>${(cmodel.mediaFile_readUrl)!''}</td><td id="view_readUrl"></td>
            </tr>
            <tr>
                <td>${(cmodel.mediaFile_callRecordId)!''}</td><td id="view_callRecordId"></td>
            </tr>
            <tr>
                <td>${(cmodel.mediaFile_spId)!''}</td><td id="view_spId"></td>
            </tr>
            <tr>
                <td>${(cmodel.mediaFile_startTime)!''}</td><td id="view_startTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.mediaFile_endTime)!''}</td><td id="view_endTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.mediaFile_tag)!''}</td><td id="view_tag"></td>
            </tr>
            <tr>
                <td>${(cmodel.mediaFile_isDelete)!''}</td><td id="view_isDelete"></td>
            </tr>
        </tbody>
    </table>
    </form>
</div>
</body>
</html>
