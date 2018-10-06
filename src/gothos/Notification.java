package gothos;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Notification {

	protected static LinkedHashMap<String, ArrayList<ActionListener>> actionListeners;
	protected static LinkedHashMap<String, ArrayList<ActionListener>> onceActionListeners;


	public static void add(String notification, ActionListener event) {
		if (actionListeners == null) {
			actionListeners = new LinkedHashMap<>();
		}

		if (actionListeners.get(notification) == null) {
			actionListeners.put(notification, new ArrayList<>());
		}
		
		actionListeners.get(notification).add(event);
	}

	public static void addOnce(String notification, ActionListener event) {
		if (onceActionListeners == null) {
			onceActionListeners = new LinkedHashMap<>();
		}

		if (onceActionListeners.get(notification) == null) {
			onceActionListeners.put(notification, new ArrayList<>());
		}

		onceActionListeners.get(notification).add(event);
	}

	public static void post(String notification) {
		post(notification, new ActionEvent(new Object(), 0, ""));
	}

	public static void post(String notification, ActionEvent event) {

		if (actionListeners != null) {
			if (actionListeners.get(notification) != null) {
				for (ActionListener listener: actionListeners.get(notification)) {
					listener.actionPerformed(event);
				}
			}
		}

		if (onceActionListeners != null) {
			if (onceActionListeners.get(notification) != null) {
				for (ActionListener listener: onceActionListeners.get(notification)) {
					listener.actionPerformed(event);
				}

				onceActionListeners.put(notification, new ArrayList<>());
			}
		}
	}
}
