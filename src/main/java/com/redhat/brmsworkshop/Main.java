package com.redhat.brmsworkshop;

import org.drools.KnowledgeBase;
import org.drools.WorkingMemory;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.event.AgendaEventListener;
import org.drools.event.DefaultAgendaEventListener;
import org.drools.event.RuleFlowGroupActivatedEvent;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;

import com.redhat.brmsworkshop.handler.ApproverWorkItemHandler;
import com.redhat.brmsworkshop.handler.SCPCWorkItemHandler;
import com.redhat.brmsworkshop.model.Customer;


public class Main {

	public static void main(String[] args) throws Exception {
		new Main().launch();
	}

	public void launch() throws Exception {
		KnowledgeAgent knowledgeAgent = KnowledgeAgentFactory.newKnowledgeAgent("MyAgeng");
		knowledgeAgent.applyChangeSet(new ClassPathResource("changeset.xml"));
		KnowledgeBase knowledgeBase = knowledgeAgent.getKnowledgeBase();
		StatefulKnowledgeSession session = knowledgeBase.newStatefulKnowledgeSession();
		startScannerService();
		configureRulesFirePolicy(session);
		Customer customer = createCustomer();
		session.insert(customer);
		session.getWorkItemManager().registerWorkItemHandler("SCPC", new SCPCWorkItemHandler(session));
		session.getWorkItemManager().registerWorkItemHandler("Approver", new ApproverWorkItemHandler(session));
		session.startProcess("cleartech.CreditProcess");

		Thread.sleep(5000);

		System.out.println("aprovado: " + customer.getApproved());
		System.out.println("credit: " + customer.getCreditValue());
	}

	private Customer createCustomer() {
		Customer customer = new Customer();
		customer.setAge(28);
		customer.setCpf("31937243869");
		customer.setMonthlyIncome(5000);
		return customer;
	}

	private void startScannerService() {
		ResourceChangeScannerConfiguration configuration = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
		configuration.setProperty( "drools.resource.scanner.interval", "10" );
		ResourceFactory.getResourceChangeScannerService().configure( configuration );
		ResourceFactory.getResourceChangeNotifierService().start();
		ResourceFactory.getResourceChangeScannerService().start();
	}

	private void configureRulesFirePolicy(StatefulKnowledgeSession knowledgeSession) {
		final AgendaEventListener agendaEventListener = new DefaultAgendaEventListener() {
			public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event, WorkingMemory workingMemory) {
				workingMemory.fireAllRules();
			}
		} ;
		((StatefulKnowledgeSessionImpl) knowledgeSession).session.addEventListener(agendaEventListener);
	}


}
