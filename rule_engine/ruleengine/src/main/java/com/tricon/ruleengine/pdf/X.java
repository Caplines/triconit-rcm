package com.tricon.ruleengine.pdf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class X {

	  public static void main(String[] args) {

	    String stringToSearch = "<b>serviceCodeNotfoundinMapping Table</b>";

	    // the pattern we want to search for
	   //String stripped = stringToSearch.replaceAll("<[^>]*>", "XXXXXXXXXX");
	    Pattern p = Pattern.compile("<b>(\\S+)</b>");
	    System.out.println("stringToSearch----"+stringToSearch);
	    Matcher m = p.matcher(stringToSearch);
//System.out.println("stripped="+stripped);
	    // if we find a match, get the group 
	    if (m.find()) {
System.out.println("FFFFFFFF");
	      // get the matching group
	      String codeGroup = m.group(1);
	      
	      // print the group
	      System.out.format("'%s'\n", codeGroup);

	    }

	  }
	}
