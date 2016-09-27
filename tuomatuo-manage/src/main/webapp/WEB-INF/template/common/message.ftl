<div class="container_12">
	<!--START NOTIFICATIONS  -->
	<div class="notification success canhide" style="display: none"
		id="successdiv">
		<p>
			<span id="successmessage"></span>
		</p>
	</div>
	<div class="notification failure canhide" style="display: none"
		id="errordiv">
		<p>
			<span id="errormessage"></span>
		</p>
	</div>
</div>
<!--END NOTIFICATIONS  -->
<script>
	function dealErrorMessage(error) {
		var $errormessage = $('#errormessage');
		var $errordiv = $('#errordiv');
		$errormessage.html(error);
		$errordiv.show();
	}
	//author:xuxinhui,2013-3-5
	function dealSuccessMessage(infor){
		if(infor.length>0){
			var $successmessage = $('#successmessage');
			var $successdiv = $('#successdiv');
			$successmessage.html(infor);
			$successdiv.show();
			setTimeout("$('#successdiv').fadeOut('normal');",3000);
		}
	}
	function callbak(msg){
		dealSuccessMessage(msg);
	}
	function dealJsonMessage(data) {
		var $successmessage = $('#successmessage');
		var $successdiv = $('#successdiv');
		$successmessage.html('');
		$successdiv.hide();

		var $errormessage = $('#errormessage');
		var $errordiv = $('#errordiv');
		$errormessage.html('');
		$errordiv.hide();
		if (data.status == 0) {
			$successdiv.show();
			$successmessage.html(data.msg);
		} else if (data.status == 1) {
			window.location.href = "${webRoot}";
		} else if (data.status == 3) {
			$errordiv.show();
			var Jerrormsg = data.value;
			var content = '';
			for ( var key in Jerrormsg) {
				content += Jerrormsg[key] + '<br/>';
			}
			$errormessage.html(content);
		}
	}
	function dealJsonDiv(data){
		if(data==undefined){
			var div = '<div class="container_12"><div class="notification failure canhide"><p>操作出错，请稍候再试！</p></div></div>';
			$.facebox(div);
		}else{
			if (data.status == 0) {
				var div = '<div class="container_12"><div class="notification success canhide"><p>'+data.msg+'</p></div></div>';
				$.facebox(div);
			} else if (data.status == 1) {
				window.location.href = "${webRoot}";
			} else if (data.status == 3) {
				var div = '<div class="container_12"><div class="notification failure canhide"><p>'+data.msg+'</p></div></div>';
				$.facebox(div);
			}
		}
	}
</script>