<div class="navbox">
		<ul class="navbox_items">
			<li name="mobile" onclick="javascript:location.href='/service/mobile'">号码</li>
			<li name="message" onclick="javascript:location.href='/record/message'">短信</li>
		</ul>
	</div>
<script type="text/javascript" language="javascript">
	var url=window.location.href;
	$("li").each(function(){
		if(url.indexOf($(this).attr("name"))>=0){
			$(this).addClass("on");
		}else{
			$(this).removeClass("on");
		}
	});
	
</script>