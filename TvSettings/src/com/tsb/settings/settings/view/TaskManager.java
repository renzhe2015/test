package com.tsb.settings.settings.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;

public class TaskManager{
	private List<Activity> actList = new ArrayList<Activity>();
	private static TaskManager taskMgt=null;
	private boolean removable=true;
    
	private TaskManager() {
		super();
	}

	public static TaskManager getInstance(){
		if(taskMgt == null){
			taskMgt = new TaskManager();
		}
		return taskMgt;
	}
	
	public List<Activity> getTasks(){
		return actList;
	}
	
	public  void add(Activity act) {
		actList.add(act);
	}
	
	public  void remove(Activity activity) {
		if (actList.size() > 0 && removable) {
			actList.remove(activity);
		}
	}
    
	// close all Activity
	public  void finishAll() {
		removable=false;
		try {
			for (Activity activity : actList) {
				if (!activity.isFinishing()) {
					Log.i("FINISH_ACTIVITY:",activity.getClass().getName());
					activity.finish();
				}
			}
		} catch (Exception e) {
		}
		removable=true;
		actList.clear();
	}
	
	public void clear() {
		actList.clear();
	}
}
