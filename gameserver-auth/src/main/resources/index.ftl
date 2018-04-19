<!DOCTYPE html>  
<html lang="en">  
<head>  
    <meta charset="UTF-8">  
    <title>Login</title>  
    <!-- <link rel="stylesheet" type="text/css" href="login.css"/>  --> 
    <style type="text/css">
    	
	    html{   
	        width: 100%;   
	        height: 100%;   
	        overflow: hidden;   
	        font-style: sans-serif;   
	    }   
	    body{   
	        width: 100%;   
	        height: 100%;   
	        font-family: 'Open Sans',sans-serif;   
	        margin: 0;   
	        background-color: #4A374A;   
	    }   
	    #login{   
	        position: absolute;   
	        top: 50%;   
	        left:50%;   
	        margin: -150px 0 0 -150px;   
	        width: 300px;   
	        height: 300px;   
	    }   
	    #login h1{   
	        color: #fff;   
	        text-shadow:0 0 10px;   
	        letter-spacing: 1px;   
	        text-align: center;   
	    }   
	    h1{   
	        font-size: 2em;   
	        margin: 0.67em 0;   
	    }   
	    input{
	        width: 278px;   
	        height: 18px;   
	        margin-bottom: 10px;   
	        outline: none;   
	        padding: 10px;   
	        font-size: 13px;   
	        color: #fff;   
	        text-shadow:1px 1px 1px;   
	        border-top: 1px solid #312E3D;   
	        border-left: 1px solid #312E3D;   
	        border-right: 1px solid #312E3D;   
	        border-bottom: 1px solid #56536A;   
	        border-radius: 4px;   
	        background-color: #2D2D3F;   
	    }   
	    .but{   
	        width: 300px;   
	        min-height: 20px;   
	        display: block;   
	        background-color: #4a77d4;   
	        border: 1px solid #3762bc;   
	        color: #fff;   
	        padding: 9px 14px;   
	        font-size: 15px;   
	        line-height: normal;   
	        border-radius: 5px;   
	        margin: 0;   
	    } 
    </style>
    
    <script type="text/javascript">
		var socket;
		if (!window.WebSocket) {
			window.WebSocket = window.MozWebSocket;
		}
		if (window.WebSocket) {
			socket = new WebSocket("ws://${host}:${port}/");
			socket.onmessage = function(event) {
				
				console.log("123");
				
			};
			socket.onopen = function(event) {
				console.log("连接开启!");
				send("good luck");
			};
			socket.onclose = function(event) {
				console.log("连接被关闭");
			};
		} else {
			alert("你的浏览器不支持 WebSocket！");
		}

		function send(message) {
			if (!window.WebSocket) {
				return;
			}
			if (socket.readyState == WebSocket.OPEN) {
				socket.send(message);
			} else {
				alert("连接没有开启.");
			}
		}

		window.onload = function() {
			var canvas = document.getElementById("canvas");
			//		if(! canvas || ! canvas.getContext){
			//			return false;
			//		}
			/* canvas.addEventListener("mousedown", doMouseDown, false);
			cw = window.innerWidth, ch = window.innerHeight,
					canvas.width = cw - 100;
			canvas.height = ch - 100;

			var ctx = canvas.getContext("2d"); //准备画布
			ctx.lineWidth = 0.5; //选择画笔
			ctx.strokeStyle = "#ee6699"; //选择颜料
			//ctx.lineCap="round";								//三种：butt,round,qruare
			//ctx.beginPath();
			ctx.moveTo(10, 10); //... */
		}

		function drawLine(x, y) {
			var canvas = document.getElementById("canvas");
			var ctx = canvas.getContext("2d"); //准备画布			
			ctx.lineTo(x, y);

			ctx.stroke(); //填充颜色
			//this.setTimeout(drawLine,200);
		}

		function doMouseDown(event) {
			var btnNum = event.button;
			var x = event.clientX;
			var y = event.clientY;
			//alert(111);
			send(x + "," + y);
		}
	</script>
    
</head>  
<body>  
    <div id="login">
        <h1>index</h1>  
        <h2>welcome ${username} to index</h2>
        <h3>You will connect server ip ${host} port ${port}</h3>
    </div>
</body>  
</html>