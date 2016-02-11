package com.eharmony.services.mymatchesservice.rest;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.TimeoutHandler;

public class MockAsyncResponse implements AsyncResponse {

	private Object responseObject;
	
	@Override
	public boolean resume(Object response) {

		responseObject = response;
		return true;
	}
	
	public Object getResponseObject(){
		return responseObject;
	}

	@Override
	public boolean resume(Throwable response) {
		return true;
	}

	@Override
	public boolean cancel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean cancel(int retryAfter) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean cancel(Date retryAfter) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSuspended() {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	public boolean setTimeout(long time, TimeUnit unit) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setTimeoutHandler(TimeoutHandler handler) {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<Class<?>> register(Class<?> callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<?>, Collection<Class<?>>> register(Class<?> callback,
			Class<?>... callbacks) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<?>> register(Object callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<?>, Collection<Class<?>>> register(Object callback,
			Object... callbacks) {
		// TODO Auto-generated method stub
		return null;
	}

}
