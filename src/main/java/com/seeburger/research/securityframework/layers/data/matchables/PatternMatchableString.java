package com.seeburger.research.securityframework.layers.data.matchables;

import java.util.regex.Pattern;

public class PatternMatchableString implements Matchable{
	
	private final Pattern pattern;
	private final String p;
	
	public PatternMatchableString(String p){
		this.p = p;
		pattern = Pattern.compile(p);
	}

	public boolean matches(Object obj) {
		if (obj instanceof String)
			return pattern.matcher((String)obj).matches();
		return false;
	}
	
	
	@Override
	public boolean equals(Object obj){
		if (obj == null)
			return false;
		else if (obj instanceof String){
			String other = (String) obj;
			return matches(other);
		}
		else if (obj instanceof PatternMatchableString)
		{
			PatternMatchableString other = (PatternMatchableString) obj;
			return matches(other.p);
		}
		else{
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		return p.hashCode();
	}

	@Override
	public String toString(){
		return "PATTERN="+p;
	}

}
