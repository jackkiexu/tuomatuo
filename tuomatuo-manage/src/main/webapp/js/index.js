//global param
var isDelete_map={"0":"未删除","1":"已删除"};
var permission_menu_map={"-1":"标签","0":"非导航","1":"导航"};
var user_status_map={"0":"初始冻结","1":"正常","2":"拉黑","3":"注销"};
var user_fromType_map={"1":"web注册用户","2":"android注册用户","3":"wap注册用户","4":"iphone注册用户","5":"测试用户","6":"微信注册用户","7":"管理平台新增用户"};
var numbers_status_map={"0":"无效","1":"有效","2":"选择未预订","3":"选择已预订"};
var numbers_operator_map={"1":"中国电信"};
var numbers_area_map={"1":"上海"};
var numbers_effect_map=service_effect_map={"1":"收发短信，接听电话","2":"收发短信，接拨电话","3":"仅接收语音指令"};
var numbers_type_map={"0":"返回给电信的号码","1":"测试投放","2":"正式投放"};
var messageRecord_sendStatus_map={"0":"初始","1":"成功","2":"失败"};
var messageRecord_receiveStatus_map={"0":"初始","1":"成功","2":"失败"};
var messageRecord_messageType_map={"1":"上行短信","2":"下行短信"};
var services_type_map={"1":"基础服务","2":"增值服务"};
var services_from_flag={"1":"Android/Web 服务","2":"WAP 服务","3":"微信 服务","4":"app 服务","5":"管理平台","6":"Ios服务","9":"ios 和 android共用服务","10":"内部匿名VIP服务"};
var services_status_map=userService_status_map={"0":"无效","1":"有效"};
var services_flag_map=numbers_flag_map={"0":"免费","1":"收费","2":"管理员手工投放","3":"商户专属号码","4":"短信密号","5":"电话直播密号","6":"短信直接接收"};
var services_platfrom_flag={"1":"android","2":"wap","3":"微信","4":"app", "5":"管理平台", "6":"ios","7":"android and weixin", "8":"MCode", "9":"ios and android", "10":"mhao 内部匿名服务", "11":"只针对老版本的App,新版本中将不再出现此服务","12":"mhao过期无效服务"};
var operator_status_map={"0":"正常","1":"锁定"};
var userNumber_status_map={"0":"无效","1":"有效","2":"预定，但未支付"};
var userNumber_currStatus_map={"0":"关机","1":"开机"};
var chargeRecord_status_map={"0":"初始化","1":"成功","2":"失败"};
var chargeRecord_type_map={"1":"支付宝","2":"网银","3":"人工代充","4":"卡密","5":"免费体验","6":"ios 内部支付","7":"微信用户申请免费","8":"微信用户申请","9":"android 用户申请免费","10":"android 用户申请付费","11":"管理平台进行免费增加服务","12":"用户通过 M 码免费进行服务购买","13":"用户通过 android 平台进行免费服务购买","14":"用户通过 android 平台进行支付宝支付","15":"用户通过 ios 进行免费支付","16":"用户通过 ios 的 iap 进行支付","17":"用户通过 weixin 进行免费支付","18":"用户通过 weixin 进行微信支付购买服务"};
var person_Issued_Flag_map={"1":"天","2":"月","3":"年"};
var userServices_status_map={"0":"初始化","1":"成功"};
var userNumber_isRedirect_map={"0":"未","1":"已"};
var user_sendDelayMsgFlag_map={"0":"未发送","1":"发送成功","-1":"发送失败"};
var stationLetter_type_map={"1":"全部用户","2":"部分用户", "3":"单个用户"};
//var stationLetter_status_map={"0":"无效","1":"有效","2":"密号公共提示语","3":"公告"};
var stationLetter_status_map={"0":"无效","1":"有效"};
var codes_status_map={"0":"初始化","1":"有效", "2":"无效,或支付订单过期", "3":"已使用"};
var codes_serviceId_map={"1":"新手体验","2":"7天计划","3":"30天计划","32":"电话直播","42":"短信直接接收","46":"30天直接呼出服务","45":"180天服务"};
var mobile_pool_map={"1":"6小时新手体验","2":"3天计划","3":"收费计划","4":"精选号码",};
var numbers_sms_channel_map={"1":"电信企信通通道","2":"电信MSP通道"};
var numbers_in_cache_map={"0":"否","1":"是"};
var numbers_call_channel_map={"1":"电信hlr语音呼转通道","2":"电信ims语音呼转通道","3":"测试语音呼转通道"};
var call_record_map={"0":"正常接听","1":"未接听","2":"全局黑名单拒接","3":"关机未接","4":"未绑定","5":"未知状态","6":"已销毁","7":"未激活","8":"用户设置黑名单拒接"};
var tele_status_map={"1":"有效","2":"冻结"};
var mhao_prodtct={"1":"固定密号","2":"关系密号",3:"所有密号服务"};
var mhao_prodtct_real={"1":"固定密号","2":"关系密号",3:"所有密号服务",4:"微信密号"};
var system_annouce_category={"1":"信息作用于所有人","2":"信息作用于单个(个别, 或几个)"};
var relation_map={"0":"初始化","1":"激活","2":"解绑"};
var relation_onOff={"0":"关机","1":"开机","2":"空号"};
var tele_display={"0":"来电显示虚拟号码","1":"来电显示真实主叫"};
var operator_checkIp_map={"0":"无需验证访问IP","1":"需验证访问IP"};
var bsBindRelation_type_map={"0":"关系模式","1":"固定模式"};
var bsBindRelation_status_map=relation_map;
var blockMobile_status_map={"1":"正常","2":"不在服务区","3":"停机"};
var blockMobile_callStrategy_map={"0":"推送","1":"不推送"};
var blockMobile_msgStrategy_map={"0":"推送","1":"不推送"};
var globalBlockMobile_status_map={"1":"正常","2":"不在服务区","3":"停机"};
var voiceServices_type_map={"1":"关系密号","2":"来电提示音","3":"留言提示音","4":"关机提示音"};
var voiceServices_status_map={"1":"未审核","2":"审核通过","3":"审核未通过"};
var voiceServices_platForm_map={"1":"电信","2":"联通","3":"移动","4":"其他"};
var voiceServices_uploadUser_map={"1":"ROOT","2":"管理员","3":"用户"};
var keyWord_type_map={"1":"谩骂","2":"诈骗","3":"侮辱"};
var callMonitorOfAll_callState_map={"1":"呼出号码","2":"接听号码"};
var msg_monitor_map={"1":"来源号码","2":"接收号码"};
var tele_checkAuthFm_map={"0":"默认不校验","1":"授权商户侧校验来电号码"};
var whiteKeyWord_createUser_map={"1":"JMM","2":"WRH","3":"HJF","4":"XJK","5":"YHX"};

