package com.worescloud.workdesk.common.jmx.inspect;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.worescloud.workdesk.common.jmx.MBean;
import com.worescloud.workdesk.common.jmx.annotations.Attribute;
import com.worescloud.workdesk.common.jmx.annotations.Managed;
import com.worescloud.workdesk.common.jmx.annotations.Operation;
import com.worescloud.workdesk.common.jmx.annotations.Parameter;

import javax.annotation.Nullable;
import javax.management.*;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.uniqueIndex;
import static java.util.Arrays.asList;
import static javax.management.MBeanOperationInfo.UNKNOWN;

public class AnnotationMBeanInspector extends AbstractMBeanInspector {

    private Class<? extends MBean> mBeanClass;
    private Managed managed;
    private Map<String, Attribute> attributeMap;
    private Map<String, Operation> operationMap;

    @Nullable
    @Override
    public MBeanInfo inspect(Class<? extends MBean> mBeanClass) {
        this.mBeanClass = mBeanClass;
        return super.inspect(mBeanClass);
    }

    @Override
    protected boolean isManaged(Class<? extends MBean> mBeanClass) {
        Managed managed = getManaged(mBeanClass);
        return managed != null && managed.enabled();
    }

    @Override
    protected boolean isManaged(final Field field) {
        Attribute attribute = getAttribute(field);
        return attribute != null;
    }

    @Override
    protected boolean isManaged(Method method) {
        Operation operation = getOperation(method);
        return operation != null;
    }

    @Override
    protected String getClassName(Class<? extends MBean> mBeanClass) {
        Managed managed = getManaged(mBeanClass);
        return fromNullable(emptyToNull(managed.name())).or(mBeanClass.getName());
    }

    @Override
    protected String getDescription(Class<? extends MBean> mBeanClass) {
        Managed managed = getManaged(mBeanClass);
        return emptyToNull(managed.description());
    }

    @Override
    protected ObjectNameInfo analysisObjectName(Class<? extends MBean> mBeanClass) {

        Managed managed = getManaged(mBeanClass);

        return new ObjectNameInfo(
                DOMAIN,
                emptyToNull(managed.type()),
                emptyToNull(managed.group()),
                fromNullable(emptyToNull(managed.name())).or(mBeanClass.getName())
        );
    }

    @Nullable
    @Override
    protected MBeanAttributeInfo analysisAttribute(PropertyDescriptor propertyDescriptor, Field field) {
        try {
            Attribute attribute = getAttribute(field);
            return new MBeanAttributeInfo(
                    field.getName(),
                    emptyToNull(attribute.description()),
                    propertyDescriptor.getReadMethod(),
                    attribute.readonly() ? null : propertyDescriptor.getWriteMethod()
            );
        } catch (IntrospectionException e) {
            throw new IllegalStateException(e);
        }
    }

    @Nullable
    @Override
    protected MBeanOperationInfo analysisOperation(Method method) {

        Operation operation = getOperation(method);

        return new MBeanOperationInfo(
                method.getName(),
                emptyToNull(operation.description()),
                analysisParameters(method),
                method.getReturnType().getName(),
                UNKNOWN
        );
    }

    private MBeanParameterInfo[] analysisParameters(Method method) {

        Operation operation = getOperation(method);

        Iterable<MBeanParameterInfo> parameterInfos = transform(asList(operation.parameters()), new Function<Parameter, MBeanParameterInfo>() {
            @Override
            public MBeanParameterInfo apply(Parameter parameter) {
                return new MBeanParameterInfo(
                        parameter.name(),
                        parameter.type().getName(),
                        emptyToNull(parameter.description())
                );
            }
        });

        return toArray(parameterInfos, MBeanParameterInfo.class);
    }

    private Managed getManaged(Class<? extends MBean> mBeanClass) {

        if (managed == null) {
            Optional<Class<?>> managedOptional = tryFind(newArrayList(mBeanClass.getInterfaces()), new Predicate<Class<?>>() {
                @Override
                public boolean apply(Class<?> interfaceClass) {
                    return interfaceClass.isAnnotationPresent(Managed.class);
                }
            });

            if (managedOptional.isPresent()) {
                managed = managedOptional.get().getAnnotation(Managed.class);
            }
        }

        return managed;
    }

    private Attribute getAttribute(Field field) {

        if (attributeMap == null) {
            attributeMap = uniqueIndex(asList(managed.attributes()), new Function<Attribute, String>() {
                @Override
                public String apply(Attribute attribute) {
                    String name = attribute.attribute();
                    checkManagedAttribute(name);
                    return name;
                }
            });
        }

        return attributeMap.get(field.getName());
    }

    private Operation getOperation(Method method) {

        if (operationMap == null) {
            operationMap = uniqueIndex(asList(managed.operations()), new Function<Operation, String>() {
                @Override
                public String apply(Operation operation) {

                    Iterable<Class<?>> parameterTypes = transform(asList(operation.parameters()), new Function<Parameter, Class<?>>() {
                        @Override
                        public Class<?> apply(Parameter parameter) {
                            return parameter.type();
                        }
                    });

                    checkManagedOperation(operation.operation(), toArray(parameterTypes, Class.class));
                    return getMethodIdentifier(operation.operation(), parameterTypes);
                }
            });
        }

        String methodIdentifier = getMethodIdentifier(method);
        return operationMap.get(methodIdentifier);
    }

    private void checkManagedAttribute(String name) {
        try {
            mBeanClass.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    private void checkManagedOperation(String method, Class[] parameterTypes) {
        try {
            mBeanClass.getMethod(method, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    private String getMethodIdentifier(Method method) {
        Iterable<String> parameters = transform(asList(method.getParameterTypes()), new Function<Class<?>, String>() {
            @Override
            public String apply(Class<?> type) {
                return type.getName();
            }
        });

        return generateMethodIdentifier(method.getName(), parameters);
    }

    private String getMethodIdentifier(String method, Iterable<Class<?>> parameters) {
        Iterable<String> parameterTypeNames = transform(parameters, new Function<Class<?>, String>() {
                    @Override
                    public String apply(Class<?> parameter) {
                        return parameter.getName();
                    }
                }
        );

        return generateMethodIdentifier(method, parameterTypeNames);
    }

    private String generateMethodIdentifier(String method, Iterable<String> parameters) {
        return on("#").join(method, on(",").join(parameters));
    }

}
