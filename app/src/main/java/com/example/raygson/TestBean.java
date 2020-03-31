package com.example.raygson;

import java.util.ArrayList;

public class TestBean<T> {
	  int age;
	  String money;
	  boolean isMan;
	  InsideData insideData;
	  //T t;
	  ArrayList<String>arrayList;

	  /*public void setT(T t) {
			this.t = t;
	  }*/

	  public void setArrayList(ArrayList<String> arrayList) {
			this.arrayList = arrayList;
	  }

	  public void setInsideData(InsideData insideData) {
			this.insideData = insideData;
	  }

	  public void setAge(int age) {
			this.age = age;
	  }

	  public void setMoney(String money) {
			this.money = money;
	  }

	  public void setIsMan(boolean man) {
			isMan = man;
	  }
}
