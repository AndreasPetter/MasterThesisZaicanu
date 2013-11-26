
package com.seeburger.research.policyengine.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for requestKeys complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="requestKeys">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="authToken" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="requestId" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="resourceCoordinates" type="{http://ws.policyengine.research.seeburger.com/}resourceCoordinates" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "requestKeys", propOrder = {
    "authToken",
    "requestId",
    "resourceCoordinates"
})
public class RequestKeys {

    protected byte[] authToken;
    protected byte[] requestId;
    protected ResourceCoordinates resourceCoordinates;

    /**
     * Gets the value of the authToken property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getAuthToken() {
        return authToken;
    }

    /**
     * Sets the value of the authToken property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setAuthToken(byte[] value) {
        this.authToken = ((byte[]) value);
    }

    /**
     * Gets the value of the requestId property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getRequestId() {
        return requestId;
    }

    /**
     * Sets the value of the requestId property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setRequestId(byte[] value) {
        this.requestId = ((byte[]) value);
    }

    /**
     * Gets the value of the resourceCoordinates property.
     * 
     * @return
     *     possible object is
     *     {@link ResourceCoordinates }
     *     
     */
    public ResourceCoordinates getResourceCoordinates() {
        return resourceCoordinates;
    }

    /**
     * Sets the value of the resourceCoordinates property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResourceCoordinates }
     *     
     */
    public void setResourceCoordinates(ResourceCoordinates value) {
        this.resourceCoordinates = value;
    }

}
