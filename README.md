# Storage Engine

Provide a convenience way to manipulate the store engine.\
For example:
1. using CDI
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
2. using ordinary API
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
