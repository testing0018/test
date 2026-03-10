package org.example.utils;

import org.testng.annotations.DataProvider;

public class DataProviderUtil {

    @DataProvider(name="loginData")
    public Object[][] loginData(){

        return new Object[][]{

                {"HL100057","Deep@1234"},
                {"HL100001","Hive@123"},
                {"HL100052","Hive123"},
                {"HL100090","Hive@123"},
                {"hl100026","Hive@123"}

        };
    }
}