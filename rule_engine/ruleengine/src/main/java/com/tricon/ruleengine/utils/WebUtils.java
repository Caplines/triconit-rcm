package com.tricon.ruleengine.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class WebUtils {
	public static String toString(User user) {
		StringBuilder sb = new StringBuilder();

		sb.append("UserName:").append(user.getUsername());

		Collection<GrantedAuthority> authorities = user.getAuthorities();
		if (authorities != null && !authorities.isEmpty()) {
			sb.append(" (");
			boolean first = true;
			for (GrantedAuthority a : authorities) {
				if (first) {
					sb.append(a.getAuthority());
					first = false;
				} else {
					sb.append(", ").append(a.getAuthority());
				}
			}
			sb.append(")");
		}
		return sb.toString();
	}
	
	
	public static void appendStream(String data,BufferedWriter bw) {
		if (data!=null  && bw!=null)
		
			try {
				bw.write(data);
				bw.write("\r\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		
		
	}
	
	public static void appendStream(List<String> datas,BufferedWriter bw) {
		if (datas!=null  && bw!=null)
		for(String data:datas) {
			
			appendStream(data, bw);
			
			
		}
		
	}
}
