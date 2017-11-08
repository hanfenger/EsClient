package com.lk;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * Es 操作的相关接口
 * @author LK
 *
 */
public interface IEs {
	
	/**
	 * es插入数据
	 * @param esUrl   http://ip:9200
	 * @param dataMapList  插入数据
	 */
	public void sendToEs(String esUrl, List<Map<String,Object>> dataMapList);
	
	/**
	 * es中查询数据
	 * @param esUrl    http://ip:9200
	 * @param searchMap  查询的jsonObject、index、type  
	 * @return  返回查询的结果jsonObject
	 */
	public JSONObject searchFromEs(String esUrl,Map<String,Object> searchMap);
}
