package org.dfhu.ippp;


import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.dfhu.ippp.MyAnnotation")
public class MyPreProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (TypeElement e: annotations) {
            try {
                doIt(e);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }

    public void doIt(TypeElement e) throws IOException {
        TypeElement classElement = (TypeElement) e;
        PackageElement packageElement =
                (PackageElement) classElement.getEnclosingElement();

        JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
                classElement.getQualifiedName() + "MyThingy");

        BufferedWriter bw = new BufferedWriter(jfo.openWriter());
        bw.append("package ");
        bw.append(packageElement.getQualifiedName());
        bw.append(";");
        bw.newLine();
        bw.newLine();
        bw.append("// here is my comment");
        bw.close();

    }
}