function getSelectList(map,selectedId){
	var s="";
	$.each(map, function(key, val){
		if(val==undefined||key==undefined){
			
		}else{
			if(selectedId!=undefined&&selectedId==key)
	    		s+="<option value='"+key+"' selected>"+val+"</option>";
	    	else
	    		s+="<option value='"+key+"'>"+val+"</option>";
		}
	});
	return s;
}

function noPermis(path,method){
	if(parent.permissionList!=undefined&&parent.permissionList.length>0){
		for (i=0;i<parent.permissionList.length;i++){
			var obj= eval('(' + parent.permissionList[i] + ')');
	    	if(obj!=undefined&&obj.path!=undefined&&obj.method!=undefined
	    		&&path==obj.path
	    		&&method==obj.method
	    	)
	    		return false;
	    }
	}
	return true;
}
function hasPermis(path,method){
	if(noPermis(path,method)){
		return false;
	}
	return true;
}
function fixWidth(percent){
    return this.clientWidth * percent ;
}
function startLoading(){
	$('#loading').show();
	$('#loading_msg').show();
}
function stopLoading(){
	$('#loading').hide();
	$('#loading_msg').hide();
}
function searchReset(){
	if($('#startTime')!=undefined){
		$('#startTime').datebox('setValue', '');
		$('#startTime').val('');
	}
	if($('#endTime')!=undefined){
		$('#endTime').datebox('setValue', '');
		$('#endTime').val('');
	}
	if($('#createTime')!=undefined){
		$('#createTime').datebox('setValue', '');
		$('#createTime').val('');
	}
	if($('#updateTime')!=undefined){
		$('#updateTime').datebox('setValue', '');
		$('#updateTime').val('');
	}
	if($('#searchTotal')!=undefined){
		$('#searchTotal').val('');
	}
}

