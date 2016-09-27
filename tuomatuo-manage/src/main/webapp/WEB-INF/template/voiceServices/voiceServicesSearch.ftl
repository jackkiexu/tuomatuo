<!DOCTYPE html>
<html>
<head>
<title>spring3</title>
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
#uploadFile{
	width:500px;
	height=350;
	
}
</style>
<script type="text/javascript">
//初始化
$(function(){
	searchInit("${webRoot}/voiceServices/search.do");   
	$("#search_status").html("<option value='' selected>请选择</option>");
	$("#search_status").append(getSelectList(voiceServices_status_map,""));   
	
	$("#search_type").html("<option value='' selected>请选择</option>");
	$("#search_type").append(getSelectList(voiceServices_type_map,""));
	      
	$("#search_platForm").html("<option value='' selected>请选择</option>");
	$("#search_platForm").append(getSelectList(voiceServices_platForm_map,""));  
	    
	$("#search_uploadUser").html("<option value='' selected>请选择</option>");
	$("#search_uploadUser").append(getSelectList(voiceServices_uploadUser_map,"")); 
	
	$("#search_isDelete").html("<option value='' selected>请选择</option>");
	$("#search_isDelete").append(getSelectList(isDelete_map,""));
	  
})
function viewInitCombobox(obj,data){
	obj.find("#view_status").html(voiceServices_status_map[obj.find("#view_status").html()]);
	obj.find("#view_type").html(voiceServices_type_map[obj.find("#view_type").html()]);
	obj.find("#view_isDelete").html(isDelete_map[obj.find("#view_isDelete").html()]);
	obj.find("#view_uploadUser").html(voiceServices_uploadUser_map[obj.find("#view_uploadUser").html()]);
	obj.find("#view_platForm").html(voiceServices_platForm_map[obj.find("#view_platForm").html()]);
}
function newInitCombobox(obj,data){
	obj.find("#status").html("<option value='' selected>请选择</option>");
	obj.find("#status").append(getSelectList(voiceServices_status_map,data.status));
	
	obj.find("#type").html("<option value='' selected>请选择</option>");
	obj.find("#type").append(getSelectList(voiceServices_type_map,data.status));
	
	obj.find("#platForm").html("<option value='' selected>请选择</option>");
	obj.find("#platForm").append(getSelectList(voiceServices_platForm_map,data.status));
	
	obj.find("#uploadUser").html("<option value='' selected>请选择</option>");
	obj.find("#uploadUser").append(getSelectList(voiceServices_uploadUser_map,data.status));
	
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
		columns:[[
			{field:'ck',checkbox:true,width:2},
			{field:'id',title:'${(cmodel.voiceServices_id)!''}',sortable:true,
				formatter:function(value,row,index){return row.id;} 
			},
			{field:'userId',title:'${(cmodel.voiceServices_soundName)!''}',sortable:true,
				formatter:function(value,row,index){return row.soundName;} 
			},
			{field:'memoryAddress',title:'${(cmodel.voiceServices_memoryAddress)!''}',sortable:true,
				formatter:function(value,row,index){return row.memoryAddress;} 
			},
			{field:'playAddress',title:'${(cmodel.voiceServices_playAddress)!''}',sortable:true,width:200,
				formatter:function(value,row,index){return row.playAddress;} 
			},
			{field:'uploadUser',title:'${(cmodel.voiceServices_uploadUser)!''}',sortable:true,
				formatter:function(value,row,index){return voiceServices_uploadUser_map[row.uploadUser];}
			},
			{field:'createTime',title:'${(cmodel.voiceServices_createTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.createTime;} 
			},
			{field:'platForm',title:'${(cmodel.voiceServices_platForm)!''}',sortable:true,
				formatter:function(value,row,index){return voiceServices_platForm_map[row.platForm];} 
			},
			{field:'status',title:'${(cmodel.voiceServices_status)!''}',sortable:true,
				formatter:function(value,row,index){return voiceServices_status_map[row.status];} 
			},
			{field:'type',title:'${(cmodel.voiceServices_type)!''}',sortable:true,
				formatter:function(value,row,index){return voiceServices_type_map[row.type];} 
			},
			{field:'isDelete',title:'${(cmodel.voiceServices_isDelete)!''}',sortable:true,
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
		      disabled:noPermis("voiceServices","view"),
		      handler:function(){
		 	     viewObj("${(cmodel.voiceServices)!''}","${webRoot}/voiceServices/view.do?id=","","400");
		      }
		},{
		      iconCls:'icon-edit',
		      text:'审核',
		      disabled:noPermis("voiceServices","examineVoice"),
		      handler:function(){
		          editObj("${(cmodel.voiceServices)!''}","${webRoot}/voiceServices/view.do?id=","${webRoot}/voiceServices/examineVoice.do");
		     	  $(".isDelete").hide();
		      }
		},{
		      iconCls:'icon-delete',
		      text:'删除',
		      disabled:noPermis("voiceServices","delete"),
		      handler:function(){
		          removeObj("${webRoot}/voiceServices/delete.do");
		      }
		},{
		      iconCls:'icon-add',
		      text:'添加',
		      disabled:noPermis("voiceServices","addsound"),
		      handler:function(){
		 	  uploadObj("${(cmodel.voiceServices)!''}语音","${webRoot}/voiceServices/addsound.do");
		    }
		},{
		      iconCls:'icon-set',
		      text:'添加Token',
		      disabled:noPermis("voiceServices","addToken"),
		      handler:function(){
		 	  saveTokenObj("Token添加","${webRoot}/voiceServices/addToken.do");
		      }
		}]
	});
}

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
	$("#uploadForm").find("#upload_type").html("<option value='' selected>请选择</option>");
	$("#uploadForm").find("#upload_type").append(getSelectList(voiceServices_type_map,""));
	$("#uploadForm").find("#upload_platForm").html("<option value='' selected>请选择</option>");
	$("#uploadForm").find("#upload_platForm").append(getSelectList(voiceServices_platForm_map,""));
	$("#uploadForm").find("#upload_uploadUser").html("<option value='' selected>请选择</option>");
	$("#uploadForm").find("#upload_uploadUser").append(getSelectList(voiceServices_uploadUser_map,""));
	
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




