package com.lk.impl;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lk.IEs;

public class EsImpl implements IEs{
	
	private static final Logger logger = LoggerFactory.getLogger(EsImpl.class);
	
	@Override
	public void sendToEs(String esUrl, List<Map<String,Object>> dataMapList) {
		//将map中的内容，从新解析、拼接成 “批量操作bulk”的请求体格式
		String content = creatContent(dataMapList);
		CloseableHttpClient client = null;
		try {
			String url=esUrl;
			if(esUrl.contains(",")){
				String[] urls=esUrl.split(",");
				Random random=new Random();
				int randomNum= Math.abs(random.nextInt());
				url=urls[randomNum%urls.length];
			}
			client = HttpClients.createDefault();
			HttpPost post = new HttpPost();
			post.setURI(URI.create(url + "/_bulk"));
			post.setEntity(new StringEntity(content, "utf-8"));
			HttpResponse resp = client.execute(post);
			String result = IOUtils.toString(resp.getEntity().getContent(), "utf-8");
			JSONObject jsonObject = JSON.parseObject(result);
			if (jsonObject.getBooleanValue("errors")) {
				logger.error("host:"+url+"/_bulk es bulk result error:" + result + " request content:" + content);
			}
		} catch (ClientProtocolException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			try {
				if(client!=null){
					client.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	
	}
	
	/**
	 * 格式化生成请求体
	 * @param dataMapList
	 * @return
	 */
	private String creatContent(List<Map<String,Object>> dataMapList) {
		StringBuffer content = new StringBuffer();
		for(Map<String,Object> dataMap:dataMapList){
			//  { "create":  { "_index": "website", "_type": "blog", "_id": "123" }}
			//{ "title":    "My first blog post" }
			String _index = (String) dataMap.get("_index");
			String _type = (String) dataMap.get("_type");
			JSONObject jsonObject = (JSONObject) dataMap.get("json");
			
			content.append("{ \"create\":  { \"_index\":\"").append(_index).
			append("\", \"_type\": \"").append(_type).append("\"}}").append("\n");
			//
			content.append(jsonObject.toString()).append("\n");
		}
		return content.toString();
	}

	@Override
	public JSONObject searchFromEs(String esUrl, Map<String,Object> searchMap) {
		CloseableHttpClient client = null;
		JSONObject searchResult = null;
		String index = searchMap.get("_index").toString();
		String type = searchMap.containsKey("_type")?searchMap.get("_type").toString():null;  //type 不是必须的
		JSONObject jsonstrObject = (JSONObject) searchMap.get("json");
		
		try {
			String url=esUrl;
			if(esUrl.contains(",")){
				String[] urls=esUrl.split(",");
				Random random=new Random();
				int randomNum= Math.abs(random.nextInt());
				url=urls[randomNum%urls.length];
			}
			client = HttpClients.createDefault();
			HttpPost post = new HttpPost();
			
			if(null==type){
				post.setURI(URI.create(url + "/"+index +"/_search"));
			}else{
				post.setURI(URI.create(url + "/"+index+"/"+type+"/_search"));				
			}
			
			
			
			post.setEntity(new StringEntity(jsonstrObject.toJSONString(), "utf-8"));
			HttpResponse resp = client.execute(post);
			String result = IOUtils.toString(resp.getEntity().getContent(), "utf-8");
			JSONObject jsonObject = JSON.parseObject(result);
			if (jsonObject.containsKey("error")) {
				logger.error("host:"+url+"/_search es search result error:" + jsonObject.getString("error") + "\n request content:" + jsonstrObject.toJSONString());
			}else{
				
				searchResult = jsonObject.getJSONObject("hits");
			}
		} catch (ClientProtocolException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			try {
				if(client!=null){
					client.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		
		
		return searchResult;
	}
	
	
}
