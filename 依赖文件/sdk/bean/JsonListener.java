package com.lib.sdk.bean;

public interface JsonListener {
	public String getSendMsg();

	public boolean onParse(String json);
}