function saveTokenObj(name,action,width,height){
	if(width==undefined||width==''){
		width=500;
	}
	if(height==undefined||height==''){
		height=350;
	}
	$('#saveTokenObj').show();
	$("#saveTokenObj").dialog({
		title:'添加'+name,
		top:100,
		width:width,
		height:height,
		cache: false,
		closed: true,
	});
	$('#saveTokenForm')[0].reset();
	$("#saveTokenSubmit").unbind();
	$("#saveTokenSubmit").click(function(){
		$.ajax({
            type: "post",
            dataType: "json",
            url: action,
            data: $('#saveTokenForm').serializeArray(),
            contentType: 'application/x-www-form-urlencoded; charset=utf-8', 
            success: function(data){
            	if(data!=undefined&&data.result==true){
            		$('#saveTokenObj').dialog('close');
            		$.messager.show({
            			title:'添加'+name,
            			msg:'添加成功！',
            			timeout:5000,
            			showType:'slide'
            		});
            	}else{
            		returnError(data);
            	}
            }
		});
	});
	$('#saveTokenObj').dialog('open');	
}




var tdErr;
function validation(obj){
var err = "";
var errId = "";
var platForm=$("platForm").val();
var soundName = $("#soundName").val();
	if(isNull(soundName)){
		err = "${(cmodel.voiceServices_soundName)!''}不能为空！";
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
<script type="text/javascript"> 
		var clickTimes = 0;
		var nn = 500; 
		var tipId; 
		function show() { 
			if(clickTimes != 0) return;
			clickTimes++;
			tipId = window.setInterval("start()", 10); //每隔10毫秒调用一次start()方法 (1000毫秒=1秒)
			}
        function start() { 
				var vv = "确定(" + Math.round(nn/100) + ")"; 
				if (nn > 0) {
					$("#uploadSubmit").attr("disabled", "disabled"); //使按钮不能被点击 
					$("#uploadSubmit").attr("value", vv);  
					nn--; 
				} else { 
					nn = 500; 
					$("#uploadSubmit").removeAttr("disabled"); //使按钮能够被点击 
					$("#uploadSubmit").attr("value", "确定"); 
					window.clearInterval(tipId); //清除循环事件 
					clickTimes--;
				} 
			}
			
</script> 
<script type="text/javascript"> 
		var clickCount = 0;	
		var mm = 500; 
		var timeclick; 
		function editShow() { 
		if(clickTimes != 0) return;
		clickCount++;
		timeclick = window.setInterval("begin()", 10); //每隔10毫秒调用一次start()方法 
			}
	function begin(){ 
		var nval = "确定(" + Math.round(mm/100) + ")"; 
		if (mm > 0) {
			$("#editSubmit").attr("disabled", "disabled"); //使按钮不能被点击 
			$("#editSubmit").attr("value", nval);  
			mm--; 
		} else { 
			mm = 500; 
			$("#editSubmit").removeAttr("disabled"); //使按钮能够被点击 
			$("#editSubmit").attr("value", "确定"); 
			window.clearInterval(timeclick); //清除循环事件 
			clickCount--;
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
      开始时间: <input class="easyui-datebox" name="search.GED_createTime" style="width:80px" id="startTime">
	  ${(cmodel.messageRecord_userId)!''}:<input type="text" name="search.LIKES_userId"/>
	  ${(cmodel.voiceServices_platForm)!''}:<select id="search_platForm" name="search.EQS_platForm"></select>
	  <br/>
    结束时间: <input class="easyui-datebox" name="search.LTD_createTime" style="width:80px" id="endTime">
	  ${(cmodel.voiceServices_soundName)!''}:<input type="text" name="search.LIKES_content"/>
	  ${(cmodel.voiceServices_status)!''}:<select id="search_status" name="search.EQI_status"></select>
      <input type="button" onclick="search();" value="查询"/><input type="reset" onclick="searchReset();" value="清空"/>
      </div>
  	</form>         
</div>
<div id="editObj" style="display:none;">
    <form id="editForm" method="post">
    <table class="formTable" width="100%" style="padding:3px">
        <tbody>
            <tr class="id">
                <td>${(cmodel.voiceServices_id)!''}</td><td><input readonly="readonly" id="id" name="id" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.voiceServices_soundName)!''}</td><td><input type="text"  readonly="readonly" id="soundName" name="soundName" readonly="readonly" value=""/></td>
            </tr>
            <tr >
                <td>${(cmodel.voiceServices_memoryAddress)!''}</td><td><input readonly="readonly" id="memoryAddress" name="memoryAddress" value=""/></td>
            </tr>
            <tr >
                <td>${(cmodel.voiceServices_playAddress)!''}</td><td><input readonly="readonly" id="playAddress" name="playAddress" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.voiceServices_status)!''}</td><td><select id="status" name="status" /></td>
            </tr>
            <tr>
                <td>${(cmodel.voiceServices_uploadUser)!''}</td><td><select id="uploadUser" name="uploadUser" /></td>
            </tr>
            <tr>
                <td>${(cmodel.voiceServices_type)!''}</td><td><select  id="type" name="type" /></td>
            </tr>
            <tr>
                <td>${(cmodel.voiceServices_platForm)!''}</td><td><select  id="platForm" name="platForm"/></td>
            </tr>
        </tbody>
    </table>
    <table border="0" width="100%" id="formOperate">
    	<tr><td align="center"><input type="button" id="editSubmit" value="确定" onclick="editShow()"/><input type="reset"/></td></tr>
    	<tr><td align="center"><font color="red">※审核需要上传第三方服务器过程较慢，请勿双击鼠标提交※</font></td></tr>
    </table>
    </form>
