package org.dfhu.ignitedpalette.example;

import org.dfhu.ippp.MyAnnotation;
import spark.servlet.SparkApplication;

import static spark.Spark.get;

public class ExampleSparkApplication implements SparkApplication {
    @Override
    public void init() {
        setUpRoutes();
    }

    @MyAnnotation
    public static void setUpRoutes() {
        get("hello", (req, res) -> "hi");
    }

    @Override
    public void destroy() {

    }
}
