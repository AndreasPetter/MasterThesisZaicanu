package com.seeburger.research.securityframework.configuration;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.w3c.dom.Document;

import com.seeburger.research.securityframework.configuration.types.Config;
import com.seeburger.research.securityframework.configuration.types.KeyspaceAsset;
import com.seeburger.research.securityframework.configuration.types.ProviderType;
import com.seeburger.research.securityframework.layers.crypto.EncryptionLayer;
import com.seeburger.research.securityframework.layers.data.DataLayer;
import com.seeburger.research.securityframework.layers.keymanagement.KeyManagementLayer;
import com.seeburger.research.securityframework.layers.keymanagement.sharing.requestor.KeyRequestor;

class FrameworkConfigurator {

	/**
	 * The method which parses the XML file and configures the elements of the framework.
	 *
	 * @param confSource - the document object of a validated XML file
	 * @throws Exception - if keystore file is not found or some security exception is thrown while configuring the framework
	 */
	public static void configureSecurityFramework(Document confSource) throws Exception {
		JAXBContext context = JAXBContext.newInstance(Config.class);
	    Unmarshaller unMarshaller = context.createUnmarshaller();
	    Config configuration = (Config) unMarshaller.unmarshal(confSource);
	    for (ProviderType provider : configuration.getProviders().getProvider())
	    	EncryptionLayer.addProvider(provider.getProviderID(), provider.getClazz(), provider.getPreference().intValue());
	    InputStream keystoreInputStream = FrameworkConfigurator.class.getResourceAsStream(configuration.getGeneratorKeys().getLocation());
	    if (keystoreInputStream == null)
	    	throw new RuntimeException("Could not locate keystore file using path: "+configuration.getGeneratorKeys().getLocation());
	    KeyManagementLayer.extractMasterData(keystoreInputStream);
	    for (KeyspaceAsset keyspaceConf : configuration.getEncryptedAssets().getKeyspace()){
	    	KeyspaceConfiguration keyspaceConfiguration = new KeyspaceConfiguration(keyspaceConf);
	    	DataLayer.processKeyspaceConfiguration(keyspaceConfiguration.new DataLayerConf());
	    	EncryptionLayer.processKeyspaceConfiguration(keyspaceConfiguration.new EncryptionLayerConf());
	    	KeyManagementLayer.processKeyspaceConfiguration(keyspaceConfiguration.new KeyManagementLayerConf());
	    }
	    // set policy engine access data
	    KeyRequestor.setPolicyEndpoint(configuration.getKeySharingEndpoint().getAddress());
	    KeyRequestor.setAuthToken(configuration.getKeySharingEndpoint().getAuthToken());
	    return;
	}
}
