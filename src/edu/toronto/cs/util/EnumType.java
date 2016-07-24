package edu.toronto.cs.util;

import java.util.*;

// XXX 
// XXX This has to be rewritten! 
// XXX

/***
 ** This is an utility class to represent and manipulate enumerated type
 **/
public class EnumType
{
  Object[] range;
  Map encodingMap;
  
  
  public EnumType (Object[] _range)
  {
    range = _range;
    assert  range.length >= 2 : "EnumType needs at least 2 elements";
    encodingMap = new HashMap ();
    int[] partialEnum = new int [bitSize ()];
    Arrays.fill (partialEnum, -1);
    encodeRecur (range, 0, bitSize (), partialEnum);
  }
  
  // -- number of bits required to represent this enum type
  public int bitSize ()
  {
    return logCeil (range.length);
  }

  public static int logCeil (int n)
  {
    // -- returns the closest power of 2 greater or equal to n
    int pow = 0;
    int count = 0;
    while (pow < n)
      pow = 1 << ++count;
    return count;
  }

  // -- returns an integer representation of this value
  // -- the value must be an element of the range array
  // XXX This is depricated in favor of bitValue
  // XXX this is very badly broken method, do not use!
//   public int intValue (Object value)
//   {    
//     int[] bits = bitValue (value);
//     int v = 0;
    
//     for (int i = 0; i < bits.length; i++)
//       if (bits [i] == 1)
// 	v |= 1 << i;
//     return v;
//   }
  
  public int[] bitValue (Object value)
  {
    return (int[])encodingMap.get (value);
  }
  


  public Object[] enumValues (int[] enumBits)
  {
    // enumBit [i] = 0 -- bit i is 0
    // enumBit [i] = 1 -- bit i is 1
    // otherwise enumBit [i] is {0,1}

    // -- we have to traverse an imaginary tree of enum values and 
    // -- collect all the children we see
    return enumValuesRecur (enumBits, 0, range);
  }


  private Object[] enumValuesRecur (int[] enumBits, int currentBit, 
				    Object[] values)
  {
    if (values.length == 1) return values;
    
    if (enumBits [currentBit] == 0)
      return enumValuesRecur (enumBits, currentBit + 1, 
			      ArrayUtil.evenElements (values));
    else if (enumBits [currentBit] == 1)
      return enumValuesRecur (enumBits, currentBit + 1, 
			      ArrayUtil.oddElements (values));
    else
      {
	Set set = new HashSet ();
	set.addAll (Arrays.asList 
		    (enumValuesRecur (enumBits, currentBit + 1, 
				      ArrayUtil.evenElements (values))));
	
	set.addAll (Arrays.asList
		    (enumValuesRecur (enumBits, currentBit + 1, 
				      ArrayUtil.oddElements (values))));
	return (Object[])set.toArray (new Object [set.size ()]);
      }
    
    
  }
  
  

  private void addEncoding (int[] enm, Object o)
  {
    encodingMap.put (o, enm);
  }
  

  private void encodeRecur (Object[] range, int currLevel, 
			    int maxLevel, int[] partialEnum)
  {
    // -- last element to encode, but if we have not reached maxLevel yet
    // -- just keep expanding the encoding
    if (range.length == 1)
      {
	addEncoding ((int[])partialEnum.clone (), range [0]);
	if (currLevel < maxLevel)
	  {
	    // -- add additional decodings since in this case
	    // -- we have several ints corresponding to a single enum value
	  }
	
      }
    else
      {
	int[] temp = (int[])partialEnum.clone ();
	temp [currLevel] = 0;
	encodeRecur (ArrayUtil.evenElements (range), 
		     currLevel + 1, maxLevel, temp);
	temp = (int[])partialEnum.clone ();
	temp [currLevel] = 1;
	encodeRecur (ArrayUtil.oddElements (range),
		     currLevel + 1, maxLevel, temp);

      }
  }


//   private void __encodeRecur (Object[] range, int currLevel, 
// 			    int maxLevel, int partialEnum)
//   {
//     // -- last element to encode, but if we have not reached maxLevel yet
//     // -- just keep expanding the encoding
//     if (range.length == 1)
//       {
// 	addEncoding (partialEnum, range [0]);
// 	if (currLevel < maxLevel)
// 	  {
// 	    // -- add additional decodings since in this case
// 	    // -- we have several ints corresponding to a single enum value
// 	  }
	
//       }
//     else
//       {
// 	encodeRecur (ArrayUtil.evenElements (range), 
// 		     currLevel + 1, maxLevel, partialEnum);
// 	encodeRecur (ArrayUtil.oddElements (range),
// 		     currLevel + 1, maxLevel, 
// 		     partialEnum | (1 << currLevel));

//       }
//   }


  public static void main (String[] args)
  {
    String[] values = new String[] {"a", "b", "c", "d", "e"};
    EnumType enumType = new EnumType (values);
    for (int i = 0; i < values.length; i++)
      {
// 	System.out.println (values [i] + " enum as " +
// 			    enumType.intValue (values [i]));
	System.out.println (values [i] + " enum as " +
			    ArrayUtil.toString 
			    (enumType.bitValue (values [i])));
	
      }
    

    System.out.println ("For [1 -1 0] " + 
			Arrays.asList 
			(enumType.enumValues (new int[] {1,-1,0})));
    
							    
				      
  }
  
}
