package com.seeburger.research.securityframework.layers.keymanagement.sharing.owner;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface IKeySharingOwnerService {
	public void shareKeys(
			@WebParam(name="encryptedRequestId")
			byte[] correlationId, 
			@WebParam(name="resourceCoordinates")
			ResourceCoordinates resourceCoordinates,
			@WebParam(name="receiverCertificate")
			byte[] certificate);
}
