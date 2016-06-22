package com.socks.md.resolver;

import com.google.gson.Gson;

public class GsonUtil {

	private static Gson mGson;

	private GsonUtil() {
	}

	public static Gson getInstance() {
		if (mGson == null) {
			synchronized (GsonUtil.class) {
				if (mGson == null) {
					mGson = new Gson();
				}
			}
		}
		return mGson;
	}

}
