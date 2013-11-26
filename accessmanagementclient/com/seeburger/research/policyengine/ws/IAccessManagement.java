package com.seeburger.research.policyengine.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.7.4
 * 2013-05-17T15:01:13.932+02:00
 * Generated source version: 2.7.4
 * 
 */
@WebService(targetNamespace = "http://ws.policyengine.research.seeburger.com/", name = "IAccessManagement")
@XmlSeeAlso({ObjectFactory.class})
public interface IAccessManagement {

    @RequestWrapper(localName = "requestKeys", targetNamespace = "http://ws.policyengine.research.seeburger.com/", className = "com.seeburger.research.policyengine.ws.RequestKeys")
    @WebMethod
    @ResponseWrapper(localName = "requestKeysResponse", targetNamespace = "http://ws.policyengine.research.seeburger.com/", className = "com.seeburger.research.policyengine.ws.RequestKeysResponse")
    public void requestKeys(
        @WebParam(name = "authToken", targetNamespace = "")
        byte[] authToken,
        @WebParam(name = "requestId", targetNamespace = "")
        byte[] requestId,
        @WebParam(name = "resourceCoordinates", targetNamespace = "")
        com.seeburger.research.policyengine.ws.ResourceCoordinates resourceCoordinates
    ) throws Exception_Exception;

    @RequestWrapper(localName = "storeActorAddress", targetNamespace = "http://ws.policyengine.research.seeburger.com/", className = "com.seeburger.research.policyengine.ws.StoreActorAddress")
    @WebMethod
    @ResponseWrapper(localName = "storeActorAddressResponse", targetNamespace = "http://ws.policyengine.research.seeburger.com/", className = "com.seeburger.research.policyengine.ws.StoreActorAddressResponse")
    public void storeActorAddress(
        @WebParam(name = "encAuthToken", targetNamespace = "")
        byte[] encAuthToken,
        @WebParam(name = "encActorAddress", targetNamespace = "")
        byte[] encActorAddress
    );
}