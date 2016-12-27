package org.dfhu.ippp;



import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.dfhu.ippp.IPViewModel")
public class MyPreProcessor extends AbstractProcessor {

    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE, "doing the noting");

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(IPViewModel.class);
        for (Element element: elements) {
            Name simpleName = element.getSimpleName();
            System.out.println(" simple name:" + simpleName.toString());

            for (TypeElement typeElement : annotations) {
                try {
                    doIt(typeElement, (TypeElement) element);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Create classes new classes
     * @param annotation - parent annotation
     * @param cls - the class that is annotated
     * @throws IOException - source file can't be created
     */
    private void doIt(TypeElement annotation, TypeElement cls) throws IOException {
        //if (1 == 1) throw new RuntimeException("oh hi!");

        System.out.println(annotation.getKind() + " " + annotation.getSimpleName() + " - " + cls.getSimpleName());

        // Check for wrong kind
        if (annotation.getKind() != ElementKind.ANNOTATION_TYPE) {
            System.out.println("wrong kind");
            return;
        }

        // Check for wrong simple name
        if( !annotation.getSimpleName().toString().equals(IPViewModel.class.getSimpleName())) {
            System.out.println("wrong simplename");
            return;
        }
        PackageElement packageElement =
                (PackageElement) cls.getEnclosingElement();
        String clsName = cls.getQualifiedName().toString();

        String[] bits = clsName.split("\\.");

        IPViewModel an = cls.getAnnotation(IPViewModel.class);

        File file = new File("src/main/resources/html/" + an.partial() + ".html");
        System.out.println(file.getAbsolutePath() + " " + file.canRead());

        System.out.println(an.partial());

        String s = new Scanner(file).useDelimiter("\\Z").next();

        List<? extends Element> enclosedElements =
                ElementFilter.fieldsIn(cls.getEnclosedElements());
        System.out.println("kinds");
        for (Element e: enclosedElements) {
            VariableElement ve = (VariableElement) e;
            System.out.println("const value " + ve.getSimpleName() + " " + ve.getConstantValue());
        }

        clsName = "IgnitedPalette" + bits[bits.length - 1];

        String clsString = buildClassString(packageElement, clsName, s);

        System.out.println(clsName + "\n" + clsString);

        JavaFileObject jfo = processingEnv.getFiler().createSourceFile(clsName);
        BufferedWriter bw = new BufferedWriter(jfo.openWriter());
        bw.write(clsString);
        bw.close();
    }

    private String buildClassString(PackageElement packageElement, String clsName, String s) {
        s = s.replace("\n", "\\n");
        s = s.replace("\"", "\\\"");

        String nl = "\n";
        String clsString = "package org.dfhu.ippp;" + nl + nl +
                "public class #clsName {" + nl +
                "  public int theNumber = 6;" + nl +
                "  public int getTheNumber() {" + nl +
                "    return theNumber;" + nl +
                "  }" + nl +
                "  public String toString() {" + nl +
                "    return \"" + s + "\";" + nl +
                "  } " + nl +
                "}" + nl;

        clsString = clsString.replace("#package", packageElement.getQualifiedName());
        clsString = clsString.replace("#clsName", clsName);
        return clsString;
    }

}
