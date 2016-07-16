package com.byids.hy.smarttable;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

/*
 TcpSocket
 */
public class TcpLongSocket {
	private DataOutputStream out;   // 发送数据流
	private DataInputStream in;   // 接收数据流
	private Socket mSocket;  // socket连接对象
	private SocketAddress address;
	private int timeOut = 1000 * 30;// 延迟时间
	// 启动�?个线程，不停接收服务器数据
	private RecThrad recThread;// 接收数据线程
	private SendThread sendThread;// 发送数据线程
	private ConnectThread connThread;//连接线程
	private boolean threadBoo = true;
	private TCPLongSocketCallback callBack;// 回调接口
	private byte[] buffer = new byte[1024 * 1];// 缓冲区字节数组，信息不能大于此缓冲区
	private byte[] tmpBuffer;// 临时缓冲字节数组
	private static List<byte[]> datas = new ArrayList<byte[]>();// 待发送数据队列
	private TcpLongSocket tcplongSocket;

	// 构造函数
	public TcpLongSocket(TCPLongSocketCallback callback) {
		this.callBack = callback;

	}

	// 开始连接
	public void startConnect(String ip, int port) {
		// 启动一个接收线程
		// try {
		connThread = new ConnectThread(ip, port);
		connThread.start();
		// } catch (IOException e) {
		// // 连接失败告诉调用者，重新连接
		// e.printStackTrace();
		// callBack.disconnect();
		// }
	}

	// 获取当前连接状态
	public boolean getConnectStatus() {
		if (mSocket != null)
			return mSocket.isConnected();
		else
			return false;
	}

	public void sendData(byte[] data) {
		if (out != null) {
			try {
				out.write(data);
			} catch (IOException e) {
				e.printStackTrace();
				callBack.disconnect();
			}
		}
	}

	public void writeDate(byte[] data) {
		datas.add(data);      // 将发送数据添加到发送队列
	}

	//连接线程
	class ConnectThread extends Thread {
		String ip;
		int port;

		public ConnectThread(String ip, int port) {
			this.ip = ip;
			this.port = port;
		}

		@Override
		public void run() {
			super.run();
			mSocket = new Socket();
			address = new InetSocketAddress(ip, port);
			try {
				mSocket.connect(address, timeOut);
				mSocket.isConnected();
				callBack.connected();
				out = new DataOutputStream(mSocket.getOutputStream());// 获取网络输出流
				in = new DataInputStream(mSocket.getInputStream());// 获取网络输入流
				threadBoo = true;
				recThread = new RecThrad();
				recThread.start();//打开接收数据线程
				sendThread = new SendThread();
				sendThread.start();//打开发送数据线程

			} catch (IOException e1) {
				Log.e("", "fanliang......连接失败");
				e1.printStackTrace();
				try {
					if (out != null) {
						out.close();
					}
					if (in != null) {
						in.close();
					}
					if (mSocket != null && !mSocket.isClosed()) {// 判断socket不为空并且是连接状态??
						mSocket.close();// 关闭socket
					}
				} catch (Exception e2) {
					// TODO: handle exception
				}
				if (callBack != null)
					callBack.disconnect();
			}
		}
	}

	/**
	 * 发送数据线程
	 */
	class SendThread extends Thread {
		@Override
		public void run() {
			super.run();
			while (threadBoo) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					close();
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (datas.size() > 0) {
					byte[] data = datas.remove(0);
					Log.e("", "fanliang......发送数据 =" + new String(data));
					sendData(data);
				}
			}
			this.close();
		}

		public void close() {
			threadBoo = false;
		}
	}

	/**
	 * 接收数据线程 关闭资源 打开资源
	 */
	class RecThrad extends Thread {

		public void run() {
			super.run();
			if (threadBoo) {
				if (in != null) {
					int len = 0;
					try {
						while ((len = in.read(buffer)) > 0) {
							tmpBuffer = new byte[len];
							System.arraycopy(buffer, 0, tmpBuffer, 0, len);
							Log.e("", "fanliang......接收数据 ="
									+ new String(tmpBuffer));
							if("Hello client".equals(new String(tmpBuffer))){
								Log.i("心跳", "---------------");
							}
							callBack.receive(tmpBuffer);
							tmpBuffer = null;
						}
					} catch (IOException e) {
						e.printStackTrace();
						try {
							if (out != null) {
								out.close();
							}
							if (in != null) {
								in.close();
							}
							if (mSocket != null && !mSocket.isClosed()) {// 判断socket不为空并且是连接状�??
								mSocket.close();// 关闭socket
							}
						} catch (Exception e2) {
							// TODO: handle exception
						}
						if (callBack != null)
							callBack.disconnect();
					}
				}
			}
		}

		//
		/*public void close() {
			threadBoo = false;
			this.close();
		}*/
	}

	// 关闭打开的资源
	public void close() {
		threadBoo = false;
		try {
			// if (mSocket != null) {
			// if (!mSocket.isInputShutdown()) {
			// mSocket.shutdownInput();
			// }
			// if (!mSocket.isOutputShutdown()) {
			// mSocket.shutdownOutput();
			// }
			// }
			if (out != null) {
				out.close();
			}
			if (in != null) {
				in.close();
			}
			if (mSocket != null && !mSocket.isClosed()) {// 判断socket不为空并且是连接状�??
				mSocket.close();// 关闭socket
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out = null;
			in = null;
			mSocket = null;// 制空socket对象
			recThread = null;
			sendThread = null;
			connThread = null;
			callBack = null;
		}
	}
}
