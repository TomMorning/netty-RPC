package server;

public class MyServiceImpl implements MyService {
    @Override
    public String sayHello(String name) {
        return "Hello " + name;
    }
}