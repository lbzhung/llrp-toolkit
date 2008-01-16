//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.5-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.01.15 at 01:21:31 PM GMT+01:00 
//


package org.llrp.ltkGenerator.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.llrp.ltkGenerator.generated package. 
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

    private final static QName _Llrpdef_QNAME = new QName("http://www.llrp.org/ltk/schema/core/encoding/binary/1.0", "llrpdef");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.llrp.ltkGenerator.generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ParameterReference }
     * 
     */
    public ParameterReference createParameterReference() {
        return new ParameterReference();
    }

    /**
     * Create an instance of {@link LlrpDefinition }
     * 
     */
    public LlrpDefinition createLlrpDefinition() {
        return new LlrpDefinition();
    }

    /**
     * Create an instance of {@link MessageDefinition }
     * 
     */
    public MessageDefinition createMessageDefinition() {
        return new MessageDefinition();
    }

    /**
     * Create an instance of {@link Documentation }
     * 
     */
    public Documentation createDocumentation() {
        return new Documentation();
    }

    /**
     * Create an instance of {@link EnumerationEntryDefinition }
     * 
     */
    public EnumerationEntryDefinition createEnumerationEntryDefinition() {
        return new EnumerationEntryDefinition();
    }

    /**
     * Create an instance of {@link CustomMessageDefinition }
     * 
     */
    public CustomMessageDefinition createCustomMessageDefinition() {
        return new CustomMessageDefinition();
    }

    /**
     * Create an instance of {@link ChoiceReference }
     * 
     */
    public ChoiceReference createChoiceReference() {
        return new ChoiceReference();
    }

    /**
     * Create an instance of {@link ParameterDefinition }
     * 
     */
    public ParameterDefinition createParameterDefinition() {
        return new ParameterDefinition();
    }

    /**
     * Create an instance of {@link VendorDefinition }
     * 
     */
    public VendorDefinition createVendorDefinition() {
        return new VendorDefinition();
    }

    /**
     * Create an instance of {@link CustomEnumerationDefinition }
     * 
     */
    public CustomEnumerationDefinition createCustomEnumerationDefinition() {
        return new CustomEnumerationDefinition();
    }

    /**
     * Create an instance of {@link CustomParameterDefinition }
     * 
     */
    public CustomParameterDefinition createCustomParameterDefinition() {
        return new CustomParameterDefinition();
    }

    /**
     * Create an instance of {@link ReservedDefinition }
     * 
     */
    public ReservedDefinition createReservedDefinition() {
        return new ReservedDefinition();
    }

    /**
     * Create an instance of {@link ChoiceDefinition }
     * 
     */
    public ChoiceDefinition createChoiceDefinition() {
        return new ChoiceDefinition();
    }

    /**
     * Create an instance of {@link AllowedInParameterReference }
     * 
     */
    public AllowedInParameterReference createAllowedInParameterReference() {
        return new AllowedInParameterReference();
    }

    /**
     * Create an instance of {@link ChoiceParameterReference }
     * 
     */
    public ChoiceParameterReference createChoiceParameterReference() {
        return new ChoiceParameterReference();
    }

    /**
     * Create an instance of {@link Annotation }
     * 
     */
    public Annotation createAnnotation() {
        return new Annotation();
    }

    /**
     * Create an instance of {@link NamespaceDefinition }
     * 
     */
    public NamespaceDefinition createNamespaceDefinition() {
        return new NamespaceDefinition();
    }

    /**
     * Create an instance of {@link FieldDefinition }
     * 
     */
    public FieldDefinition createFieldDefinition() {
        return new FieldDefinition();
    }

    /**
     * Create an instance of {@link CustomChoiceDefinition }
     * 
     */
    public CustomChoiceDefinition createCustomChoiceDefinition() {
        return new CustomChoiceDefinition();
    }

    /**
     * Create an instance of {@link Description }
     * 
     */
    public Description createDescription() {
        return new Description();
    }

    /**
     * Create an instance of {@link EnumerationDefinition }
     * 
     */
    public EnumerationDefinition createEnumerationDefinition() {
        return new EnumerationDefinition();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LlrpDefinition }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.llrp.org/ltk/schema/core/encoding/binary/1.0", name = "llrpdef")
    public JAXBElement<LlrpDefinition> createLlrpdef(LlrpDefinition value) {
        return new JAXBElement<LlrpDefinition>(_Llrpdef_QNAME, LlrpDefinition.class, null, value);
    }

}
