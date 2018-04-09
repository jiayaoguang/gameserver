package org.gameserver.auth;

import java.util.Random;
import java.util.Set;

import com.jyg.net.HttpProcessor;
import com.jyg.net.Request;
import com.jyg.net.Response;

import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;

/**
 * created by jiayaoguang at 2018年3月20日
 */
public class LoginHtmlHttpProcessor extends HttpProcessor{

	String html = "<!DOCTYPE html>  \r\n" + 
			"<html lang=\"en\">  \r\n" + 
			"<head>  \r\n" + 
			"    <meta charset=\"UTF-8\">  \r\n" + 
			"    <title>Login</title>  \r\n" + 
			"    <!-- <link rel=\"stylesheet\" type=\"text/css\" href=\"login.css\"/>  --> \r\n" + 
			"    <style type=\"text/css\">\r\n" + 
			"    	\r\n" + 
			"	    html{   \r\n" + 
			"	        width: 100%;   \r\n" + 
			"	        height: 100%;   \r\n" + 
			"	        overflow: hidden;   \r\n" + 
			"	        font-style: sans-serif;   \r\n" + 
			"	    }   \r\n" + 
			"	    body{   \r\n" + 
			"	        width: 100%;   \r\n" + 
			"	        height: 100%;   \r\n" + 
			"	        font-family: 'Open Sans',sans-serif;   \r\n" + 
			"	        margin: 0;   \r\n" + 
			"	        background-color: #4A374A;   \r\n" + 
			"	    }   \r\n" + 
			"	    #login{   \r\n" + 
			"	        position: absolute;   \r\n" + 
			"	        top: 50%;   \r\n" + 
			"	        left:50%;   \r\n" + 
			"	        margin: -150px 0 0 -150px;   \r\n" + 
			"	        width: 300px;   \r\n" + 
			"	        height: 300px;   \r\n" + 
			"	    }   \r\n" + 
			"	    #login h1{   \r\n" + 
			"	        color: #fff;   \r\n" + 
			"	        text-shadow:0 0 10px;   \r\n" + 
			"	        letter-spacing: 1px;   \r\n" + 
			"	        text-align: center;   \r\n" + 
			"	    }   \r\n" + 
			"	    h1{   \r\n" + 
			"	        font-size: 2em;   \r\n" + 
			"	        margin: 0.67em 0;   \r\n" + 
			"	    }   \r\n" + 
			"	    input{   \r\n" + 
			"	        width: 278px;   \r\n" + 
			"	        height: 18px;   \r\n" + 
			"	        margin-bottom: 10px;   \r\n" + 
			"	        outline: none;   \r\n" + 
			"	        padding: 10px;   \r\n" + 
			"	        font-size: 13px;   \r\n" + 
			"	        color: #fff;   \r\n" + 
			"	        text-shadow:1px 1px 1px;   \r\n" + 
			"	        border-top: 1px solid #312E3D;   \r\n" + 
			"	        border-left: 1px solid #312E3D;   \r\n" + 
			"	        border-right: 1px solid #312E3D;   \r\n" + 
			"	        border-bottom: 1px solid #56536A;   \r\n" + 
			"	        border-radius: 4px;   \r\n" + 
			"	        background-color: #2D2D3F;   \r\n" + 
			"	    }   \r\n" + 
			"	    .but{   \r\n" + 
			"	        width: 300px;   \r\n" + 
			"	        min-height: 20px;   \r\n" + 
			"	        display: block;   \r\n" + 
			"	        background-color: #4a77d4;   \r\n" + 
			"	        border: 1px solid #3762bc;   \r\n" + 
			"	        color: #fff;   \r\n" + 
			"	        padding: 9px 14px;   \r\n" + 
			"	        font-size: 15px;   \r\n" + 
			"	        line-height: normal;   \r\n" + 
			"	        border-radius: 5px;   \r\n" + 
			"	        margin: 0;   \r\n" + 
			"	    }  \r\n" + 
			"\r\n" + 
			"    	\r\n" + 
			"    </style>\r\n" + 
			"</head>  \r\n" + 
			"<body>  \r\n" + 
			"    <div id=\"login\">  \r\n" + 
			"        <h1>Login</h1>  \r\n" + 
			"		<h1>$random</h1>"+
			"        <form method=\"post\" action=\"/login\">\r\n" + 
			"            <input name=\"username\" type=\"text\" required=\"required\" placeholder=\"用户名\"></input>  \r\n" + 
			"            <input name=\"password\" type=\"password\" required=\"required\" placeholder=\"密码\"></input>  \r\n" + 
			"            <button class=\"but\" type=\"submit\">登录</button>\r\n" + 
			"        </form>  \r\n" + 
			"    </div>  \r\n" + 
			"</body>  \r\n" + 
			"</html>";
	
	@Override
	public void service(Request request, Response response) {
		String msg = html.replace("$random", ""+new Random().nextInt());
		response.write(msg);
	}

}

