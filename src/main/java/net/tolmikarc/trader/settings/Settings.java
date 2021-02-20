package net.tolmikarc.trader.settings;

import org.mineacademy.fo.settings.SimpleSettings;

public class Settings extends SimpleSettings {
	@Override
	protected int getConfigVersion() {
		return 0;
	}


	public static Boolean SHIFT_CLICK_TRADE;
	public static Boolean ECONOMY_ENABLED;
	public static Boolean LOCK_ON_CONFIRM;

	private static void init() {
		SHIFT_CLICK_TRADE = getBoolean("Shift_Click_Trade");
		ECONOMY_ENABLED = getBoolean("Economy_Enabled");
		LOCK_ON_CONFIRM = getBoolean("Lock_On_Confirm");
	}


}
