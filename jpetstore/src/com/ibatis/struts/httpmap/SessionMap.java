package com.ibatis.struts.httpmap;

import com.ibatis.jpetstore.domain.CartItem;
import com.ibatis.jpetstore.presentation.CartBean;
import com.ibatis.struts.httpmap.BaseHttpMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * Map to wrap session scope attributes.
 * <p/>
 * Date: Mar 11, 2004 10:35:42 PM
 * 
 * @author Clinton Begin
 */
public class SessionMap extends BaseHttpMap {

	private HttpSession session;

	public SessionMap(HttpServletRequest request) {
		this.session = request.getSession();
	}

	protected Enumeration getNames() {
		return session.getAttributeNames();
	}

	protected Object getValue(Object key) {
		System.out.println("get ----------------" + key
				+ "---------------------------"
				+ session.getAttribute(String.valueOf(key)));
		return session.getAttribute(String.valueOf(key));
	}

	public void loopItems(Object value) {
		if (value instanceof CartBean) {
			CartBean cart = (CartBean) value;
			Iterator<CartItem> i = cart.getCart().getCartItems();
			while (i.hasNext()) {
				CartItem item = i.next();
				System.out.println("cart items ----------------"
						+ "---------------------------"
						+ item.getItem().getItemId());
			}
		}
	}

	protected void putValue(Object key, Object value) {
		System.out.println("set session----------------"
				+ "---------------------------" + value);
		loopItems(value);
		session.setAttribute(String.valueOf(key), value);
		System.out.println("after set session----------------"
				+ "---------------------------" + value);
		loopItems(session.getAttribute(String.valueOf(key)));
	}

	protected void removeValue(Object key) {
		session.removeAttribute(String.valueOf(key));
	}

}
