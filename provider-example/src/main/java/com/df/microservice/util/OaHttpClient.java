package com.df.microservice.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

public class OaHttpClient {
	
	private CloseableHttpClient httpClient;

	private static Logger logger = LoggerFactory.getLogger(OaHttpClient.class);
	
	public static final String CHARSET = "UTF-8";
	
	public static String ip = "127.0.0.1";
	
	public static int port = 8088;
	
	public static final String username = "admin";
	
	public static final String password = "000000";
	
	public static final int connectionTimeout = 60000;
	
	public static final int socketTimeout = 350000;
	
	private static OaHttpClient instance;

	private static CookieStore cookieStore = null;
	
	public CloseableHttpClient getHttpClient() {
		return httpClient;
	}

	public static OaHttpClient getInstance() {
		instance = new OaHttpClient();
		return instance;
	}

	private OaHttpClient() {
		RequestConfig config = RequestConfig.custom().setConnectTimeout(connectionTimeout).setSocketTimeout(socketTimeout).build();
		this.httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
	}
	
	public JSONObject httpDownloadFile(String url, Map<String, Object> params) throws IOException {
		return httpDownloadFile(url, params, "UTF-8");
	}

	public String doGet(String url, Map<String, Object> params) {
		return doGet(url, params, "UTF-8");
	}

	public String doGetSSL(String url, Map<String, String> params) {
		return doGetSSL(url, params, "UTF-8");
	}

	public String doPost(String url, Map<String, Object> objParams) throws IOException {
		return doPost(url, objParams, "UTF-8");
	}

