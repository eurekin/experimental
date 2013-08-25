package experimental.guireloader;

import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;

public class ReloadingClassLoader extends ClassLoader {

    private final ClassLoader parentClassLoader;
    private final String reloadableClassName;
    private URL exampleReloadableClassURL;

    public ReloadingClassLoader(ClassLoader parent, String reloadableClassName) {
        super(parent);
        parentClassLoader = parent;
        this.reloadableClassName = reloadableClassName;

        exampleReloadableClassURL = getUrl(reloadableClassName, parentClassLoader);
        System.out.println("exampleReloadableClassURL: " + exampleReloadableClassURL);
    }

    public static void main(String[] args) throws Exception {

        final String exampleReloadableClassName = ReloadablePanel.class.getName();

        ClassLoader parentClassLoader = ReloadingClassLoader.class.getClassLoader();
        ReloadingClassLoader classLoader = new ReloadingClassLoader(parentClassLoader, exampleReloadableClassName);
        Class myObjectClass = classLoader.loadClass(exampleReloadableClassName);

        Component object1 =
                (Component) myObjectClass.newInstance();
        System.out.println("object1.toString(): " + object1.toString());

        System.out.println("Waiting for console input before before reload...");
        new BufferedReader(new InputStreamReader(System.in)).readLine();
        System.out.println("... reload...");


        //create new class loader so classes can be reloaded.
        classLoader = new ReloadingClassLoader(parentClassLoader, exampleReloadableClassName);
        myObjectClass = classLoader.loadClass(exampleReloadableClassName);
        object1 = (Component) myObjectClass.newInstance();
        System.out.println("... reloaded.");

        System.out.println("object1.toString(): " + object1.toString());

    }

    public static URL getUrl(String exampleReloadableClassName, ClassLoader parentClassLoader) {
        final String exampleReloadableClassResourceString =
                exampleReloadableClassName.replaceAll(Pattern.quote("."), "/")
                        + ".class";
        System.out.println("exampleReloadableClassResourceString: " + exampleReloadableClassResourceString);
        return parentClassLoader.getResource(exampleReloadableClassResourceString);
    }

    public Class loadClass(String name) throws ClassNotFoundException {


        if (!reloadableClassName.equals(name)) {
            System.out.println("Reloader: skipping class " + name);
            return super.loadClass(name);
        }

        try {
            String url = exampleReloadableClassURL.toString();
            URL myUrl = new URL(url);
            URLConnection connection = myUrl.openConnection();
            InputStream input = connection.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int data = input.read();

            while (data != -1) {
                buffer.write(data);
                data = input.read();
            }

            input.close();

            byte[] classData = buffer.toByteArray();

            return defineClass(reloadableClassName,
                    classData, 0, classData.length);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
