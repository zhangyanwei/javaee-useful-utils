##### common
Provides some useful utilities

###### 1. JMX - used to expose any java bean could be managed by JMX

Step 1. define an interface to describe the managed bean
```java
import com.worescloud.workdesk.common.jmx.annotations.*;

@Managed(
        type = "example",
        group = "simple",
        name = "managed bean",
        description = "This is a sample managed bean",
        attributes = {
                @Attribute(attribute = "name", description = "This is user name  only for display"),
                @Attribute(attribute = "address", readonly = true, description = "This is a optional address")
        },
        operations = {
                @Operation(
                        operation = "getSessionID",
                        description = "get-formed operation"
                ),
                @Operation(
                        operation = "doSomething",
                        description = "This is an operation with parameter",
                        parameters = {
                                @Parameter(type = String.class, name = "Salt", description = "Salt Description")
                        }
                ),
        }
)
public interface SampleMXBean {}
```

Step 2. implement the above interface, and extends [CdiBean](worescloud-common/src/main/java/com/worescloud/workdesk/common/jmx/CdiMBean.java) to make it could be auto registered.
```java
import com.worescloud.workdesk.common.jmx.CdiMBean;
import com.worescloud.workdesk.service.startup.jmx.SampleManagedBeanMXBean;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.Random;

import static java.lang.String.format;

@Startup
@Singleton
public class Sample extends CdiMBean implements SampleMXBean {

    private String name;

    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.address = format("name + %s", new Random().nextDouble());
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSessionID() {
        return format("this is a runtime session id with name [%s]", name);
    }

    public void doSomething(String input) {
        System.out.println("You have invoked this method through JMX client");
    }
}
```

##### storage-engine
Provides a convenience way to manipulate the store engine.

For example:

###### 1. using CDI
```java
@Produces
public EngineConfiguration mongodbConfiguration() throws CommonException {

    MongoDB mongodb = CoreProperties.getProperties(MongoDB.class);

    return new EngineConfiguration() {

        @Override
        public Class<? extends Engine> engineType() {
            return MongoDBEngine.class;
        }

        @Override
        public Configuration getConfiguration() {
            return new MongoDBConfiguration()
                    .uri(mongodb.getUri())
                    .database(mongodb.getDatabase());
        }
    };
}
...
@Inject
private Storage<Message> messageStorage;
...
Message message = messageStorage.query()
        .full()
        .filter(eq(USER_ID, user.getId()))
        .filter(eq(MESSAGE_ID, messageId))
        .one()
```
###### 2. using ordinary API
```java
MongoDB mongodb = CoreProperties.getProperties(MongoDB.class);
EngineConfiguration engine = new EngineConfiguration() {

	@Override
	public Class<? extends Engine> engineType() {
		return MongoDBEngine.class;
	}

	@Override
	public Configuration getConfiguration() {
		return new MongoDBConfiguration()
                .uri(mongodb.getUri())
                .database(mongodb.getDatabase());
	}
};

Storage storage = EngineFactory.create(engine).storage(type);
...
Message message = storage.query()
        .full()
        .filter(eq(USER_ID, user.getId()))
        .filter(eq(MESSAGE_ID, messageId))
        .one()
```

And also supported other useful methods, for more information, please see [com.worescloud.workdesk.storage.Storage](worescloud-storage-engine/src/main/java/com/worescloud/workdesk/storage/Storage.java)
