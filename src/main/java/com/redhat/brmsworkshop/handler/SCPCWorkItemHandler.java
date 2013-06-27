package com.redhat.brmsworkshop.handler;

import org.drools.process.instance.WorkItemHandler;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;

public class SCPCWorkItemHandler implements WorkItemHandler {

	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {}

	public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		System.out.println("SCPC Work Item Handler called");
		workItemManager.completeWorkItem(workItem.getId(), null);
	}

}
