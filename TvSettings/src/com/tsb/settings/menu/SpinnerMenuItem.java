package com.tsb.settings.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tsb.settings.R;

import java.util.ArrayList;
import java.util.List;

public class SpinnerMenuItem extends MenuItem {

	private static class Option {
		
		int value;
		int titleId;
		Object title;
		boolean skip = false;
		
		public Option(int value) {
			this.value = value;
			titleId = value;
		}
		
		public Option(int value, Object title) {
			this.value = value;
			this.title = title;
		}
		
		public Option(int value, int title) {
			this.value = value;
			titleId = title;
		}

		public String getTitle(Context context) {
			if (title != null) {
				return title.toString();
			}
			return context.getResources().getString(titleId);
		}
		
		public Option setSkipped(boolean skipped) {
			skip = skipped;
			return this;
		}
	}
	
	// Data and status
	private final List<Option> options;
	private int selection = -1;

	// View
	private TextView mTextControl;

	SpinnerMenuItem(int title) {
		super(title, ITEM_TYPE_SPINNER);
		options = new ArrayList<Option>();
	}

	/**
	 * Add an option to the spinner.
	 * Note that the value of each item is set to its title resource id.
	 * @param title The string resource id for the option title.
	 * @return
	 */
	public SpinnerMenuItem addSpinnerOption(int title) {
		options.add(new Option(title));
		if (selection < 0) {
			selection = 0;
		}
		return this;
	}
	
	/**
	 * Add an option to the spinner.
	 * @param title The string resource id for the option title.
	 * @param value The corresponding value of this option.
	 * @return
	 */
	public SpinnerMenuItem addSpinnerOption(int title, int value) {
		return addSpinnerOption(title, value, false);
	}
	
	public SpinnerMenuItem addSpinnerOption(int title, int value, boolean skipped) {
		options.add(new Option(value, title).setSkipped(skipped));
		if (selection < 0) {
			selection = 0;
		}
		return this;
	}
	
	public SpinnerMenuItem addSpinnerOption(Object title, int value) {
		options.add(new Option(value, title));
		if (selection < 0) {
			selection = 0;
		}
		return this;
	}
	
	/**
	 * Setup spinner options by a String array.
	 * Note that the value of each item would be its index.
	 * @param languages
	 * @return
	 */
	public SpinnerMenuItem setSpinnerOptionsByArray(String[] languages) {
		for (int i = 0; i < languages.length; i++) {
			options.add(new Option(i, languages[i]));
		}
		if (selection < 0) {
			selection = 0;
		}
		return this;
	}
	
	public SpinnerMenuItem setSpinnerOptionsByArray(String[] languages, int[] values) {
		if (languages.length != values.length) {
			throw new IllegalArgumentException();
		}
		
		for (int i = 0; i < languages.length; i++) {
			options.add(new Option(values[i], languages[i]));
		}
		if (selection < 0) {
			selection = 0;
		}
		return this;
	}
	
	public SpinnerMenuItem setCurrentPosition(int position) {
		return setCurrentPosition(position, false);
	}
	
	public SpinnerMenuItem setCurrentPosition(int position, boolean notify) {
		selection = position;
		if (notify) {
			notifyValueChange(options.get(position).value);
		}
		updateContent();
		return this;
	}
	
	public SpinnerMenuItem setCurrentValue(int value) {
		return setCurrentValue(value, false);
	}
	
	public SpinnerMenuItem setCurrentValue(int value, boolean notify) {
		for (int i = 0; i < options.size(); i++) {
			Option o = options.get(i);
			if (o.value == value) {
				selection = i;
				if (notify) {
					notifyValueChange(value);
				}
				break;
			}
		}
		updateContent();
		return this;
	}
	
	public int getCurrentPosition() {
		return selection;
	}
	
	public int getSelectedValue() {
		if (selection < 0 || selection >= options.size()) {
			return -1;
		}
		return options.get(selection).value;
	}

	@Override
	public void selectNext() {
		// Find next item not skipped
		int s = selection;
		for (int i = 0; i < options.size(); i++) {
			s++;
			if (s >= options.size()) {
				s = 0;
			}
			if (!options.get(s).skip) {
				break;
			}
		}
		selection = s;
		
		if (selection < options.size()) {
			notifyValueChange(options.get(selection).value);
		}
		updateContent();
	}

	@Override
	public void selectPrev() {
		// Find previous item not skipped
		int s = selection;
		for (int i = 0; i < options.size(); i++) {
			s--;
			if (s < 0) {
				s = options.size() - 1;
			}
			if (!options.get(s).skip) {
				break;
			}
		}
		selection = s;
		
		if (selection >= 0) {
			notifyValueChange(options.get(selection).value);
		}
		updateContent();
	}

	@Override
	protected void onBindView(LayoutInflater inflater, ViewGroup container, boolean inflate) {
		if (inflate) {
			inflater.inflate(R.layout.layout_item_setting_spinner, container);
		}
		mTextControl = (TextView) container.findViewById(R.id.text_control);
		updateContent();
	}
	
	private void updateContent() {
		if (mTextControl == null) {
			return;
		}
		
		if (selection >= 0 && selection < options.size()) {
			mTextControl.setText(options.get(selection).getTitle(
					mTextControl.getContext()));
		} else {
			mTextControl.setText("");
		}
	}

	public void clearOptions() {
		options.clear();
		selection = -1;
		updateContent();
	}

	@Override
	protected void onUnbindView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showView() {
		// TODO Auto-generated method stub
		
	}

}
