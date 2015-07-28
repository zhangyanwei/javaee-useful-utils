package com.worescloud.workdesk.storage.cdi;

import com.worescloud.workdesk.storage.EngineConfiguration;
import com.worescloud.workdesk.storage.EngineFactory;
import com.worescloud.workdesk.storage.Storage;
import com.worescloud.workdesk.storage.exception.StorageException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@ApplicationScoped
public class Resources {

	@Produces
	@SuppressWarnings("unchecked")
	public <T extends Serializable> Storage<T> storage(InjectionPoint injectionPoint) throws StorageException {
		ParameterizedType parameterizedType = (ParameterizedType) injectionPoint.getType();
		Type[] typeArgs = parameterizedType.getActualTypeArguments();
		Class<T> entityClass = (Class<T>) typeArgs[0];

		EngineConfiguration configuration = CDI.current().select(EngineConfiguration.class).get();
		return EngineFactory.create(configuration).storage(entityClass);
	}

}
