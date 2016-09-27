<div class="pagebox">
			<p class="r" style="float:right;">
				<a href="javascript:submitForm('1')" title="">首页</a>
				<a href="javascript:submitForm('${pb.priorNo}')">上页</a> 
				<a href="javascript:submitForm('${pb.nextNo}')">下页</a> 
				<a href="javascript:submitForm('${pb.pageCount}')">末页</a> 
				当前是第${pb.pageNo}页 / 
				共${pb.pageCount}页 ${pb.rowCount}条记录，跳转到 <input type="text" class="page" name="searchPage" id="searchPage" style="width:20px;"/> 页 
				<input type="hidden" id="orderBy" name="orderBy" value="<#if pb.orderBy ? exists>${pb.orderBy}</#if>"/>
				<input type="hidden" id="orderType" name="orderType" value="<#if pb.orderType ? exists>${pb.orderType}</#if>"/>
				<input type="hidden" id="pageNo" name="pageNo" value="<#if pb.pageNo ? exists>${pb.pageNo}</#if>"/>
				<input type="button" value="确定" class="btn4" onclick="submitPage()"/>
			</p>
</div>
<script language="javascript">
function submitOrderby(key){
	if(key!=undefined&&key==$('#orderBy').val()){
		if($('#orderType').val()!=undefined&&$('#orderType').val()=='desc'){
			$('#orderType').val('asc');
		}else{
			$('#orderType').val('desc');
		}
	}else{
		$('#orderBy').val(key);
		$('#orderType').val('desc');
	}	
	submitForm('1');
}
function submitPage(){
	submitForm($('#searchPage').val());
}
function submitForm(currentPage){
	var form = document.getElementById("searchAction");
	$('#pageNo').val(currentPage);
	//form.action=form.action+"?pageNo="+currentPage+'&orderBy=';
	form.submit();
}
function reload(){
	submitForm('${pb.pageNo}');
}
</script>