package org.dfhu.ignitedpalette.example;

import static spark.Spark.*;
import spark.servlet.SparkApplication;

public class ExampleSparkApplication implements SparkApplication {
    @Override
    public void init() {
        setUpRoutes();
    }

    public static void setUpRoutes() {
        get("hello", (req, res) -> "hi");
    }

    @Override
    public void destroy() {

    }
}
