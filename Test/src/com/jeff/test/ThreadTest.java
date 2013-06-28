package com.jeff.test;

public class ThreadTest extends Thread {

	private Test test;

	public ThreadTest(Test test) {
		this.test = test;
	}

	public static void main(String[] args) throws InterruptedException {
		
		for (int i = 11; i <161; i++) {
			int j=i-10;
			String num=""+j;
			if(j<10){
				num="00"+num;
			}else{
				if(j<100){
					num="0"+num;
				}
			}
 			System.out.println("10.106.23."+i+ "	"+"a"+num+".datanode.hadoop.qingdao.youku");
//  			System.out.println("a"+num+".datanode.hadoop.qingdao.youku");
		}

//		Test test = new Test();
//		
//		new ThreadTest(test).start();
//		
////		Thread.sleep(1000);
//		test.methodTwo();

		

	}

	@Override
	public void run() {

		this.test.methodOne();
	}

}
