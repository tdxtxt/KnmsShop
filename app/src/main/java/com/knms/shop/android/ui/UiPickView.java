package com.knms.shop.android.ui;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.knms.shop.android.R;
import com.knms.shop.android.util.CalendarUtils;
import com.knms.shop.android.view.wheel.WheelAdapterList;
import com.knms.shop.android.view.wheel.WheelView;

import java.util.List;

/** 选择器 <br/>默认时间选择器 <br/>其它需求可自定义Builder  */
public class UiPickView extends FrameLayout {

	/** 第一项初始数据,第2项初始数据 */
	private String initData1, initData2;
	private WheelView pick_1,pick_2,pick_3;
	private WheelAdapterList adapter1,adapter2,adapter3;
	private OnPickChange onPickChange;
	private Builder builder;

	public UiPickView(@NonNull Context context) {
		super(context);
		initView();
	}

	public UiPickView(@NonNull Context context, @Nullable AttributeSet attrs) {
		this(context, attrs,0);
	}

	public UiPickView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}

	public String getItem1(){
		return pick_1.getAdapter().getItem(pick_1.getCurrentItem()).toString();
	}
	public String getItem2(){
		return pick_2.getAdapter().getItem(pick_2.getCurrentItem()).toString();
	}
	public String getItem3(){
		return pick_3.getAdapter().getItem(pick_3.getCurrentItem()).toString();
	}

	public Builder getBuilder() {
		return builder;
	}

	public void setBuilder(Builder builder) {
		this.builder = builder;
		initView();
	}

	public WheelAdapterList getAdapter1() {
		return adapter1;
	}

	public WheelAdapterList getAdapter2() {
		return adapter2;
	}

	public WheelAdapterList getAdapter3() {
		return adapter3;
	}

	public WheelView getPick_1() {
		return pick_1;
	}

	public WheelView getPick_2() {
		return pick_2;
	}

	public WheelView getPick_3() {
		return pick_3;
	}

	public String getInitData1() {
		return initData1;
	}

	public String getInitData2() {
		return initData2;
	}

	public void setOnPickChange(OnPickChange onPickChange) {
		this.onPickChange = onPickChange;
	}

	private void initView() {
		this.removeAllViews();
	 	View v = LayoutInflater.from(getContext()).inflate(R.layout.ui_pick_layout, null);
	 	pick_1 = (WheelView) v.findViewById(R.id.pick_1);
	 	pick_2 = (WheelView) v.findViewById(R.id.pick_2);
	 	pick_3 = (WheelView) v.findViewById(R.id.pick_3);
		if (null == builder)
			builder = createTimeBuilder(1);
	 	iniWheelView(builder);
		this.addView(v);
	}

	/** 当前时间Builder
	 * @param end	与当前年份差 */
	public Builder createTimeBuilder(int end){
		int year = CalendarUtils.getyear(null);
		List<String> dataYear = CalendarUtils.getYearsList(null,year,year + end);
		int moth = CalendarUtils.getMonth(null);
		List<String> dataMonth = CalendarUtils.getMothsList(moth);
		int day = CalendarUtils.getDay(null);
		List<String> dataDay = CalendarUtils.getDaysList(null, day);
		return createTimeBuilder(dataYear,dataMonth,dataDay);
	}
	/** 自定义起止时间Builder
	 * @param start 开始的年份
	 * @param end	结束年份
	 * */
	public Builder createTimeBuilder(int start, int end){
		List<String> dataYear = CalendarUtils.getYearsList(null,start,end);
		List<String> dataMonth = CalendarUtils.getMothsList();
		List<String> dataDay = CalendarUtils.getDaysList(null);
		return createTimeBuilder(dataYear,dataMonth,dataDay);
	}

	/** 12个月,当前年份剩余月份, 当前月份剩余天数*/
	private List<String> dataMothAll,dataMonthNow,dataDayNow;
    /**
     *  创建时间Builder
	 * @param data1		年份数据
	 * @param data2		初始化月份数据(当月份从当月开始时 data2all需要传入12个月的)
	 * @param data3		初始化天数数据
	 * @return
     */
	public Builder createTimeBuilder(List<String> data1, List<String> data2, List<String> data3){
		dataMothAll = CalendarUtils.getMothsList();
		dataMonthNow = data2;
		dataDayNow = data3;
		return new Builder().setData(data1,data2,data3)
				.setCyclic(true)
				.setLabelRight(5)
				.setListener(mPick1ItemSelectedListener,mPick2ItemSelectedListener,mPick3ItemSelectedListener)
				.setLabel("年","月","日");
	}
	private void iniWheelView(Builder builder){
		if (null == builder || null == builder.getData1())
			return;
		if (null == builder.getData2()) {
			builder.setData3(null);
			pick_2.setVisibility(View.GONE);
		}else {
			pick_2.setVisibility(View.VISIBLE);
		}
		if (null == builder.getData3())
			pick_3.setVisibility(View.GONE);
		else
			pick_3.setVisibility(View.VISIBLE);
		int minSize = builder.getItemsVisible()/2+builder.getItemsVisible()%2;
		if (builder.getData1().size() < minSize){
			pick_1.setCyclic(false);
		}else {
			pick_1.setCyclic(builder.isCyclic());
		}
		if (null == builder.getData2() || builder.getData2().size() < minSize){
			pick_2.setCyclic(false);
		}else {
			pick_2.setCyclic(builder.isCyclic());
		}

		if (null == builder.getData3() || builder.getData3().size() < minSize){
			pick_3.setCyclic(false);
		}else {
			pick_3.setCyclic(builder.isCyclic());
		}
		pick_1.setItemsVisible(builder.getItemsVisible());
		pick_2.setItemsVisible(builder.getItemsVisible());
		pick_3.setItemsVisible(builder.getItemsVisible());

		pick_1.setLabel(builder.getLabel1());
		pick_2.setLabel(builder.getLabel2());
		pick_3.setLabel(builder.getLabel3());
		if (1 < builder.labelRight) {
			pick_1.setLabelRight(builder.getLabelRight() / 2);
			pick_2.setLabelRight(builder.getLabelRight());
			pick_3.setLabelRight(builder.getLabelRight());
		}
		pick_1.setOnTouchListener(oTouchListener);
		pick_2.setOnTouchListener(oTouchListener);
		pick_3.setOnTouchListener(oTouchListener);
		pick_1.setOnItemSelectedListener(builder.getListener1());
		pick_2.setOnItemSelectedListener(builder.getListener2());
		pick_3.setOnItemSelectedListener(builder.getListener3());
		pick_1.setCurrentItem(builder.getDefSelectItem1());
		pick_2.setCurrentItem(builder.getDefSelectItem2());
		pick_3.setCurrentItem(builder.getDefSelectItem3());

		adapter1 = new WheelAdapterList(builder.getData1());
		adapter2 = new WheelAdapterList(builder.getData2());
		adapter3 = new WheelAdapterList(builder.getData3());
		pick_1.setAdapter(adapter1);
		pick_2.setAdapter(adapter2);
		pick_3.setAdapter(adapter3);
		initData1 = getItem1();
		initData2 = getItem2();
	}

	/** 改变月份数据 */
	private void changMonth(boolean old){
		if (old){
			if (!builder.getData2().equals(dataMonthNow)) {
				builder.setData2(dataMonthNow);
				if (null == builder.getData2() || builder.getData2().size() < builder.getItemsVisible()/2+builder.getItemsVisible()%2){
					pick_2.setCyclic(false);
				}else {
					pick_2.setCyclic(builder.isCyclic());
				}
				adapter2.setData(builder.getData2());
			}
		}else {
			if (!builder.getData2().equals(dataMothAll)) {
				builder.setData2(dataMothAll);
				pick_2.setCyclic(builder.isCyclic());
				adapter2.setData(builder.getData2());
			}
		}

	}
	/** 掉回调 */
	private void pickChange(){
		if (null != onPickChange)
			onPickChange.onChange(getItem1(),getItem2(),getItem3());
	}
	WheelView.OnItemSelectedListener mPick1ItemSelectedListener = new WheelView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(int index) {
			String year = getItem1();
			if (View.GONE == pick_2.getVisibility()){
				pickChange();
				return;
			}
			String month = getItem2();
			if (initData1.equals(year)) {
				changMonth(true);
				if (View.GONE == pick_3.getVisibility()){
					pickChange();
					return;
				}
				if (!initData2.equals(month)){
					builder.setData3(CalendarUtils.getDaysList(Integer.parseInt(year), Integer.parseInt(month)));
					pick_3.setCyclic(builder.isCyclic());
				}else {
					builder.setData3(dataDayNow);
					if (null == builder.getData3() || builder.getData3().size() < builder.getItemsVisible()/2+builder.getItemsVisible()%2){
						pick_3.setCyclic(false);
					}else {
						pick_3.setCyclic(builder.isCyclic());
					}
				}
			}else{
				changMonth(false);
				if (View.GONE == pick_3.getVisibility()){
					pickChange();
					return;
				}
				builder.setData3(CalendarUtils.getDaysList(Integer.parseInt(year), Integer.parseInt(month)));
				pick_3.setCyclic(builder.isCyclic());
			}
			adapter3.setData(builder.getData3());
			pickChange();
		}
	};
	WheelView.OnItemSelectedListener mPick2ItemSelectedListener = new WheelView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(int index) {
			if (View.GONE == pick_3.getVisibility()){
				pickChange();
				return;
			}
			String year = getItem1();
			String month = getItem2();
			if (initData1.equals(year) && initData2.equals(month)){
				builder.setData3(dataDayNow);
				if (null == builder.getData3() || builder.getData3().size() < builder.getItemsVisible()/2+builder.getItemsVisible()%2){
					pick_3.setCyclic(false);
				}else {
					pick_3.setCyclic(builder.isCyclic());
				}
			}else {
				pick_3.setCyclic(builder.isCyclic());
				builder.setData3(CalendarUtils.getDaysList(Integer.parseInt(year), Integer.parseInt(month)));
			}
			adapter3.setData(builder.getData3());
			pickChange();
		}
	};
	WheelView.OnItemSelectedListener mPick3ItemSelectedListener = new WheelView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(int index) {
			pickChange();
		}
	};
	OnTouchListener oTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				v.getParent().requestDisallowInterceptTouchEvent(true);
				break;
			case MotionEvent.ACTION_UP:
				v.getParent().requestDisallowInterceptTouchEvent(false);
				break;
			}
			return false;
		}
	};

	public class Builder{
		protected List<String> data1,data2,data3;
		protected WheelView.OnItemSelectedListener listener1,listener2,listener3;
		protected boolean isCyclic;
		protected String label1,label2,label3;
		protected int defSelectItem1,defSelectItem2,defSelectItem3;//默认选中
		protected String defData1,defData2,defData3;//默认选中数据
		protected int itemsVisible;//显示item个数
		protected int labelRight;//label右边偏移
		public Builder(){
			defSelectItem1 = 0;
			defSelectItem2 = 0;
			defSelectItem3 = 0;
			itemsVisible = 7;
			labelRight = 0;
			isCyclic = true;
		}

		public void builder(){
			setBuilder(this);
		}

		public Builder setDefData1(String defData1) {
			this.defData1 = defData1;
			setDefSelectItem1(getIndex(data1,defData1));
			return this;
		}
		public Builder setDefData(String defData1,String defData2,String defData3) {
			return setDefData1(defData1).setDefData2(defData2).setDefData3(defData3);
		}

		public Builder setDefData2(String defData2) {
			this.defData2 = defData2;
			setDefSelectItem2(getIndex(data2,defData2));
			return this;
		}

		private int getIndex(List<String> data,String idata){
			if (null == data || data.size() < 1 || TextUtils.isEmpty(idata))
				return 0;
			int index = data.indexOf(idata);
			if(-1 == index) index = 0;
			return index;

		}
		public Builder setDefData3(String defData3) {
			this.defData3 = defData3;
			setDefSelectItem3(getIndex(data3,defData3));
			return this;
		}

		public int getLabelRight() {
			return labelRight;
		}

		public Builder setLabelRight(int labelRight) {
			this.labelRight = labelRight;
			return this;
		}

		public int getItemsVisible() {
			return itemsVisible;
		}

		public Builder setItemsVisible(int itemsVisible) {
			this.itemsVisible = itemsVisible;
			return this;
		}

		public int getDefSelectItem1() {
			return defSelectItem1;
		}

		public Builder setDefSelectItem1(int defSelectItem1) {
			this.defSelectItem1 = defSelectItem1;
			return this;
		}

		public int getDefSelectItem2() {
			return defSelectItem2;
		}

		public Builder setDefSelectItem2(int defSelectItem2) {
			this.defSelectItem2 = defSelectItem2;
			return this;
		}

		public int getDefSelectItem3() {
			return defSelectItem3;
		}

		public Builder setDefSelectItem3(int defSelectItem3) {
			this.defSelectItem3 = defSelectItem3;
			return this;
		}

		public Builder setDefSelectItem(int defSelectItem1, int defSelectItem2, int defSelectItem3){
			return setDefSelectItem1(defSelectItem1).setDefSelectItem2(defSelectItem2).setDefSelectItem3(defSelectItem3);
		}

		public List<String> getData1() {
			return data1;
		}

		public Builder setData1(List<String> data1) {
			this.data1 = data1;
			return this;
		}

		public List<String> getData2() {
			return data2;
		}

		public Builder setData2(List<String> data2) {
			this.data2 = data2;
			return this;
		}

		public List<String> getData3() {
			return data3;
		}

		public Builder setData3(List<String> data3) {
			this.data3 = data3;
			return this;
		}

		public WheelView.OnItemSelectedListener getListener1() {
			return listener1;
		}

		public Builder setListener1(WheelView.OnItemSelectedListener listener1) {
			this.listener1 = listener1;
			return this;
		}

		public WheelView.OnItemSelectedListener getListener2() {
			return listener2;
		}

		public Builder setListener2(WheelView.OnItemSelectedListener listener2) {
			this.listener2 = listener2;
			return this;
		}

		public WheelView.OnItemSelectedListener getListener3() {
			return listener3;
		}

		public Builder setListener3(WheelView.OnItemSelectedListener listener3) {
			this.listener3 = listener3;
			return this;
		}

		public String getLabel1() {
			return label1;
		}

		public Builder setLabel1(String label1) {
			this.label1 = label1;
			return this;
		}

		public String getLabel2() {
			return label2;
		}

		public Builder setLabel2(String label2) {
			this.label2 = label2;
			return this;
		}

		public String getLabel3() {
			return label3;
		}

		public Builder setLabel3(String label3) {
			this.label3 = label3;
			return this;
		}

		public Builder setListener(WheelView.OnItemSelectedListener listener1, WheelView.OnItemSelectedListener listener2, WheelView.OnItemSelectedListener listener3){
			return setListener1(listener1).setListener2(listener2).setListener3(listener3);
		}
		public Builder setLabel(String label1, String label2, String label3){
			return setLabel1(label1).setLabel2(label2).setLabel3(label3);
		}
		public Builder setData(List<String> data1, List<String> data2, List<String> data3){
			return setData1(data1).setData2(data2).setData3(data3);
		}
		public boolean isCyclic() {
			return isCyclic;
		}

		public Builder setCyclic(boolean cyclic) {
			isCyclic = cyclic;
			return this;
		}
	}


	/** 滚动监听 */
	public interface OnPickChange{
		public void onChange(String item1, String item2, String item3);
	}
}
