package com.helphand.ccdms.beans;

/**
 * Ext.grid.Panel columns:
 */
public class report_column_bean {

	private String text;
	private String dataIndex;
	private int width;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getDataIndex() {
		return dataIndex;
	}

	public void setDataIndex(String dataIndex) {
		this.dataIndex = dataIndex;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

}
