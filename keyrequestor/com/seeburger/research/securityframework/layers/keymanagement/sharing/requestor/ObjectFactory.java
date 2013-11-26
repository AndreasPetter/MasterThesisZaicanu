
package com.seeburger.research.securityframework.layers.keymanagement.sharing.requestor;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.seeburger.research.securityframework.layers.keymanagement.sharing.requestor package. 
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

    private final static QName _StoreKeyPackageResponse_QNAME = new QName("http://requestor.sharing.keymanagement.layers.securityframework.research.seeburger.com/", "storeKeyPackageResponse");
    private final static QName _StoreKeyPackage_QNAME = new QName("http://requestor.sharing.keymanagement.layers.securityframework.research.seeburger.com/", "storeKeyPackage");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.seeburger.research.securityframework.layers.keymanagement.sharing.requestor
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link StoreKeyPackage }
     * 
     */
    public StoreKeyPackage createStoreKeyPackage() {
        return new StoreKeyPackage();
    }

    /**
     * Create an instance of {@link KeySharingPackage }
     * 
     */
    public KeySharingPackage createKeySharingPackage() {
        return new KeySharingPackage();
    }

    /**
     * Create an instance of {@link KeyCoordinate }
     * 
     */
    public KeyCoordinate createKeyCoordinate() {
        return new KeyCoordinate();
    }

    /**
     * Create an instance of {@link StoreKeyPackageResponse }
     * 
     */
    public StoreKeyPackageResponse createStoreKeyPackageResponse() {
        return new StoreKeyPackageResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StoreKeyPackageResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://requestor.sharing.keymanagement.layers.securityframework.research.seeburger.com/", name = "storeKeyPackageResponse")
    public JAXBElement<StoreKeyPackageResponse> createStoreKeyPackageResponse(StoreKeyPackageResponse value) {
        return new JAXBElement<StoreKeyPackageResponse>(_StoreKeyPackageResponse_QNAME, StoreKeyPackageResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StoreKeyPackage }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://requestor.sharing.keymanagement.layers.securityframework.research.seeburger.com/", name = "storeKeyPackage")
    public JAXBElement<StoreKeyPackage> createStoreKeyPackage(StoreKeyPackage value) {
        return new JAXBElement<StoreKeyPackage>(_StoreKeyPackage_QNAME, StoreKeyPackage.class, null, value);
    }

}
