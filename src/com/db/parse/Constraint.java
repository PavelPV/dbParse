package com.db.parse;

public class Constraint {
	
	private String name = "";
	
	private String field = "";
	
	private String type = "";
	
	private String ref = "";
	
	private boolean flag = false;
		
	public Constraint() {		
	}

	public Constraint(String name, String field, String type, String ref) {
		this.name = name;
		this.field = field;
		this.type = type;
		this.ref = ref;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	@Override
	public String toString() {
		return "Constraint [name=" + name + ", field=" + field + ", type="
				+ type + ", ref=" + ref + "]";
	}
	
}
