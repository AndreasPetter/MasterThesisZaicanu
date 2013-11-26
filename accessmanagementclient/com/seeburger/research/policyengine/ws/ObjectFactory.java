
package com.seeburger.research.policyengine.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.seeburger.research.policyengine.ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _StoreActorAddress_QNAME = new QName("http://ws.policyengine.research.seeburger.com/", "storeActorAddress");
    private final static QName _RequestKeys_QNAME = new QName("http://ws.policyengine.research.seeburger.com/", "requestKeys");
    private final static QName _StoreActorAddressResponse_QNAME = new QName("http://ws.policyengine.research.seeburger.com/", "storeActorAddressResponse");
    private final static QName _RequestKeysResponse_QNAME = new QName("http://ws.policyengine.research.seeburger.com/", "requestKeysResponse");
    private final static QName _Exception_QNAME = new QName("http://ws.policyengine.research.seeburger.com/", "Exception");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.seeburger.research.policyengine.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link StoreActorAddressResponse }
     * 
     */
    public StoreActorAddressResponse createStoreActorAddressResponse() {
        return new StoreActorAddressResponse();
    }

    /**
     * Create an instance of {@link StoreActorAddress }
     * 
     */
    public StoreActorAddress createStoreActorAddress() {
        return new StoreActorAddress();
    }

    /**
     * Create an instance of {@link RequestKeysResponse }
     * 
     */
    public RequestKeysResponse createRequestKeysResponse() {
        return new RequestKeysResponse();
    }

    /**
     * Create an instance of {@link RequestKeys }
     * 
     */
    public RequestKeys createRequestKeys() {
        return new RequestKeys();
    }

    /**
     * Create an instance of {@link Exception }
     * 
     */
    public Exception createException() {
        return new Exception();
    }

    /**
     * Create an instance of {@link ResourceCoordinates }
     * 
     */
    public ResourceCoordinates createResourceCoordinates() {
        return new ResourceCoordinates();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StoreActorAddress }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.policyengine.research.seeburger.com/", name = "storeActorAddress")
    public JAXBElement<StoreActorAddress> createStoreActorAddress(StoreActorAddress value) {
        return new JAXBElement<StoreActorAddress>(_StoreActorAddress_QNAME, StoreActorAddress.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestKeys }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.policyengine.research.seeburger.com/", name = "requestKeys")
    public JAXBElement<RequestKeys> createRequestKeys(RequestKeys value) {
        return new JAXBElement<RequestKeys>(_RequestKeys_QNAME, RequestKeys.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StoreActorAddressResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.policyengine.research.seeburger.com/", name = "storeActorAddressResponse")
    public JAXBElement<StoreActorAddressResponse> createStoreActorAddressResponse(StoreActorAddressResponse value) {
        return new JAXBElement<StoreActorAddressResponse>(_StoreActorAddressResponse_QNAME, StoreActorAddressResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestKeysResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.policyengine.research.seeburger.com/", name = "requestKeysResponse")
    public JAXBElement<RequestKeysResponse> createRequestKeysResponse(RequestKeysResponse value) {
        return new JAXBElement<RequestKeysResponse>(_RequestKeysResponse_QNAME, RequestKeysResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Exception }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.policyengine.research.seeburger.com/", name = "Exception")
    public JAXBElement<Exception> createException(Exception value) {
        return new JAXBElement<Exception>(_Exception_QNAME, Exception.class, null, value);
    }

}
