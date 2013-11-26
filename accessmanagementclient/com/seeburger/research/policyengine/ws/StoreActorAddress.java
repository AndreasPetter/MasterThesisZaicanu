
package com.seeburger.research.policyengine.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for storeActorAddress complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="storeActorAddress">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="encAuthToken" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="encActorAddress" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "storeActorAddress", propOrder = {
    "encAuthToken",
    "encActorAddress"
})
public class StoreActorAddress {

    protected byte[] encAuthToken;
    protected byte[] encActorAddress;

    /**
     * Gets the value of the encAuthToken property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getEncAuthToken() {
        return encAuthToken;
    }

    /**
     * Sets the value of the encAuthToken property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setEncAuthToken(byte[] value) {
        this.encAuthToken = ((byte[]) value);
    }

    /**
     * Gets the value of the encActorAddress property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getEncActorAddress() {
        return encActorAddress;
    }

    /**
     * Sets the value of the encActorAddress property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setEncActorAddress(byte[] value) {
        this.encActorAddress = ((byte[]) value);
    }

}
