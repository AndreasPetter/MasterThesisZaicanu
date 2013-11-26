
package com.seeburger.research.securityframework.layers.keymanagement.sharing.requestor;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for keySharingPackage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="keySharingPackage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="keyspaceName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="columnFamilyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="correlationId" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="keys" type="{http://requestor.sharing.keymanagement.layers.securityframework.research.seeburger.com/}keyCoordinate" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "keySharingPackage", propOrder = {
    "keyspaceName",
    "columnFamilyName",
    "correlationId",
    "keys"
})
public class KeySharingPackage {

    protected String keyspaceName;
    protected String columnFamilyName;
    protected byte[] correlationId;
    @XmlElement(nillable = true)
    protected List<KeyCoordinate> keys;

    /**
     * Gets the value of the keyspaceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeyspaceName() {
        return keyspaceName;
    }

    /**
     * Sets the value of the keyspaceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeyspaceName(String value) {
        this.keyspaceName = value;
    }

    /**
     * Gets the value of the columnFamilyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColumnFamilyName() {
        return columnFamilyName;
    }

    /**
     * Sets the value of the columnFamilyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColumnFamilyName(String value) {
        this.columnFamilyName = value;
    }

    /**
     * Gets the value of the correlationId property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getCorrelationId() {
        return correlationId;
    }

    /**
     * Sets the value of the correlationId property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setCorrelationId(byte[] value) {
        this.correlationId = ((byte[]) value);
    }

    /**
     * Gets the value of the keys property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the keys property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKeys().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link KeyCoordinate }
     * 
     * 
     */
    public List<KeyCoordinate> getKeys() {
        if (keys == null) {
            keys = new ArrayList<KeyCoordinate>();
        }
        return this.keys;
    }

}
