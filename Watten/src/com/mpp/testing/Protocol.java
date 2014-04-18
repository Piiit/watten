package com.mpp.testing;

import java.util.HashMap;
import java.util.Map;

public class Protocol {

	private Map<String, ProtocolCommand> commandList = new HashMap<String, ProtocolCommand>();
	
	public void registerCommand(ProtocolCommand command) {
		commandList.put(command.getId(), command);
	}
}
