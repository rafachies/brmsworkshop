package com.redhat.brmsworkshop.brms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import org.drools.SystemEventListenerFactory;
import org.jbpm.task.AccessType;
import org.jbpm.task.Content;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.hornetq.HornetQTaskClientConnector;
import org.jbpm.task.service.hornetq.HornetQTaskClientHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetContentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetTaskResponseHandler;
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

	// -Djboss.socket.binding.port-offset=200
	
	public Map<String, Object> getDataInput(TaskSummary taskSummary) {
		try {
			BlockingGetTaskResponseHandler handler = new BlockingGetTaskResponseHandler();
			taskClient.getTask(taskSummary.getId(), handler);
			Task task = handler.getTask();
			TaskData taskData = task.getTaskData();
			BlockingGetContentResponseHandler contentHandler = new BlockingGetContentResponseHandler();
			taskClient.getContent(taskData.getDocumentContentId(), contentHandler);
			Content content = contentHandler.getContent();
			ByteArrayInputStream bais = new ByteArrayInputStream(content.getContent());
			ObjectInputStream objectInputStream = new ObjectInputStream(bais);
			Object object = null;
			return (Map<String, Object>) objectInputStream.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
