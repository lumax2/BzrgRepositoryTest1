package com.jt.common.test;

import java.util.HashSet;
import java.util.Set;

class RandomNumber {

	public static void main(String[] args) {
		int a;
		int i=0;
		Set<Integer> set = new HashSet<Integer>();
		while(true){
			a =(int) (Math.random()*1000+1);
			if(!set.contains(a)){
				set.add(a);
				i++;
				System.out.println("第"+i+"个数是：  "+a);
			}
			if(i>=900){
				break;
			}
		}
	}

}
 