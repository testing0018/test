package org.example.test;

import io.restassured.response.Response;
import org.example.api.HmsApi;
import org.example.utils.DataProviderUtil;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/**
 * Full HRMS API test suite.
 *
 * All tests that require authentication reuse the token obtained in
 * LoginTest#verifyValidLogin (stored in the shared field TOKEN).
 * Run this file with TestNG; the suite order is Login → everything else.
 */
public class HrmsApiTest {

    private static final HmsApi api = new HmsApi();

    /** Shared token populated by the first successful login test. */
    private static String TOKEN = "";

    // =========================================================================
    // 1. LOGIN
    // =========================================================================

    @Test(dataProvider = "loginData", dataProviderClass = DataProviderUtil.class, groups = "login", priority = 1)
    public void verifyLogin(String email, String password) {
        Response response = api.login(email, password);
        System.out.println("LOGIN [" + email + "] → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
    }

    @Test(dataProvider = "validLogin", dataProviderClass = DataProviderUtil.class,
            groups = "login", priority = 2)
    public void verifyValidLogin(String email, String password) {
        Response response = api.login(email, password);
        System.out.println("VALID LOGIN → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        int status = response.jsonPath().getInt("status");
        Assert.assertEquals(status, 1, "Expected login success (status=1)");
        String token = response.jsonPath().getString("token");
        Assert.assertNotNull(token, "Token must not be null");
        TOKEN = token;   // store for downstream tests
    }

    // =========================================================================
    // 2. DASHBOARD
    // =========================================================================

    @Test(groups = "dashboard", dependsOnGroups = "login", priority = 3)
    public void verifyDashboard() {
        Response response = api.dashboard(TOKEN);
        System.out.println("DASHBOARD → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "dashboard", dependsOnGroups = "login", priority = 4)
    public void verifyCalender() {
        Response response = api.calender(TOKEN, "2025-07-01", "2025-08-01");
        System.out.println("CALENDER → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    // =========================================================================
    // 3. PROFILE & PASSWORD
    // =========================================================================

    @Test(groups = "profile", dependsOnGroups = "login", priority = 5)
    public void verifyProfile() {
        Response response = api.profile(TOKEN);
        System.out.println("PROFILE → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
        Assert.assertNotNull(response.jsonPath().getString("userInfo.userId"),
                "userInfo.userId must be present");
    }

    @Test(groups = "profile", dependsOnGroups = "login", priority = 6)
    public void verifyChangePassword_invalidCurrentPwd() {
        // Using wrong current password – server should return status=0
        Response response = api.changePassword(TOKEN, "WrongPass123", "Hive@123", "Hive@123");
        System.out.println("CHANGE PASSWORD (invalid) → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        // status=0 expected when current_pwd is wrong
        Assert.assertEquals(response.jsonPath().getInt("status"), 0);
    }

    // =========================================================================
    // 4. PERMISSION
    // =========================================================================

    @Test(groups = "permission", dependsOnGroups = "login", priority = 7)
    public void verifyPermissionPage() {
        Response response = api.permissionPage(TOKEN);
        System.out.println("PERMISSION PAGE → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "permission", dependsOnGroups = "login", priority = 8)
    public void verifyApplyPermission() {
        Response response = api.applyPermission(TOKEN,
                "2025-10-01 18:00:00", "2025-10-01 19:00:00", "personal");
        System.out.println("APPLY PERMISSION → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
    }

    @Test(groups = "permission", dependsOnGroups = "login", priority = 9)
    public void verifyPermissionHistory() {
        Response response = api.permissionHistory(TOKEN);
        System.out.println("PERMISSION HISTORY → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    // =========================================================================
    // 5. LEAVE
    // =========================================================================

    @Test(groups = "leave", dependsOnGroups = "login", priority = 10)
    public void verifyLeavePage() {
        Response response = api.leavePage(TOKEN);
        System.out.println("LEAVE PAGE → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(dataProvider = "leaveTypes", dataProviderClass = DataProviderUtil.class,
            groups = "leave", dependsOnGroups = "login", priority = 11)
    public void verifyCreateLeave(String type, String duration, String date, String reason) {
        Response response = api.createLeave(TOKEN, type, duration, date, reason);
        System.out.println("CREATE LEAVE [" + type + "/" + duration + "] → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
    }

    @Test(groups = "leave", dependsOnGroups = "login", priority = 12)
    public void verifyLeaveHistory() {
        Response response = api.leaveHistory(TOKEN);
        System.out.println("LEAVE HISTORY → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    // =========================================================================
    // 6. PROJECTS (user)
    // =========================================================================

    @Test(groups = "projects", dependsOnGroups = "login", priority = 13)
    public void verifyGetProjects() {
        Response response = api.getProjects(TOKEN);
        System.out.println("GET PROJECTS → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    // =========================================================================
    // 7. WORK LOGS
    // =========================================================================

    @Test(groups = "worklogs", dependsOnGroups = "login", priority = 14)
    public void verifyGetWorkingHour() {
        Response response = api.getWorkingHour(TOKEN, "2025-07-16");
        System.out.println("WORKING HOUR → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "worklogs", dependsOnGroups = "login", priority = 15)
    public void verifyLogHistory() {
        Response response = api.logHistory(TOKEN, "2025-07-16");
        System.out.println("LOG HISTORY → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "worklogs", dependsOnGroups = "login", priority = 16)
    public void verifyUpdateReason() {
        Response response = api.updateReason(TOKEN, "personal", "2025-07-16");
        System.out.println("UPDATE REASON (punch missing) → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
    }

    // =========================================================================
    // 8. NOTIFICATIONS
    // =========================================================================

    @Test(groups = "notifications", dependsOnGroups = "login", priority = 17)
    public void verifyGetNotification() {
        Response response = api.getNotification(TOKEN);
        System.out.println("GET NOTIFICATION → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "notifications", dependsOnGroups = "login", priority = 18)
    public void verifyGetUserNotifications() {
        Response response = api.getUserNotifications(TOKEN);
        System.out.println("GET USER NOTIFICATIONS → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "notifications", dependsOnGroups = "login", priority = 19)
    public void verifyChangeReadAll() {
        Response response = api.changeReadAll(TOKEN);
        System.out.println("CHANGE READ ALL → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
    }

    // =========================================================================
    // 9. TICKETS (User)
    // =========================================================================

    @Test(groups = "tickets", dependsOnGroups = "login", priority = 20)
    public void verifyGetUserTickets() {
        Response response = api.getUserTickets(TOKEN);
        System.out.println("GET USER TICKETS → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "tickets", dependsOnGroups = "login", priority = 21)
    public void verifyCreateTicket() {
        Response response = api.createTicket(TOKEN,
                "Test ticket subject", "HL100008-Harishma K", "Test ticket message");
        System.out.println("CREATE TICKET → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
    }

    // =========================================================================
    // 10. FCM TOKEN
    // =========================================================================

    @Test(groups = "fcm", dependsOnGroups = "login", priority = 22)
    public void verifySaveFcmToken() {
        Response response = api.saveFcmToken(TOKEN, "dummy_fcm_token_for_test");
        System.out.println("SAVE FCM TOKEN → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
    }

    // =========================================================================
    // 11. ADMIN – Request History
    // =========================================================================

    @Test(groups = "admin_request", dependsOnGroups = "login", priority = 23)
    public void verifyRequestHistory() {
        Response response = api.requestHistory(TOKEN);
        System.out.println("REQUEST HISTORY → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
    }

    // =========================================================================
    // 12. ADMIN – Working History
    // =========================================================================

    @Test(dataProvider = "workingHistoryUserTypes", dataProviderClass = DataProviderUtil.class,
            groups = "admin_working", dependsOnGroups = "login", priority = 24)
    public void verifyWorkingHistory(String userType) {
        Response response = api.workingHistory(TOKEN, "2025-04-02", "2025-04-02", userType);
        System.out.println("WORKING HISTORY [" + userType + "] → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    // =========================================================================
    // 13. ADMIN – Manage Working Time
    // =========================================================================

    @Test(groups = "admin_timing", dependsOnGroups = "login", priority = 25)
    public void verifyManageWorkingTime() {
        Response response = api.manageWorkingTime(TOKEN);
        System.out.println("MANAGE WORKING TIME → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(dataProvider = "timingHistoryUserTypes", dataProviderClass = DataProviderUtil.class,
            groups = "admin_timing", dependsOnGroups = "login", priority = 26)
    public void verifyTimingHistory(String userType) {
        Response response = api.timingHistory(TOKEN, userType);
        System.out.println("TIMING HISTORY [" + userType + "] → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    // =========================================================================
    // 14. ADMIN – Manual Timings
    // =========================================================================

    @Test(groups = "admin_manual_timing", dependsOnGroups = "login", priority = 27)
    public void verifyManageManualTime() {
        Response response = api.manageManualTime(TOKEN);
        System.out.println("MANAGE MANUAL TIME → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "admin_manual_timing", dependsOnGroups = "login", priority = 28)
    public void verifyManualTimingHistory() {
        Response response = api.manualTimingHistory(TOKEN);
        System.out.println("MANUAL TIMING HISTORY → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "admin_manual_timing", dependsOnGroups = "login", priority = 29)
    public void verifyUpdateMultipleTiming() {
        // emp list uses userCodes per the API sheet
        Response response = api.updateMultipleTiming(TOKEN,
                Arrays.asList("HL100026", "HL100052"),
                "2025-07-28 14:00:00",
                "2025-07-28 18:00:00");
        System.out.println("UPDATE MULTIPLE TIMING → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
    }

    // =========================================================================
    // 15. ADMIN – User Management
    // =========================================================================

    @Test(groups = "admin_user", dependsOnGroups = "login", priority = 30)
    public void verifyAddUserPage() {
        Response response = api.addUserPage(TOKEN);
        System.out.println("ADD USER PAGE → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "admin_user", dependsOnGroups = "login", priority = 31)
    public void verifyCreateUser() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "add-user");
        payload.put("username", "AutoTestUser");
        payload.put("email", "autotestuser@example.com");
        payload.put("password", "Test@1234");
        payload.put("userCode", "HL_AUTO01");
        payload.put("userId", "HL_AUTO01");
        payload.put("doj", "2025-01-01");
        payload.put("dob", "1995-06-15");
        payload.put("doe", null);
        payload.put("gender", "male");
        payload.put("hours", "08:00");
        payload.put("user_type", "Hivelance");
        payload.put("team", "Development");
        payload.put("designation", "Junior Web Developer");
        payload.put("reporting", Arrays.asList("HL100001-Sulthan Syed Ibrahim A"));
        payload.put("reporting_access", "1");
        payload.put("status", "1");
        payload.put("comp_off", "0.0");

        Response response = api.createUser(TOKEN, payload);
        System.out.println("CREATE USER → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
    }

    // =========================================================================
    // 16. ADMIN – Manage Projects
    // =========================================================================

    @Test(groups = "admin_projects", dependsOnGroups = "login", priority = 32)
    public void verifyViewProjects() {
        Response response = api.viewProjects(TOKEN);
        System.out.println("VIEW PROJECTS → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "admin_projects", dependsOnGroups = "login", priority = 33)
    public void verifyGetProjectHistory() {
        Response response = api.getProjectHistory(TOKEN);
        System.out.println("PROJECT HISTORY → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "admin_projects", dependsOnGroups = "login", priority = 34)
    public void verifyCreateProject() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("project_name", "AutoTestProject");
        payload.put("emp", Arrays.asList("HL100045-Sekar V", "HL100052-Harini B"));
        payload.put("total_hours", "100");
        payload.put("total_hours1", "50");
        payload.put("scope", "Automated test project");
        Response response = api.createProject(TOKEN, payload);
        System.out.println("CREATE PROJECT → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
    }

    // =========================================================================
    // 17. ADMIN – Tickets
    // =========================================================================

    @Test(groups = "admin_tickets", dependsOnGroups = "login", priority = 35)
    public void verifyGetTicketHistory() {
        Response response = api.getTicketHistory(TOKEN);
        System.out.println("GET TICKET HISTORY → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    // =========================================================================
    // 18. ADMIN – Progress
    // =========================================================================

    @Test(groups = "admin_progress", dependsOnGroups = "login", priority = 36)
    public void verifyEmployeeProgress() {
        Response response = api.employeeProgress(TOKEN);
        System.out.println("EMPLOYEE PROGRESS → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "admin_progress", dependsOnGroups = "login", priority = 37)
    public void verifyEmployeeProgressHistory() {
        Response response = api.employeeProgressHistory(TOKEN, "2025-07", "2025-08", "Hivelance");
        System.out.println("EMPLOYEE PROGRESS HISTORY → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "admin_progress", dependsOnGroups = "login", priority = 38)
    public void verifyTesterProgress() {
        Response response = api.testerProgress(TOKEN);
        System.out.println("TESTER PROGRESS → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "admin_progress", dependsOnGroups = "login", priority = 39)
    public void verifyTesterProgressHistory() {
        Response response = api.testerProgressHistory(TOKEN, "2025-07", "2025-08", "Hivelance");
        System.out.println("TESTER PROGRESS HISTORY → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    // =========================================================================
    // 19. ADMIN – Manage Tasks
    // =========================================================================

    @Test(groups = "admin_tasks", dependsOnGroups = "login", priority = 40)
    public void verifyViewTasksPage() {
        Response response = api.viewTasksPage(TOKEN);
        System.out.println("VIEW TASKS PAGE → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "admin_tasks", dependsOnGroups = "login", priority = 41)
    public void verifyGetTaskHistory_allFiltersEmpty() {
        Response response = api.getTaskHistory(TOKEN, "", "", "", "");
        System.out.println("TASK HISTORY (empty filters) → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
    }

    @Test(groups = "admin_tasks", dependsOnGroups = "login", priority = 42)
    public void verifyGetTaskHistory_withUser() {
        Response response = api.getTaskHistory(TOKEN, "HL100052-Harini B", "", "2025-08-18", "");
        System.out.println("TASK HISTORY (user filter) → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "admin_tasks", dependsOnGroups = "login", priority = 43)
    public void verifyGetTaskHistoryInprogress() {
        Response response = api.getTaskHistoryInprogress(TOKEN, "", "", "", "");
        System.out.println("TASK HISTORY INPROGRESS → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
    }

    @Test(groups = "admin_tasks", dependsOnGroups = "login", priority = 44)
    public void verifyGetTaskHistoryCompleted() {
        Response response = api.getTaskHistoryCompleted(TOKEN, "", "", "", "");
        System.out.println("TASK HISTORY COMPLETED → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "admin_tasks", dependsOnGroups = "login", priority = 45)
    public void verifyGetTaskHistoryHold() {
        Response response = api.getTaskHistoryHold(TOKEN, "", "", "", "");
        System.out.println("TASK HISTORY HOLD → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
    }

    @Test(groups = "admin_tasks", dependsOnGroups = "login", priority = 46)
    public void verifyClosedTasksPage() {
        Response response = api.closedTasks(TOKEN);
        System.out.println("CLOSED TASKS PAGE → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "admin_tasks", dependsOnGroups = "login", priority = 47)
    public void verifyGetClosedTasks() {
        Response response = api.getClosedTasks(TOKEN);
        System.out.println("GET CLOSED TASKS → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "admin_tasks", dependsOnGroups = "login", priority = 48)
    public void verifyCreateTask() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("team", "Development");
        payload.put("developer", "HL100052-Harini B");
        payload.put("tester", "HL100057-Deepak K");
        payload.put("task_assigned_date", "2025-10-01");
        payload.put("project_name", Arrays.asList("15"));
        payload.put("project_status", Arrays.asList("New"));
        payload.put("task_level", Arrays.asList("Medium"));
        payload.put("task_info", Arrays.asList("Automation test task"));
        Response response = api.createTask(TOKEN, payload);
        System.out.println("CREATE TASK → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
    }

    @Test(groups = "admin_tasks", dependsOnGroups = "login", priority = 49)
    public void verifyViewTaskDetail() {
        Response response = api.viewTaskDetail(TOKEN, "HLT39276");
        System.out.println("VIEW TASK DETAIL → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "admin_tasks", dependsOnGroups = "login", priority = 50)
    public void verifyCheckProjectResources() {
        Response response = api.checkProjectResources(TOKEN, "HL100052-Harini B", "10");
        System.out.println("CHECK PROJECT RESOURCES → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    // =========================================================================
    // 20. ADMIN – Employee Performance
    // =========================================================================

    @Test(groups = "admin_performance", dependsOnGroups = "login", priority = 51)
    public void verifyEmployeePerformance() {
        Response response = api.employeePerformance(TOKEN);
        System.out.println("EMPLOYEE PERFORMANCE → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "admin_performance", dependsOnGroups = "login", priority = 52)
    public void verifyEmployeeTaskHistory() {
        Map<String, Object> filters = new HashMap<>();
        filters.put("from", "2025-06");
        filters.put("to", "2025-08");
        filters.put("user_type", "Hivelance");
        filters.put("resource", "HL100052");
        filters.put("project", "");
        filters.put("status", "");
        Response response = api.employeeTaskHistory(TOKEN, filters);
        System.out.println("EMPLOYEE TASK HISTORY → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "admin_performance", dependsOnGroups = "login", priority = 53)
    public void verifyTesterPerformance() {
        Response response = api.testerPerformance(TOKEN);
        System.out.println("TESTER PERFORMANCE → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "admin_performance", dependsOnGroups = "login", priority = 54)
    public void verifyTesterTaskHistory() {
        Map<String, Object> filters = new HashMap<>();
        filters.put("from", "2025-06");
        filters.put("to", "2025-08");
        filters.put("user_type", "Hivelance");
        filters.put("resource", "HL100051");
        Response response = api.testerTaskHistory(TOKEN, filters);
        System.out.println("TESTER TASK HISTORY → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    // =========================================================================
    // 21. ADMIN – Manage Permission
    // =========================================================================

    @Test(groups = "admin_permission", dependsOnGroups = "login", priority = 55)
    public void verifyGetPermissionHistory() {
        Response response = api.getPermissionHistory(TOKEN);
        System.out.println("GET PERMISSION HISTORY → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    // =========================================================================
    // 22. ADMIN – Manage Leave
    // =========================================================================

    @Test(groups = "admin_leave", dependsOnGroups = "login", priority = 56)
    public void verifyGetLeaveHistory() {
        Response response = api.getLeaveHistory(TOKEN);
        System.out.println("GET LEAVE HISTORY → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "admin_leave", dependsOnGroups = "login", priority = 57)
    public void verifyGetLeaveBalanceHistory() {
        Response response = api.getLeaveBalanceHistory(TOKEN);
        System.out.println("GET LEAVE BALANCE HISTORY → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    // =========================================================================
    // 23. CALENDAR
    // =========================================================================

    @Test(groups = "calendar", dependsOnGroups = "login", priority = 58)
    public void verifyViewEvents() {
        Response response = api.viewEvents(TOKEN, "2025-04-01", "2025-05-01");
        System.out.println("VIEW EVENTS → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "calendar", dependsOnGroups = "login", priority = 59)
    public void verifyAddAndDeleteEvent() {
        // Add
        Response addResp = api.addEvent(TOKEN, "Test Birthday Event", "2025-12-25");
        System.out.println("ADD EVENT → " + addResp.asPrettyString());
        Assert.assertEquals(addResp.statusCode(), 200);
        Assert.assertEquals(addResp.jsonPath().getInt("status"), 1);

        // Delete
        Response delResp = api.deleteEvent(TOKEN, "2025-12-25");
        System.out.println("DELETE EVENT → " + delResp.asPrettyString());
        Assert.assertEquals(delResp.statusCode(), 200);
        Assert.assertEquals(delResp.jsonPath().getInt("status"), 1);
    }

    // =========================================================================
    // 24. ANNOUNCEMENTS
    // =========================================================================

    @Test(groups = "announcements", dependsOnGroups = "login", priority = 60)
    public void verifyCreateAnnouncement() {
        Response response = api.createAnnouncement(TOKEN,
                "Automation test announcement - please ignore", "1");
        System.out.println("CREATE ANNOUNCEMENT → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "announcements", dependsOnGroups = "login", priority = 61)
    public void verifyAnnouncementHistory() {
        Response response = api.announcementHistory(TOKEN);
        System.out.println("ANNOUNCEMENT HISTORY → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    @Test(groups = "announcements", dependsOnGroups = "login", priority = 62)
    public void verifyAvailableAnnouncementHistory() {
        Response response = api.availableAnnouncementHistory(TOKEN);
        System.out.println("AVAILABLE ANNOUNCEMENT HISTORY → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    // =========================================================================
    // 25. RELIEVED EMPLOYEES
    // =========================================================================

    @Test(groups = "relieved", dependsOnGroups = "login", priority = 63)
    public void verifyRelievedEmpHistory_default() {
        Response response = api.relievedEmpHistory(TOKEN, "", "", "Hivelance");
        System.out.println("RELIEVED EMP HISTORY → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    // =========================================================================
    // 26. BLOCKED IPs
    // =========================================================================

    @Test(groups = "blocked_ips", dependsOnGroups = "login", priority = 64)
    public void verifyGetBlockedIpsHistory() {
        Response response = api.getBlockedIpsHistory(TOKEN);
        System.out.println("BLOCKED IPS HISTORY → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }

    // =========================================================================
    // 27. LOGOUT (last)
    // =========================================================================

    @Test(groups = "logout", dependsOnGroups = "login", priority = 100)
    public void verifyLogout() {
        Response response = api.logout(TOKEN);
        System.out.println("LOGOUT → " + response.asPrettyString());
        Assert.assertEquals(response.statusCode(), 200);
        Assert.assertEquals(response.jsonPath().getInt("status"), 1);
    }
}
