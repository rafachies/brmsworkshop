package com.redhat.brmsworkshop.handler;

import java.util.Collection;
import java.util.Random;

import org.drools.process.instance.WorkItemHandler;
import org.drools.runtime.ClassObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;

import com.redhat.brmsworkshop.model.Customer;

public class SCPCWorkItemHandler implements WorkItemHandler {

	private StatefulKnowledgeSession session;
	
	public SCPCWorkItemHandler(StatefulKnowledgeSession session) {
		this.session = session;
	}
	
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {}

	public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		int randomScore = new Random().nextInt(100);
		System.out.println("SCPC Score: " + randomScore);
		Collection<Object> facts = session.getObjects(new ClassObjectFilter(Customer.class));
		Customer customer = (Customer) facts.toArray()[0];
		customer.setScpcScore(randomScore);
		workItemManager.completeWorkItem(workItem.getId(), null);
	}

}
