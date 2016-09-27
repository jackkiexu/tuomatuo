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
	var checkId;
	searchInit("${webRoot}/stationLetter/search.do");     
	
	$("#search_statue").html("<option value='' selected>请选择</option>");
	$("#search_statue").append(getSelectList(stationLetter_status_map,""));
	
	$("#search_type").html("<option value='' selected>请选择</option>");
	$("#search_type").append(getSelectList(stationLetter_type_map,""));
	 
	 $("#type").change(function(){
	 	var value = $("#type").val();
	 	if(value==1){
	 		$("#fromNumber").attr("readonly","readonly");
	 		$("#fromNumber").val("");
	 	}else{
	 		$("#fromNumber").attr("readonly",false);
	 	}
	 });
	 $("#submitmoney").click(function(){
	 	submitStationLetter();
	 });
	     
})
function viewInitCombobox(obj,data){
	obj.find("#view_type").html(stationLetter_type_map[obj.find("#view_type").html()]);
	obj.find("#view_status").html(services_status_map[obj.find("#view_status").html()]);
}
function newInitCombobox(obj,data){
	obj.find("#type").html("<option value=>请选择</option>");
	obj.find("#type").append(getSelectList(stationLetter_type_map,""));
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
			{field:'id',title:'${(cmodel.stationLetter_id)!''}',sortable:true,
				formatter:function(value,row,index){return row.id;} 
			},
			{field:'createTime',title:'${(cmodel.stationLetter_createTime)!''}',sortable:true,
				formatter:function(value,row,index){return row.createTime;} 
			},
			{field:'content',title:'${(cmodel.stationLetter_content)!''}',sortable:true,width:240,
				formatter:function(value,row,index){return row.content;} 
			},
			{field:'type',title:'${(cmodel.stationLetter_type)!''}',sortable:true,
				formatter:function(value,row,index){return stationLetter_type_map[row.type];} 
			},
			{field:'status',title:'${(cmodel.stationLetter_status)!''}',sortable:true,
				formatter:function(value,row,index){return stationLetter_status_map[row.status];} 
			},
			{field:'fromUserId',title:'${(cmodel.stationLetter_fromUserId)!''}',sortable:true,
				formatter:function(value,row,index){if(row.fromUserId==0)return "密号助理";		return row.fromUserId;} 
			},
			{field:'fromNumber',title:'${(cmodel.stationLetter_fromNumber)!''}',sortable:true,
				formatter:function(value,row,index){return row.fromNumber;} 
			},
			{field:'toUserId',title:'${(cmodel.stationLetter_toUserId)!''}',sortable:true,
				formatter:function(value,row,index){if(row.toUserId==0)return "密号助理";	return row.toUserId;} 
			},
			{field:'toNumber',title:'${(cmodel.stationLetter_toNumber)!''}',sortable:true,
				formatter:function(value,row,index){return row.toNumber;} 
			},
			{field:'userIds',title:'目标用户',sortable:true,
				formatter:function(value,row,index){return row.userIds;} 
			},
			{field:'startTime',title:'生效时间',sortable:true,
				formatter:function(value,row,index){return row.startTime;} 
			},
			{field:'endTime',title:'失效时间',sortable:true,
				formatter:function(value,row,index){return row.endTime;} 
			},
			{field:'isDelete',title:'是否标记删除',sortable:true,
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
		      iconCls:'icon-add',
		      text:'发送',
		      disabled:noPermis("stationLetter","saveStation"),
		      handler:function(){
		 	     sendObj("发送站内信","${webRoot}/stationLetter/saveStation.do", 600, 400);
		      }
		},{
		      iconCls:'icon-info',
		      text:'详细',
		      disabled:noPermis("services","view"),
		      handler:function(){
		 	     viewStationLetterToOne("用户所有站内信","${webRoot}/stationLetter/stationLettersById.do?id=");
		      }
		},{
		      iconCls:'icon-delete',
		      text:'删除',
		      disabled:noPermis("stationLetter","delete"),
		      handler:function(){
		          removeObj("${webRoot}/stationLetter/delete.do");
		      }
		}]
	});
}
var tdErr;
function validation(obj){
var err = "";
var errId = "";
var content = $("#content").val();
//var startTime = $("#startTime").val();	//校验时间不能通过，我个人觉得easyUI应该是重写了一个什么来遮盖了原本的input，让我获取不到值
//var endTime = $("#endTime").val();
	if(!checkLength(content,1,200)){
		err = "${(cmodel.stationLetter_content)!''}不能少于1个字且不能超过200个字！";
		errId = "content";
	} else {
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


	function viewStationLetterToOne(name,url,width,height){
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
	        			var fromUserId = rows[i].fromUserId;
	        			var toUserId = rows[i].toUserId;
	        			if (fromUserId==0){
	        				fromUserId="密号助理";
	        			}
	        			if (toUserId==0){
	        				toUserId="密号助理";
	        			}
	        			trstr += "<tr class=trstr><td style=text-align:center>"+rows[i].id+"</td><td style=text-align:center>"
	        				+rows[i].createTime+"</td><td style=text-align:center;word-WRAP:break-word;>"
	        				+rows[i].content+"</td><td style=text-align:center>"
	        				+stationLetter_status_map[rows[i].status]+"</td><td style=text-align:center>"
	        				+fromUserId+"</td><td style=text-align:center>"
	        				+rows[i].fromNumber+"</td><td style=text-align:center>"
	        				+toUserId+"</td><td style=text-align:center>"
	        				+rows[i].toNumber+"</td><td style=text-align:center>"
	        				+rows[i].userIds+"</tr>";
	        		}
	        		$("#tablecontent").append(trstr);
	        		trstr=" ";
	        		$("#viewObjsforEveryCustome").dialog({
	    				title:name,
	    				top:100,
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
	
	function submitStationLetter(){
		var $textreason = $("#textreason");
		var textreason = $textreason.val();
		if(textreason.length > 0 && textreason.length <=500){
			$.ajax({
				url : '${webRoot}/stationLetter/save.do',
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
	function sendObj(name,action,width,height){
		if(width==undefined||width==''){
			width=500;
		}
		if(height==undefined||height==''){
			height=300;
		}
		$('#editObj').show();
		$("#editObj").dialog({
			title:'添加'+name,
			top:100,
			width:width,
			height:height,
			cache: false,
			closed: true,
		});
		var rows = $('#dg').datagrid('getSelections');
		var u = "";
		var size = 0;
		var ok = false;
		$.each(rows,function(i,n){
	    	size++;
	       	u += (n.fromUserId + ",");
	       	if (n.fromUserId == 0){
	       		ok = true;
	       	}
	    });
	    if (ok) {
	    	$.messager.alert('提示','不能给密号助理发送！','info');
	   		return;
	    }
	    $('#editForm')[0].reset();//不能放在if之后
	    if(size<=0){
	    	//如果什么都没有选择，说明是针对于所有用户发送消息
	    	$("#userIds").val("");
	    	$("#form_view_userIds").html("系统的所有用户");
	    } else {
	    	u = u.substring(0,u.length-1);
	    	$("#userIds").val(u);
	    	$("#form_view_userIds").html(u);
	    }
		
		$("#editSubmit").unbind();
		$("#editSubmit").click(function(){
			if ($.isFunction(window.validation)){
				var rs=validation($('#editForm'));
				if(rs==undefined||rs==false){
					return;
				}
			}
			$.ajax({
	            type: "post",
	            dataType: "json",
	            url: action,
	            data: $('#editForm').serializeArray(),
	            contentType: 'application/x-www-form-urlencoded; charset=utf-8', 
	            success: function(data){
	            	if(data!=undefined&&data.status==0){
	            		$('#editObj').dialog('close');
	            		$('#dg').datagrid('reload');
	            		$.messager.show({
	            			title:'发送'+name,
	            			msg:'发送成功！',
	            			timeout:5000,
	            			showType:'slide'
	            		});
	            	}else{
	            		returnError(data);
	            	}
	            }
			});
		});
		$('#editObj').dialog('open');	
		
	}
</script>	
</head>
  
<body>
<table id="dg">
</table>
<div id="tb" style="padding:5px;height:auto">
	<form id="searchForm">
   	<div>
	  ${(cmodel.stationLetter_content)!''}:<input type="text" name="search.LIKES_content"/>
	  ${(cmodel.stationLetter_fromUserId)!''}:<input type="text" name="search.EQI_fromUserId" />
	  ${(cmodel.stationLetter_toUserId)!''}:<input type="text" name="search.EQI_toUserId" />
	  ${(cmodel.stationLetter_type)!''}:<select id="search_type" name="search.EQS_type"></select>
	  ${(cmodel.stationLetter_status)!''}:<select id="search_statue" name="search.EQS_status"></select>
      <input type="button" onclick="search();" value="查询"/><input type="reset" value="清空"/>
      </div>
  	</form>         
</div>
<div id="editObj" style="display:none;">
    <form id="editForm" method="post">
    <table class="formTable" width="100%">
        <tbody>
        	<input type="hidden" id="userIds" name="userIds" value=""/>
        	<tr>
        		<td>针对用户</td><td id="form_view_userIds"></td>
        	<tr>
            <tr>
                <td>${(cmodel.stationLetter_content)!''}</td><td><textarea id="content" name="content" rows="3" value="" style="width:380px"></textarea></td>
            </tr>
           	<tr>
           		<td>站内信生效时间</td><td><input class="easyui-datetimebox" type="text" id="startTime" name="startTime" value="" ></input></td>
           	</tr>
           	<tr>
           		<td>站内信截止时间</td><td><input class="easyui-datetimebox" type="text" id="endTime" name="endTime" value="" /></td>
           	</tr>
        </tbody>
    </table>
    <table border="0" width="100%" id="formOperate">
    	<tr><td align="center"><input type="button" id="editSubmit" value="确定"/><input type="reset"/></td></tr>
    </table>
    </form>
</div>
<div id="viewObj" style="display:inline;">
    <form id="viewForm">
    <table class="formTable" width="100%">
        <tbody>
            <tr>
                <td width="10">${(cmodel.stationLetter_id)!''}</td><td id="view_id"></td>
            </tr>
            <tr>
                <td>${(cmodel.stationLetter_createTime)!''}</td><td id="view_createTime"></td>
            </tr>
            <tr>
                <td>${(cmodel.stationLetter_content)!''}</td><td id="view_content"></td>
            </tr>
            <tr>
                <td>${(cmodel.stationLetter_type)!''}</td><td id="view_type"></td>
            </tr>
            <tr>
                <td>${(cmodel.stationLetter_status)!''}</td><td id="view_status"></td>
            </tr>
            <tr>
                <td>${(cmodel.stationLetter_fromUserId)!''}</td><td id="view_fromUserId"></td>
            </tr>
            <tr>
                <td>${(cmodel.stationLetter_fromNumber)!''}</td><td id="view_fromNumber"></td>
            </tr>
            <tr>
                <td>${(cmodel.stationLetter_toUserId)!''}</td><td id="view_toUserId"></td>
            </tr>
            <tr>
                <td>${(cmodel.stationLetter_toNumber)!''}</td><td id="view_toNumber"></td>
            </tr>
            <tr>
                <td>${(cmodel.stationLetter_latestId)!''}</td><td id="view_latestId"></td>
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
				<th width="3%"  style="font-size:10px;font-weight:normal;text-align:center;">${(cmodel.stationLetter_id)!''}</th>
				<th width="7%"  style="font-size:10px;font-weight:normal;text-align:center;">${(cmodel.stationLetter_createTime)!''}</th>
				<th width="20%" style="font-size:10px;font-weight:normal;text-align:center;">${(cmodel.stationLetter_content)!''}</th>
				<th width="6%"  style="font-size:10px;font-weight:normal;text-align:center;">${(cmodel.stationLetter_status)!''}</th>	
				<th width="28px"  style="font-size:10px;font-weight:normal;text-align:center;">${(cmodel.stationLetter_fromUserId)!''}</th>
				<th width="10%" style="font-size:10px;font-weight:normal;text-align:center;">${(cmodel.stationLetter_fromNumber)!''}</th>
				<th width="6%"  style="font-size:10px;font-weight:normal;text-align:center;">${(cmodel.stationLetter_toUserId)!''}</th>
				<th width="10%" style="font-size:10px;font-weight:normal;text-align:center;">${(cmodel.stationLetter_toNumber)!''}</th>
				<th width="8%"  style="font-size:10px;font-weight:normal;text-align:center;">目标用户</th>
				
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
