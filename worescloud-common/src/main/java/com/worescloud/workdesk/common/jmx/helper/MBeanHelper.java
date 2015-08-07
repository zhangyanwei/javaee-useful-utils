package com.worescloud.workdesk.common.jmx.helper;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.worescloud.workdesk.common.jmx.MBean;

import javax.annotation.Nullable;
import javax.management.*;

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

public class MBeanHelper {

	private MBean mBean;

	public MBeanHelper(MBean mBean) {
		this.mBean = mBean;
	}

	public MBeanAttribute getMBeanAttribute(final String attribute) throws AttributeNotFoundException {

		if (mBean != null) {
			MBeanAttributeInfo[] attributes = mBean.getMBeanInfo().getAttributes();
			Optional<MBeanAttributeInfo> optional = tryFind(newArrayList(attributes), attributeInfo -> {
				String name = attributeInfo.getName();
				return name.equals(attribute);
			});

			if (optional.isPresent()) {
				return new MBeanAttribute(mBean, optional.get());
			}
		}

		throw new AttributeNotFoundException(format("Attribute [%s] not found", attribute));
	}

	public MBeanOperation getMBeanOperation(String actionName, String[] signature) throws ReflectionException {
		if (mBean != null) {
			final String methodIdentifier = methodIdentifier(actionName, signature);

			MBeanInfo mBeanInfo = mBean.getMBeanInfo();
			MBeanOperationInfo[] operations = mBeanInfo.getOperations();

			Optional<MBeanOperationInfo> optional = tryFind(newArrayList(operations), operationInfo -> {
				String identifier = methodIdentifier(operationInfo);
				return identifier.equals(methodIdentifier);
			});

			if (optional.isPresent()) {
				return new MBeanOperation(mBean, optional.get());
			}
		}

		throw new ReflectionException(
				new NoSuchMethodException(format("No matching method found for operation [%s]", actionName))
		);
	}

	private String methodIdentifier(String actionName, String[] signature) {
		return methodIdentifier(actionName, newArrayList(signature));
	}

	private String methodIdentifier(MBeanOperationInfo operationInfo) {
		Iterable<String> signature = transform(newArrayList(operationInfo.getSignature()), new Function<MBeanParameterInfo, String>() {
			@Nullable
			@Override
			public String apply(MBeanParameterInfo parameterInfo) {
				return parameterInfo.getType();
			}
		});

		return methodIdentifier(operationInfo.getName(), signature);
	}

	private String methodIdentifier(String actionName, Iterable signature) {
		return on("#").join(actionName, on(",").join(signature));
	}

}
