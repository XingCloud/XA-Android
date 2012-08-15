package com.xingcloud.analytic.user;

public interface IUserAction {
	public void reportUserAction(UserField user_data,int event);
	public void reportUserAction(int event);
}
