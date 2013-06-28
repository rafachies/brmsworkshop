package com.redhat.brmsworkshop.brms;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import org.drools.SystemEventListenerFactory;
import org.jbpm.task.AccessType;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.hornetq.HornetQTaskClientConnector;
import org.jbpm.task.service.hornetq.HornetQTaskClientHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;

@Singleton
public class HumanTaskManager {

	private TaskClient taskClient;

	@PostConstruct
	public void onConstruct() {
		try {
			taskClient = new TaskClient(new HornetQTaskClientConnector("tasksQueue/client" + UUID.randomUUID().toString(), new HornetQTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));;
			taskClient.connect("localhost", 5153);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TaskClient getTaskClient() {
		return taskClient;
	}

	public TaskSummary getNextTask(String username) throws Exception {
		BlockingTaskSummaryResponseHandler taskSummaryHandler = new BlockingTaskSummaryResponseHandler();
		taskClient.getTasksAssignedAsPotentialOwner(username, "en-UK", taskSummaryHandler);
		List<TaskSummary> tasks = taskSummaryHandler.getResults();
		return tasks.get(0);
	}

	public List<TaskSummary> getTasks(String username) throws Exception {
		BlockingTaskSummaryResponseHandler taskSummaryHandler = new BlockingTaskSummaryResponseHandler();
		taskClient.getTasksAssignedAsPotentialOwner(username, "en-UK", taskSummaryHandler);
		List<TaskSummary> result = taskSummaryHandler.getResults();
		return result;
	}

	public void startTask(Long taskId, String user) throws Exception {
		BlockingTaskOperationResponseHandler taskOperationHandler = new BlockingTaskOperationResponseHandler();
		taskClient.start(taskId, user, taskOperationHandler);
	}

	public void endTask(Long taskId, String username, Map<String, Object> outputMap) {
		try {
			BlockingTaskOperationResponseHandler taskOperationHandler = new BlockingTaskOperationResponseHandler();
			ContentData contentData = createOutputData(outputMap);
			taskClient.complete(taskId, username, contentData, taskOperationHandler);
		} catch (Exception e) {
		}
	}


	private ContentData createOutputData(Map<String, Object> outputMap) {
        try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(outputMap);
                objectOutputStream.close();
                ContentData contentData = new ContentData();
                contentData.setContent(byteArrayOutputStream.toByteArray());
                contentData.setAccessType(AccessType.Inline);
                return contentData;
        } catch (Exception e) {
                return null;
        }
}

}
