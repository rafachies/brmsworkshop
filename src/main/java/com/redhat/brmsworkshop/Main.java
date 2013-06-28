package com.redhat.brmsworkshop;

import org.drools.KnowledgeBase;
import org.drools.WorkingMemory;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.definition.type.FactType;
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
		FactType factType = knowledgeBase.getFactType("cleartech", "Customer");
		Object fact = factType.newInstance();
		factType.set(fact, "age", 28);
		factType.set(fact, "monthlyIncome", 5000);
		factType.set(fact, "cpf", "11111111111");
		session.insert(fact);
		session.getWorkItemManager().registerWorkItemHandler("SCPC", new SCPCWorkItemHandler(session));
		session.getWorkItemManager().registerWorkItemHandler("Approver", new ApproverWorkItemHandler(session));
		session.startProcess("cleartech.CreditProcess");

		Thread.sleep(5000);

		System.out.println("aprovado: " + factType.get(fact, "approved"));
		System.out.println("credit: " + factType.get(fact, "creditValue"));
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
