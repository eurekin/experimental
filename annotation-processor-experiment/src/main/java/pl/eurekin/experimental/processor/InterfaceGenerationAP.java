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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SupportedAnnotationTypes("pl.eurekin.experimental.GenerateJavaBeanInterface")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class InterfaceGenerationAP extends AbstractProcessor {



    public InterfaceGenerationAP() {
        super();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    // Qualifiers

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "processor invoked");

        // Phase 1. preprocess
        //
        // Gather all annotated elements, to know them in advance in the second phase.
        // That will allow to upgrade the return types to their corresponding ViewModels,
        // if they're also marked for Generation.
        //
        // For the example:
        //
        //      @GenerateJavaBeanInterface
        //      class Base {
        //         public Other attribute;
        //      }
        //
        //      @GenerateJavaBeanInterface
        //      class Other { ... }
        //
        //
        // following ViewModel will be generated:
        //
        //     BaseViewModel {
        //        // note the return type - it's a ViewModel
        //        public OtherViewModel attribute;
        //     }
        List<String> classElementsMarkedForProcessing = new ArrayList<String>();
        for (Element elementWithTheAnnotation : elementsToVisit(roundEnv)) {
            if (elementWithTheAnnotation.getKind() == ElementKind.CLASS) {
                classElementsMarkedForProcessing.add(((TypeElement)elementWithTheAnnotation).getQualifiedName().toString());
            }
        }

        // Phase 2. the processing
        //
        // generate source files
        for (Element elem : elementsToVisit(roundEnv)) {
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

                    JavaFileObject jfo, jfo2;
                    String classSuffix = "ViewModel";
                    String factorySuffix = "ViewModelFactory";
                    Name baseClassName = classElement.getQualifiedName();
                    String generatedClassFQName = baseClassName + classSuffix;
                    String generatedFactoryFQName = baseClassName + factorySuffix;

                    jfo = processingEnv.getFiler().createSourceFile(generatedClassFQName);
                    jfo2 = processingEnv.getFiler().createSourceFile(generatedFactoryFQName);

                    String generatedClassName = classElement.getSimpleName() + classSuffix;
                    BufferedWriter viewModelSourceFile = new BufferedWriter(jfo.openWriter());
                    BufferedWriter viewModelFactorySourceFile = new BufferedWriter(jfo2.openWriter());
                    writeTopOfViewModelFileIntoBuffer(classElement, packageElement, baseClassName, generatedClassName, viewModelSourceFile);


                    List<String> propNameList = new ArrayList<String>();

                    // properties
                    for (Element element : classElement.getEnclosedElements()) {
                        if (debug) {
                            generateDebugInfoAboutPropertyIntoBuffer(viewModelSourceFile, element);
                        }
                        boolean qualifiesForPropertyGeneration = doesElementQualifyForPropertyGeneration(element);

                        viewModelSourceFile.append("\n");
                        if (qualifiesForPropertyGeneration) {
                            String fieldName = element.getSimpleName().toString();
                            String propType = element.asType().toString();
                            String baseType = classElement.getQualifiedName().toString();

                            String staticFName = fieldName.toUpperCase() + "_PROPERTY";

                            writePropertyIntoBuffer(viewModelSourceFile, fieldName, propType);
                            viewModelSourceFile.newLine();
                            writeStaticPropertyIntoBuffer(viewModelSourceFile, fieldName, propType, baseType, staticFName);

                            propNameList.add(fieldName + "Property");

                            viewModelSourceFile.newLine();
                            viewModelSourceFile.newLine();

                            // Property Promotion
                            if(classElementsMarkedForProcessing.contains(element.asType().toString())) {
                                if(debug) {
                                    generateDebugInfoAboutReturnTypeUpgrade(viewModelSourceFile);
                                }
                                String propTypeViewModel = propType + classSuffix;

                                String substitutedPropViewModelTemplate = substituteTemplate("public $propTypeViewModel $fieldNameViewModel = new $propTypeViewModel($fieldNameProperty);\n",
                                        "$propTypeViewModel", propTypeViewModel,
                                        "$fieldName", fieldName
                                );
                                viewModelSourceFile.append(substitutedPropViewModelTemplate);
                                viewModelSourceFile.newLine();
                            }
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

                            final String actionTemplate =
                                    "    public Runnable $actionAction = new Runnable() {\n" +
                                    "        @Override public void run() { base.$action(); }};";

                            String substitutedTemplate =  substituteTemplate(actionTemplate,
                                    "$action", element.getSimpleName().toString());
                            viewModelSourceFile.append(substitutedTemplate);
                        }


                        // CALLABLE


                        boolean qualifiesForCallableGeneration = isMethod && voidParameterType && !voidReturnType;
                        if (qualifiesForCallableGeneration) {

                            final ExecutableType executableType = (ExecutableType) element.asType();
                            TypeMirror returnType = executableType.getReturnType();
                            if (debug) {
                                generateDebugInfoAboutCallableCandidateMethodIntoBuffer(viewModelSourceFile, executableType, returnType);
                            }
                            if (executableType.getReturnType() instanceof PrimitiveType) {
                                PrimitiveType primitiveType = (PrimitiveType) executableType.getReturnType();

                                Types typeUtils = processingEnv.getTypeUtils();
                                if (debug)
                                    generateDebugInfoAboutPrimitiveReturnTypeOfCallableMethodCandidateIntoBuffer(viewModelSourceFile, primitiveType, typeUtils);
                                returnType = typeUtils.boxedClass(primitiveType).asType();
                            }
                            viewModelSourceFile.newLine();
                            if (debug)
                                generateDebugInfoAboutCallableReturnTypeIntoBuffer(viewModelSourceFile, returnType);

                            viewModelSourceFile.newLine();
                            String callableTemplate =
                                    "    public Callable<$returnType> $executableName = new Callable<$returnType>() {\n" +
                                    "        @Override public $returnType call() throws Exception {return base.$executableName();}};\n";
                            String callableTemplateAfterSubstitution = substituteTemplate(callableTemplate,
                                    "$returnType", returnType.toString(),
                                    "$executableName", element.getSimpleName().toString());
                            viewModelSourceFile.append(callableTemplateAfterSubstitution);
                        }
                    }

                    String propertyArraySB = Arrays.toString(propNameList.toArray(new String[propNameList.size()]));
                    String propertyArray = propertyArraySB.substring(1, propertyArraySB.length() - 1);

                    generateAllPropertiesMethodIntoBuffer(viewModelSourceFile, propertyArray);

                    viewModelSourceFile.append("}");
                    viewModelSourceFile.close();


                    String vmName = classElement.getSimpleName() + "ViewModel";
                    generateViewModelFactoryIntoBuffer(classElement, packageElement, baseClassName, generatedClassName, viewModelFactorySourceFile, vmName);
                    viewModelFactorySourceFile.close();
                } catch (IOException e) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "ERROR during write of the file" + e.getMessage());
                }


            }


        }
        return true;
    }

    private Set<? extends Element> elementsToVisit(RoundEnvironment roundEnv) {
        return roundEnv.getElementsAnnotatedWith(GenerateJavaBeanInterface.class);
    }


    private boolean doesElementQualifyForPropertyGeneration(Element element) {
        Set<Modifier> modifiers = element.getModifiers();
        boolean isField = ElementKind.FIELD.equals(element.getKind());
        boolean isFinal = modifiers.contains(Modifier.FINAL);
        boolean isPrivate = modifiers.contains(Modifier.PRIVATE);
        boolean isStatic = modifiers.contains(Modifier.STATIC);
        boolean isClassOrInterfaceType = element.asType().getKind() == TypeKind.DECLARED;

        return isField && isClassOrInterfaceType
                && !isFinal && !isPrivate && !isStatic;
    }

    private void writeStaticPropertyIntoBuffer(BufferedWriter viewModelSourceFile, String fieldName, String propType, String baseType, String staticFName) throws IOException {
        String staticPropertyString = generateStaticPropertyDescriptor(fieldName, propType, baseType, staticFName);
        viewModelSourceFile.append(staticPropertyString);
    }

    private void writePropertyIntoBuffer(BufferedWriter viewModelSourceFile, String fieldName, String propType) throws IOException {
        viewModelSourceFile.append(generatePropertyDeclarationString(propType, fieldName));
    }

    // ViewModel source

    private static final Class<?>[] viewModelImports = {
            pl.eurekin.experimental.Observable.class
            , pl.eurekin.experimental.Property.class
            , pl.eurekin.experimental.PropertyAccessor.class
            , pl.eurekin.experimental.SafePropertyListener.class
            , pl.eurekin.experimental.UnsafePropertyListener.class
            , pl.eurekin.experimental.Setter.class
            , pl.eurekin.experimental.Getter.class
            , pl.eurekin.experimental.TemplateGetter.class
            , pl.eurekin.experimental.TemplateSetter.class
            , pl.eurekin.experimental.state.ObservableState.class
            , pl.eurekin.experimental.state.SimpleState.class
            , pl.eurekin.experimental.viewmodel.ViewModel.class
            , java.util.concurrent.Callable.class

    };

    private static final Class<?>[] viewModelStaticImports = {
            pl.eurekin.experimental.SafePropertyListener.ChangeListener.class
    };

    private void writeTopOfViewModelFileIntoBuffer(TypeElement classElement, PackageElement packageElement, Name baseClassName, String generatedClassName, BufferedWriter bw) throws IOException {
        // top of the file
        bw.append("package ");
        bw.append(packageElement.getQualifiedName());
        bw.append(";");
        bw.newLine();
        bw.newLine();
        bw.newLine();

        for (Class<?> classToImport : viewModelImports) {
            bw.append(declarationOfImport(classToImport));
            bw.newLine();
        }
        for (Class<?> classToImportStatically : viewModelStaticImports) {
            bw.append(declarationOfStaticImport(classToImportStatically));
            bw.newLine();
        }
        bw.newLine();
        String baseClassSimpleName = classElement.getSimpleName().toString();
        bw.append("public class " + generatedClassName + " implements ViewModel<" + baseClassSimpleName + "> {");
        bw.newLine();

        String basePropertySupportTemplate =
                "    private Property<$BaseClass> baseProperty = new Property<$BaseClass>(\n" +
                "            new Getter<$BaseClass>(){@Override public $BaseClass get()    {return base();}},\n" +
                "            new Setter<$BaseClass>(){@Override public void set($BaseClass val) {set(val);}});\n" +
                "\n" +
                "    @Override\n" +
                "    public Property<$BaseClass> baseProperty() {\n" +
                "        return baseProperty;\n" +
                "    }";
        String substitutedBasePropertySupportTemplate = substituteTemplate(basePropertySupportTemplate,
                "$BaseClass", baseClassSimpleName);
        bw.append(substitutedBasePropertySupportTemplate);
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
                "        set(base);\n" +
                "    }\n");

        bw.newLine();

        // constructor
        bw.append("    public "+generatedClassName+"(final Observable<" + baseClassName + "> base) {\n" +
                "        base.registerChangeListener(new UnsafePropertyListener<" + baseClassName + ">(new ChangeListener() {\n" +
                "                    @Override public void act() {set(base.get());}}));\n" +
                "        set(base.get());\n" +
                "    }\n");
    }

    private CharSequence declarationOfImport(Class<?> clazz) {
        return substituteTemplate("import $clazz;", "$clazz", clazz.getName());
    }

    private CharSequence declarationOfStaticImport(Class<?> clazz) {
        return substituteTemplate("import static $clazz;", "$clazz", clazz.getCanonicalName());
    }


    private void generateAllPropertiesMethodIntoBuffer(BufferedWriter bw, String propertyArray) throws IOException {
        bw.append("\n" +
                "    @Override\n" +
                "    public Property<?>[] allProperties() {\n" +
                "        return new Property<?>[]{" + propertyArray + "};\n" +
                "    }\n");
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


    // Source of the Factory

    private static final Class<?>[] viewModelFactoryImports = {
            pl.eurekin.experimental.Observable.class
            , pl.eurekin.experimental.viewmodel.ViewModelFactory.class

    };

    private void generateViewModelFactoryIntoBuffer(TypeElement classElement, PackageElement packageElement,
                                                    Name baseClassName, String generatedClassName, BufferedWriter bw2,
                                                    String vmName) throws IOException {
        bw2.append("package ");
        bw2.append(packageElement.getQualifiedName());
        bw2.append(";");
        for (Class<?> classToImport : viewModelFactoryImports) {
            bw2.append(declarationOfImport(classToImport));
            bw2.newLine();
        }
        String classElementSimpleName = classElement.getSimpleName().toString();
        String viewModelFactorySourceCodeTemplate = "\n" +
                "import pl.eurekin.experimental.Observable;\n" +
                "import pl.eurekin.experimental.viewmodel.ViewModelFactory;\n" +
                "\n" +
                "public class $generatedClassNameFactory implements ViewModelFactory<$classElementSimpleName, $vmName> {\n" +
                "    @Override\n" +
                "    public $vmName newValueModel($baseClassName base) {\n" +
                "        return new $vmName(base);\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public $vmName newObservingValueModel(Observable<$baseClassName> observableBase) {\n" +
                "        return new $vmName(observableBase);\n" +
                "    }\n" +
                "}\n";
        String substitutedViewModelFactorySourceCodeTemplate = substituteTemplate(viewModelFactorySourceCodeTemplate,
                "$generatedClassName", generatedClassName,
                "$classElementSimpleName", classElementSimpleName,
                "$vmName", vmName,
                "$baseClassName", baseClassName.toString()
                );
        bw2.append(
                substitutedViewModelFactorySourceCodeTemplate);
    }

    // Utility Methods

    private String substituteTemplate(String template, String ... variableNamesAndValues) {
        if(variableNamesAndValues.length % 2 != 0)
            throw new RuntimeException("Bad argument list length: " + variableNamesAndValues.length);

        String substituted = template, variableName, variableValue;
        for (int i = 0; i < variableNamesAndValues.length; i+=2) {
            variableName = variableNamesAndValues[i];
            variableValue = variableNamesAndValues[i+1];
            substituted = substituted.replaceAll(
                    Pattern.quote(variableName),
                    Matcher.quoteReplacement(variableValue));
        }
        return substituted;
    }

    // DEBUG

    private void generateDebugInfoAboutReturnTypeUpgrade(BufferedWriter viewModelSourceFile) throws IOException  {
        viewModelSourceFile.append("\n// PROPERTY -> VIEWMODEL PROMOTION. Found property with a ViewModel as a return type!\n");
    }

    private void generateDebugInfoAboutPropertyIntoBuffer(BufferedWriter bw, Element element) throws IOException {
        bw.append("\n// found enclosed field element: " + element);
        bw.append("\n// it's kind: " + element.getKind());
        bw.append("\n// it's class: " + element.getClass());
        bw.append("\n// it's modifiers: " + element.getModifiers());
        bw.append("\n// it's simple Name: " + element.getSimpleName());
        bw.append("\n// it's typeMirror's toString: " + element.asType().toString());
        bw.append("\n// it's typeMirror's class: " + element.asType().getClass());
        bw.append("\n// it's typeMirror's kind: " + element.asType().getKind());
        bw.append("\n// it's typeMirror's kind class: " + element.asType().getKind().getClass());
    }

    private void generateDebugInfoAboutCallableCandidateMethodIntoBuffer(BufferedWriter viewModelSourceFile, ExecutableType executableType, TypeMirror returnType) throws IOException {
        viewModelSourceFile.append("\n// executableType.getReturnType().class " + executableType.getReturnType().getClass());
        viewModelSourceFile.append("\n// executableType.getReturnType().getKind() " + executableType.getReturnType().getKind());
        viewModelSourceFile.append("\n// executableType.getReturnType().getKind().getClass() " + executableType.getReturnType().getKind().getClass());
        viewModelSourceFile.append("\n// executableType.getReturnType().getKind() instanceof PrimitiveType " + Boolean.toString(returnType instanceof PrimitiveType));
    }

    private void generateDebugInfoAboutCallableReturnTypeIntoBuffer(BufferedWriter viewModelSourceFile, TypeMirror returnType) throws IOException {
        viewModelSourceFile.append("\n// final returnType " + returnType);
    }

    private void generateDebugInfoAboutPrimitiveReturnTypeOfCallableMethodCandidateIntoBuffer(BufferedWriter viewModelSourceFile, PrimitiveType primitiveType, Types typeUtils) throws IOException {
        viewModelSourceFile.append("\n// executableType.getReturnType().getKind().getClass() box " + typeUtils.boxedClass(primitiveType));
    }
}
