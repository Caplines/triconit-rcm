package com.tricon.esdatareplication.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.tricon.esdatareplication.util.CustomPropFileCache;

@Component
public class RunAfterStartup {

	
	@EventListener(ApplicationReadyEvent.class)
	public void runAfterStartup() {
		CustomPropFileCache.readFileProperty();
	}
}
