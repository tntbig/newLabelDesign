package com.demo.utils;

public class DrawerItem {

	private String ItemName;
	private String title;
	private String user ;
	private String reset ;
	private int color ;
	public DrawerItem(String itemName , String title , String user , String reset ) {
		ItemName = itemName;
		this.title  = title ;
		this.user = user ;
		this.setReset(reset) ;
	}
	public DrawerItem(String itemName , String title , String user , String reset ,int color ) {
		ItemName = itemName;
		this.title  = title ;
		this.user = user ;
		this.setReset(reset) ;
		this.setColor(color) ;
	}
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getItemName() {
		return ItemName;
	}

	public void setItemName(String itemName) {
		ItemName = itemName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	public String getReset() {
		return reset;
	}
	public void setReset(String reset) {
		this.reset = reset;
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}

}
