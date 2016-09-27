<div id="header" class="png_bg">
    <div id="head_wrap" class="container_12">
        <div id="logo" class="grid_4">
          <h1>手机密号<span>管理平台</span></h1>
        </div>
        <!-- end logo -->
        <!-- start control panel -->
    	<div id="controlpanel" class="grid_8">
            <ul>
    			<li><p>欢 迎 <strong id="managername" ></strong> </p></li>
    			<li><a href="${webRoot}/user/editPassword.do" class="last" id="outdoor"> 修改密码 </a></li>
                <li><a href="${webRoot}/user/logout.do" class="last" id="outdoor"> 退出 </a></li>
            </ul>
        </div>
        <!-- end control panel -->
        <!-- start navigation -->
      	<div id="navigation" class="grid_12">
            <ul>
                <li><a href="${webRoot}/sysOperator/search.do">管理员管理</a>
                <li><a href="${webRoot}/messageRecord/search.do">短信管理</a>
                <li><a href="${webRoot}/user/search.do">用户管理</a>
                <li><a href="${webRoot}/services/search.do">服务管理</a>
                <li><a href="${webRoot}/numbers/search.do">号码管理</a>
            </ul>
        </div>
        <!-- end navigation -->
    </div><!-- end headwarp  -->
</div><!-- end header -->
<!-- staqrt subnav -->
<div id="sub_nav">
<div id="subnav_wrap" class="container_12">
    <!-- start sub nav list -->
	<div id="subnav" class="grid_12">
	   <ul class="sub"><!-- 管理员管理 -->
            <li><a href="${webRoot}/sysOperator/new.do">添加新管理员</a></li>
            <li><a href="${webRoot}/sysOperator/search.do">管理员列表</a></li>
            <li><a href="${webRoot}/sysRole/new.do">添加角色</a></li>
            <li><a href="${webRoot}/sysRole/search.do">角色列表</a></li>
            <li><a href="${webRoot}/sysPermission/new.do">添加权限</a></li>
            <li><a href="${webRoot}/sysPermission/search.do">权限列表</a></li>
        </ul>
        <ul class="sub"><!-- 短信管理 -->
            <li><a href="${webRoot}/messageRecord/search.do">短信列表</a></li>
        </ul>
        <ul class="sub"><!-- 用户管理 -->
            <li><a href="${webRoot}/user/search.do">用户列表</a></li>
        </ul>
        <ul class="sub"><!-- 服务管理 -->
            <li><a href="${webRoot}/services/new.do">添加服务</a></li>
            <li><a href="${webRoot}/services/search.do">服务列表</a></li> 
        </ul>
        <ul class="sub"><!-- 号码管理 -->
            <li><a href="${webRoot}/numbers/search.do">号码列表</a></li>
        </ul>
    </div>
    <!-- end subnavigation list -->	
</div>
</div>
<!-- end sub_nav -->
<script type="text/javascript" >
var lastId=0;
var navigation = $("#navigation li a");
var main_cont = $(".sub");
$(function () {
	// 主菜单切换
	showNavigation();
	navigation.hover(function () {
		lastId=navigation.index(this);
		showNav();
	});	
});
function showNav(){
	navigation.each(function(i,obj){
		$(navigation[i]).removeClass("active");
		if(i==lastId){
			$(navigation[i]).addClass("active");
		}
	});
	main_cont.each(function(i,obj) {
		$(obj).hide();
		if(i==lastId){
			$(obj).show();
		}
	});
}
function showNavigation(){
	var loc_url = location.href;
    var urlLen = loc_url.indexOf("?");
    if (urlLen >= 0) {
        loc_url = loc_url.substring(0, urlLen);
    }
    urlLen = loc_url.indexOf("#");
    if (urlLen >= 0) {
        loc_url = loc_url.substring(0, urlLen);
    }
    navigation.each(function(i,obj){
    	if(loc_url.indexOf(this.href)>=0){
    		$(navigation[i]).addClass("active").siblings().removeClass("active");
    	}
    });
    main_cont.each(function(i,o1) {
    	$("li a",o1).each(function(k,o2) {
    		if(loc_url.indexOf(o2.href) >= 0){
        		$(navigation[i]).addClass("active").siblings().removeClass("active");
        		$(o2).addClass("sub_nav_active");
            	$(o1).show().siblings().hide();
       		}
    	});
    });
}
</script>






