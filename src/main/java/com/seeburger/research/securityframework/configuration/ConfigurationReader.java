package com.seeburger.research.securityframework.configuration;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

public class ConfigurationReader {

	private static final String CONF_XML = "CryptoProviderConf.xml";
	private static final String CONF_XSD = "CryptoProviderConf.xsd";

	public static void configureFramework(String rootDir) throws Exception {
		InputStream confXml = ConfigurationReader.class
				.getResourceAsStream(rootDir + "/" + CONF_XML);
		InputStream confXsd = ConfigurationReader.class
				.getResourceAsStream(rootDir + "/" + CONF_XSD);
		Document confSource = validate(confXml, confXsd);
		FrameworkConfigurator.configureSecurityFramework(confSource);
	}

	private static Document validate(InputStream confXml, InputStream confXsd)
			throws Exception {

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		builderFactory.setNamespaceAware(true);

		DocumentBuilder parser = builderFactory.newDocumentBuilder();

		// parse the XML into a document object
		Document document = parser.parse(confXml);

		SchemaFactory factory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		// associate the schema factory with the resource resolver, which is
		// responsible for resolving the imported XSD's
		factory.setResourceResolver(new LSResourceResolver() {

			public LSInput resolveResource(String type, String namespaceURI,
					String publicId, String systemId, String baseURI) {
				InputStream resourceAsStream = this.getClass().getClassLoader()
						.getResourceAsStream(systemId);
				return new Input(publicId, systemId, resourceAsStream);
			}
		});

		// note that if your XML already declares the XSD to which it has to
		// conform, then there's no need to create a validator from a Schema
		// object
		Source schemaFile = new StreamSource(confXsd);
		Schema schema = factory.newSchema(schemaFile);
		Validator validator = schema.newValidator();
		validator.validate(new DOMSource(document));
		return document;
	}


}

class Input implements LSInput {

	private String publicId;

	private String systemId;

	public String getPublicId() {
		return publicId;
	}

	public void setPublicId(String publicId) {
		this.publicId = publicId;
	}

	public String getBaseURI() {
		return null;
	}

	public InputStream getByteStream() {
		return null;
	}

	public boolean getCertifiedText() {
		return false;
	}

	public Reader getCharacterStream() {
		return null;
	}

	public String getEncoding() {
		return null;
	}

	public String getStringData() {
		synchronized (inputStream) {
			try {
				byte[] input = new byte[inputStream.available()];
				inputStream.read(input);
				String contents = new String(input);
				return contents;
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Exception " + e);
				return null;
			}
		}
	}

	public void setBaseURI(String baseURI) {
	}

	public void setByteStream(InputStream byteStream) {
	}

	public void setCertifiedText(boolean certifiedText) {
	}

	public void setEncoding(String encoding) {
	}

	public void setStringData(String stringData) {
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public BufferedInputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(BufferedInputStream inputStream) {
		this.inputStream = inputStream;
	}

	private BufferedInputStream inputStream;

	public Input(String publicId, String sysId, InputStream input) {
		this.publicId = publicId;
		this.systemId = sysId;
		this.inputStream = new BufferedInputStream(input);
	}

	public void setCharacterStream(Reader characterStream) {
		// TODOs Auto-generated method stub

	}
}
