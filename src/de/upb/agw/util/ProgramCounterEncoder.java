package de.upb.agw.util;

import java.util.ArrayList;

public class ProgramCounterEncoder {
	private int processNumber;
	private static String maybe = "M";
	private static String top = "T";
	private static String bottom = "F";
	
	private int numberOfTrits;
	
	public ProgramCounterEncoder(int processNumber, int stateCount) {
		this.processNumber = processNumber;		
		this.numberOfTrits = (int)Math.floor(Math.log(stateCount)/Math.log(3))+1;
	}
	
	/**
	 * 
	 * @param counter
	 * @return List of pairs. E.g. {(pc_0_0;, 'T'), (pc_0_1;, 'M')}
	 */
	public ArrayList<String[]> encodeAsPairs(int counter) {
		ArrayList<String[]> result = new ArrayList<String[]>(numberOfTrits);
		for(int i = 0; i < numberOfTrits; i++) {
			result.add(new String[]{getPCString(i), getValueString(0)});
		}
		for(int i = 0; counter > 0; i++) {
			assert (i < numberOfTrits) : "Not enough number of trits available to encode given value";
			int remainder = counter % 3;
			result.get(i)[1] = getValueString(remainder);
			counter /= 3;
		}		
		return result; 
	}
	
	/**
	 * 
	 * @param counter
	 * @return List of Strings. E.g.{pc_0_0;=T, pc_0_1;=M}
	 */
	
	public ArrayList<String> encodeAsString(int counter) {
		ArrayList<String[]> list = encodeAsPairs(counter);
		ArrayList<String> result = new ArrayList<String>(list.size());
		for(String[] pair : list) {
			result.add(pair[0] + "=" + pair[1]);
		}
		return result;
	}
	
	public int getNumberOfTrits() {
		return numberOfTrits;
	}
	
	private static String getValueString(int value) {
		switch(value) {
		case 0: return bottom;
		case 1: return maybe;
		case 2: return top;
		}
		throw new IllegalArgumentException("Could not encode " + value + "."); 
	}
	
	private static int getStringValue(String value) {
		if(value.equals("T")) {
			return 2;
		}
		if(value.equals("M")) {
			return 1;
		} 
		if(value.equals("F")) {
			return 0;
		} 
		throw new IllegalArgumentException("Could not encode " + value + "."); 
	}
	
	
	public String getPCString(int trit) {
		return "pc_" + processNumber + "_" + trit + ";"; 
	}
	
	public static int parseProcessNumber(String pcString) {
		String processNumber = pcString.substring("pc_".length());
		int index = Integer.MAX_VALUE;
		int index2 = processNumber.indexOf("=");
		int index3 = processNumber.indexOf("_");
		if(index2 != -1) {
			index = index2;
		}
		if(index3 != -1 && index3 < index) {
			index = index3;
		}
		processNumber = processNumber.substring(0,index);
		return Integer.parseInt(processNumber);
	}
	
	public static int parseTrit(String pcString) {
		int start = "pc_".length();
		String trit = pcString.substring(pcString.indexOf("_", start));
		trit = trit.substring("_".length(), trit.indexOf(";"));
		return Integer.parseInt(trit);
	}
	
	public static int decodeCountFromPC(ArrayList<String> pcStrings, ArrayList<String> values) {
		int[] trits = new int[pcStrings.size()];
		for(int i = 0; i < pcStrings.size(); i++) {
			int trit = parseTrit(pcStrings.get(i));
			int value = getStringValue(values.get(i));
			trits[trit] = value;
		}
		int result = 0;
		for(int i = trits.length-1; i >= 0; i--) {
			result = result * 3 + trits[i];
		}
		return result;
	}
}
