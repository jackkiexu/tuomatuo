<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>${projectName}管理平台</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-store">
<meta http-equiv="expires" content="0">
<script type="text/javascript" src="${webRoot}/js/jquery.min.js"></script>
<style type="text/css">
<!--
body {
	margin: 0px;
	background-image: url(${webRoot}/images/login_r1_c1.jpg);
	background-repeat:repeat-x;
	
}
body,td,th {
	font-size: 12px;
	color: #000000;
}
.loginError {
	color:red;
	background-image:url(${webRoot}/images/login_r4_c9.jpg);
	background-repeat:no-repeat;
	padding-left:18px;
}
-->
</style>
</head>
<script type="text/javascript">
//去空格函数
String.prototype.Trim = function()
{
    return this.replace(/(^\s*)|(\s*$)/g, "");
}
/**
 * 验证管理员登录使用
 */
function loginCheck(){
	document.getElementById("loginError").innerHTML='<img src="${webRoot}/images/loading.gif" align="absmiddle" /> 登陆中...';
	var userId = document.getElementById("login").value;
    var userPws = document.getElementById("password").value;
    var code = document.getElementById("code").value;
    
    if(userId==undefined||userId == ''){
		document.getElementById("loginError").innerHTML='对不起,管理员不能为空';
		return;
	}
    if(userId.length > 30){
	    document.getElementById("loginError").innerHTML='对不起,管理员用户名不得超过30位';
		return;
	}
    if(userPws==undefined||userPws == ''){
		document.getElementById("loginError").innerHTML='密码不能为空';
		return;
	}
	if(userPws.length>30){
	 	document.getElementById("loginError").innerHTML='密码不得超过30位';
		return;
	}
	if(code==undefined||code == ''){
		document.getElementById("loginError").innerHTML='验证码不能为空';
		return;
	}
	if(code.length!=4){
		document.getElementById("loginError").innerHTML='验证码长度只能是4位';
		return;
    }
    $.ajax({
        type: "post",
        dataType: "json",
        url: "${webRoot}/checkLogin.do",
        data: $('#form').serializeArray(),
        success: function(data){
        	if(data!=undefined&&data.result==true){
	    		window.location.href="${webRoot}/index.do";
	    	}else{
	    		document.getElementById("loginError").innerHTML=data.errorInfo;
	    		//重新整理一下验证码
	    		document.getElementById("code").value='';
	    		reloadcCode();
	    	}
        }
	});
}

function reloadcCode(){
	var verify = document.getElementById('codeImg');
	var new_rand_url = '${webRoot}/vn.do?sign='+Math.round(Math.random()*10000000000)+'000000000000000000000000000000';
	verify.src=new_rand_url;
}

/**
 * 清空所有的填写的数据
 */
function reset(){
	document.getElementById("login").value='';
    document.getElementById("password").value='';
    document.getElementById("code").value='';
}
function keydown(ev){
	ev = ev ? ev : window.event;
	var el = ev.target ? ev.target : ev.srcElement;
	if(ev.keyCode == 13){ev.keyCode = 9;}
}
function keydown2(ev){
	ev = ev ? ev : window.event;
	var el = ev.target ? ev.target : ev.srcElement;
	if(ev.keyCode == 13){loginCheck();}
}
//初始化
function init(){
	var un = document.getElementById("login");
    var up = document.getElementById("password");
    var cc = document.getElementById("code");
    
    un.focus();
    if(un.addEventListener){
        un.addEventListener('keydown', keydown, false);
        up.addEventListener('keydown', keydown, false);
        cc.addEventListener('keydown', keydown2, false);
    } else {
        un.attachEvent('onkeydown', keydown);
        up.attachEvent('onkeydown', keydown);
        cc.attachEvent('onkeydown', keydown2);
    }
    reloadcCode();
}

if (top.location != self.location) top.location=self.location;
</script>
   <body onLoad="init();">
	<br />
	<br />
	<br />
	<br />
	<br />
	<br />
	<br />
	<br />
	<form id="form" name="form">
	
	<table border="0" align="center" cellpadding="0" cellspacing="0">
	  <tr>
		<td colspan="3"><img src="${webRoot}/images/login_r2_c3.jpg" width="430" height="145" /></td>
	  </tr>
	  <tr>
		<td rowspan="2"><img src="${webRoot}/images/login_r3_c3.jpg" width="10" height="185" /></td>
		<td width="410" height="180" valign="top"><table width="400" border="0" align="center" cellpadding="2" cellspacing="5">
		  <tr>
			<td height="25">&nbsp;</td>
		    <td height="25" colspan="2"><div id="loginError" class="loginError"></div></td>
	      </tr>
		  <tr>
			<td width="200" height="25" align="right">用户名：</td>
			<td height="25" colspan="2"><input name="login" type="text" id="login" size="20" value=""  style="border:1px #6CD0F4 solid" /></td>
		  </tr>
		  <tr>
			<td height="25" align="right">密&nbsp;码：</td>
			<td height="25" colspan="2"><input name="password" type="password" id="password" value=""  size="20" style="border:1px #6CD0F4 solid" autocomplete="off"/></td>
		  </tr>
		  <tr>
			<td height="25" align="right">验证码：</td>
			<td width="40" height="25"><input name="code" value="" type="text" id="code" size="5" style="border:1px #6CD0F4 solid" /></td>
			<td width="262"><a href="javascript:reloadcCode();" ><img src="" width="75" border="0" height="20" id="codeImg" /></a></td>
		  </tr>
		  <tr>
			<td height="25" colspan="3" align="center">
			<img src="${webRoot}/images/login_r4_c6.jpg" onClick="loginCheck();" style="cursor:pointer" />&nbsp; 
			<img src="${webRoot}/images/login_r4_c8.jpg" onClick="reset();" style="cursor:pointer" />
			<input type=hidden id="ssid" name="ssid" value="${sessionId}">
			</td>
		  </tr>
		</table></td>
		<td rowspan="2"><img src="${webRoot}/images/login_r3_c10.jpg" width="10" height="185" /></td>
	  </tr>
	  <tr>
		<td><img src="${webRoot}/images/login_r6_c4.jpg" width="410" height="5"></td>
	  </tr>
	</table>
	</form>
</body>
</html>