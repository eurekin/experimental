package experimental;

import com.sun.javaws.LocalInstallHandler;
import com.sun.javaws.jnl.LaunchDesc;
import com.sun.jnlp.JNLPClassLoader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author gmatoga
 */
public class JNLPHackToGetShortcutPath {

    private LocalInstallHandler localInstallHandler;
    private Method methodGetJNLPLocation;
    private JNLPClassLoader jnlpClassLoader;
    private LaunchDesc launchDescriptorAKAJNLPFile;
    private Object returnFromGetLocationMethod;

    public String getPath(Class mainClassLoadedFromJNLP) {
        try {
            findLocalInstallationHandler();
            getAReferenceToTheGetJnlpLocationMethod();
            findJnlpClassLoaderWhichHoldsTheJnlpFile(mainClassLoadedFromJNLP);
            if (jnlpClassLoader == null) return null;
            getTheJnlpFile();
            invokeTheGetLocationMethodOnTheJnlpFile();
            if (returnFromGetLocationMethod instanceof String)
                return (String) returnFromGetLocationMethod;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void invokeTheGetLocationMethodOnTheJnlpFile() throws IllegalAccessException, InvocationTargetException {
        returnFromGetLocationMethod = methodGetJNLPLocation.invoke(localInstallHandler, launchDescriptorAKAJNLPFile);
    }

    private void getTheJnlpFile() {
        launchDescriptorAKAJNLPFile = jnlpClassLoader.getLaunchDesc();
    }

    private void findJnlpClassLoaderWhichHoldsTheJnlpFile(Class mainClassLoadedFromJNLP) {
        jnlpClassLoader = findJNLPClassLoader(mainClassLoadedFromJNLP);
    }

    private void getAReferenceToTheGetJnlpLocationMethod() throws NoSuchMethodException {
        methodGetJNLPLocation = localInstallHandler
                .getClass()
                .getSuperclass()
                .getDeclaredMethod("getJnlpLocation", LaunchDesc.class);

        methodGetJNLPLocation.setAccessible(true);
    }

    private void findLocalInstallationHandler() {
        localInstallHandler = LocalInstallHandler.getInstance();
    }

    private JNLPClassLoader findJNLPClassLoader(Class mainClassLoadedFromJNLP) {
        final ClassLoader thisClassClassLoader = this.getClass().getClassLoader();
        final ClassLoader thisThreadsContextClassLoader = Thread.currentThread().getContextClassLoader();
        final ClassLoader mainClassLoadedFromJNLPClassLoader = mainClassLoadedFromJNLP.getClassLoader();
        final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

        // good explanation on http://lopica.sourceforge.net/faq.html#customcl
        ClassLoader[] orderedCandidates = {
                mainClassLoadedFromJNLPClassLoader,
                thisClassClassLoader,
                thisThreadsContextClassLoader,
                systemClassLoader};

        // Debug candidates
        for (ClassLoader cl : orderedCandidates) {
            boolean isJNLPClassLoader = cl instanceof JNLPClassLoader;
            System.out.println("JNLPClassLoader ? " + isJNLPClassLoader + ". Instance: " + cl.toString());
        }

        for (ClassLoader candidate : orderedCandidates)
            if (candidate instanceof JNLPClassLoader)
                return (JNLPClassLoader) candidate;
        return null;
    }
}
