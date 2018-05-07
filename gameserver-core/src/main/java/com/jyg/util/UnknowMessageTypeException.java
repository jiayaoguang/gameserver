package com.jyg.util;
/**
 * created by jiayaoguang at 2018年5月5日
 */
public class UnknowMessageTypeException extends Exception{

	private static final long serialVersionUID = 1L;

	public UnknowMessageTypeException(){
		super("Unknow message type");
		
	}
	
	public UnknowMessageTypeException(String message) {
		super(message);
	}
	
	public UnknowMessageTypeException(String message, Exception cause) {
		super(message, cause);
	}
}

