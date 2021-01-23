package net.tolmikarc.trader.settings;

import org.mineacademy.fo.settings.SimpleLocalization;

import java.util.List;

public class Localization extends SimpleLocalization {
	@Override
	protected int getConfigVersion() {
		return 0;
	}

	public static String NO_PENDING_INVITE;
	public static String DECLINE;
	public static String DECLINE_NOTIFICATION;
	public static String INVENTORY_SPACE;
	public static String RETURN_COMPLETE;
	public static String TRADE_SENT;
	public static String INVITE_NOTIFICATION;
	public static String COMMAND_DESCRIPTION;
	public static String MONEY_PROMPT;
	public static String INVALID_AMOUNT;

	private static void init() {
		pathPrefix(null);
		NO_PENDING_INVITE = getString("No_Pending_Invite");
		DECLINE = getString("Decline");
		DECLINE_NOTIFICATION = getString("Decline_Notification");
		INVENTORY_SPACE = getString("Inventory_Space");
		RETURN_COMPLETE = getString("Return_Complete");
		TRADE_SENT = getString("Trade_Sent");
		INVITE_NOTIFICATION = getString("Invite_Notification");
		COMMAND_DESCRIPTION = getString("Command_Description");
		MONEY_PROMPT = getString("Money_Prompt");
		INVALID_AMOUNT = getString("Invalid_Amount");
	}

	public static class Menu {
		public static String TITLE;
		public static String CANCEL;
		public static String CANCELLED;
		public static String SUCCESS;
		public static String NOT_CONFIRMED;
		public static String CONFIRM_BUTTON_TITLE;
		public static List<String> CONFIRM_BUTTON_LORE;
		public static String MONEY_BUTTON_TITLE;
		public static List<String> MONEY_BUTTON_LORE;

		private static void init() {
			pathPrefix("Menu");
			TITLE = getString("Title");
			CANCEL = getString("Cancel");
			CANCELLED = getString("Cancelled");
			SUCCESS = getString("Success");
			NOT_CONFIRMED = getString("Not_Confirmed");
			CONFIRM_BUTTON_TITLE = getString("Confirm_Button_Title");
			CONFIRM_BUTTON_LORE = getStringList("Confirm_Button_Lore");
			MONEY_BUTTON_TITLE = getString("Money_Button_Title");
			MONEY_BUTTON_LORE = getStringList("Money_Button_Lore");
		}

	}


}
