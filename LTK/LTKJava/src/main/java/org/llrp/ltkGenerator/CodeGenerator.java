package org.llrp.ltkGenerator;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.llrp.ltkGenerator.generated.ChoiceDefinition;
import org.llrp.ltkGenerator.generated.CustomChoiceDefinition;
import org.llrp.ltkGenerator.generated.CustomEnumerationDefinition;
import org.llrp.ltkGenerator.generated.CustomMessageDefinition;
import org.llrp.ltkGenerator.generated.CustomParameterDefinition;
import org.llrp.ltkGenerator.generated.EnumerationDefinition;
import org.llrp.ltkGenerator.generated.LlrpDefinition;
import org.llrp.ltkGenerator.generated.MessageDefinition;
import org.llrp.ltkGenerator.generated.ParameterDefinition;

/**
 * generates LLRP messages, parameters, enumeration from the definitions in
 * llrpdef.xml and any extensions definitions in specified in the generator.properties file.
 * 
 * the generations process uses apache velocity template engine. Each MessageDefinition,
 * ParameterDefinition, ChoiceDefinition, ... in llrpdef.xml is applied to a template.
 * The resulting Java classes are stored at the locations specified in the propery file.
 * 
 * Extensions can be specified by listing the path to the extension 
 * "extensionXMLs" propery in generator.properties e.g.
 * extensionXMLs = src/main/resources/customExtensions.xml; 
 * 
 */


public class CodeGenerator {
    public static Logger logger = Logger.getLogger(Class.class.getName());
    public Properties properties;
    private List<ParameterDefinition> parameters;
    private List<MessageDefinition> messages;
    private List<EnumerationDefinition> enumerations;
    private List<ChoiceDefinition> choices;
    private List<CustomParameterDefinition> customParams;
    private List<CustomMessageDefinition> customMessages;
    private List<CustomChoiceDefinition> customChoices;
    private List<CustomEnumerationDefinition> customEnumerations;
    private Utility utility;

    /**
     * instantiate new code generator - probably want to call generate after.
     *
     * @param propertiesFile
     *            path to properties file
     */
    public CodeGenerator(String propertiesFile) {
        try {
            System.out.println(System.getProperty("user.dir"));
            properties = new Properties();
            properties.load(new FileInputStream(propertiesFile));
            PropertyConfigurator.configure(properties);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("file " + propertiesFile +
                " not found");
        } catch (IOException e) {
            throw new IllegalArgumentException("file " + propertiesFile +
                " can not be read");
        }

        parameters = new LinkedList<ParameterDefinition>();
        messages = new LinkedList<MessageDefinition>();
        enumerations = new LinkedList<EnumerationDefinition>();
        choices = new LinkedList<ChoiceDefinition>();
        customParams = new LinkedList<CustomParameterDefinition>();
        customMessages = new LinkedList<CustomMessageDefinition>();
        customChoices = new LinkedList<CustomChoiceDefinition>();
        customEnumerations = new LinkedList<CustomEnumerationDefinition>();
        utility = new Utility(properties);
        utility.setChoices(choices);
        utility.setCustomChoices(customChoices);
    }

