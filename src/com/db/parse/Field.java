package com.db.parse;

public class Field {

	private String name = "";

	private String type = "";

	private String nn = "";

	private String def = "";

	private boolean flag = false;

	public Field() {
	}

	public Field(String name, String type, String nn, String def) {
		this.name = name;
		this.type = type;
		this.nn = nn;
		this.def = def;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNn() {
		return nn;
	}

	public void setNn(String nn) {
		this.nn = nn;
	}

	public String getDef() {
		return def;
	}

	public void setDef(String def) {
		this.def = def;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	@Override
	public String toString() {
		return "Field [name=" + name + ", type=" + type + ", nn=" + nn
				+ ", def=" + def + "]";
	}

}