function onClickRowBySingle(rowIndex,rowData){
	$('#dg').datagrid('clearChecked');
	$('#dg').datagrid('selectRow',rowIndex);
}
function do_search(){
	var startTime = $('#startTime').datebox('getValue');
	var endTime = $('#endTime').datebox('getValue');
	if(startTime==''||startTime==undefined){
		alert('查询历史记录，开始日期必须选择！');
		return;
	}
	$('#search_startTime').val(startTime);
	if((startTime==''||startTime==undefined)&&(endTime==''||endTime==undefined)){
		alert('查询历史记录，请选择时间区间（查询区间仅限于自然月内）！');
		return;
	}
	if((startTime!=''&&startTime!=undefined)&&(endTime!=''&&endTime!=undefined)){
		var sd = new Date(startTime);
		var ed = new Date(endTime);
		ed.setTime(ed.getTime()-24*60*60*1000);
		var sf = formatDate(sd,'yyyyMM');
		var ef = formatDate(ed,'yyyyMM');
		if(sf!=ef){
			alert('查询的时间间隔为当月');
			return;
		}
	}
	//var date = new Date();
	//var preMonth = new Date(date.getFullYear(), date.getMonth() -1, 1);
	//if(sd>=preMonth){
	//	$('#dg').datagrid({
	//		url:'${webRoot}/callRecordDwh/search.do'
	//	});
	//}else{
	//	$('#dg').datagrid({
	//		url:'${webRoot}/callRecordDwh/search.do'
	//	});
	//}
	search();
}
function search(){
	if($('#searchTotal')!=undefined){
		$('#searchTotal').val('');
	}
    var params = $('#dg').datagrid('options').queryParams;
    var fields =$('#searchForm').serializeArray();
    $.each( fields, function(i, field){  
        params[field.name] = field.value;
    });   
    $('#dg').datagrid('reload');
}
function isReturnError(data){
	if(data!=undefined&&data.value!=undefined&&data.value=='nologin'){
		window.location.reload();
	}
	return false;
}
function returnError(data){
	if(data!=undefined&&data.value!=undefined&&data.value=='nologin'){
		window.location.reload();
	}else{
		if(data==undefined||data.msg==undefined)
			$.messager.alert('提示','未知错误！','info');
		else
			$.messager.alert('提示',data.msg,'info');
	}
}
function newObj(name,action,width,height){
	if(width==undefined||width==''){
		width=500;
	}
	if(height==undefined||height==''){
		height=350;
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
	$('#editForm')[0].reset();
	if ($.isFunction(window.newInitCombobox))
		newInitCombobox($('#editForm'),"");
	$("#editSubmit").unbind();
	$("#editSubmit").click(function(){
		if ($.isFunction(window.validation)){
			var rs=validation($('#editForm'));
			if(rs==undefined||rs==false){
				return ;
			}
		}
		$.ajax({
            type: "post",
            dataType: "json",
            url: action,
            data: $('#editForm').serializeArray(),
            contentType: 'application/x-www-form-urlencoded; charset=utf-8', 
            success: function(data){
            	if(data!=undefined&&data.result==true){
            		$('#editObj').dialog('close');
            		$('#dg').datagrid('reload');
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
	$('#editObj').dialog('open');	
}
function viewObj(name,url,width,height){
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
		if(i==0){
			id = n.id;
			if(n.startTime!=undefined){
				id = id + "&startTime="+n.startTime;
			}else if(n.createTime!=undefined){
				id = id + "&createTime="+n.createTime;
			}
		}
		size=i;
	});
	if(size>0){
			$.messager.alert('提示','只能操作一条数据','info');
	    return;
	}
	url = url+id;
	$('#viewObj').show();
	$.ajax({
        type: "get",
        dataType: "json",
        url: url,
        contentType: 'application/x-www-form-urlencoded; charset=utf-8', 
        success: function(data){
        	if(data!=undefined&&data.result){
    			var obj = $("#viewForm");
    			$.each(data.row,function(id,ival){
    				if(obj.find("#view_"+id)!=undefined)
    					obj.find("#view_"+id).html(ival); 
    			});
    			if ($.isFunction(window.viewInitCombobox))
    				viewInitCombobox(obj,data.row);
    			$("#viewObj").dialog({
    				title:name+'详细信息',
    				top:100,
    				width:width,
    				height:height,
    				cache: false,
    				closed: true,
    			});
    			$('#viewObj').dialog('open');
    		}else{
    			returnError(data);
    		}
        }
	});
}
function editObj(name,url,action,width,height){
	if(width==undefined||width==''){
		width=500;
	}
	if(height==undefined||height==''){
		height=330;
	}
	var rows = $('#dg').datagrid('getSelections');
	var id = "";
	var size = 0;
	if(rows==undefined||rows==''){
		$.messager.alert('提示','请选择一条记录进行编辑！','info');
	    return;
	}
	$.each(rows,function(i,n){
		if(i==0) 
	    	id = n.id;
		size=i;
	});
	if(size>0){
		$.messager.alert('提示','该服务只允许单条操作','info');
	    return;
	}
	url = url+id;
	$('#editObj').show();
	$.get(url,function(data){
		if(data!=undefined&&data.result!=undefined&&data.result){
			var obj = $("#editForm");
			$.each(data.row,function(id,ival){
				if(obj.find("#"+id)!=undefined)
					obj.find("#"+id).val(ival); 
			});
			if ($.isFunction(window.editInitCombobox))
				editInitCombobox(obj,data.row);
			$("#editObj").dialog({
				title:'编辑'+name,
				top:100,
				width:width,
				height:height,
				cache: false,
				closed: true,
			});
			$('#editObj').dialog('open');
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
		            	if(data!=undefined&&data.result==true){
		            		$('#editObj').dialog('close');
		            		$('#dg').datagrid('reload');
		            		$.messager.show({
		                		title:'编辑'+name,
		                		msg:'编辑成功！',
		                		timeout:5000,
		                		showType:'slide'
		            		});
		            	}else{
		            		returnError(data);
		            	}
		            }
				});
			});
		}else{
			returnError(data);
		}
	});
}
function removeObj(action){
	var rows = $('#dg').datagrid('getSelections');
	var size=0;
    $.each(rows,function(i,n){
    	size++;
    });
    if(size<=0){
    	$.messager.alert('提示','未选择所需删除的项！','info');
    	return;
    }
	$.messager.confirm('提示','确定要删除 '+size+' 项信息吗?',function(result){
		if (result){
	        var ps = "";
	        $.each(rows,function(i,n){
	        	if(i==0) 
	        		ps += "?id="+n.id;
	        	else
	        		ps += "&id="+n.id;
	        });
	        $.post(action+ps,function(data){
	        	if(data!=undefined&&data.result==true){
			       	$('#dg').datagrid('reload'); 
	        		$.messager.show({
	            		title:'提示信息：',
	            		msg:size+' 项信息删除成功！',
	            		timeout:5000,
	            		showType:'slide'
	        		});
	        	}else{
	        		returnError(data);
	        	}
	        });
	    }
	});
}

