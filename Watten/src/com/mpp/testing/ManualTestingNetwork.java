package com.mpp.testing;

import com.mpp.network.ClientForConsole;
import com.mpp.network.Server;

public class ManualTestingNetwork {

	public static void main(String[] args) {
		
//		new Thread() {
//			@Override 
//			public void run() {
//				Server.start();
//			}
//		}.start();

		new Thread() {
			@Override 
			public void run() {
				ClientTest c = new ClientTest();
				c.startManualInput();
			}
		}.start();
		new Thread() {
			@Override 
			public void run() {
				ClientTest c = new ClientTest();
				c.startManualInput();
			}
		}.start();
		new Thread() {
			@Override 
			public void run() {
				ClientTest c = new ClientTest();
				c.startManualInput();
			}
		}.start();

//		new Thread() {
//			@Override 
//			public void run() {
//				ClientForConsole c = new ClientForConsole();
//				c.startManualInput();
//			}
//		}.start();

	}
}
