package com.seeburger.research.securityframework.layers.data.matchables;

public class PrefixMatchableString implements Matchable{
	
	private final String thisPrefix;
	
	public PrefixMatchableString(String prefix){
		thisPrefix = prefix;
	}

	public boolean matches(Object obj) {
		if (obj instanceof String)
			return ((String)obj).startsWith(thisPrefix);
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
		else if (obj instanceof PrefixMatchableString)
		{
			PrefixMatchableString other = (PrefixMatchableString) obj;
			return matches(other.thisPrefix);
		}
		else{
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		return thisPrefix.hashCode();
	}
	
	@Override
	public String toString(){
		return "STARTS_WITH="+thisPrefix;
	}

}
