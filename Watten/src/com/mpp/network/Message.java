package com.mpp.network;

import java.io.Serializable;

import com.mpp.tools.MessageAction;

public class Message implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "Message [action=" + action + ", message=" + message + ", name="
				+ name + "]";
	}

	private MessageAction action;
	private String message;
	private String name;
	
	public Message() {
	}
	
	public Message(MessageAction action){
		this(action, null, null);
	}

	public Message(MessageAction action, String message){
		this(action, message, null);
	}
	
	public Message(MessageAction action, String message, String name){
		setAction(action);
		setMessage(message);
		setName(name);
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