function dealObj(name,url){
	var rows = $('#dg').datagrid('getSelections');
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
		$.messager.alert('提示','该服务只允许单条处理','info');
	    return;
	}
	url = url+id;
	$.ajax({
        type: "post",
        dataType: "json",
        url: url,
        contentType: 'application/x-www-form-urlencoded; charset=utf-8', 
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

//更改开关机状态
function switchObj(action) {
	var rows = $('#dg').datagrid('getSelections');
	var size = 0;
	var ok = false;
	$.each(rows, function(i, n){
		var al = n.mobile + ", " + n.redirectMobile;
		if (n.isDelete!=0 || n.status!=1){
			ok = true;
			return ;
		}
		size ++;
	});
	$.messager.confirm('提示', '确定要更改用户的开关机状态嘛？', function(result){
		if (result) {
			
			$.each(rows, function(i,n){
				var ps = "";
				ps = "?mobile=" + n.mobile + "&redirectMobile=" + n.redirectMobile;
				$.post(action + ps, function(data){
					if (data!=undefined&&data.status==0){
						$('#dg').datagrid('reload');
						$.messager.show({
							title:'提示信息:',
							msg:'更改开关机状态成功',
							timeout:5000,
							showType:'slide'
						});
					} else {
						returnError(data);
					}
				});
			});
			
			
		}
	});
}
//重置状态
function changeObj(action) {
	var rows = $('#dg').datagrid('getSelections');
	var size = 0;
	$.each(rows, function(i, n){
		size ++;
	});
	if (size<=0) {
		$.messager.alert('提示', '未选中所需重置的项！', 'info');
		return ;
	}
	if (size>1) {
		$.messager.alert('提示','只能操作一条数据','info');
		return ;
	}
	$.messager.confirm('提示', '确定要重置状态嘛？', function(result){
		if (result) {
			var ps = "";
			$.each(rows, function(i,n){
				ps = "?id=" + n.id;
			});
			$.post(action + ps, function(data){
				if (data!=undefined&&data.status==0){
					$('#dg').datagrid('reload');
					$.messager.show({
						title:'提示信息:',
						msg:'重置状态成功',
						timeout:5000,
						showType:'slide'
					});
				} else {
					returnError(data);
				}
			});
			
		}
	});
}
//为服务配置号码池
function mobilePoolSet(url, width, height){
	var rows = $('#dg').datagrid('getSelections');
	var size = 0;
	var basic = true;
	var datarow;
	$.each(rows, function(i, n){
		if (n.type!=1){	//判断是否为基础服务，只有为基础服务才能配置号码池
			basic = false;
		}
		datarow = n;
		size ++;
	});
	if (size<=0) {
		$.messager.alert('提示', '未选中所需重置的项！', 'info');
		return ;
	}
	if (size>1) {
		$.messager.alert('提示','只能操作一条数据','info');
		return ;
	}
	if (!basic) {
		$.messager.alert('提示', '只有基础服务才能配置号码池', 'info');
		return ;
	}
	if(width==undefined||width==''){
		width=500;
	}
	if(height==undefined||height==''){
		height=300;
	}
	$('#setMobilePoolObj').show();
	$("#setMobilePoolObj").dialog({
		title:"为" + datarow.name+ "服务配置号码池",
		top:100,
		width:width,
		height:height,
		cache: false,
		closed: true,
	});
	$('#setMobilePoolForm')[0].reset();
	$('#setMobilePoolForm').find("#service_view_id").val(datarow.id);
	$('#setMobilePoolForm').find("#service_view_name").html(datarow.name);
	$('#setMobilePoolForm').find("#service_view_sort").html("<option value='' selected>请选择</option>");
	$('#setMobilePoolForm').find("#service_view_sort").append(getSelectList(mobile_pool_map, datarow.mobilePool));
	$('#setMobilePoolObj').dialog('open');
	$("#setMobilePoolSubmit").unbind();
	$("#setMobilePoolSubmit").click(function(){
		var id = $("#service_view_id").val();
		var mobilePool = $("#service_view_sort").val();
		if (id=='') {
			$.messager.alert('提示', '请刷新页面重试', 'info');
			return ;
		}
		if (mobilePool==''){
			$.messager.alert('提示', '号码池不能为空', 'info');
			return ;
		}
		$.ajax({
            type: "post",
            dataType: "json",
            url: url,
            data: $('#setMobilePoolForm').serializeArray(),
            contentType: 'application/x-www-form-urlencoded; charset=utf-8', 
            success: function(data){
            	if(data!=undefined&&data.status==0){
            		$('#setMobilePoolObj').dialog('close');
            		$('#dg').datagrid('reload');
            		$.messager.show({
                		title:'配置号码池',
                		msg:data.msg,
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
//弥补高版本jQuery没有的东西
jQuery.extend({
	handleError: function( s, xhr, status, e ) {
		if ( s.error )
			s.error( xhr, status, e );
		else if(xhr.responseText) {
			var data = eval('(' + xhr.responseText + ')');
			if(data!=undefined&&data.status==0){
				$('#uploadObj').dialog('close');
        		$('#dg').datagrid('reload');
        		$.messager.show({
        			title:'批量添加号码',
        			msg:'添加成功！',
        			timeout:5000,
        			showType:'slide'
        		});
			} else {
				returnError(data);
			}
		}
	}
});