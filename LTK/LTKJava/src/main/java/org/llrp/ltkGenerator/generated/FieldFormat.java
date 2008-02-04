//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.5-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.01.15 at 01:21:31 PM GMT+01:00 
//


package org.llrp.ltkGenerator.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for fieldFormat.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="fieldFormat">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Dec"/>
 *     &lt;enumeration value="Hex"/>
 *     &lt;enumeration value="Datetime"/>
 *     &lt;enumeration value="UTF8"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "fieldFormat")
@XmlEnum
public enum FieldFormat {

    @XmlEnumValue("Dec")
    DEC("Dec"),
    @XmlEnumValue("Hex")
    HEX("Hex"),
    @XmlEnumValue("Datetime")
    DATETIME("Datetime"),
    @XmlEnumValue("UTF8")
    UTF_8("UTF8");
    private final String value;

    FieldFormat(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FieldFormat fromValue(String v) {
        for (FieldFormat c: FieldFormat.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}