package com.redhat.brmsworkshop.handler;

import java.util.Collection;
import java.util.Random;

import org.drools.definition.type.FactType;
import org.drools.process.instance.WorkItemHandler;
import org.drools.runtime.ClassObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;

public class SCPCWorkItemHandler implements WorkItemHandler {

	private StatefulKnowledgeSession session;
	
	public SCPCWorkItemHandler(StatefulKnowledgeSession session) {
		this.session = session;
	}
	
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {}

	public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		int randomScore = new Random().nextInt(100);
		System.out.println("SCPC Score: " + randomScore);
		FactType factType = session.getKnowledgeBase().getFactType("cleartech", "Customer");
		Collection<Object> facts = session.getObjects(new ClassObjectFilter(factType.getFactClass()));
		Object fact = facts.toArray()[0];
		factType.set(fact, "scpcScore", randomScore);
		workItemManager.completeWorkItem(workItem.getId(), null);
	}

}
