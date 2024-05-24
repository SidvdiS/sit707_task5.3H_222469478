package onTrackService;

import org.junit.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import onTrackService.OnTrackService.Feedback;
import onTrackService.OnTrackService.ProgressReport;
import onTrackService.OnTrackService.Session;
import onTrackService.OnTrackService.StudyGroup;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;

public class OnTrackTests {

    private OnTrackService.TaskService taskService;
    private OnTrackService.FeedbackService feedbackService;
    private OnTrackService.TutoringService tutoringService;
    private OnTrackService.StudyGroupService studyGroupService;
    private OnTrackService.ProgressReportService progressReportService;
    private OnTrackService.NotificationService notificationService;

    @BeforeEach
    public void setUp() {
        taskService = new OnTrackService.TaskService();
        feedbackService = new OnTrackService.FeedbackService();
        tutoringService = new OnTrackService.TutoringService();
        studyGroupService = new OnTrackService.StudyGroupService();
        progressReportService = new OnTrackService.ProgressReportService();
        notificationService = new OnTrackService.NotificationService();
    }

    @Test
    public void testTaskService() {
        OnTrackService.Task task1 = taskService.createTask("Task 1", "Description 1", "Creator 1");
        assertNotNull(task1);
        assertEquals("Task 1", task1.getTitle());
        assertTrue(taskService.addCollaborator(task1.getId(), "Collaborator 1"));
        assertFalse(taskService.addCollaborator(task1.getId(), "Collaborator 1"));
        assertTrue(taskService.submitTask(task1.getId(), "Student 1", "Submission 1"));
    }
    
    @Test
    public void testAddCollaboratorToNonExistingTask() {
        boolean result = taskService.addCollaborator(999, "Collaborator1");
        assertFalse(result);
    }

    @Test
    public void testSubmitTaskToNonExistingTask() {
        boolean result = taskService.submitTask(999, "Student1", "Submission1");
        assertFalse(result);
    }


    @Test
    public void testFeedbackService() {
        OnTrackService.Feedback feedback1 = feedbackService.provideFeedback(1, "Tutor 1", "Good work");
        assertNotNull(feedback1);
        assertEquals("Good work", feedback1.getComments());
    }

    @Test
    public void testTutoringService() {
        OnTrackService.Session session1 = tutoringService.scheduleSession("Tutor 1", "Student 1", new Date(), "10:00 AM");
        assertNotNull(session1);
        assertEquals("Tutor 1", session1.getTutor());
    }
    
    @Test
    public void testProvideFeedback() {
        Feedback feedback = feedbackService.provideFeedback(1, "Tutor", "Good job");
        assertNotNull(feedback);
        assertEquals("Tutor", feedback.getTutor());
        assertEquals("Good job", feedback.getComments());
    }

    @Test
    public void testProvideFeedbackForNonExistingTask() {
        Feedback feedback = feedbackService.provideFeedback(999, "Tutor", "Good job");
        assertNotNull(feedback);  // The feedback is created regardless of task existence in this implementation.
        assertEquals("Tutor", feedback.getTutor());
        assertEquals("Good job", feedback.getComments());
    }
    
    @Test
    public void testScheduleSession() {
        Date date = new Date();
        Session session = tutoringService.scheduleSession("Tutor1", "Student1", date, "10:00 AM");
        assertNotNull(session);
        assertEquals("Tutor1", session.getTutor());
        assertEquals("Student1", session.getStudent());
        assertEquals(date, session.getDate());
        assertEquals("10:00 AM", session.getTime());
    }



    @Test
    public void testStudyGroupService() {
        OnTrackService.StudyGroup group1 = studyGroupService.createStudyGroup("Group 1", "Creator 1");
        assertNotNull(group1);
        assertEquals("Group 1", group1.getGroupName());
        assertTrue(studyGroupService.joinStudyGroup("Student 1", group1.getId()));
    }
    
    @Test
    public void testCreateStudyGroup() {
        StudyGroup group = studyGroupService.createStudyGroup("Group1", "Creator");
        assertNotNull(group);
        assertEquals("Group1", group.getGroupName());
        assertEquals("Creator", group.getCreator());
        assertTrue(group.getMembers().contains("Creator"));
    }

    @Test
    public void testJoinStudyGroup() {
        StudyGroup group = studyGroupService.createStudyGroup("Group1", "Creator");
        boolean result = studyGroupService.joinStudyGroup("Student1", group.getId());
        assertTrue(result);
        assertTrue(group.getMembers().contains("Student1"));
    }


    @Test
    public void testGenerateReport() {
        ProgressReport report = progressReportService.generateReport("Student1");
        assertNotNull(report);
        assertEquals("Student1", report.getStudent());
        assertEquals(90, report.getAverageScore());
        assertEquals(20, report.getTasksCompleted());
    }

    @Test
    public void testProgressReportService() {
        OnTrackService.ProgressReport report1 = progressReportService.generateReport("Student 1");
        assertNotNull(report1);
        assertEquals(90, report1.getAverageScore());
    }

    @Test
    public void testNotificationService() {
        notificationService.notifyStudentOnTaskUpdate("Student 1", 1, "Task updated");
        assertEquals(1, notificationService.getNotifications("Student 1").size());
        notificationService.notifyTutorOnTaskSubmission("Tutor 1", 1, "Student 1");
        assertEquals(1, notificationService.getNotifications("Tutor 1").size());
    }
    
    @Test
    public void testNotifyStudentOnTaskUpdate() {
        notificationService.notifyStudentOnTaskUpdate("Student1", 1, "Task updated");
        List<String> notifications = notificationService.getNotifications("Student1");
        assertEquals(1, notifications.size());
        assertEquals("Task 1: Task updated", notifications.get(0));
    }

    @Test
    public void testNotifyTutorOnTaskSubmission() {
        notificationService.notifyTutorOnTaskSubmission("Tutor1", 1, "Student1");
        List<String> notifications = notificationService.getNotifications("Tutor1");
        assertEquals(1, notifications.size());
        assertEquals("Task 1 submitted by Student1", notifications.get(0));
    }

}