</div>
<div id="viewObj" style="display:none;">
    <form id="viewForm">
    <table class="formTable" width="100%" style="padding:3px">
        <tbody>
            <tr>
                <td width="120px">${(cmodel.voiceServices_id)!''}</td><td id="view_id"></td>
            </tr>
            <tr>
                <td>${(cmodel.voiceServices_soundName)!''}</td><td id="view_soundName"></td>
            </tr>
            <tr>
                <td>${(cmodel.voiceServices_memoryAddress)!''}</td><td id="view_memoryAddress"></td>
            </tr>
            <tr>
                <td>${(cmodel.voiceServices_playAddress)!''}</td><td id="view_playAddress"></td>
            </tr>
            <tr>
                <td>${(cmodel.voiceServices_uploadUser)!''}</td><td id="view_uploadUser"></td>
            </tr>
            <tr>
                <td>${(cmodel.voiceServices_createTime)!''}</td><td id="view_createTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.voiceServices_platForm)!''}</td><td id="view_platForm"></td>
            </tr>
            <tr>
                <td>${(cmodel.voiceServices_status)!''}</td><td id="view_status"></td>
            </tr>
            <tr>
                <td>${(cmodel.voiceServices_type)!''}</td><td id="view_type"></td>
            </tr>
            <tr >
                <td>${(cmodel.voiceServices_isDelete)!''}</td><td id="view_isDelete"></td>
            </tr>
        </tbody>
    </table>
    </form>
