
function open( host, port){
	var socket;
	if (!window.WebSocket) {
		window.WebSocket = window.MozWebSocket;
	}
	if (window.WebSocket) {
		socket = new WebSocket("ws://"+host+":"+port);
		socket.onmessage = function(event) {
			var strs;
			strs = event.data.split(","); // 字符分割
			x = parseInt(strs[0]);
			y = parseInt(strs[1]);
			drawLine(x, y);
		};
		socket.onopen = function(event) {
			var ta = document.getElementById('responseText');
			ta.value = "连接开启!";
		};
		socket.onclose = function(event) {
			var ta = document.getElementById('responseText');
			ta.value = ta.value + "连接被关闭";
		};
	} else {
		alert("你的浏览器不支持 WebSocket！");
	}
	return socket;
}


function send(socket,message) {
	if (!window.WebSocket) {
		return;
	}
	if (socket.readyState == WebSocket.OPEN) {
		socket.send(message);
	} else {
		alert("连接没有开启.");
	}
}