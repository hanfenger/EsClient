package com.lk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.lk.impl.EsImpl;

public class Test {
	
	
	public static void main(String[] args) {
		
		String esUrl = "http://localhost:9200";  //ip		
		IEs ies = new EsImpl();
		 /* //插入数据
		  List<Map<String,Object>> dataMapList = createTestDemo();
		ies.sendToEs(esUrl, dataMapList);*/
		
		/*******************
		 *  查询过程
		 * 1.创建查询的json对象
		 * 2.创建查询的index
		 * 3.(选择性)创建查询的type,可以不用
		 * 4.组装Map对象
		 * 
		 ****/
		Map<String, Object> searchMap = new HashMap<String,Object>();
		//1.查询语句
		String searchStr = "{ \"query\": { "+
							    "\"match\": { " +
							     " \"name\": \"lk1\" "+
							    "}"+
							  "}"+
						   "}";
		JSONObject jsonObject = JSONObject.parseObject(searchStr);  //查询的jsonObject
		//2.创建查询的index
		String index = "lktest";
		//3.(选择性)创建查询的type,可以不用
		//String type = "20171101";
		//4.组装Map对象
		searchMap.put("_index", index);   //index  必须
		//searchMap.put("_type", type);    //type   type不是必须的，查询可以不用加上
 		searchMap.put("json", jsonObject);  //查询请求体
 		
		String s = ies.searchFromEs(esUrl, searchMap).toJSONString();
		System.out.println(s);
		
	}


	
	/**
	 * 创建入库的数据
	 * @return
	 */
	private static List<Map<String, Object>> createTestDemo() {
		List<Map<String, Object>> dataMapList = new ArrayList<Map<String, Object>>();
		
		Map<String, Object> dataMap1 = new HashMap<String, Object>();
		Map<String, Object> dataMap2 = new HashMap<String, Object>();
		Map<String, Object> dataMap3 = new HashMap<String, Object>();
		
		String JsonStr1 = "{\"name\":\"lk4\",\"age\":2,\"like\":\"apple\"}";
		String JsonStr2 = "{\"name\":\"lk5\",\"age\":2,\"like\":\"apple\"}";
		String JsonStr3 = "{\"name\":\"lk6\",\"age\":2,\"like\":\"apple\"}";
		
		JSONObject jo1 = JSONObject.parseObject(JsonStr1);
		JSONObject jo2 = JSONObject.parseObject(JsonStr2);
		JSONObject jo3 = JSONObject.parseObject(JsonStr3);
		
		dataMap1.put("_index", "lktest");
		dataMap1.put("_type", "20171201");
		dataMap1.put("json", jo1);
		
		dataMap2.put("_index", "lktest");
		dataMap2.put("_type", "20171202");
		dataMap2.put("json", jo2);
		
		dataMap3.put("_index", "lktest");
		dataMap3.put("_type", "20171102");
		dataMap3.put("json", jo3);
		
		dataMapList.add(dataMap1);
		dataMapList.add(dataMap2);
		dataMapList.add(dataMap3);
		
		
		
		return dataMapList;
	}


}
