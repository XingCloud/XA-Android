package com.xingcloud.xa.error;

public interface IErrorAction {
	public void reportErrorAction(ErrorField errorField);
	public void reportErrorAction(String content);
	public void reportErrorAction();
}