</div>
<div id="uploadObj" style="display:none;">
    <form id="uploadForm" method="post" ENCTYPE= "multipart/form-data">
    <table class="formTable" width="100%">
        <tbody>
            <tr>
                <td>文件地址</td>
                <td>
                	<input type="file" id="txtFile" name="txtFile" />
                </td>
            </tr>
             <tr>
                <td>${(cmodel.voiceServices_soundName)!''}</td><td><input type="text"  id="upload_soundName" name="soundName"  value=""/></td>
            </tr>
             <tr>
                <td>${(cmodel.voiceServices_uploadUser)!''}</td><td><select  id="upload_uploadUser" name="uploadUser"/></td>
            </tr>
            <tr>
                <td>${(cmodel.voiceServices_platForm)!''}</td><td><select  id="upload_platForm" name="platForm"/></td>
            </tr>
             <tr>
                <td>${(cmodel.voiceServices_type)!''}</td><td><select " id="upload_type" name="type" /></td>
            </tr>
        </tbody>
    </table>
    <table border="0" width="100%" id="formOperate">
    	<tr><td align="center"><input type="button" id="uploadSubmit" value="确定" onclick="show()"  /><input type="reset"/></td></tr>
    	<tr><td align="center"><font color="red">※ROOT权限提交需上传第三方服务器过程较慢，请勿双击鼠标提交※</font></td></tr>
    </table>
    </form>
</div>
<div id="saveTokenObj" style="display:none;">
    <form id="saveTokenForm" method="post" ENCTYPE= "multipart/form-data">
    <table class="formTable" width="100%">
        <tbody>
     	   <tr>
                <td>Token：</td><td><input type="text"  id="token" name="token"  value=""/></td>
            </tr>
             <tr>
                <td>到期时间：</td><td><input type="text"  id="endTime" name="endTime"  value=""/></td>
            </tr>
        </tbody>
    </table>
    <table border="0" width="100%" id="formOperate">
    	<tr><td align="center"><input type="button" id="saveTokenSubmit" value="确定" onclick="show()"  /><input type="reset"/></td></tr>
    	<tr><td align="center"><font color="red">※Token填写时请注意填写到期时间※</font></td></tr>
    </table>
    </form>
</div>
</body>
</html>
