//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.05.15 at 11:08:27 PM CEST 
//


package com.seeburger.research.securityframework.configuration.types;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for supportedAuthenticatedEncryptionAlgorithmNameEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="supportedAuthenticatedEncryptionAlgorithmNameEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="AES/GCM/NoPadding"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "supportedAuthenticatedEncryptionAlgorithmNameEnum")
@XmlEnum
public enum SupportedAuthenticatedEncryptionAlgorithmNameEnum {

    @XmlEnumValue("AES/GCM/NoPadding")
    AES_GCM_NO_PADDING("AES/GCM/NoPadding");
    private final String value;

    SupportedAuthenticatedEncryptionAlgorithmNameEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SupportedAuthenticatedEncryptionAlgorithmNameEnum fromValue(String v) {
        for (SupportedAuthenticatedEncryptionAlgorithmNameEnum c: SupportedAuthenticatedEncryptionAlgorithmNameEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
