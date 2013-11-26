package com.seeburger.research.policyengine.ws;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.7.4
 * 2013-05-17T15:01:13.938+02:00
 * Generated source version: 2.7.4
 * 
 */
@WebServiceClient(name = "IAccessManagementService", 
                  wsdlLocation = "file:/E:/IMSE/4.Thesis/Projects/ThesisProject/securityframework/../PolicyEngineWS/src/main/resources/wsdl/AccessManagement.wsdl",
                  targetNamespace = "http://ws.policyengine.research.seeburger.com/") 
public class IAccessManagementService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://ws.policyengine.research.seeburger.com/", "IAccessManagementService");
    public final static QName IAccessManagementPort = new QName("http://ws.policyengine.research.seeburger.com/", "IAccessManagementPort");
    static {
        URL url = null;
        try {
            url = new URL("file:/E:/IMSE/4.Thesis/Projects/ThesisProject/securityframework/../PolicyEngineWS/src/main/resources/wsdl/AccessManagement.wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(IAccessManagementService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "file:/E:/IMSE/4.Thesis/Projects/ThesisProject/securityframework/../PolicyEngineWS/src/main/resources/wsdl/AccessManagement.wsdl");
        }
        WSDL_LOCATION = url;
    }

    public IAccessManagementService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public IAccessManagementService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public IAccessManagementService() {
        super(WSDL_LOCATION, SERVICE);
    }
    

    /**
     *
     * @return
     *     returns IAccessManagement
     */
    @WebEndpoint(name = "IAccessManagementPort")
    public IAccessManagement getIAccessManagementPort() {
        return super.getPort(IAccessManagementPort, IAccessManagement.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns IAccessManagement
     */
    @WebEndpoint(name = "IAccessManagementPort")
    public IAccessManagement getIAccessManagementPort(WebServiceFeature... features) {
        return super.getPort(IAccessManagementPort, IAccessManagement.class, features);
    }

}