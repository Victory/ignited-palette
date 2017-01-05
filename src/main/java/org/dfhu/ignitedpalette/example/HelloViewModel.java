package org.dfhu.ignitedpalette.example;

import org.dfhu.ippp.IPViewModel;
import org.dfhu.ippp.TemplateInjector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

@IPViewModel(partial = "hello")
public class HelloViewModel {
    public String greeting;

    public String onTheFly() {

        File file = new File("src/main/resources/html/hello.html");
        String s;
        try {
            s = new Scanner(file).useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        String template;
        try {
            template = new TemplateInjector().onFly(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Pattern pattern = Pattern.compile("#~#greeting#~#");
        template = pattern.matcher(template).replaceAll(greeting);

        return template;
    }
}
