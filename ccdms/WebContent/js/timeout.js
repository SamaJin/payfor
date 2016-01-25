
Ext.Ajax.on('requestcomplete', session_timeout, this);

function session_timeout(conn, response, options) {
	var str = response.responseText;  
    if (str == 'timeout') {
    	alert('连接已超时,请重新登录!');
    }
}