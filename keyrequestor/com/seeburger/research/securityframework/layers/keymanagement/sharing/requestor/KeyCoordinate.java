
package com.seeburger.research.securityframework.layers.keymanagement.sharing.requestor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for keyCoordinate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="keyCoordinate">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="rowid" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="rowSerializerClass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="columnName" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="columnNameSerializerClass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="key1" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="key2" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "keyCoordinate", propOrder = {
    "rowid",
    "rowSerializerClass",
    "columnName",
    "columnNameSerializerClass",
    "key1",
    "key2"
})
public class KeyCoordinate {

    protected byte[] rowid;
    protected String rowSerializerClass;
    protected byte[] columnName;
    protected String columnNameSerializerClass;
    protected byte[] key1;
    protected byte[] key2;

    /**
     * Gets the value of the rowid property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getRowid() {
        return rowid;
    }

    /**
     * Sets the value of the rowid property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setRowid(byte[] value) {
        this.rowid = ((byte[]) value);
    }

    /**
     * Gets the value of the rowSerializerClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRowSerializerClass() {
        return rowSerializerClass;
    }

    /**
     * Sets the value of the rowSerializerClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRowSerializerClass(String value) {
        this.rowSerializerClass = value;
    }

    /**
     * Gets the value of the columnName property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getColumnName() {
        return columnName;
    }

    /**
     * Sets the value of the columnName property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setColumnName(byte[] value) {
        this.columnName = ((byte[]) value);
    }

    /**
     * Gets the value of the columnNameSerializerClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColumnNameSerializerClass() {
        return columnNameSerializerClass;
    }

    /**
     * Sets the value of the columnNameSerializerClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColumnNameSerializerClass(String value) {
        this.columnNameSerializerClass = value;
    }

    /**
     * Gets the value of the key1 property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getKey1() {
        return key1;
    }

    /**
     * Sets the value of the key1 property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setKey1(byte[] value) {
        this.key1 = ((byte[]) value);
    }

    /**
     * Gets the value of the key2 property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getKey2() {
        return key2;
    }

    /**
     * Sets the value of the key2 property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setKey2(byte[] value) {
        this.key2 = ((byte[]) value);
    }

}
