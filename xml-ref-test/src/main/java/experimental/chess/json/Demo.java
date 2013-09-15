package experimental.chess.json;

import javax.xml.bind.JAXBContext;
 
public class Demo {
 
    public static void main(String[] args) throws Exception{
        System.out.println(JAXBContext.newInstance(Foo.class).getClass());
        System.out.println(JAXBContext.newInstance(Bar.class).getClass());
        System.out.println(JAXBContext.newInstance(Foo.class, Bar.class).getClass());
    }
 
}