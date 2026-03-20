package org.example.api;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.Base.BaseTest;
import java.util.HashMap;
import java.util.Map;

public class HmsApi extends BaseTest {

    // ─── Helper ──────────────────────────────────────────────────────────────

    private Response get(String token, String endpoint) {
        return RestAssured.given()
                .relaxedHTTPSValidation()
                .baseUri(baseurl)
                .contentType("application/json")
                .accept("application/json")
                .header(securityHeaderKey, securityHeaderValue)
                .header("Authorization", "Bearer " + token)
                .get("/api/" + endpoint);
    }

    private Response post(String token, String endpoint, Map<String, Object> body) {
        return RestAssured.given()
                .relaxedHTTPSValidation()
                .baseUri(baseurl)
                .contentType("application/json")
                .accept("application/json")
                .header(securityHeaderKey, securityHeaderValue)
                .header("Authorization", "Bearer " + token)
                .body(body)
                .post("/api/" + endpoint);
    }

    private Response postNoBody(String token, String endpoint) {
        return RestAssured.given()
                .relaxedHTTPSValidation()
                .baseUri(baseurl)
                .contentType("application/json")
                .accept("application/json")
                .header(securityHeaderKey, securityHeaderValue)
                .header("Authorization", "Bearer " + token)
                .post("/api/" + endpoint);
    }

    // ─── Auth ─────────────────────────────────────────────────────────────────

    public Response login(String email, String password) {
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        return RestAssured.given()
                .relaxedHTTPSValidation()
                .baseUri(baseurl)
                .contentType("application/json")
                .accept("application/json")
                .header(securityHeaderKey, securityHeaderValue)
                .body(body)
                .post("/api/userLogin");
    }

    public Response logout(String token) {
        return get(token, "logout");
    }

    public Response saveFcmToken(String token, String fcmToken) {
        Map<String, Object> body = new HashMap<>();
        body.put("fcm_token", fcmToken);
        return post(token, "save-token", body);
    }

    // ─── Dashboard ────────────────────────────────────────────────────────────

    public Response dashboard(String token) {
        return get(token, "dashboard");
    }

    public Response calender(String token, String start, String end) {
        Map<String, Object> body = new HashMap<>();
        body.put("start", start);
        body.put("end", end);
        return post(token, "calender", body);
    }

    // ─── Profile ─────────────────────────────────────────────────────────────

    public Response profile(String token) {
        return get(token, "profile");
    }

    public Response changePassword(String token, String currentPwd, String newPwd, String confirmPwd) {
        Map<String, Object> body = new HashMap<>();
        body.put("current_pwd", currentPwd);
        body.put("password", newPwd);
        body.put("confirm_password", confirmPwd);
        return post(token, "updatePassword", body);
    }

    // ─── Permission ──────────────────────────────────────────────────────────

    public Response permissionPage(String token) {
        return get(token, "permission");
    }

    public Response applyPermission(String token, String from, String to, String reason) {
        Map<String, Object> body = new HashMap<>();
        body.put("from", from);
        body.put("to", to);
        body.put("reason", reason);
        return post(token, "createPermission", body);
    }

    public Response permissionHistory(String token) {
        return get(token, "permissionHistory");
    }

