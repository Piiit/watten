package com.mpp.testing;

public class ManualTestingNetwork {

	public static void main(String[] args) {
		
		new Thread() {
			@Override 
			public void run() {
				Server.start();
			}
		}.start();

		new Thread() {
			@Override 
			public void run() {
				Client c = new Client();
				c.startManualInput();
			}
		}.start();

	}
}
