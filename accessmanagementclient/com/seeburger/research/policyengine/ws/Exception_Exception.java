
package com.seeburger.research.policyengine.ws;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.7.4
 * 2013-05-17T15:01:13.926+02:00
 * Generated source version: 2.7.4
 */

@WebFault(name = "Exception", targetNamespace = "http://ws.policyengine.research.seeburger.com/")
public class Exception_Exception extends java.lang.Exception {
    
    private com.seeburger.research.policyengine.ws.Exception exception;

    public Exception_Exception() {
        super();
    }
    
    public Exception_Exception(String message) {
        super(message);
    }
    
    public Exception_Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public Exception_Exception(String message, com.seeburger.research.policyengine.ws.Exception exception) {
        super(message);
        this.exception = exception;
    }

    public Exception_Exception(String message, com.seeburger.research.policyengine.ws.Exception exception, Throwable cause) {
        super(message, cause);
        this.exception = exception;
    }

    public com.seeburger.research.policyengine.ws.Exception getFaultInfo() {
        return this.exception;
    }
}
