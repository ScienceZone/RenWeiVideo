package com.ipmph.v.object;


public class UserInfoObject {
	
	public static UserInfoObject userInfoObject;

	public String userImg,createDate,username;
	public static UserInfoObject getInstance() {
		if (userInfoObject == null) {
			userInfoObject = new UserInfoObject();
		}
		return userInfoObject;
	}
}
