package com.redhat.brmsworkshop.brms;

import javax.inject.Inject;
import javax.inject.Singleton;

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
import org.jbpm.task.service.hornetq.CommandBasedHornetQWSHumanTaskHandler;

import com.redhat.brmsworkshop.handler.ApproverWorkItemHandler;
import com.redhat.brmsworkshop.handler.SCPCWorkItemHandler;

@Singleton
public class KnowledgeBaseManager {

	@Inject private HumanTaskManager humanTaskManager;
	
	private KnowledgeAgent knowledgeAgent;
	private StatefulKnowledgeSession knowledgeSession;

	public KnowledgeBaseManager() {
		knowledgeAgent = KnowledgeAgentFactory.newKnowledgeAgent("MyAgeng");
		knowledgeAgent.applyChangeSet(new ClassPathResource("changeset.xml"));
	}
	
	public StatefulKnowledgeSession newSession() {
		KnowledgeBase knowledgeBase = knowledgeAgent.getKnowledgeBase();
		knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
		CommandBasedHornetQWSHumanTaskHandler humanTaskHandler = new CommandBasedHornetQWSHumanTaskHandler(knowledgeSession);
		humanTaskHandler.setClient(humanTaskManager.getTaskClient());
		knowledgeSession.getWorkItemManager().registerWorkItemHandler("SCPC", new SCPCWorkItemHandler(knowledgeSession));
		knowledgeSession.getWorkItemManager().registerWorkItemHandler("Approver", new ApproverWorkItemHandler(knowledgeSession));
		knowledgeSession.getWorkItemManager().registerWorkItemHandler("Human Task", humanTaskHandler);
		startScannerService();
		configureRulesFirePolicy(knowledgeSession);
		return knowledgeSession;
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
