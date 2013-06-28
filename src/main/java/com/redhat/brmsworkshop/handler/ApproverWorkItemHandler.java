package com.redhat.brmsworkshop.handler;

import java.util.Collection;

import org.drools.process.instance.WorkItemHandler;
import org.drools.runtime.ClassObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.process.WorkflowProcessInstance;

import com.redhat.brmsworkshop.model.Customer;

public class ApproverWorkItemHandler implements WorkItemHandler {

	private StatefulKnowledgeSession session;
	
	public ApproverWorkItemHandler(StatefulKnowledgeSession session) {
		this.session = session;
	}
	
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {}

	public void executeWorkItem(WorkItem workItem, WorkItemManager workItemManager) {
		Collection<Object> facts = session.getObjects(new ClassObjectFilter(Customer.class));
		Customer customer = (Customer) facts.toArray()[0];
		WorkflowProcessInstance processInstance = (WorkflowProcessInstance) session.getProcessInstance(workItem.getProcessInstanceId());
		processInstance.setVariable("approved", customer.getApproved());
		workItemManager.completeWorkItem(workItem.getId(), null);
	}

}
