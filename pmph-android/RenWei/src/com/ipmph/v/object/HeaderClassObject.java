package com.ipmph.v.object;

import java.util.ArrayList;

public class HeaderClassObject {
	public String videoclassName, videoclassID;
	public ArrayList<SubClassObject> zclassList;

	public class SubClassObject {
		public String videoclassName, videoclassID;
	}
}
