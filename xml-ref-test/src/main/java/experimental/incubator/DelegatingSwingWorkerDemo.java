package experimental.incubator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.SwingWorker;

public class DelegatingSwingWorkerDemo {

    public static void main(String[] args) {
        new Launcher().launch();
    }
}

/**
 * Use this, to launch the worker.
 */
class DelegatingSwingWorker<I, O> extends SwingWorker<I, O> {

    SwingWorkerable<I, O> delegate;

    Publisher<O> publisher;

    public DelegatingSwingWorker(SwingWorkerable<I, O> delegate) {
        this.delegate = delegate;

        publisher = new Publisher<O>() {

            @Override
            public void publish(O... chunks) {
                DelegatingSwingWorker.this.publish(chunks);
            }

            @Override
            public void setProgress(int progress) {
                DelegatingSwingWorker.this.setProgress(progress);
            }
        };
    }

    @Override
    protected void process(List<O> chunks) {
        delegate.process(chunks);
    }

    @Override
    protected void done() {
        delegate.done();
    }

    @Override
    protected I doInBackground() throws Exception {
        return delegate.doInBackground(publisher);
    }

}

interface Publisher<O> {

    void publish(O... chunks);

    void setProgress(int progress);
}

/**
 * Make your class implement the interface.
 */
interface SwingWorkerable<I, O> {

    void process(List<O> chunks);

    I doInBackground(Publisher<O> publisher);

    void done();

}

/**
 * Let's say this is your super class:
 */

class MySuperClass {

}

/**
 * Use your super class, but implement the SwingWorkerable. Then
 * launch using a DelegatingSwingWorker
 */
class SleepingDemoSwingWorkerable
    extends MySuperClass
    implements SwingWorkerable<String, String> {

    @Override
    public void process(List<String> chunks) {
        System.out.println("Received partial update list " + chunks);
    }

    @Override
    public String doInBackground(Publisher<String> publisher) {
        publisher.publish("Worker: start");
        try {
            publisher.setProgress(0);
            Thread.sleep(200);
            publisher.setProgress(20);
            Thread.sleep(500);
            publisher.setProgress(70);
            Thread.sleep(300);
            publisher.setProgress(100);
            publisher.publish("Worker: woken up");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        publisher.publish("Worker: sending the result");
        return "Second passed.";
    }

    @Override
    public void done() {
        System.out.println("Worker Done");
    }

}

final class ConsoleProgressListener implements PropertyChangeListener {

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if(event.getPropertyName().equals("progress")) {
            System.out.println("Progress: " + event.getNewValue() + "%...");
        }
    }
}

/**
 * Launch
 */
class Launcher {

    public void launch() {
        SleepingDemoSwingWorkerable workerable = new SleepingDemoSwingWorkerable();
        DelegatingSwingWorker<String, String> delegatingSwingWorker =
                new DelegatingSwingWorker<String, String>(workerable);
        delegatingSwingWorker.addPropertyChangeListener(new ConsoleProgressListener());
        delegatingSwingWorker.execute();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Launcher done.");
    }
}
