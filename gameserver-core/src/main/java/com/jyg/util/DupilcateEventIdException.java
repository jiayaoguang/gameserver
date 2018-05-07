package com.jyg.util;
/**
 * created by jiayaoguang at 2018年5月5日
 */
public class DupilcateEventIdException extends Exception{

	private static final long serialVersionUID = 1L;

	public DupilcateEventIdException(){
		super("dupilcated eventid");
		
	}
	
	public DupilcateEventIdException(String message) {
		super(message);
	}
	
	public DupilcateEventIdException(String message, Exception cause) {
		super(message, cause);
	}
}