    /**
     * generates LLRP messages, parameters, enumeration from the definitions in
     * llrpdef.xml and any extensions definitions in specified in the generator.properties file.
     */
    public void generate() {
        logger.debug("start code generation");
        logger.debug("retrieve llrp definitions");

        String jaxBPackage = properties.getProperty("jaxBPackage");
        String llrpSchemaPath = properties.getProperty("llrpSchema");
        String llrpXMLPath = properties.getProperty("llrpXML");
        String extensionsPath = properties.getProperty("extensionXMLs");

        if (extensionsPath == null) {
            // if no extensions provided it is null - avoid null pointer
            // exception
            extensionsPath = "";
        }

        LlrpDefinition llrp = LLRPUnmarshaller.getLLRPDefinition(jaxBPackage,
                llrpSchemaPath, llrpXMLPath, extensionsPath);
        logger.debug("finished retrieving llrp definitions");
        logger.debug("start filling objects");
        fillObjects(llrp);
        createLookupMaps();
        logger.debug("finished filling objects");
        // generateCustom must be before Parameters because it sets the allowed
        // in values
        logger.debug("start generating custom parameters");
        generateCustomParameters();
        logger.debug("finished generating custom parameters");
        // generateMessages() and generateParameters must be executed before
        // generateEnumerations because enumeration supertypes are determined in
        // this methods
        logger.debug("start generating messages");
        generateMessages();
        logger.debug("finished generating messages");
        logger.debug("start generating messageFactory");
        generateMessageFactory();
        logger.debug("finished generating messageFactory");
        logger.debug("start generating parameters");
        generateParameters();
        logger.debug("finished generating parameters");
        logger.debug("start generating interfaces");
        generateInterfaces();
        logger.debug("finished generating interfaces");
        logger.debug("start generating enumerations");
        generateEnumerations();
        logger.debug("finished generating enumerations");
        logger.debug("start generating custom messages");
        generateCustomMessages();
        logger.debug("finished generating custom messages");
        logger.debug("start generating custom enumerations");
        generateCustomEnumerations();
        logger.debug("finished generating custom enumerations");
        logger.debug("start generating custom choices");
        generateCustomInterfaces();
        logger.debug("finished generating custom choices");
        logger.debug("start generating constants");
        generateConstants();
        logger.debug("finished generatins constants");
    }

    /**
     * generates LLRPMessageFactory.java using the MessageFactoryTemplate.
     */
    private void generateMessageFactory() {
        logger.debug("using template " +
            properties.getProperty("messageFactoryTemplate"));
        logger.debug("generating MessageFactory");

        try {
            VelocityContext context = new VelocityContext();
            context.put("messages", messages);

            Template template = Velocity.getTemplate(properties.getProperty(
                        "messageFactoryTemplate"));
            BufferedWriter writer = new BufferedWriter(new FileWriter(properties.getProperty(
                            "generatedMessagePackage") + "LLRPMessageFactory" +
                        properties.getProperty("fileEnding")));
            template.merge(context, writer);
            writer.flush();
            writer.close();
        } catch (ResourceNotFoundException e) {
            logger.error("Exception while generating code: " +
                e.getLocalizedMessage() + " caused by " + e.getCause());
        } catch (ParseErrorException e) {
            logger.error("Exception while generating code: " +
                e.getLocalizedMessage() + " caused by " + e.getCause());
        } catch (MethodInvocationException e) {
            logger.error("Exception while generating code: " +
                e.getLocalizedMessage() + " caused by " + e.getCause());
        } catch (IOException e) {
            logger.error("Exception while generating code: " +
                e.getLocalizedMessage() + " caused by " + e.getCause());
        } catch (Exception e) {
            logger.error("Exception while generating code: " +
                e.getLocalizedMessage() + " caused by " + e.getCause());
        }
    }

