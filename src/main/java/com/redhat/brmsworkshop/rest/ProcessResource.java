package com.redhat.brmsworkshop.rest;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.task.query.TaskSummary;

import com.redhat.brmsworkshop.brms.HumanTaskManager;
import com.redhat.brmsworkshop.brms.KnowledgeBaseManager;
import com.redhat.brmsworkshop.model.Customer;


@Path("/process")
public class ProcessResource {

	@Inject private HumanTaskManager humanTaskManager;
	@Inject private KnowledgeBaseManager knowledgeBaseManager;
	
	@GET
	@Path("/start/{cpf}/{age}/{monthlyIncome}")
	public String startProcess(@PathParam("cpf") String cpf, @PathParam("age") Integer age, @PathParam("monthlyIncome") Integer monthlyIncome){
		Customer customer = createCustomer(cpf, age, monthlyIncome);
		StatefulKnowledgeSession session = knowledgeBaseManager.newSession();
		session.insert(customer);
		Map<String, Object> processVariables = new HashMap<String, Object>();
		processVariables.put("customer", customer);
		session.startProcess("cleartech.CreditProcess", processVariables );
		return "ok";
	}

	@GET
	@Path("/tasks/{user}")
	public String listTask(@PathParam("user") String user) throws Exception{
		TaskSummary task = humanTaskManager.getNextTask(user);
		Map<String, Object> taskInput = humanTaskManager.getDataInput(task);
		Customer customer = (Customer) taskInput.get("customer");
		return "taskId: " + task.getId() + "\ncpf: " + customer.getCpf() + "\nage: " + customer.getAge() + "\nrenda " + customer.getMonthlyIncome() + "\nscore: " + customer.getScpcScore() + "\ncredit: " + customer.getCreditValue();
	}
	
	@GET
	@Path("/tasks/{taskId}/{user}")
	public String startTask(@PathParam("taskId") Long taskId, @PathParam("user") String user) throws Exception{
		humanTaskManager.startTask(taskId, user);
		return "STARTED";
	}
	
	@GET
	@Path("/tasks/complete/{taskId}/{user}")
	public String endTask(@PathParam("taskId") Long taskId, @PathParam("user") String user) throws Exception{
		HashMap<String, Object> dataOutput = new HashMap<String, Object>();
		dataOutput.put("riskApproved", true);
		humanTaskManager.endTask(taskId, user, dataOutput);
		return "STARTED";
	}
	
	@GET
	@Path("/ping")
	public String startProcess(){
		return "pong";
	}

	private Customer createCustomer(String cpf, Integer age, Integer monthlyIncome) {
		Customer customer = new Customer();
		customer.setAge(age);
		customer.setCpf(cpf);
		customer.setMonthlyIncome(monthlyIncome);
		return customer;
	}
}
