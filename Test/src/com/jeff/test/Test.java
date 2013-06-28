package com.jeff.test;

public class Test {
	
	private String keys;

	public  void methodOne() {
		
		synchronized (keys) {
			while (true) {
				System.out.println("----------- methodOne");
			}
		}
		
	}

	public  synchronized void methodTwo() {
		 for (int i = 0; i < 1000000000; i++) {
			 System.out.println("----------- methodTwo");
		}
			
		 

	}
	
	public static void main(String[] args) {
		String [] xx="1222|0".split("|", 1);
		System.out.println(xx.length);
	}

}
