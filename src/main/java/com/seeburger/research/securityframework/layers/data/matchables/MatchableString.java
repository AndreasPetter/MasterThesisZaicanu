package com.seeburger.research.securityframework.layers.data.matchables;

public class MatchableString implements Matchable{
	
	private final String thisString;
	
	public MatchableString(String str){
		thisString = str;
	}

	public boolean matches(Object obj) {
		if (obj instanceof String)
			return thisString.equals(obj);
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
		else if (obj instanceof MatchableString){
			MatchableString other = (MatchableString) obj;
			return matches(other.thisString);
		}
		else
			return false;
	}
	
	@Override
	public int hashCode(){
		return thisString.hashCode();
	}
	
	@Override
	public String toString(){
		return "FIXED="+thisString;
	}

}
