package com.redhat.brmsworkshop;

import org.drools.KnowledgeBase;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.definition.type.FactType;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;

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
		FactType factType = knowledgeBase.getFactType("cleartech", "Customer");
		Object fact = factType.newInstance();
		factType.set(fact, "age", 28);
		factType.set(fact, "monthlyIncome", 5000);
		factType.set(fact, "cpf", "111111111");
		session.insert(fact);	

		
		
		session.getWorkItemManager().registerWorkItemHandler("SCPC", new SCPCWorkItemHandler(session));
		session.startProcess("cleartech.CreditProcess");
		
		
		System.out.println("aprovado? -> " + factType.get(fact, "approved"));

	}

	private void startScannerService() {
		ResourceChangeScannerConfiguration configuration = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
		configuration.setProperty( "drools.resource.scanner.interval", "10" );
		ResourceFactory.getResourceChangeScannerService().configure( configuration );
		ResourceFactory.getResourceChangeNotifierService().start();
		ResourceFactory.getResourceChangeScannerService().start();
	}

}
