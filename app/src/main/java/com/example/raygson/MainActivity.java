package com.example.raygson;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.raygson.RayGSON.FakeGson;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
			FakeGson fakeGson = new FakeGson();
			DeeperInside deeperInside = new DeeperInside();
			deeperInside.setDeeperInside(360);
			InsideData insideData = new InsideData();
			insideData.setInside(250);
			insideData.setDeeperInside(deeperInside);
			TestBean testBean = new <String>TestBean();
			ArrayList<String>list = new ArrayList<>();
			list.add("SSSS");
			testBean.setInsideData(insideData);
			testBean.setAge(18);
			testBean.setIsMan(false);
			testBean.setMoney("100");
			testBean.setArrayList(list);
			//testBean.setT("Fuck");
			Gson gson = new Gson();
			String data = gson.toJson(testBean);
			Log.d("data",data);
			try {
				  TestBean newTestBean = gson.fromJson(data,TestBean.class);//fakeGson.fromJson(new TestBean(),data);
				  Log.d("age",newTestBean.age+"");
				  Log.d("money",newTestBean.money);
				  Log.d("isMan",newTestBean.isMan+"");
				  Log.d("inside",newTestBean.insideData.inside+"");
				  Log.d("deepInside",newTestBean.insideData.deeperInside.deeperInside+"");
				  Log.d("Array",newTestBean.arrayList.get(0).toString());
				  //Log.d("ParaType",newTestBean.t+"");
			} catch (Exception e) {
				  e.printStackTrace();
			}
	  }
}
