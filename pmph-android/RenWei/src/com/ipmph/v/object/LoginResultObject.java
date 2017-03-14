package com.ipmph.v.object;


public class LoginResultObject {
	
	public static LoginResultObject loginResultObject;

	public String status,message,sessionId;
	public static LoginResultObject getInstance() {
		if (loginResultObject == null) {
			loginResultObject = new LoginResultObject();
		}
		return loginResultObject;
	}
}
