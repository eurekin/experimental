package experimental.guireloader;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author gmatoga
 */
public class AutomaticClassReloader<T> {

    private final ClassLoader parentClassLoader;
    private final String classNameStr;
    private final Callback<T> callback;

    AutomaticClassReloader(Class<? extends T> toReload, Callback<T> callback) {
        this.callback = callback;
        parentClassLoader = this.getClass().getClassLoader();
        classNameStr = toReload.getName();

        final URL url = ReloadingClassLoader.getUrl(classNameStr, parentClassLoader);
        try {
            final File file = new File(url.toURI());
            PollingFileWatcher pollingFileWatcher = new PollingFileWatcher(file, new CreateObjectOnClassChangeCallback());
            pollingFileWatcher.watch();
        } catch (URISyntaxException e) {
            throw new RuntimeException("url to uri error", e);
        }
    }

    public static void main(String... args) throws Exception {
        new AutomaticClassReloader<ReloadablePanel>(ReloadablePanel.class, null);

        new BufferedReader(new InputStreamReader(System.in)).readLine();
    }

    private void newVersionOfClassExist() {
        try {
            ReloadingClassLoader reloadingClassLoader = new ReloadingClassLoader(parentClassLoader, classNameStr);
            Class myObjectClass = reloadingClassLoader.loadClass(classNameStr);
            T object1 = (T) myObjectClass.newInstance();
            if (callback != null)
                callback.freshInstance( object1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static interface Callback<T> {
        public T freshInstance(T object);
    }

    private class CreateObjectOnClassChangeCallback implements PollingFileWatcher.Callback {
        @Override
        public void fileChanged() {
            newVersionOfClassExist();
        }

    }
}
