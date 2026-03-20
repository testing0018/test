package org.example.utils;

import org.testng.annotations.DataProvider;

public class DataProviderUtil {

    @DataProvider(name = "loginData")
    public Object[][] loginData() {
        return new Object[][]{
                {"HL100057", "Deep@1234"},   // valid
                {"HL100001", "Hive@123"},    // valid
                {"HL100052", "Hive123"},     // invalid password – expect status=0
                {"HL100090", "Hive@123"},    // unknown user – expect status=0
                {"hl100026", "Hive@123"},    // lowercase id – valid (case-insensitive)
        };
    }

    @DataProvider(name = "validLogin")
    public Object[][] validLogin() {
        // Single valid credential used by chained tests
        return new Object[][]{{"HL100001", "Hive@123"}};
    }

    @DataProvider(name = "workingHistoryUserTypes")
    public Object[][] workingHistoryUserTypes() {
        return new Object[][]{
                {"All"},
                {"Hivelance"},
                {"Plurance"},
        };
    }

    @DataProvider(name = "timingHistoryUserTypes")
    public Object[][] timingHistoryUserTypes() {
        return new Object[][]{
                {""},
                {"Hivelance"},
                {"Plurance"},
        };
    }

    @DataProvider(name = "leaveTypes")
    public Object[][] leaveTypes() {
        return new Object[][]{
                {"CL",  "Half Day(FN)", "2025-10-01", "personal"},
                {"SL",  "Full Day",     "2025-10-02", "sick"},
                {"LOP", "Full Day",     "2025-10-03", "emergency"},
        };
    }
}
