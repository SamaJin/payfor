package com.helphand.ccdms.beans;

import java.util.List;

public class beans_system_menu {
	private String text;
	private boolean leaf;
	private List<beans_system_menu> children;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<beans_system_menu> getChildren() {
		return children;
	}

	public void setChildren(List<beans_system_menu> children) {
		this.children = children;
	}

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}
}
