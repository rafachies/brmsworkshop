package com.redhat.brmsworkshop;

import org.drools.KnowledgeBase;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.definition.type.FactType;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;


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
		session.insert(fact);	
		session.fireAllRules();
		System.out.println("aprovado? -> " + factType.get(fact, "approved"));

		Thread.sleep(90000); //Let's wait until rchies change the rule

		Object fact2 = factType.newInstance();
		factType.set(fact2, "age", 28);
		KnowledgeBase knowledgeBase2 = knowledgeAgent.getKnowledgeBase();
		StatefulKnowledgeSession session2 = knowledgeBase2.newStatefulKnowledgeSession();
		session2.insert(fact2);
		session2.fireAllRules();
		System.out.println("aprovado? -> " + factType.get(fact2, "approved"));
	}

	private void startScannerService() {
		ResourceChangeScannerConfiguration configuration = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
		configuration.setProperty( "drools.resource.scanner.interval", "10" );
		ResourceFactory.getResourceChangeScannerService().configure( configuration );
		ResourceFactory.getResourceChangeNotifierService().start();
		ResourceFactory.getResourceChangeScannerService().start();
	}

}
