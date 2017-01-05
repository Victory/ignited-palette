package org.dfhu.ignitedpalette.example;

import org.dfhu.ippp.IgnitedPaletteHelloViewModel;
import spark.servlet.SparkApplication;

import static spark.Spark.get;

public class ExampleSparkApplication implements SparkApplication {
    @Override
    public void init() {
        setUpRoutes();
    }

    public static void setUpRoutes() {
        get("hello", (req, res) -> {
            IgnitedPaletteHelloViewModel vm = new IgnitedPaletteHelloViewModel();
            vm.greeting = "No default greeting";
            System.out.println(vm);
            return vm;
        });

        get("hello-default", (req, res) -> {
            IgnitedPaletteHelloViewModel ignitedPaletteHelloViewModel = new IgnitedPaletteHelloViewModel();
            System.out.println(ignitedPaletteHelloViewModel);
            return ignitedPaletteHelloViewModel;
        });

        get("otf", (req, res) -> {
            IgnitedPaletteHelloViewModel viewModel = new IgnitedPaletteHelloViewModel();
            viewModel.greeting = "my new greeting";
            System.out.println(viewModel.onTheFly());
            return viewModel.onTheFly();
        });
    }

    @Override
    public void destroy() {

    }
}