    /**
     * generates LLRP Messages using the MessageTemplate and the definitions in llrpdef.xml.
     */
    private void generateMessages() {
        // set xml schema location in LLRPMessage. This is necessary to validate
        // xml messages
        logger.debug(messages.size() + " messages to generate");
        logger.debug("using template " +
            properties.getProperty("messageTemplate"));
        logger.debug("generating files into " +
            properties.getProperty("generatedMessagePackage"));

        for (MessageDefinition m : messages) {
            try {
                VelocityContext context = new VelocityContext();
                context.put("message", m);
                context.put("utility", utility);
                context.put("XMLSCHEMALOCATION",
                    properties.getProperty("messageSchema"));

                Template template = Velocity.getTemplate(properties.getProperty(
                            "messageTemplate"));
                BufferedWriter writer = new BufferedWriter(new FileWriter(properties.getProperty(
                                "generatedMessagePackage") + m.getName() +
                            properties.getProperty("fileEnding")));
                template.merge(context, writer);
                writer.flush();
                writer.close();
            } catch (ResourceNotFoundException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (ParseErrorException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (MethodInvocationException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (IOException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (Exception e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            }
        }
    }

    /**
     * generates LLRP Parameters using the ParameterTemplate and the definitions in llrpdef.xml.
     */
    private void generateParameters() {
        logger.debug(parameters.size() + " parameters to generate");

        logger.debug("using template " +
            properties.getProperty("parameterTemplate"));
        logger.debug("generating files into " +
            properties.getProperty("parameterMessagePackage"));

        for (ParameterDefinition p : parameters) {
            try {
                VelocityContext context = new VelocityContext();
                context.put("parameter", p);
                context.put("choices", choices);
                context.put("utility", utility);

                Template template = Velocity.getTemplate(properties.getProperty(
                            "parameterTemplate"));
                BufferedWriter writer = new BufferedWriter(new FileWriter(properties.getProperty(
                                "generatedParameterPackage") + p.getName() +
                            properties.getProperty("fileEnding")));
                template.merge(context, writer);
                writer.flush();
                writer.close();
            } catch (ResourceNotFoundException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (ParseErrorException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (MethodInvocationException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (IOException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (Exception e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            }
        }
    }

    
    /**
     * generates interfaces that represent the choice constructs in llrpdef.xml using the InterfaceTemplate.
     */
    
    private void generateInterfaces() {
        logger.debug(choices.size() + " interfaces to generate");
        logger.debug("using template " +
            properties.getProperty("interfaceTemplate"));
        logger.debug("generating files into " +
            properties.getProperty("generatedInterfacePackage"));

        for (ChoiceDefinition cd : choices) {
            try {
                VelocityContext context = new VelocityContext();
                context.put("interface", cd);
                context.put("utility", utility);

                Template template = Velocity.getTemplate(properties.getProperty(
                            "interfaceTemplate"));
                BufferedWriter writer = new BufferedWriter(new FileWriter(properties.getProperty(
                                "generatedInterfacePackage") + cd.getName() +
                            properties.getProperty("fileEnding")));
                template.merge(context, writer);
                writer.flush();
                writer.close();
            } catch (ResourceNotFoundException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (ParseErrorException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (MethodInvocationException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (IOException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (Exception e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            }
        }
    }
    
    

    
    /**
     * generates custom interfaces that represent the choice constructs in a vendor extension xml using the CustomInterfaceTemplate.
     */
    
    private void generateCustomInterfaces() {
        logger.debug(choices.size() + " interfaces to generate");
        logger.debug("using template " +
            properties.getProperty("customInterfaceTemplate"));
        logger.debug("generating files into " +
            properties.getProperty("generatedCustomInterfacePackage"));

        for (CustomChoiceDefinition cd : customChoices) {
            try {
                VelocityContext context = new VelocityContext();
                context.put("interface", cd);
                context.put("utility", utility);

                Template template = Velocity.getTemplate(properties.getProperty(
                            "customInterfaceTemplate"));
                BufferedWriter writer = new BufferedWriter(new FileWriter(properties.getProperty(
                                "generatedCustomInterfacePackage") + cd.getName() +
                            properties.getProperty("fileEnding")));
                template.merge(context, writer);
                writer.flush();
                writer.close();
            } catch (ResourceNotFoundException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (ParseErrorException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (MethodInvocationException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (IOException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (Exception e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            }
        }
    }

    /**
     * generates enumerations that represent the enumerations defined in llrpdef.xml using the EnumerationTemplate.
     */
    
    private void generateEnumerations() {
        logger.debug(enumerations.size() + " enumerations to generate");
        logger.debug("using template " +
            properties.getProperty("enumerationTemplate"));
        logger.debug("generating files into " +
            properties.getProperty("generatedEnumerationPackage"));

        for (EnumerationDefinition enu : enumerations) {
            try {
                VelocityContext context = new VelocityContext();
                context.put("enum", enu);
                context.put("utility", utility);

                Template template = Velocity.getTemplate(properties.getProperty(
                            "enumerationTemplate"));
                BufferedWriter writer = new BufferedWriter(new FileWriter(properties.getProperty(
                                "generatedEnumerationPackage") + enu.getName() +
                            properties.getProperty("fileEnding")));
                template.merge(context, writer);
                writer.flush();
                writer.close();
            } catch (ResourceNotFoundException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (ParseErrorException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (MethodInvocationException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (IOException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (Exception e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            }
        }
    }
    
    /**
     * create maps to look up if a enumeration or choice is a custom enumeration, choice respectively.
     * Call before creating java files
     */
    private void createLookupMaps(){
    	for (CustomEnumerationDefinition cust : customEnumerations){
    		utility.addCustomEnumeration(cust.getName());
    	}
    	for (CustomChoiceDefinition cust : customChoices){
    		utility.addCustomChoice(cust.getName());
    	}
    	for (CustomMessageDefinition cust : customMessages){
    		utility.addCustomMessage(cust.getName());
    	}
    	for (CustomParameterDefinition cust : customParams){
    		utility.addCustomParameter(cust.getName());
    	}
    }

    /**
     * generates custom enumerations that represent the custom enumerations defined in a vendor xml using the CustomEnumerationTemplate.
     */
    
    private void generateCustomEnumerations() {
        logger.debug(enumerations.size() + " custom enumerations to generate");
        logger.debug("using template " +
            properties.getProperty("customEnumerationTemplate"));
        logger.debug("generating files into " +
            properties.getProperty("generatedCustomEnumerationPackage"));

        for (CustomEnumerationDefinition enu : customEnumerations) {
            try {
                VelocityContext context = new VelocityContext();
                context.put("enum", enu);
                
                context.put("utility", utility);
                
                Template template = Velocity.getTemplate(properties.getProperty(
                            "customEnumerationTemplate"));
                BufferedWriter writer = new BufferedWriter(new FileWriter(properties.getProperty(
                                "generatedCustomEnumerationPackage") + enu.getName() +
                            properties.getProperty("fileEnding")));
                template.merge(context, writer);
                writer.flush();
                writer.close();
            } catch (ResourceNotFoundException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (ParseErrorException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (MethodInvocationException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (IOException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (Exception e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            }
        }
    }
    
    /**
     * generates custom parameters using the CustomParameterTemplate and user defined parameters in 
     * an xml file that validates against llrpdef.xsd
     */

    private void generateCustomParameters() {
        logger.debug(customParams.size() + " custom parameters to generate");
        logger.debug("using template " +
            properties.getProperty("customParameterTemplate"));
        logger.debug("generating files into " +
            properties.getProperty("generatedCustomParameterPackage"));

        for (CustomParameterDefinition cd : customParams) {
            try {
                VelocityContext context = new VelocityContext();
                context.put("custom", cd);
                context.put("utility", utility);

                Template template = Velocity.getTemplate(properties.getProperty(
                            "customParameterTemplate"));
                BufferedWriter writer = new BufferedWriter(new FileWriter(properties.getProperty(
                                "generatedCustomParameterPackage") +
                            cd.getName() +
                            properties.getProperty("fileEnding")));
                template.merge(context, writer);
                writer.flush();
                writer.close();
            } catch (ResourceNotFoundException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (ParseErrorException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (MethodInvocationException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (IOException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (Exception e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            }
        }
    }
    
    /**
     * generates custom messages using the CustomMessageTemplate and user defined messages from 
     * an xml file that validates against llrpdef.xsd
     */

    private void generateCustomMessages() {
        logger.debug(customMessages.size() + " custom messages to generate");
        logger.debug("using template " +
            properties.getProperty("customMessageTemplate"));
        logger.debug("generating files into " +
            properties.getProperty("generatedCustomMessagePackage"));

        for (CustomMessageDefinition cd : customMessages) {
            try {
                VelocityContext context = new VelocityContext();
                context.put("message", cd);
                context.put("utility", utility);

                Template template = Velocity.getTemplate(properties.getProperty(
                            "customMessageTemplate"));
                BufferedWriter writer = new BufferedWriter(new FileWriter(properties.getProperty(
                                "generatedCustomMessagePackage") +
                            cd.getName() +
                            properties.getProperty("fileEnding")));
                template.merge(context, writer);
                writer.flush();
                writer.close();
            } catch (ResourceNotFoundException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (ParseErrorException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (MethodInvocationException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (IOException e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            } catch (Exception e) {
                logger.error("Exception while generating code: " +
                    e.getLocalizedMessage() + " caused by " + e.getCause());
            }
        }
    }
    
    
    /**
     * generates LLRPConstants.java from properties defined in generator.properties
     * this properties include NamespacePrefix for XML encoding, namespace etc.
     */

    private void generateConstants() {
        logger.debug("using template " +
            properties.getProperty("constantsTemplate"));
        logger.debug("generating files into " +
            properties.getProperty("generateConstantsPackage"));

        try {
            VelocityContext context = new VelocityContext();
            context.put("schemaPath",
                properties.getProperty("XMLEncodingSchemaPath"));
            context.put("namespace",
                properties.getProperty("XMLEncodingNamespace"));
            context.put("XMLEncodingSchema",
                properties.getProperty("XMLEncodingSchema"));

            context.put("NamespacePrefix",
                properties.getProperty("NamespacePrefix"));

            Template template = Velocity.getTemplate(properties.getProperty(
                        "constantsTemplate"));
            BufferedWriter writer = new BufferedWriter(new FileWriter(properties.getProperty(
                            "generateConstantsPackage") + "LLRPConstants" +
                        properties.getProperty("fileEnding")));
            template.merge(context, writer);
            writer.flush();
            writer.close();
        } catch (ResourceNotFoundException e) {
            logger.error("Exception while generating code: " +
                e.getLocalizedMessage() + " caused by " + e.getCause());
        } catch (ParseErrorException e) {
            logger.error("Exception while generating code: " +
                e.getLocalizedMessage() + " caused by " + e.getCause());
        } catch (MethodInvocationException e) {
            logger.error("Exception while generating code: " +
                e.getLocalizedMessage() + " caused by " + e.getCause());
        } catch (IOException e) {
            logger.error("Exception while generating code: " +
                e.getLocalizedMessage() + " caused by " + e.getCause());
        } catch (Exception e) {
            logger.error("Exception while generating code: " +
                e.getLocalizedMessage() + " caused by " + e.getCause());
        }
    }
    
    /**
     * add all messages, parameters, or choice defintions to corresponding lists. Each item in these lists 
     * will later be passed to the velocity context which generates the corresponding java class. 
     * 
     * @param llrp java objects of definitions from llrpdef.xml
     */

    private void fillObjects(LlrpDefinition llrp) {
        List<Object> childs = llrp.getMessageDefinitionOrParameterDefinitionOrChoiceDefinition();

        for (Object o : childs) {
            if (o instanceof ParameterDefinition) {
                parameters.add((ParameterDefinition) o);
            } else if (o instanceof MessageDefinition) {
                messages.add((MessageDefinition) o);
            } else if (o instanceof EnumerationDefinition) {
                enumerations.add((EnumerationDefinition) o);
            } else if (o instanceof ChoiceDefinition) {
                choices.add((ChoiceDefinition) o);
            } else if (o instanceof CustomParameterDefinition) {
                customParams.add((CustomParameterDefinition) o);
            } else if (o instanceof CustomMessageDefinition) {
                customMessages.add((CustomMessageDefinition) o);
            } else if (o instanceof CustomEnumerationDefinition) {
                customEnumerations.add((CustomEnumerationDefinition) o);
            } else if (o instanceof CustomChoiceDefinition) {
                customChoices.add((CustomChoiceDefinition) o);
            } else {
                logger.warn("type not used: " + o.getClass() +
                    " in CodeGenerator.fillObjects");
            }
        }
    }

    public static void main(String[] args) {
        String propertiesFile = null;

        try {
            propertiesFile = args[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(
                "usage: provide path to properties file as first and only parammeter");
        }

        CodeGenerator cg = new CodeGenerator(propertiesFile);
        cg.generate();
    }

	public List<CustomChoiceDefinition> getCustomChoices() {
		return customChoices;
	}

	public void setCustomChoices(List<CustomChoiceDefinition> customChoices) {
		this.customChoices = customChoices;
	}
}