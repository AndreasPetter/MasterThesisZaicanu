
package com.seeburger.research.policyengine.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for resourceCoordinates complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="resourceCoordinates">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cfName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="columnName" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="columnNameSerializerClass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="keyspace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="rowid" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="rowidSerializerClass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resourceCoordinates", propOrder = {
    "cfName",
    "columnName",
    "columnNameSerializerClass",
    "keyspace",
    "rowid",
    "rowidSerializerClass"
})
public class ResourceCoordinates {

    protected String cfName;
    protected byte[] columnName;
    protected String columnNameSerializerClass;
    protected String keyspace;
    protected byte[] rowid;
    protected String rowidSerializerClass;

    /**
     * Gets the value of the cfName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCfName() {
        return cfName;
    }

    /**
     * Sets the value of the cfName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCfName(String value) {
        this.cfName = value;
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
     * Gets the value of the keyspace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeyspace() {
        return keyspace;
    }

    /**
     * Sets the value of the keyspace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeyspace(String value) {
        this.keyspace = value;
    }

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
     * Gets the value of the rowidSerializerClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRowidSerializerClass() {
        return rowidSerializerClass;
    }

    /**
     * Sets the value of the rowidSerializerClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRowidSerializerClass(String value) {
        this.rowidSerializerClass = value;
    }

}