    public Response updateUserPermission(String token, String id, int type) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("type", type);
        return post(token, "updateUserPermission", body);
    }

    // ─── Leave ───────────────────────────────────────────────────────────────

    public Response leavePage(String token) {
        return get(token, "leave");
    }

    public Response createLeave(String token, String type, String duration, String date, String reason) {
        Map<String, Object> body = new HashMap<>();
        body.put("type", type);
        body.put("duration", duration);
        body.put("date", date);
        body.put("reason", reason);
        return post(token, "createLeave", body);
    }

    public Response leaveHistory(String token) {
        return get(token, "leaveHistory");
    }

    public Response updateUserLeave(String token, String id, int type) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("type", type);
        return post(token, "updateUserLeave", body);
    }

    // ─── Projects ────────────────────────────────────────────────────────────

    public Response getProjects(String token) {
        return get(token, "getProjects");
    }

    // ─── Work Logs ───────────────────────────────────────────────────────────

    public Response getWorkingHour(String token, String date) {
        Map<String, Object> body = new HashMap<>();
        body.put("date", date);
        return post(token, "getWorkingHour", body);
    }

    public Response logHistory(String token, String date) {
        Map<String, Object> body = new HashMap<>();
        body.put("date", date);
        return post(token, "logHistory", body);
    }

    public Response updateReason(String token, String reason, String datetime) {
        Map<String, Object> body = new HashMap<>();
        body.put("reason", reason);
        body.put("datetime", datetime);
        return post(token, "updateReason", body);
    }

    // ─── Notifications ───────────────────────────────────────────────────────

    public Response getNotification(String token) {
        return get(token, "getNotification");
    }

    public Response getUserNotifications(String token) {
        return get(token, "getNotifications");
    }

    public Response changeReadStatus(String token, String id, String userid) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("userid", userid);
        return post(token, "change-readsts", body);
    }

    public Response changeReadAll(String token) {
        return get(token, "change-readall");
    }

    // ─── Tickets (User) ──────────────────────────────────────────────────────

    public Response getUserTickets(String token) {
        return get(token, "getUserTickets");
    }

    public Response viewTicketDetail(String token, String id) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        return post(token, "viewTicketDetail", body);
    }

    public Response createTicket(String token, String subject, String assignTo, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("subject", subject);
        body.put("assign_to", assignTo);
        body.put("message", message);
        return post(token, "createTicket", body);
    }

    public Response closeTicket(String token, String tid, String reply) {
        Map<String, Object> body = new HashMap<>();
        body.put("tid", tid);
        body.put("reply", reply);
        return post(token, "closeTicket", body);
    }

    public Response reopenTicket(String token, String tid) {
        Map<String, Object> body = new HashMap<>();
        body.put("tid", tid);
        return post(token, "reopen-ticket", body);
    }

    // ─── Admin: Request History ───────────────────────────────────────────────

    public Response requestHistory(String token) {
        return get(token, "requestHistory");
    }

    public Response viewRequest(String token, String id) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        return post(token, "viewRequest", body);
    }

    public Response updateRequest(String token, String id, int type) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("type", type);
        return post(token, "updateRequest", body);
    }

    public Response editRequest(String token, String id, String outTime) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("out_time", outTime);
        return post(token, "editRequest", body);
    }

    public Response refreshHours(String token, String id) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        return post(token, "refreshHours", body);
    }

    // ─── Admin: Working History ───────────────────────────────────────────────

    public Response workingHistory(String token, String from, String to, String userType) {
        Map<String, Object> body = new HashMap<>();
        body.put("from", from);
        body.put("to", to);
        body.put("user_type", userType);
        return post(token, "workingHistory", body);
    }

    // ─── Admin: Manage Working Time ───────────────────────────────────────────

    public Response manageWorkingTime(String token) {
        return get(token, "manage-working-time");
    }

    public Response updateTiming(String token, String userId, String time) {
        Map<String, Object> body = new HashMap<>();
        body.put("user", userId);
        body.put("time", time);
        return post(token, "updateTiming", body);
    }

    public Response timingHistory(String token, String userType) {
        Map<String, Object> body = new HashMap<>();
        body.put("user_type", userType);
        return post(token, "timingHistory", body);
    }

    // ─── Admin: User Management ───────────────────────────────────────────────

    public Response addUserPage(String token) {
        return get(token, "add-user");
    }

    public Response createUser(String token, Map<String, Object> userPayload) {
        return post(token, "createUser", userPayload);
    }

    public Response editUser(String token, String id) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        return post(token, "edit-user", body);
    }

    // ─── Admin: Manual Timings ────────────────────────────────────────────────

    public Response manageManualTime(String token) {
        return get(token, "manage-manual-time");
    }

    public Response updateMultipleTiming(String token, Object emp, String in, String out) {
        Map<String, Object> body = new HashMap<>();
        body.put("emp", emp);
        body.put("in", in);
        body.put("out", out);
        return post(token, "updateMultipleTiming", body);
    }

    public Response manualTimingHistory(String token) {
        return get(token, "manualtimingHistory");
    }

    public Response editTime(String token, String id) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        return post(token, "edit-time", body);
    }

    public Response updateTime(String token, String id, String intime, String outtime) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("intime", intime);
        body.put("outtime", outtime);
        return post(token, "updateTime", body);
    }

    public Response deleteTime(String token, String id) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        return post(token, "delete-time", body);
    }

    // ─── Admin: Manage Projects ───────────────────────────────────────────────

    public Response viewProjects(String token) {
        return get(token, "view-projects");
    }

    public Response getProjectHistory(String token) {
        return get(token, "getProjectHistory");
    }

    public Response editProject(String token, String pId) {
        Map<String, Object> body = new HashMap<>();
        body.put("pId", pId);
        return post(token, "edit-project", body);
    }

    public Response viewTasks(String token, String name, String prj) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("prj", prj);
        return post(token, "viewTasks", body);
    }

    public Response getProjectTasksHistory(String token, String name, String prj) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("prj", prj);
        return post(token, "getProjectTasksHistory", body);
    }

    public Response deleteProject(String token, String pId) {
        Map<String, Object> body = new HashMap<>();
        body.put("pId", pId);
        return post(token, "delete-project", body);
    }

    public Response deleteAttachment(String token, String pId) {
        Map<String, Object> body = new HashMap<>();
        body.put("pId", pId);
        return post(token, "deleteAttchment", body);
    }

    public Response createProject(String token, Map<String, Object> payload) {
        return post(token, "createProject", payload);
    }

    // ─── Admin: Tickets ───────────────────────────────────────────────────────

    public Response getTicketHistory(String token) {
        return get(token, "getTicketHistory");
    }

    public Response deleteTicket(String token, String tId) {
        Map<String, Object> body = new HashMap<>();
        body.put("tId", tId);
        return post(token, "delete-ticket", body);
    }

    // ─── Admin: Progress ─────────────────────────────────────────────────────

    public Response employeeProgress(String token) {
        return get(token, "employee-progress");
    }

    public Response employeeProgressHistory(String token, String from, String to, String userType) {
        Map<String, Object> body = new HashMap<>();
        body.put("from", from);
        body.put("to", to);
        body.put("user_type", userType);
        return post(token, "employeeProgressHistory", body);
    }

    public Response testerProgress(String token) {
        return get(token, "tester-progress");
    }

    public Response testerProgressHistory(String token, String from, String to, String userType) {
        Map<String, Object> body = new HashMap<>();
        body.put("from", from);
        body.put("to", to);
        body.put("user_type", userType);
        return post(token, "testerProgressHistory", body);
    }

    // ─── Admin: Manage Tasks ─────────────────────────────────────────────────

    public Response viewTasksPage(String token) {
        return get(token, "view-tasks");
    }

    public Response getTaskHistory(String token, String user, String proj, String date, String sts) {
        Map<String, Object> body = new HashMap<>();
        body.put("user1", user);
        body.put("proj1", proj);
        body.put("date1", date);
        body.put("sts1", sts);
        return post(token, "getTaskHistory", body);
    }

    public Response getTaskHistoryInprogress(String token, String user, String proj, String date, String sts) {
        Map<String, Object> body = new HashMap<>();
        body.put("user1", user);
        body.put("proj1", proj);
        body.put("date1", date);
        body.put("sts1", sts);
        return post(token, "getTaskHistoryInprogress", body);
    }

    public Response getTaskHistoryCompleted(String token, String user, String proj, String date, String sts) {
        Map<String, Object> body = new HashMap<>();
        body.put("user1", user);
        body.put("proj1", proj);
        body.put("date1", date);
        body.put("sts1", sts);
        return post(token, "getTaskHistoryCompleted", body);
    }

    public Response getTaskHistoryHold(String token, String user, String proj, String date, String sts) {
        Map<String, Object> body = new HashMap<>();
        body.put("user1", user);
        body.put("proj1", proj);
        body.put("date1", date);
        body.put("sts1", sts);
        return post(token, "getTaskHistoryHold", body);
    }

    public Response closedTasks(String token) {
        return get(token, "closed-tasks");
    }

    public Response getClosedTasks(String token) {
        return postNoBody(token, "getClosedTasks");
    }

    public Response createTask(String token, Map<String, Object> payload) {
        return post(token, "createTask", payload);
    }

    public Response updateTask(String token, Map<String, Object> payload) {
        return post(token, "updateTask", payload);
    }

    public Response viewTaskDetail(String token, String taskId) {
        Map<String, Object> body = new HashMap<>();
        body.put("task_id", taskId);
        return post(token, "viewTaskDetail", body);
    }

    public Response viewTaskLogs(String token, String id, String taskId) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("task_id", taskId);
        return post(token, "viewTaskLogs", body);
    }

    public Response viewTaskLogsClosed(String token, String id) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        return post(token, "viewTaskLogs_closed", body);
    }

    public Response saveRemark(String token, String taskId, String remark) {
        Map<String, Object> body = new HashMap<>();
        body.put("taskId", taskId);
        body.put("remark", remark);
        return post(token, "save-remark", body);
    }

    public Response viewProgressData(String token, String id) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        return post(token, "viewProgress_data", body);
    }

    public Response saveProgress(String token, String taskId, String devRating, String testerRating) {
        Map<String, Object> body = new HashMap<>();
        body.put("taskId", taskId);
        body.put("devRating", devRating);
        body.put("testerRating", testerRating);
        return post(token, "save-progress", body);
    }

    public Response editTask(String token, String tId, String taskid) {
        Map<String, Object> body = new HashMap<>();
        body.put("tId", tId);
        body.put("taskid", taskid);
        return post(token, "edit-task", body);
    }

    public Response activateTask(String token, String tId, String taskid) {
        Map<String, Object> body = new HashMap<>();
        body.put("tId", tId);
        body.put("taskid", taskid);
        return post(token, "activate-task", body);
    }

    public Response deleteTask(String token, String tId) {
        Map<String, Object> body = new HashMap<>();
        body.put("tId", tId);
        return post(token, "delete-task", body);
    }

    public Response addResource(String token, String addRes, String addProId) {
        Map<String, Object> body = new HashMap<>();
        body.put("add_res", addRes);
        body.put("add_proId", addProId);
        return post(token, "addResource", body);
    }

    public Response checkProjectResources(String token, String resource, String projectId) {
        Map<String, Object> body = new HashMap<>();
        body.put("resource", resource);
        body.put("projectId", projectId);
        return post(token, "check_project_resources", body);
    }

    public Response deleteEditTask(String token, String tId, String taskid) {
        Map<String, Object> body = new HashMap<>();
        body.put("tId", tId);
        body.put("taskid", taskid);
        return post(token, "delete-edittask", body);
    }

    // ─── Admin: Employee Performance ─────────────────────────────────────────

    public Response employeePerformance(String token) {
        return get(token, "employee-performance");
    }

    public Response employeeTaskHistory(String token, Map<String, Object> filters) {
        return post(token, "employeeTaskHistory", filters);
    }

    public Response testerPerformance(String token) {
        return get(token, "tester-performance");
    }

    public Response testerTaskHistory(String token, Map<String, Object> filters) {
        return post(token, "testerTaskHistory", filters);
    }

    // ─── Admin: Manage Permission ─────────────────────────────────────────────

    public Response getPermissionHistory(String token) {
        return get(token, "getpermissionHistory");
    }

    public Response updatePermission(String token, String id, String type) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("type", type);
        return post(token, "updatePermission", body);
    }

    // ─── Admin: Manage Leave ──────────────────────────────────────────────────

    public Response getLeaveHistory(String token) {
        return get(token, "getLeaveHistory");
    }

    public Response updateLeave(String token, String id, String type) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("type", type);
        return post(token, "updateLeave", body);
    }

    public Response getLeaveBalanceHistory(String token) {
        return get(token, "getLeaveBalanceHistory");
    }

    // ─── Calendar ────────────────────────────────────────────────────────────

    public Response viewEvents(String token, String start, String end) {
        Map<String, Object> body = new HashMap<>();
        body.put("start", start);
        body.put("end", end);
        return post(token, "viewEvents", body);
    }

    public Response addEvent(String token, String title, String date) {
        Map<String, Object> body = new HashMap<>();
        body.put("title", title);
        body.put("date", date);
        body.put("type", "add");
        return post(token, "event", body);
    }

    public Response deleteEvent(String token, String date) {
        Map<String, Object> body = new HashMap<>();
        body.put("date", date);
        body.put("type", "delete");
        return post(token, "eventDelete", body);
    }

    // ─── Announcements ───────────────────────────────────────────────────────

    public Response createAnnouncement(String token, String reason, String status) {
        Map<String, Object> body = new HashMap<>();
        body.put("reason", reason);
        body.put("status", status);
        return post(token, "createAnnouncement", body);
    }

    public Response announcementHistory(String token) {
        return get(token, "announcementHistory");
    }

    public Response availableAnnouncementHistory(String token) {
        return get(token, "availableannounceHistory");
    }

    public Response changeAnnounceSts(String token, String id, String type) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("type", type);
        return post(token, "ChangeAnnounceSts", body);
    }

    // ─── Admin: Relieved Employees ────────────────────────────────────────────

    public Response relievedEmpHistory(String token, String from, String to, String userType) {
        Map<String, Object> body = new HashMap<>();
        body.put("from", from);
        body.put("to", to);
        body.put("user_type", userType);
        return post(token, "relivedempHistory", body);
    }

    // ─── Admin: Blocked IPs ───────────────────────────────────────────────────

    public Response getBlockedIpsHistory(String token) {
        return get(token, "getblockedipsHistory");
    }

    // ─── Admin: User Calendar ─────────────────────────────────────────────────

    public Response viewUserCalender(String token, String userId, String start, String end) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", userId);
        body.put("start", start);
        body.put("end", end);
        return post(token, "viewUserCalender", body);
    }

    public Response userLogs(String token, String id, String date) {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("date", date);
        return post(token, "userlogs", body);
    }
}
