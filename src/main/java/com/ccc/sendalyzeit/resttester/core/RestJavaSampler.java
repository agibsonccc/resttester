package com.ccc.sendalyzeit.resttester.core;

import java.io.IOException;
import java.util.Arrays;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
/**
 * JMeter REST sample
 * @author agibsonccc
 *
 */
public class RestJavaSampler extends AbstractJavaSamplerClient {

	public SampleResult runTest(JavaSamplerContext context) {
		SampleResult sampleResult = new SampleResult();
		DefaultHttpClient client = new DefaultHttpClient();
		String body = context.getParameter( "body" );
		final String contentType = context.getParameter( "content-type", "application/json" );
		//failed
		if( body == null)
			return sampleResult;
		client.addRequestInterceptor(new HttpRequestInterceptor() {

			public void process(HttpRequest request, HttpContext context)
					throws HttpException, IOException {
				request.setHeader( "Content-Type", contentType );
			}
			
		});
		//count errors
		int errorCount = 0;
		
		String method = context.getParameter( "method","post" );
		String uri = context.getParameter( "url" );
		if( uri == null )
			return sampleResult;
		HttpUriRequest request = requestforMethod( method, uri );
		try {
			//set time for response
			sampleResult.sampleStart();
			//send http request
			HttpResponse resp = client.execute( request );
			sampleResult.sampleEnd();
			sampleResult.setContentType( contentType );
			sampleResult.setBodySize( body.length() );
			sampleResult.setResponseCode( String.valueOf( resp.getStatusLine().getStatusCode()) );
			int errorCode = resp.getStatusLine().getStatusCode();
			if( errorCode < 400 )
				sampleResult.setSuccessful( true );
			sampleResult.setResponseHeaders( Arrays.toString(resp.getAllHeaders() ) );
			sampleResult.setRequestHeaders( Arrays.toString(request.getAllHeaders()) );
			sampleResult.setMonitor( true );
			
		} catch (ClientProtocolException e) {
			errorCount++;
		} catch (IOException e) {
			errorCount++;
		}
		sampleResult.setErrorCount( errorCount );
		
		JMeterVariables vars = JMeterContextService.getContext().getVariables();
		return sampleResult;
	}

	private HttpUriRequest requestforMethod( String method, String uri ) {
		if( method == null || method.isEmpty() )
			return new HttpPost( uri );
		else if( method.toLowerCase().equals( "get" ) )
			return new HttpGet( uri );
		else if( method.toLowerCase().equals("put "))
			return new HttpPut( uri );
		else if( method.toLowerCase().equals( "post" )) 
			return new HttpPost( uri );
		else if( method.toLowerCase().equals( "delete" ) )
			return new HttpDelete( uri );
		return new HttpPost( uri );
		}
	}

