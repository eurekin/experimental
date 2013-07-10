package experimental;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyExperiments {
    InvocationHandler invocationHandler;

    public static void main(String... args) {
        new ProxyExperiments().experiment();
    }

    public void experiment() {
        Object proxiedObject;
        proxiedObject = mockObject(InterfaceToIntercept.class);
        proxiedObject.toString();

    }

    private <T> T mockObject(Class<T> interfaceClass) {
        invocationHandler = new ExperimentalInvocationHandler();
        Object proxiedObject = Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[]{interfaceClass},
                invocationHandler
        );
        return interfaceClass.cast(proxiedObject);
    }


    public interface InterfaceToIntercept {
        String interceptMe(String in);
    }

    private static class ToIntercept implements InterfaceToIntercept {
        @Override
        public String interceptMe(String in) {
            return "base implementation returns " + in;
        }
    }

    private static class ExperimentalInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("You are calling method " + method.getName());
            return null;
        }
    }
}
