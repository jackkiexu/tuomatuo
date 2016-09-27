//将array拼接成字符串，默认分隔符为逗号
//如[1,2,3]转换为1,2,3
function joinArray(array,spot){
	var str='';
	if(isundefined(spot)){
		spot=',';
	}
	for(var i in array){
		str+=array[i]+''+spot;
	}
	if(str.length!=0){
		str=str.slice(0, str.length-1);
	}
	return str;
}
//验证是否为整形数字
function isInteger( str ){
	var regu = /^[-]{0,1}[0-9]{1,}$/;
	return regu.test(str);
}
//验证是否为数字
function isNum(num){
	if(num=='')return false;
	if(isundefined(num))return false;
	return !isNaN(num);
}
//判断是否未定义
function isundefined(params){
	return typeof(params)=="undefined";
}
//提取url中的参数
function geturlp(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if (r != null)
		return unescape(r[2]);
	return null;
}
//将未定义数值转换为0
function undefined2int(param) {
	if (typeof (param) == 'undefined')
		return 0;
	else
		return param;
}
//将未定义数值转换为空字符串
function undefined2empty(param) {
	if (typeof (param) == 'undefined')
		return '';
	else
		return param;
}
function debugObject(obj){
	var str='';
	for(var i in obj){
		str+=obj[i]+'\n';
	}
	return str;
}
function isIP(strIP) { 
	if (isNull(strIP)) return false; 
	var re=/^(\d+)\.(\d+)\.(\d+)\.(\d+)$/g //匹配IP地址的正则表达式 
	if(re.test(strIP)) 
	{ 
	if( RegExp.$1 <256 && RegExp.$2<256 && RegExp.$3<256 && RegExp.$4<256) return true; 
	} 
	return false; 
}
function isNull( str ){ 
	if ( str == "" ) return true; 
	var regu = "^[ ]+$"; 
	var re = new RegExp(regu); 
	return re.test(str); 
} 
function checkMobile( s ){   
	var regu =/^[1][358][0-9]{9}$/; 
	var re = new RegExp(regu); 
	if (re.test(s)) { 
		return true; 
	}else{ 
		return false; 
	} 
} 
function isEmail( str ){  
	var myReg = /^[-_A-Za-z0-9]+@([_A-Za-z0-9]+\.)+[A-Za-z0-9]{2,3}$/; 
	if(myReg.test(str)) return true; 
	return false; 
} 
function isDate( date, fmt ) { 
	if (fmt==null) fmt="yyyyMMdd"; 
	var yIndex = fmt.indexOf("yyyy"); 
	if(yIndex==-1) return false; 
	var year = date.substring(yIndex,yIndex+4); 
	var mIndex = fmt.indexOf("MM"); 
	if(mIndex==-1) return false; 
	var month = date.substring(mIndex,mIndex+2); 
	var dIndex = fmt.indexOf("dd"); 
	if(dIndex==-1) return false; 
	var day = date.substring(dIndex,dIndex+2); 
	if(!isNumber(year)||year>"2100" || year< "1900") return false; 
	if(!isNumber(month)||month>"12" || month< "01") return false; 
	if(day>getMaxDay(year,month) || day< "01") return false; 
	return true; 
} 
function checkSelect( checkboxID ) { 
	var check = 0; 
	var i=0; 
	if( document.all(checkboxID).length > 0 ) { 
		for(  i=0; i<document.all(checkboxID).length; i++ ) { 
			if( document.all(checkboxID).item( i ).checked  ) { 
			check += 1; 
			} 
		} 
	}else{ 
		if( document.all(checkboxID).checked ) 
			check = 1; 
	} 
	return check; 
} 
function getTotalBytes(varField) { 
	if(varField == null) 
	return -1; 

	var totalCount = 0; 
	for (i = 0; i< varField.value.length; i++) { 
	if (varField.value.charCodeAt(i) > 127) 
	totalCount += 2; 
	else 
	totalCount++ ; 
	} 
	return totalCount; 
} 
function checkSame(str1,str2){
	if(str1==str2)
		return true;
	else
		return false;
}
function checkLength(str,min,max){
	if(str.length>=min&&str.length<=max)
		return true;
	else
		return false;
}
function  isChinese(name){  
	if(name.length == 0)
		return  false;
	for(i = 0;i<name.length;i++){ 
		if(name.charCodeAt(i) > 128)
			return  true;
	}
	return  false;
}
function formatDate(dd,fmt){
	if(!fmt || fmt == ""){
		fmt = "yyyy-MM-dd hh:mm:ss";
    }
	var date = new Date(dd);
	var o = {        
		    "M+" : date.getMonth()+1, //月份        
		    "d+" : date.getDate(), //日        
		    "h+" : date.getHours()%12 == 0 ? 12 : date.getHours()%12, //小时        
		    "H+" : date.getHours(), //小时        
		    "m+" : date.getMinutes(), //分        
		    "s+" : date.getSeconds(), //秒        
		    "q+" : Math.floor((date.getMonth()+3)/3), //季度        
		    "S" : date.getMilliseconds() //毫秒        
		    };    
	if(/(y+)/.test(fmt)){        
        fmt=fmt.replace(RegExp.$1, (date.getFullYear()+"").substr(4 - RegExp.$1.length));        
    } 
	if(/(E+)/.test(fmt)){        
        fmt=fmt.replace(RegExp.$1, ((RegExp.$1.length>1) ? (RegExp.$1.length>2 ? "\u661f\u671f" : "\u5468") : "")+week[date.getDay()+""]);
	}
	for(var k in o){        
        if(new RegExp("("+ k +")").test(fmt)){        
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));        
        }        
    }
    return fmt;	
}