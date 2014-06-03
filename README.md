# Titanite [![Build Status](https://drone.io/github.com/nosceon/titanite/status.png)](https://drone.io/github.com/nosceon/titanite/latest)

__titanite__ is a small web framework for Java 8, inspired by others like [Sinatra](http://www.sinatrarb.com/) and [Finatra](http://finatra.info/), running on top of [Netty](http://netty.io/)

## examples

```
import static org.nosceon.titanite.Titanite.*;
import static org.nosceon.titanite.Titanite.Responses.*;
import static org.nosceon.titanite.Method.*;

public class HelloWorld {

    public static void main(String[] args) {

        httpServer()
            .register(GET, "/hello/:name", r -> {
                String name = r.pathParams().getString("name");
                return ok().text("hello " + name).toFuture();
            })
            .start();

    }

}
```
```
import org.nosceon.titanite.Controller;
import static org.nosceon.titanite.Titanite.httpServer;

public class HelloWorldController extends Controller {

    {
        get("/hello/:name", req -> {
            String name = req.pathParams().getString("name");
            return ok().text("hello " + name.toUpperCase()).toFuture();
        });
    }

    public static void main(String[] args) {
        httpServer()
            .register(HelloWorldController.class)
            .start();
    }

}
```

### Maven
Releases of titanite are available in the maven central repository.
```
<dependency>
    <groupId>org.nosceon.titanite</groupId>
    <artifactId>titanite</artifactId>
    <version>1.0.0.Alpha1</version>
</dependency>
```
