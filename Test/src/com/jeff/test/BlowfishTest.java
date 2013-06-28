package com.jeff.test;

public class BlowfishTest implements Runnable {

	@Override
	public void run() {
		for (int i = 0; i < 10000000; i++) {

			if (i % 1000000 == 0) {
				System.out.println(Thread.currentThread().getName() +"|count:" + i);
			}

			if (!TudouUtils.encodeVid(i).equals(IdEncrypter.encrypt(i))) {
				System.out.println(Thread.currentThread().getName() +"error:" + i);
			}
		}

	}
	
	public static void main(String[] args) throws InterruptedException {
		new Thread(new BlowfishTest()).start();
//		Thread.sleep(1000);
//		new Thread(new BlowfishTest()).start();
////		Thread.sleep(1000);
//		new Thread(new BlowfishTest()).start();
////		Thread.sleep(1000);
//		new Thread(new BlowfishTest()).start();
////		Thread.sleep(1000);
//		new Thread(new BlowfishTest()).start();
//		Thread.sleep(1000);
//		new Thread(new BlowfishTest()).start();
//		Thread.sleep(1000);
//		new Thread(new BlowfishTest()).start();
//		Thread.sleep(1000);
	}

}
