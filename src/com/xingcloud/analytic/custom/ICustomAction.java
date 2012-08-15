package com.xingcloud.analytic.custom;

public interface ICustomAction {
	public void reportCustomAction(CustomField custom_data);
	public void reportCustomAction(SignedParams param,Stats stats);
	public void reportCustomAction(String custom_data);
	public void reportCustomAction();
	public void reportBatchAction(String custom_data);
}