	public String doGet(String url, Map<String, Object> params, String charset) {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		if (StringUtils.isBlank(charset))
			charset = "UTF-8";
		try {
			if ((params != null) && (!params.isEmpty())) {
				List pairs = new ArrayList(params.size());
				for (Map.Entry entry : params.entrySet()) {
					String value = (String) entry.getValue();
					if (value != null) {
						pairs.add(new BasicNameValuePair((String) entry.getKey(), value));
					}
				}

				url = url + "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
			}
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("referer",Constants.urlBase+"/defaultroot/Logon!logon.action");
			CloseableHttpResponse response = this.httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpGet.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, "utf-8");
			}
			EntityUtils.consume(entity);
			response.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("HttpClientUtil-doGet请求返回:" + e);
		}
		return null;
	}

	public String doPost(String url, Map<String, Object> params, String charset) throws IOException {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		if (StringUtils.isBlank(charset)) {
			charset = "UTF-8";
		}
		List pairs = null;
		if ((params != null) && (!params.isEmpty())) {
			pairs = new ArrayList(params.size());
			for (Map.Entry entry : params.entrySet()) {
				//如果传递了数组、进行如下处理
				if(entry.getValue() instanceof List) {
					if(entry.getValue() instanceof JSONArray) {
						JSONArray jsonArry = (JSONArray)entry.getValue();
						for(int i=0;i<jsonArry.size();i++){
							JSONObject jsonParam = jsonArry.getJSONObject(i);
							Set<String> jsonSet = jsonParam.keySet();
							Iterator<String> jsonIt = jsonSet.iterator();
							while (jsonIt.hasNext()) {
								String key = jsonIt.next();
								//System.out.println(key+": "+jsonParam.getString(key));
								pairs.add(new BasicNameValuePair(key, jsonParam.getString(key)));
							}
						}
					}
					else {
						List values = (List) entry.getValue();
						for(Object value : values) {
							//System.out.println((String) entry.getKey()+": "+value.toString());
							pairs.add(new BasicNameValuePair((String) entry.getKey(), value.toString()));
						}
					}
				}
				else{
					String value = (String) entry.getValue();
					if (value != null) {
						//System.out.println((String) entry.getKey()+": "+value.toString());
						pairs.add(new BasicNameValuePair((String) entry.getKey(), value));
					}
				}
			}
		}
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("referer",Constants.urlBase+"/defaultroot/Logon!logon.action");
		if ((pairs != null) && (pairs.size() > 0)) {
			httpPost.setEntity(new UrlEncodedFormEntity(pairs, charset));
		}
		CloseableHttpResponse response = null;
		try {
			response = this.httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpPost.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, charset);
			}
			EntityUtils.consume(entity);
			return result;
		} catch (ParseException e) {
			e.printStackTrace();
			logger.error("HttpClientUtil-doPost请求返回:" + e);
		} finally {
			if (response != null)
				response.close();
		}
		return null;
	}
	
	public JSONObject httpDownloadFile(String url, Map<String, Object> params, String charset) throws IOException {
		JSONObject result = new JSONObject();
		result.put("code", "1");
		if (StringUtils.isBlank(url)) {
			return result;
		}
		if (StringUtils.isBlank(charset)) {
			charset = "UTF-8";
		}
		List pairs = null;
		if ((params != null) && (!params.isEmpty())) {
			pairs = new ArrayList(params.size());
			for (Map.Entry entry : params.entrySet()) {
				//如果传递了数组、进行如下处理
				if(entry.getValue() instanceof List) {
					if(entry.getValue() instanceof JSONArray) {
						JSONArray jsonArry = (JSONArray)entry.getValue();
						for(int i=0;i<jsonArry.size();i++){
							JSONObject jsonParam = jsonArry.getJSONObject(i);
							Set<String> jsonSet = jsonParam.keySet();
							Iterator<String> jsonIt = jsonSet.iterator();
							while (jsonIt.hasNext()) {
								String key = jsonIt.next();
								//System.out.println(key+": "+jsonParam.getString(key));
								pairs.add(new BasicNameValuePair(key, jsonParam.getString(key)));
							}
						}
					}
					else {
						List values = (List) entry.getValue();
						for(Object value : values) {
							//System.out.println((String) entry.getKey()+": "+value.toString());
							pairs.add(new BasicNameValuePair((String) entry.getKey(), value.toString()));
						}
					}
				}
				else{
					String value = (String) entry.getValue();
					if (value != null) {
						//System.out.println((String) entry.getKey()+": "+value.toString());
						pairs.add(new BasicNameValuePair((String) entry.getKey(), value));
					}
				}
			}
		}
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("referer",Constants.urlBase+"/defaultroot/Logon!logon.action");
		if ((pairs != null) && (pairs.size() > 0)) {
			httpPost.setEntity(new UrlEncodedFormEntity(pairs, charset));
		}
		CloseableHttpResponse response = null;
		InputStream is = null;
		String contentType = null;
		try {
			response = this.httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpPost.abort();
				if(statusCode == 302){//下载文件 302问题
					String locationUrl = response.getLastHeader("Location").getValue();
					HttpPost httpPost2 = new HttpPost(locationUrl);
					httpPost2.setHeader("referer",Constants.urlBase+"/defaultroot/Logon!logon.action");
//					if ((pairs != null) && (pairs.size() > 0)) {
//						httpPost2.setEntity(new UrlEncodedFormEntity(pairs, charset));
//					}
					response = this.httpClient.execute(httpPost2);
					statusCode = response.getStatusLine().getStatusCode();
					if (statusCode != 200) {
						httpPost2.abort();
						throw new RuntimeException("HttpClient,error status code :" + statusCode);
					}
				}
				else{
					throw new RuntimeException("HttpClient,error status code :" + statusCode);
				}
			}
			HttpEntity entity = response.getEntity();
			contentType = entity.getContentType().getValue();
			if (entity != null) {
				long contentLength = entity.getContentLength();
				if(contentType.indexOf("text/html")!=-1){
					String res = EntityUtils.toString(entity, charset);
					System.out.println(res);
					result.put("result", res);
				}
				else{
					is = entity.getContent();
					result.put("code", "0");
					result.put("result", is);
				}
			}
			if(contentType.indexOf("text/html")!=-1){
				EntityUtils.consume(entity);//返回流则不能关闭
			}
		} catch (ParseException e) {
			e.printStackTrace();
			logger.error("HttpClientUtil-doPost请求返回:" + e);
		} finally {
			//返回流则不能关闭
			if (response != null){
				if(contentType==null||contentType.indexOf("text/html")!=-1){
					response.close();
				}
			}
		}
		return result;
	}

	public String doPostWithJson(String url, String json) throws IOException {
		return doPostWithJson(url, json, "UTF-8");
	}

	public String doPostWithJson(String url, String json, String charset) throws IOException {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		if (StringUtils.isBlank(charset)) {
			charset = "UTF-8";
		}
		HttpPost httpPost = new HttpPost(url);
		if ((json != null) && (json != "")) {
			httpPost.addHeader("Content-type", "application/json; charset=utf-8");
			httpPost.setHeader("Accept", "application/json");
			httpPost.setEntity(new StringEntity(json, Charset.forName(charset)));
		}
		CloseableHttpResponse response = null;
		try {
			response = this.httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpPost.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, charset);
			}
			EntityUtils.consume(entity);
			return result;
		} catch (ParseException e) {
			e.printStackTrace();
			logger.error("HttpClientUtil-doPostWithJson请求返回:" + e);
		} finally {
			if (response != null)
				response.close();
		}
		return null;
	}

	public String doGetSSL(String url, Map<String, String> params, String charset) {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		if (StringUtils.isBlank(charset))
			charset = "UTF-8";
		try {
			if ((params != null) && (!params.isEmpty())) {
				List pairs = new ArrayList(params.size());
				for (Map.Entry entry : params.entrySet()) {
					String value = (String) entry.getValue();
					if (value != null) {
						pairs.add(new BasicNameValuePair((String) entry.getKey(), value));
					}
				}
				url = url + "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
			}
			HttpGet httpGet = new HttpGet(url);

			CloseableHttpClient httpsClient = createSSLClientDefault();
			CloseableHttpResponse response = httpsClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpGet.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, "utf-8");
			}
			EntityUtils.consume(entity);
			response.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("HttpClientUtil-doGetSSL请求返回:" + e);
		}
		return null;
	}

	public String doPostSSL(String url, Map<String, String> params, String charset) {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		if (StringUtils.isBlank(charset))
			charset = "UTF-8";
		try {
			List pairs = null;
			if ((params != null) && (!params.isEmpty())) {
				for (Map.Entry entry : params.entrySet()) {
					String value = (String) entry.getValue();
					if (value != null)
						pairs.add(new BasicNameValuePair((String) entry.getKey(), value));
				}
			} else {
				pairs = new ArrayList();
			}

			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs, Charset.forName(charset)));

			CloseableHttpClient httpsClient = createSSLClientDefault();
			CloseableHttpResponse response = httpsClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpPost.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, "utf-8");
			}
			EntityUtils.consume(entity);
			response.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("HttpClientUtil-doPostSSL请求返回:" + e);
		}
		return null;
	}

	public String doPostJsonSSL(String url, String json, String charset) {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		if (StringUtils.isBlank(charset)) {
			charset = "UTF-8";
		}
		HttpPost httpPost = new HttpPost(url);
		if ((json != null) && (json != "")) {
			httpPost.addHeader("Content-type", "application/json; charset=utf-8");
			httpPost.setHeader("Accept", "application/json");
			httpPost.setEntity(new StringEntity(json, Charset.forName(charset)));
		}
		CloseableHttpResponse response = null;
		try {
			response = this.httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				httpPost.abort();
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			HttpEntity entity = response.getEntity();
			String result = null;
			if (entity != null) {
				result = EntityUtils.toString(entity, charset);
			}
			EntityUtils.consume(entity);
			return result;
		} catch (ParseException e) {
			e.printStackTrace();
			logger.error("HttpClientUtil-doPostJsonSSL请求返回:" + e);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			logger.error("HttpClientUtil-doPostJsonSSL请求返回:" + e);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("HttpClientUtil-doPostJsonSSL请求返回:" + e);
		} finally {
			if (response != null)
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return null;
	}

	public CloseableHttpClient createSSLClientDefault() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (KeyManagementException e) {
			e.printStackTrace();
			logger.error("HttpClientUtil-createSSLClientDefault请求返回:" + e);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			logger.error("HttpClientUtil-createSSLClientDefault请求返回:" + e);
		} catch (KeyStoreException e) {
			e.printStackTrace();
			logger.error("HttpClientUtil-createSSLClientDefault请求返回:" + e);
		}
		return HttpClients.createDefault();
	}
}
