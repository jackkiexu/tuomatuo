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
	searchInit("${webRoot}/comments/search.do");   
	 $("#submitmoney").click(function(){
	 	submitStationLetter();
	 });
})
function viewInitCombobox(obj,data){
}
function newInitCombobox(obj,data){
}
function editInitCombobox(obj,data){
	//newInitCombobox(obj,data);
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
			{field:'id',title:'${(cmodel.comments_id)!''}',sortable:true,
				formatter:function(value,row,index){return row.id;} 
			},
			{field:'userId',title:'${(cmodel.comments_userId)!''}',sortable:true,
				formatter:function(value,row,index){return row.userId;} 
			},
			{field:'missCount',title:'${(cmodel.comments_missCount)!''}',sortable:true,
				styler:function(value,row,index){if(value>0) {return 'color:red;';}else {return 'color:black;';}} 
			},
			{field:'mobile',title:'${(cmodel.comments_mobile)!''}',sortable:true,
				formatter:function(value,row,index){return row.mobile;} 
			},
			{field:'createTime',title:'${(cmodel.comments_createTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.createTime;} 
			},
			{field:'updateTime',title:'${(cmodel.comments_updateTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.updateTime;} 
			},
			{field:'content',title:'${(cmodel.comments_content)!''}',width:200,sortable:true,
				formatter:function(value,row,index){return row.content;} 
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
		      iconCls:'icon-search',
		      text:'详细',
		      disabled:noPermis("comments","view"),
		      handler:function(){
		 	     viewCommentsToOne("${(cmodel.comments)!''}","${webRoot}/comments/commentsById.do?id=");
		      }
		},{
		      iconCls:'icon-delete',
		      text:'删除',
		      disabled:noPermis("comments","delete"),
		      handler:function(){
		          removeObj("${webRoot}/comments/delete.do");
		      }
		},{
		      iconCls:'icon-add',
		      text:'信息推送',
		      disabled:noPermis("comments","forUserMessage"),
		      handler:function(){
		 	     newObj("消息推送","${webRoot}/comments/forUserMessage.do");
		 	      $(".mobile").hide();
		 	      $(".createTime").hide();
		      }
		},]
	});
}
var tdErr;
var checkId;
function validation(obj){
var err = "";
var errId = "";
var userId = $("#userId").val();
var content = $("#content").val();
var mobile = $("#mobile").val();
var createTime = $("#createTime").val();
	if(isNull(userId)){
		err = "${(cmodel.comments_userId)!''}不能为空！";
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

function commentsSender(id){
	if(id == 0){
	return "管理员";
	}
	return id;
}

function submitStationLetter(){
		var $textreason = $("#textreason");
		var textreason = $textreason.val();
		if(textreason.length > 0 && textreason.length <=500){
			$.ajax({
				url : '${webRoot}/comments/save.do',
				type : 'post',
				data : {
					'id':checkId,
					'content':textreason
				},
				dataType : 'json',
				success : function(data){
					if(data.status==0){
						location.reload();
					} else {
						alert(data.msg);
					}
				}
			});

		}
		
	}

function viewCommentsToOne(name,url,width,height){
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
			size=i;
		});
		if(size>0){
			$.messager.alert('提示','请选择一条需要查看的记录！','info');
		    return;
		}
		checkId = id;
		url = url+id;
		$.ajax({
	        type: "get",
	        dataType: "json",
	        url: url,
	        contentType: 'application/x-www-form-urlencoded; charset=utf-8', 
	        success: function(data){
	        	if(data!=undefined&data.rows!=undefined){
	        		var rows = data.rows;
	        		var trstr = "";
	        		$(".trstr").remove();
	        		for(var i = 0; i < rows.length; i++){
	        			trstr += "<tr class=trstr><td style=text-align:center>"+rows[i].id+"</td><td style=text-align:center>"
	        				+rows[i].createTime+"</td><td style=text-align:center;word-WRAP:break-word;>"
	        				+rows[i].content+"</td><td style=text-align:center>"
	        				+commentsSender(rows[i].receivedId)+"</td><td style=text-align:center>"
	        				+rows[i].userId+"</tr>";
	        		}
	        		$("#tablecontent").append(trstr);
	        		trstr=" ";
	        		$("#viewObjsforEveryCustome").dialog({
	    				title:name,
	    				top:0,
	    				width:800,
	    				height:500,
	    				cache: false,
	    				closed: true,
	    			});
					$('#viewObjsforEveryCustome').dialog('open');
	    		}else{
	    			alert("error");
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
      开始时间: <input class="easyui-datebox" name="search.GED_createTime" id="startTime" style="width:80px">
      结束时间: <input class="easyui-datebox" name="search.LTD_createTime" id="endTime" style="width:80px">
	  ${(cmodel.comments_userId)!''}:<input type="text" name="search.EQS_userId"/>
	  ${(cmodel.comments_mobile)!''}:<input type="text" name="search.EQS_mobile"/>
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
                <td>${(cmodel.comments_userId)!''}</td><td><input type="text" id="userId" name="userId" value=""/></td>
            </tr>
            <tr>
                <td>${(cmodel.comments_content)!''}</td><td>
                <textarea  type="text"id="content"  name="content"  value=""style="font-size: 12px;margin-left:0px;width: 440px;height: 33px;"></textarea></td>
            </tr>
            <tr class="mobile">
                <td>${(cmodel.comments_mobile)!''}</td><td><input type="text" id="mobile" name="mobile" value=""/></td>
            </tr>
            <tr class="createTime">
                <td>${(cmodel.comments_createTime)!''}</td><td><input type="text" id="createTime" name="createTime" value=""/></td>
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
                <td>${(cmodel.comments_id)!''}</td><td id="view_id"></td>
            </tr>
            <tr>
                <td>${(cmodel.comments_userId)!''}</td><td id="view_userId"></td>
            </tr>
            <tr>
                <td>${(cmodel.comments_missCount)!''}</td><td id="view_missCount"></td>
            </tr>
           
            <tr>
                <td>${(cmodel.comments_mobile)!''}</td><td id="view_mobile"></td>
            </tr>
            <tr>
                <td>${(cmodel.comments_createTime)!''}</td><td id="view_createTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.comments_updateTime)!''}</td><td id="view_updateTime"></td>
            </tr>
             <tr>
                <td>${(cmodel.comments_content)!''}</td><td id="view_content"></td>
            </tr>
        </tbody>
    </table>
    </form>
</div>

<div id="viewObjsforEveryCustome" style="display:inline;font-size: 12px">
    <form id="viewForm">
    	<div style="overflow-x: auto; overflow-y: auto; height: 360px; width:786px;">
	    <table class="formTable" width="100%" style="font-size:10px;font-weight:normal;TABLE-LAYOUT: fixed;" id="tablecontent">
	        <tr style="font-size:10px;font-weight:normal;">
				<th width="6%"  style="font-size:10px;font-weight:normal;text-align:center;">${(cmodel.comments_id)!''}</th>
				<th width="15%"  style="font-size:10px;font-weight:normal;text-align:center;">${(cmodel.comments_createTime)!''}</th>
				<th width="50%" style="font-size:10px;font-weight:normal;text-align:center;">${(cmodel.comments_content)!''}</th>
				<th width="8%"  style="font-size:10px;font-weight:normal;text-align:center;">${(cmodel.comments_receivedId)!''}</th>	
				<th width="10%"  style="font-size:10px;font-weight:normal;text-align:center;">${(cmodel.comments_userId)!''}</th>
			</tr>
	        
	    </table>
	    </div>
	    <p>
			<label style="font-size: 12px;margin-left:50px">进行此操作原因 :</label><input type="button" class="chonzhisubmit"  value="提  交" style="margin-left:310px" id="submitmoney" /> </br>
			<textarea  id="textreason" style="font-size: 12px;margin-left:50px;width: 440px;height: 33px;"></textarea>
		</p>
	    
    </form>
</div>
</body>
</html>
