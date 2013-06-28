package com.jeff.test;

import java.util.ArrayList;

public class OwnStatisticsMethodClass {

	/**
	 * �����˫����������ֵ�����ֵ
	 * 
	 * @param inputData
	 *            ������������
	 * @return ������,�������ֵ���Ϸ�������Ϊ-1
	 */
	public static double getMax(double[] inputData) {
		if (inputData == null || inputData.length == 0)
			return -1;
		int len = inputData.length;
		double max = inputData[0];
		for (int i = 0; i < len; i++) {
			if (max < inputData[i])
				max = inputData[i];
		}
		
		return max;
	}

	/**
	 * �������˫����������ֵ����Сֵ
	 * 
	 * @param inputData
	 *            ������������
	 * @return ������,�������ֵ���Ϸ�������Ϊ-1
	 */
	public static double getMin(double[] inputData) {
		if (inputData == null || inputData.length == 0)
			return -1;
		int len = inputData.length;
		double min = inputData[0];
		for (int i = 0; i < len; i++) {
			if (min > inputData[i])
				min = inputData[i];
		}
		
		return min;
	}

	/**
	 * �����˫����������ֵ�ĺ�
	 * 
	 * @param inputData
	 *            ������������
	 * @return ������
	 */
	public static double getSum(double[] inputData) {
		if (inputData == null || inputData.length == 0)
			return -1;
		int len = inputData.length;
		double sum = 0;
		for (int i = 0; i < len; i++) {
			sum = sum + inputData[i];
		}
		
		return sum;
	}
	
	public static double getSum(ArrayList<Integer> inputData) {
		if (inputData == null || inputData.size() == 0)
			return -1;
		int len = inputData.size();
		double sum = 0;
		for (int i = 0; i < len; i++) {
			sum = sum + inputData.get(i);
		}
		
		return sum;
	}
	/**
	 * �����˫����������ֵ����Ŀ
	 * 
	 * @param input
	 *            Data ������������
	 * @return ������
	 */
	public static int getCount(double[] inputData) {
		if (inputData == null)
			return -1;
		
		return inputData.length;
	}
	
	public static int getCount(ArrayList<Integer> inputData) {
		if (inputData == null)
			return -1;
		
		return inputData.size();
	}

	/**
	 * �����˫����������ֵ��ƽ��ֵ
	 * 
	 * @param inputData
	 *            ������������
	 * @return ������
	 */
	public static double getAverage(double[] inputData) {
		if (inputData == null || inputData.length == 0)
			return -1;
		int len = inputData.length;
		double result;
		result = getSum(inputData) / len;
		
		return result;
	}
	
	public static double getAverage(ArrayList<Integer> inputData) {
		if (inputData == null || inputData.size() == 0)
			return -1;
		int len = inputData.size();
		double result;
		result = getSum(inputData) / len;
		
		return result;
	}
	/**
	 * �����˫����������ֵ��ƽ����
	 * 
	 * @param inputData
	 *            ������������
	 * @return ������
	 */
	public static double getSquareSum(double[] inputData) {
		if (inputData == null || inputData.length == 0)
			return -1;
		int len = inputData.length;
		double sqrsum = 0.0;
		for (int i = 0; i < len; i++) {
			sqrsum = sqrsum + inputData[i] * inputData[i];
		}
		
		return sqrsum;
	}
	
	public static double getSquareSum(ArrayList<Integer> inputData) {
		if (inputData == null || inputData.size() == 0)
			return -1;
		int len = inputData.size();
		double sqrsum = 0.0;
		for (int i = 0; i < len; i++) {
			sqrsum = sqrsum + inputData.get(i) * inputData.get(i);
		}
		
		return sqrsum;
	}
	/**
	 * �����˫����������ֵ�ķ���
	 * 
	 * @param inputData
	 *            ������������
	 * @return ������
	 */
	public static double getVariance(double[] inputData) {
		int count = getCount(inputData);
		double sqrsum = getSquareSum(inputData);
		double average = getAverage(inputData);
		double result;
		result = (sqrsum - count * average * average) / count;
		
		return result;
	}
	
	public static double getVariance(ArrayList<Integer> inputData) {
		int count = getCount(inputData);
		double sqrsum = getSquareSum(inputData);
		double average = getAverage(inputData);
		double result;
		result = (sqrsum - count * average * average) / count;
		
		return result;
	}
	/**
	 * �����˫����������ֵ�ı�׼��
	 * 
	 * @param inputData
	 *            ������������
	 * @return ������
	 */
	public static double getStandardDiviation(double[] inputData) {
		double result;
		// ����ֵ������Ҫ
		result = Math.sqrt(Math.abs(getVariance(inputData)));
		
		return result;
	}
	public static double getStandardDiviation(ArrayList<Integer> inputData) {
		double result;
		// ����ֵ������Ҫ
		result = Math.sqrt(Math.abs(getVariance(inputData)));
		
		return result;
	}
	
}
