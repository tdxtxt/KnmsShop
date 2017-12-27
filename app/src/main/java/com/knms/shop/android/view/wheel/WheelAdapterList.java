package com.knms.shop.android.view.wheel;

import java.util.List;

public class WheelAdapterList extends WheelView.BaseWheelAdapter<String> {
	private List<String> data;
	public WheelAdapterList(){
		data = null;
	}
	public WheelAdapterList(List<String> data) {
		this.data = data;
	}

	/** 设置数据 */
	public void setData(List<String> data) {
		this.data = data;
		notifyDataSetChanged();
	}
	/** 添加数据 */
	public void addData(List<String> data){
		if (null == data) return;
		if (this.data != null ){
			this.data.addAll(data);
		}else {
			this.data = data;
		}
		notifyDataSetChanged();
	}

	@Override
	public int getItemsCount() {
		return data !=null ? data.size(): 0;
	}
	@Override
	public String getItem(int index) {
		return null== data ? "": (data.size() > 0 ? data.get(index) : "");
	}
	@Override
	public int indexOf(String o) {
		return null == data? 0: (-1 == data.indexOf(o)?0:data.indexOf(o));
	}
}
