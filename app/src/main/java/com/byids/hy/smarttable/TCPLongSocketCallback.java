package com.byids.hy.smarttable;

/**
 * 获取网络数据回调类
 * 
 * 
 */
public abstract interface TCPLongSocketCallback {
	
	/**
	 * 当建立连接时的回调
	 */
	public abstract void connected();

	/**
	 * 当获取网络数据回调接口
	 * 
	 * @param buffer
	 *            字节数据
	 */
	public abstract void receive(byte[] buffer);

	/**
	 * 当断开连接的回调
	 */
	public abstract void disconnect();

}
