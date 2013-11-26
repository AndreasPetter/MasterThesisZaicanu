package com.seeburger.research.securityframework.configuration;

import com.seeburger.research.securityframework.layers.keymanagement.sharing.requestor.WSAddress;

class HTTPWSAddress extends WSAddress{
	private String addr;
	
	public HTTPWSAddress(String addr){
		this.addr = addr;
	}

	@Override
	public String getAddress() {
		return addr;
	}
}
