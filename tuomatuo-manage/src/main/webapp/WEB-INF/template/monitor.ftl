<!DOCTYPE html>
<html>
<head>
<title>m</title>
<meta charset="UTF-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<style>
body {
	margin:0px;
	padding:0px;
	width:100%;
	height:100%;
	border:0px;
}
</style>
<script type="text/javascript"> 
</script>
</head>
<table style="float:left">  
	<tr class="alt">  
		<td>free</td>  
		<td>${(map.number_pool_free_count)!''}</td>  
	</tr>
	<tr class="alt">  
	<td>pay</td>  
	<td>${(map.number_pool_pay_count)!''}</td>  
	</tr>
	<tr class="alt">  
		<td>manage</td>  
		<td>${(map.number_pool_manager_count)!''}</td>  
	</tr>
	<tr class="alt">  
		<td>sms</td>  
		<td>${(map.number_pool_sms_count)!''}</td>  
	</tr>
	<tr class="alt">  
		<td>call out</td>  
		<td>${(map.number_pool_callout_count)!''}</td>  
	</tr>
	<tr class="alt">  
		<td>sms out</td>  
		<td>${(map.number_pool_smsout_count)!''}</td>  
	</tr>
	<tr class="alt">  
		<td>dd</td>  
		<td>${(map.didi_number_pool_count)!''},${(map.didi_concurrent)!''}</td>  
	</tr>
	<tr class="alt">  
		<td>ddzc</td>  
		<td>${(map.didiZhuanche_number_pool_count)!''},${(map.didiZhuanche_concurrent)!''}</td>  
	</tr>
	<tr class="alt">  
		<td>za</td>  
		<td>${(map.zhenai_number_pool_count)!''},${(map.zhenai_concurrent)!''}</td>  
	</tr>
</table>
</div>
</body>
</html>