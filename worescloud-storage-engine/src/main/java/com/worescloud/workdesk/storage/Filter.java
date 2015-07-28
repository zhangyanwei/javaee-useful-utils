package com.worescloud.workdesk.storage;

public class Filter {

	private Action action;
	private String fieldName;
	private Object parameters;

	public Filter(Action action, String fieldName, Object parameters) {
		this.action = action;
		this.fieldName = fieldName;
		this.parameters = parameters;
	}

	public Action getAction() {
		return action;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Object getParameter() {
		return parameters;
	}

	public enum Action {

		IN(Object[].class),
		ALL(Object[].class),
		EQ(Object.class),
		NE(Object.class),
		NOT(Filter.class),
		EXISTS(null);

		private Class parameterType;

		Action(Class parameterType) {
			this.parameterType = parameterType;
		}

		public Class getParameterType() {
			return parameterType;
		}
	}

}
