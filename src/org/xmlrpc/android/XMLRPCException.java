package org.xmlrpc.android;

public class XMLRPCException extends cc.task3.pomotrac.exceptions.PomodroidException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7499675036625522379L;

	public XMLRPCException(Exception e) {
		super(e);
	}

	public XMLRPCException(String string) {
		super(string);
	}
}
