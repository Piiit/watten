package com.mpp.network;

import java.io.Serializable;

import com.mpp.tools.MessageAction;

public class Message implements Serializable{
	
	@Override
	public String toString() {
		return "Message [action=" + action + ", message=" + message + ", name="
				+ name + "]";
	}

	private MessageAction action;
	private String message;
	private String name;
	
	public Message(MessageAction action){
		this.setAction(action);
	}

	public MessageAction getAction() {
		return action;
	}

	public void setAction(MessageAction action) {
		this.action = action;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
