package pl.eurekin.experimental.processor;

import pl.eurekin.experimental.GenerateJavaBeanInterface;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.quote;

@SupportedAnnotationTypes("pl.eurekin.experimental.GenerateJavaBeanInterface")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
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

                    JavaFileObject jfo = null, jfo2;
                    String classSuffix = "ViewModel";
                    String factorySuffix = "ViewModelFactory";
                    Name baseClassName = classElement.getQualifiedName();
                    String generatedClassFQName = baseClassName + classSuffix;
                    String generatedFactoryFQName = baseClassName + factorySuffix;

                    jfo = processingEnv.getFiler().createSourceFile(generatedClassFQName);
                    jfo2 = processingEnv.getFiler().createSourceFile(generatedFactoryFQName);

                    String generatedClassName = classElement.getSimpleName() + classSuffix;
                    BufferedWriter bw = new BufferedWriter(jfo.openWriter());
                    BufferedWriter bw2 = new BufferedWriter(jfo2.openWriter());


                    // top of the file
                    bw.append("package ");
                    bw.append(packageElement.getQualifiedName());
                    bw.append(";");
                    bw.newLine();
                    bw.newLine();
                    bw.newLine();
                    bw.append("import pl.eurekin.experimental.Getter;\n" +
                            "import pl.eurekin.experimental.Observable;\n" +
                            "import pl.eurekin.experimental.Property;\n" +
                            "import pl.eurekin.experimental.PropertyAccessor;\n" +
                            "import pl.eurekin.experimental.SafePropertyListener;\n" +
                            "import pl.eurekin.experimental.Setter;\n" +
                            "import pl.eurekin.experimental.TemplateGetter;\n" +
                            "import pl.eurekin.experimental.TemplateSetter;\n" +
                            "import pl.eurekin.experimental.state.ObservableState;\n" +
                            "import pl.eurekin.experimental.state.SimpleState;\n" +
                            "import pl.eurekin.experimental.viewmodel.ViewModel;\n" +
                            "\n" +
                            "import java.util.concurrent.Callable;\n" +
                            "\n" +
                            "import static pl.eurekin.experimental.SafePropertyListener.ChangeListener;\n");
                    bw.newLine();
                    bw.append("public class " + generatedClassName + " implements ViewModel<" + classElement.getSimpleName() + "> {");
                    bw.newLine();
                    bw.newLine();
                    bw.append("\n" +
                            "    @Override\n" +
                            "    public " + baseClassName + " base() {\n" +
                            "        return base;\n" +
                            "    }\n");
                    bw.append("\n" +
                            "    private SimpleState baseNotNullState = new SimpleState(false);\n" +
                            "    @Override\n" +
                            "    public ObservableState baseNotNullState() {\n" +
                            "        return baseNotNullState;\n" +
                            "    }\n");

                    bw.append("\n" +
                            "    public void fireAllPropertyChange() {\n" +
                            "        for(Property p : allProperties())\n" +
                            "            p.signalExternalUpdate();\n" +
                            "    }\n");
                    bw.append("\n" +
                            "    @Override\n" +
                            "    public void set(" + baseClassName + " newBase) {\n" +
                            "        this.base = newBase;\n" +
                            "        baseNotNullState.set(newBase != null);\n" +
                            "        fireAllPropertyChange();\n" +
                            "    }\n");

                    // base object
                    bw.append("\n" +
                            "    public " + baseClassName + " base;\n" +
                            "\n" +
                            "    public " + generatedClassName + "(" + baseClassName + " base) {\n" +
                            "        this.base = base;\n" +
                            "    }\n");

                    bw.newLine();
                    bw.append("    public FieldViewModel(final Observable<" + baseClassName + "> base) {\n" +
                            "        base.registerChangeListener(new SafePropertyListener<" + baseClassName + ">(new ChangeListener() {\n" +
                            "                    @Override public void act() {set(base.get());}}));\n" +
                            "    }\n");

                    List<String> propNameList = new ArrayList<String>();

                    // properties
                    for (Element element : classElement.getEnclosedElements()) {
                        if (debug) {
                            bw.append("// found enclosed field element: " + element + "\n");
                            bw.append("// it's kind: " + element.getKind() + "\n");
                            bw.append("// it's class: " + element.getClass() + "\n");
                            bw.append("// it's modifiers: " + element.getModifiers() + "\n");
                            bw.append("// it's simple Name: " + element.getSimpleName() + "\n");
                            bw.append("// it's typeMirror's toString: " + element.asType().toString() + "\n");
                            bw.append("// it's typeMirror's class: " + element.asType().getClass() + "\n");
                            bw.append("// it's typeMirror's kind: " + element.asType().getKind() + "\n");
                            bw.append("// it's typeMirror's kind class: " + element.asType().getKind().getClass() + "\n");
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

                            String staticFName = fieldName.toUpperCase() + "_PROPERTY";
                            String staticPropertyString = generateStaticPropertyDescriptor(fieldName, propType, baseType, staticFName);

                            bw.append(generatePropertyDeclarationString(propType, fieldName));
                            bw.newLine();
                            bw.append(staticPropertyString);
                            propNameList.add(fieldName + "Property");

                            bw.newLine();
                            bw.newLine();
                        }

                        boolean isMethod = ElementKind.METHOD.equals(element.getKind());
                        boolean voidReturnType = false;
                        boolean voidParameterType = false;

                        if (element.asType() instanceof ExecutableType) {
                            final ExecutableType executableType = (ExecutableType) element.asType();

                            voidParameterType = executableType.getParameterTypes().size() == 0;
                            voidReturnType = TypeKind.VOID.equals(executableType.getReturnType().getKind());
                        }
                        boolean qualifiesForActionGeneration = isMethod && voidParameterType && voidReturnType;

                        // ACTION


                        if (qualifiesForActionGeneration) {
                            // final ExecutableType executableType = (ExecutableType) element.asType();

                            final String actionTemplate =
                                    "    public Runnable $$actionAction = new Runnable() {\n" +
                                            "        @Override public void run() { base.$$action(); }};";

                            bw.append(actionTemplate
                                    .replaceAll(quote("$$action"), element.getSimpleName().toString()));
                        }


                        // CALLABLE


                        boolean qualifiesForCallableGeneration = isMethod && voidParameterType && !voidReturnType;
                        if (qualifiesForCallableGeneration) {
                            final ExecutableType executableType = (ExecutableType) element.asType();
                            bw.append("\n// executableType.getReturnType().class " + executableType.getReturnType().getClass());
                            bw.append("\n// executableType.getReturnType().getKind() " + executableType.getReturnType().getKind());
                            bw.append("\n// executableType.getReturnType().getKind().getClass() " + executableType.getReturnType().getKind().getClass());
                            TypeMirror returnType = executableType.getReturnType();
                            bw.append("\n// executableType.getReturnType().getKind() instanceof PrimitiveType " + Boolean.toString(returnType instanceof PrimitiveType));

                            if (executableType.getReturnType() instanceof PrimitiveType) {
                                PrimitiveType primitiveType = (PrimitiveType) executableType.getReturnType();

                                TypeKind kind = executableType.getReturnType().getKind();
                                Types typeUtils = processingEnv.getTypeUtils();
                                bw.append("\n// executableType.getReturnType().getKind().getClass() box " + typeUtils.boxedClass(primitiveType));
                                returnType = typeUtils.boxedClass(primitiveType).asType();
                            }
                            bw.newLine();
                            bw.append("// final returnType " + returnType);
                            bw.newLine();
                            String callableTemplate = "    public Callable<$$returntype> $$executablename = new Callable<$$returntype>() {\n" +
                                    "        @Override public $$returntype call() throws Exception {return base.$$executablename();}};\n";
                            String callableTemplateAfterSubstitution = callableTemplate
                                    .replaceAll(Pattern.quote("$$returntype"), returnType.toString())
                                    .replaceAll(Pattern.quote("$$executablename"), element.getSimpleName().toString())
                                    ;
                            bw.append(callableTemplateAfterSubstitution);
                            bw.newLine();
                            bw.newLine();
                            bw.newLine();
                            //processingEnv.getTypeUtils().boxedClass(executableType.getReturnType());

                        }
                    }

                    String propertyArraySB = Arrays.toString(propNameList.toArray(new String[]{}));
                    String propertyArray = propertyArraySB.substring(1, propertyArraySB.length() - 1);

                    bw.append("\n" +
                            "    @Override\n" +
                            "    public Property<?>[] allProperties() {\n" +
                            "        return new Property<?>[]{" + propertyArray + "};\n" +
                            "    }\n");

                    bw.append("}");
                    bw.close();


                    String vmName = classElement.getSimpleName() + "ViewModel";
                    bw2.append("package pl.eurekin.editor;\n" +
                            "\n" +
                            "import pl.eurekin.experimental.Observable;\n" +
                            "import pl.eurekin.experimental.viewmodel.ViewModelFactory;\n" +
                            "\n" +
                            "public class " + generatedClassName + "Factory implements ViewModelFactory<" + classElement.getSimpleName()
                            + ", " + vmName + "> {\n" +
                            "    @Override\n" +
                            "    public " + vmName + " newValueModel(" + baseClassName + " base) {\n" +
                            "        return new " + vmName + "(base);\n" +
                            "    }\n" +
                            "\n" +
                            "    @Override\n" +
                            "    public " + vmName + " newObservingValueModel(Observable<" + baseClassName + "> observableBase) {\n" +
                            "        return new " + vmName + "(observableBase);\n" +
                            "    }\n" +
                            "}\n");
                    bw2.close();
                } catch (IOException e) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "ERROR during write of the file" + e.getMessage());
                }


            }


        }
        return true;
    }

    private String generateStaticPropertyDescriptor(String fieldName, String propType, String baseType, String staticFName) {
        return "    public static PropertyAccessor<" + propType + ", " + baseType + "> " + staticFName + " = new PropertyAccessor<" + propType + ", " + baseType + ">(\n" +
                "            new TemplateGetter<" + propType + ", " + baseType + ">() {@Override public " + propType + " get(" + baseType + " base) { if(base!=null) return base." + fieldName + "; else return null;}},\n" +
                "            new TemplateSetter<" + propType + ", " + baseType + ">() {@Override public void set(" + baseType + " base, " + propType + " newValue) { if(base!=null) base." + fieldName + " = newValue; }});";
    }

    private String generatePropertyDeclarationString(String propType, String propName) {
        return "    public Property<" + propType + "> " + propName + "Property = new Property<" + propType + ">(\n" +
                "        new Getter<" + propType + ">() {@Override public " + propType + " get() {  if(base()!=null) return base()." + propName + "; else return null;}},\n" +
                "        new Setter<" + propType + ">() {@Override public void set(" + propType + " newValue) { if(base()!=null) base()." + propName + " = newValue; }});";
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
