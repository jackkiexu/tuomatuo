
<!DOCTYPE html>
<html>
<head>
<title>spring3</title>
<meta charset="UTF-8">
<link href="/css/themes/default/easyui.css" rel="stylesheet"
	<link rel="stylesheet" type="text/css" href="/css/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="/css/themes/icon.css">
	<script type="text/javascript" src="/js/jquery.min.js"></script>
	<script type="text/javascript" src="/js/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="/js/easyui-lang-zh_CN.js"></script>
<script>
document.write("<s"+"cript type='text/javascript' src='/js/index.js?"+Math.random()+"'></scr"+"ipt>");
document.write("<s"+"cript type='text/javascript' src='/js/tools.js?"+Math.random()+"'></scr"+"ipt>");
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
	searchInit("/messageContainWords/search.do");   
	$("#search_sendStatus").html("<option value='' selected>请选择</option>");
	$("#search_sendStatus").append(getSelectList(messageRecord_sendStatus_map,""));   
	$("#search_receiveStatus").html("<option value='' selected>请选择</option>");
	$("#search_receiveStatus").append(getSelectList(messageRecord_receiveStatus_map,""));      
	$("#search_messageType").html("<option value='' selected>请选择</option>");
	$("#search_messageType").append(getSelectList(messageRecord_messageType_map,""));      
})
function newInitCombobox(obj,data){
}
function viewInitCombobox(obj,data){
	obj.find("#view_sendStatus").html(messageRecord_sendStatus_map[obj.find("#view_sendStatus").html()]);
	obj.find("#view_receiveStatus").html(messageRecord_receiveStatus_map[obj.find("#view_receiveStatus").html()]);
	obj.find("#view_isDelete").html(isDelete_map[obj.find("#view_isDelete").html()]);
	obj.find("#view_messageType").html(messageRecord_messageType_map[obj.find("#view_messageType").html()]);
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
			{field:'id',title:'${(cmodel.messageContainWords_id)!''}',sortable:true,
				formatter:function(value,row,index){return row.id;} 
			},
			{field:'userId',title:'${(cmodel.messageContainWords_userId)!''}',sortable:true,
				formatter:function(value,row,index){return row.userId;} 
			},
			{field:'fromNumber',title:'${(cmodel.messageContainWords_fromNumber)!''}',sortable:true,
				formatter:function(value,row,index){return row.fromNumber;} 
			},
			{field:'toNumber',title:'${(cmodel.messageContainWords_toNumber)!''}',sortable:true,
				formatter:function(value,row,index){return row.toNumber;} 
			},
			{field:'content',title:'${(cmodel.messageContainWords_content)!''}',sortable:true,width:200,
				formatter:function(value,row,index){return row.content;} 
			},
			{field:'createTime',title:'${(cmodel.messageContainWords_createTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.createTime;} 
			},
			{field:'messageType',title:'${(cmodel.messageContainWords_messageType)!''}',sortable:true,
				formatter:function(value,row,index){return messageRecord_messageType_map[row.messageType];} 
			},
			{field:'sendStatus',title:'${(cmodel.messageContainWords_sendStatus)!''}',sortable:true,
				formatter:function(value,row,index){return messageRecord_sendStatus_map[row.sendStatus];} 
			},
			{field:'receiveStatus',title:'${(cmodel.messageContainWords_receiveStatus)!''}',sortable:true,
				formatter:function(value,row,index){return messageRecord_receiveStatus_map[row.receiveStatus];} 
			},
			{field:'errorMsg',title:'${(cmodel.messageContainWords_errorMsg)!''}',sortable:true,
				formatter:function(value,row,index){return row.errorMsg;} 
			},
			{field:'isDelete',title:'${(cmodel.messageContainWords_isDelete)!''}',sortable:true,
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
		      disabled:noPermis("messageContainWords","view"),
		      handler:function(){
		 	     viewObj("${(cmodel.messageContainWords)!''}","/messageContainWords/view.do?id=","","400");
		      }
		},{
		      iconCls:'icon-lock',
		      text:'锁定帐号',
		      disabled:noPermis("messageContainWords","lock"),
		      handler:function(){
		 	     dealUserStatus("${(cmodel.messageContainWords)!''}锁定","/messageContainWords/lock.do?id=");
		      }
		},{
		      iconCls:'icon-unlock',
		      text:'解锁帐号',
		      disabled:noPermis("messageContainWords","unlock"),
		      handler:function(){
		          dealUserStatus("${(cmodel.messageContainWords)!''}解锁","/messageContainWords/unlock.do?id=");
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
var userId = $("#userId").val();
var fromNumber = $("#fromNumber").val();
var toNumber = $("#toNumber").val();
var content = $("#content").val();
var createTime = $("#createTime").val();
var updateTime = $("#updateTime").val();
var sendStatus = $("#sendStatus").val();
var receiveStatus = $("#receiveStatus").val();
var isDelete = $("#isDelete").val();
var errorMsg = $("#errorMsg").val();
	if(isNull(userId)){
		err = "用户序号不能为空！";
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
      开始时间: <input class="easyui-datetimebox" name="search.GED_createTime" style="width:80px" id="startTime">
	 ${(cmodel.messageContainWords_userId)!''}:<input type="text" name="search.LIKES_userId"/>
	  ${(cmodel.messageContainWords_fromNumber)!''}:<input type="text" name="search.LIKES_fromNumber"/>
	 ${(cmodel.messageContainWords_sendStatus)!''}:<select id="search_sendStatus" name="search.EQI_sendStatus"></select>
	  ${(cmodel.messageContainWords_messageType)!''}:<select id="search_messageType" name="search.EQI_messageType"></select>
	  <br/>
	  结束时间: <input class="easyui-datetimebox" name="search.LTD_createTime" style="width:80px" id="endTime">
	 ${(cmodel.messageContainWords_content)!''}:<input type="text" name="search.LIKES_content"/>
	 ${(cmodel.messageContainWords_toNumber)!''}:<input type="text" name="search.LIKES_toNumber"/>
	 ${(cmodel.messageContainWords_receiveStatus)!''}:<select id="search_receiveStatus" name="search.EQS_receiveStatus"></select>
      <input type="button" onclick="search();" value="查询"/><input type="reset" onclick="searchReset();" value="清空"/>
      </div>
  	</form>         
</div>
<div id="editObj" style="display:none;">
    <form id="editForm" method="post">
    <table class="formTable" width="100%" style="padding:3px">
        <tbody>
			<input type="hidden" id="id" name="id" value=""/>
			 <tr>
                <td>关键字：</td><td><input type="text" id="keyWord" name="keyWord" value=""/></td>
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
    <table class="formTable" width="100%" style="padding:3px">
        <tbody>
            <tr>
                <td width="120px">${(cmodel.messageContainWords_id)!''}</td><td id="view_id"></td>
            </tr>
            <tr>
                <td>${(cmodel.messageContainWords_userId)!''}</td><td id="view_userId"></td>
            </tr>
            <tr>
                <td>${(cmodel.messageContainWords_fromNumber)!''}</td><td id="view_fromNumber"></td>
            </tr>
            <tr>
                <td>${(cmodel.messageContainWords_toNumber)!''}</td><td id="view_toNumber"></td>
            </tr>
            <tr>
                <td>${(cmodel.messageContainWords_content)!''}</td><td id="view_content"></td>
            </tr>
            <tr>
                <td>${(cmodel.messageContainWords_createTime)!''}</td><td id="view_createTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.messageContainWords_messageType)!''}</td><td id="view_messageType"></td>
            </tr>
            <tr>
                <td>${(cmodel.messageContainWords_sendStatus)!''}</td><td id="view_sendStatus"></td>
            </tr>
            <tr>
                <td>${(cmodel.messageContainWords_receiveStatus)!''}</td><td id="view_receiveStatus"></td>
            </tr>
            <tr>
                <td>${(cmodel.messageContainWords_isDelete)!''}</td><td id="view_isDelete"></td>
            </tr>
            <tr>
                <td>${(cmodel.messageContainWords_errorMsg)!''}</td><td id="view_errorMsg"></td>
            </tr>
        </tbody>
    </table>
    </form>
</div>
</body>
</html>
