package com.redhat.brmsworkshop.handler;

import java.util.Collection;

import org.drools.definition.type.FactType;
import org.drools.process.instance.WorkItemHandler;
import org.drools.runtime.ClassObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.process.WorkflowProcessInstance;

public class ApproverWorkItemHandler implements WorkItemHandler {

	private StatefulKnowledgeSession session;
	
	public ApproverWorkItemHandler(StatefulKnowledgeSession session) {
		this.session = session;
	}
	
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {}

	public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		FactType factType = session.getKnowledgeBase().getFactType("cleartech", "Customer");
		Collection<Object> facts = session.getObjects(new ClassObjectFilter(factType.getFactClass()));
		Object fact = facts.toArray()[0];
		boolean approved = (Boolean) factType.get(fact, "approved");
		WorkflowProcessInstance processInstance = (WorkflowProcessInstance) session.getProcessInstance(workItem.getProcessInstanceId());
		processInstance.setVariable("approved", approved);
		workItemManager.completeWorkItem(workItem.getId(), null);
	}

}
