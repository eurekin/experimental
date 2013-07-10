package pl.eurekin.experimental;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Set;

@SupportedAnnotationTypes("pl.eurekin.experimental.GenerateJavaBeanInterface")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class InterfaceGenerationAP extends AbstractProcessor {

    public InterfaceGenerationAP() {
        super();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "processor invoked");

        for (Element elem : roundEnv.getElementsAnnotatedWith(GenerateJavaBeanInterface.class)) {
            GenerateJavaBeanInterface complexity = elem.getAnnotation(GenerateJavaBeanInterface.class);
            Name simpleName = elem.getSimpleName();
            String message = "annotation found in " + simpleName
                    + " with debug " + complexity.debug();
            boolean debug = "true".equalsIgnoreCase(complexity.debug());
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);


            if (elem.getKind() == ElementKind.CLASS) {
                try {
                    TypeElement classElement = (TypeElement) elem;
                    PackageElement packageElement =
                            (PackageElement) classElement.getEnclosingElement();

                    JavaFileObject jfo = null;
                    String classSuffix = "ViewModel";
                    Name baseClassName = classElement.getQualifiedName();
                    String generatedClassFQName = baseClassName + classSuffix;
                    jfo = processingEnv.getFiler().createSourceFile(
                            generatedClassFQName);
                    String generatedClassName = classElement.getSimpleName() + classSuffix;
                    BufferedWriter bw = new BufferedWriter(jfo.openWriter());


                    // top of the file
                    bw.append("package ");
                    bw.append(packageElement.getQualifiedName());
                    bw.append(";");
                    bw.newLine();
                    bw.newLine();
                    bw.newLine();
                    bw.append("import pl.eurekin.experimental.Getter;");
                    bw.newLine();
                    bw.append("import pl.eurekin.experimental.Property;");
                    bw.newLine();
                    bw.append("import pl.eurekin.experimental.Setter;");
                    bw.newLine();
                    bw.append("import pl.eurekin.experimental.TemplateSetter;\n" +
                            "import pl.eurekin.experimental.TemplateGetter;\n" +
                            "import pl.eurekin.experimental.PropertyAccessor;");
                    bw.newLine();
                    bw.append("public class " + generatedClassName + " {");
                    bw.newLine();
                    bw.newLine();

                    // base object
                    bw.append("    public " + baseClassName + " base;\n" +
                            "\n" +
                            "    public " + generatedClassName + "(" + baseClassName + " base) {\n" +
                            "        this.base = base;\n" +
                            "    }\n");

                    bw.newLine();


                    // properties
                    for (Element element : classElement.getEnclosedElements()) {
                        if (debug) {
                            bw.append("// found enclosed field element: " + element + "\n");
                            bw.append("// it's kind: " + element.getKind() + "\n");
                            bw.append("// it's class: " + element.getClass() + "\n");
                            bw.append("// it's modifiers: " + element.getModifiers() + "\n");
                            bw.append("// it's simple Name: " + element.getSimpleName() + "\n");
                            bw.append("// it's typeMirror's toString: " + element.asType().toString() + "\n");
                            bw.append("// it's typeMirror's kind: " + element.asType().getKind() + "\n");
                            bw.append("// it's typeMirror's class: " + element.asType().getKind().getClass() + "\n");
                        }
                        Set<Modifier> modifiers = element.getModifiers();
                        boolean isField = ElementKind.FIELD.equals(element.getKind());
                        boolean isFinal = modifiers.contains(Modifier.FINAL);
                        boolean isPrivate = modifiers.contains(Modifier.PRIVATE);
                        boolean isStatic = modifiers.contains(Modifier.STATIC);
                        boolean isClassOrInterfaceType = element.asType().getKind() == TypeKind.DECLARED;

                        boolean qualifiesForPropertyGeneration = isField && isClassOrInterfaceType
                                && !isFinal && !isPrivate && !isStatic;

                        bw.append("\n");
                        if (qualifiesForPropertyGeneration) {
                            String fieldName = element.getSimpleName().toString();
                            String propType = element.asType().toString();
                            String baseType = classElement.getQualifiedName().toString();

                            String staticFName = fieldName.toUpperCase()+"_PROPERTY";
                            String staticPropertyString = generateStaticPropertyDescriptor(fieldName, propType, baseType, staticFName);

                            bw.append(generatePropertyDeclarationString(propType, fieldName));
                            bw.newLine();
                            bw.append(staticPropertyString);

                            bw.newLine();
                            bw.newLine();
                        }

                    }


                    bw.append("}");
                    bw.close();
                } catch (IOException e) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "ERROR during write of the file" + e.getMessage());
                }


            }


        }
        return true;
    }

    private String generateStaticPropertyDescriptor(String fieldName, String propType, String baseType, String staticFName) {
        return "    public static PropertyAccessor<"+propType+", "+baseType+"> " + staticFName + " = new PropertyAccessor<"+propType+", "+baseType+">(\n" +
        "            new TemplateGetter<"+propType+", "+baseType+">() {@Override public "+propType+" get("+baseType+" base) { return base."+fieldName+";}},\n" +
        "            new TemplateSetter<"+propType+", "+baseType+">() {@Override public void set("+baseType+" base, "+propType+" newValue) { base."+fieldName+" = newValue; }});";
    }

    private String generatePropertyDeclarationString(String propType, String propName) {
        return "    public Property<" + propType + "> " + propName + "Property = new Property<" + propType + ">(\n" +
                "        new Getter<" + propType + ">() {@Override public " + propType + " get() { return base." + propName + ";}},\n" +
                "        new Setter<" + propType + ">() {@Override public void set(" + propType + " newValue) { base." + propName + " = newValue; }});";
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
