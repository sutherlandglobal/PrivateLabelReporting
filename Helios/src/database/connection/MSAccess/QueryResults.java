package database.connection.MSAccess;

import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Pattern;

import util.DateParser;
import exceptions.InvalidConditionException;

/**
 * Results from querying an MS Access database directly, stored in a way where conditions can be applied.
 * 
 * @author Jason Diamond
 *
 */
public class QueryResults 
{
	private Vector<String[]> results;
	private Vector<String> columnNames;
	private Vector<Condition> conditions;
	
	
	/**
	 * Build an empty results object, values to be initialized later.
	 */
	public QueryResults()
	{
		this(new Vector<String>(), new Vector<String[]>());
	}
	
	/**
	 * Build a results object, with known column names and raw results.
	 * 
	 * @param columnNames	List of column names in the query results.
	 * @param results	The result set itself.
	 */
	public QueryResults(Vector<String> columnNames, Vector<String[]> results)
	{
		this.results = results;
		this.columnNames = columnNames;
		conditions = new Vector<Condition>();
	}
	
	/**
	 * Add a condition to this query's results. Uniqueness is required.
	 * 
	 * @param conditionString		String describing a condition, in the form of "(left) (op) (right)".
	 * @return	True if the condition was accepted, false otherwise.
	 */
	public boolean addCondition(String conditionString)
	{
		boolean retval = true;
		
		try
		{
			retval =  conditions.add(new Condition(conditionString));
		}
		catch(InvalidConditionException e)
		{
			retval = false;
		}
		
		return retval;
	}
	
	/**
	 * Accessor for the list of accepted conditions.
	 * 
	 * @return	The list of accepted conditions.
	 */
	public Vector<Condition> getConditions()
	{
		return conditions;
	}

	/**
	 * Accessor for the result set of the query.
	 * 
	 * @return	 The result set.
	 */
	public Vector<String[]> getResults()
	{
		return results;
	}
	
	/**
	 * 	 Mutator for the result set of the query.
	 * 
	 * @param results	The result set to use.
	 */
	public void setResults(Vector<String[]> results)
	{
		this.results = results;
	}
	
	/**
	 * Accessor for the column names used in the query.
	 * 
	 * @return	The column names used in the query.
	 */
	public Vector<String> getColumnNames()
	{
		return columnNames;
	}
	
	/**
	 * Add a column name to the query. Uniqueness is required.
	 * 
	 * @param name		The column name.
	 * @return	True if the column was added successfully, false otherwise.
	 */
	public boolean addColumn(String name)
	{
		boolean retval = false;
		if( !columnNames.contains(name) )
		{
			columnNames.add(name);
			retval = true;
		}
		
		return retval;
	}
	
//	/**
//	 * Remove a condition from the query.
//	 * 
//	 * @param conditionString		String describing the condition to remove, in the form of "(left) (op) (right)".
//	 */
//	public void removeCondition(String conditionString)
//	{
//		conditions.remove(conditionString);
//	}
	
	/**
	 * Print the result set.
	 */
	public void printResults()
	{
		StringBuilder output = new StringBuilder();
		for(String[] row : getResults())
		{
			for(String cell : row)
			{
				output.append(cell + ",");
			}
			output.append("\n");
		}
		System.out.print(output.toString());
	}

	/**
	 *	Apply all stored conditions to the final result set. Sort of obsolete, since this prohibits easy multithreading. Leaving it in for possible future use.
	 */
	public void applyConditions()
	{
		int numMatches = 0;
		for(int i =numMatches; i<getResults().size(); i++)
		{
			//System.out.println(i + ": " +  getResults().get(i)[0]);
			
			//hashup field names and values for this row
			HashMap<String,String> thisRow =  new HashMap<String,String>();
			for(int j = 0; j <getColumnNames().size(); j++ )
			{
				thisRow.put(getColumnNames().get(j), getResults().get(i)[j]);
			}
			
			//for each row, apply all conditions, false -> remove from results, since we imply anding of conditions
			for(Condition c : conditions)
			{
				//here try to regulate the boolean operations of condition matching
				
				if( !c.apply(thisRow) )
				{
					//System.out.println("removing " + thisRow.get(this.getColumnNames().get(0)) + " per " + c);
					results.removeElementAt(i);

					//remove element, start from the beginning
					i=numMatches-1;
					break;
				}
				else if(c.equals(conditions.lastElement()))
				{
					//only count as a match if all conditions pass or the boolean statement holds true
					numMatches++;
				}
			}
		}
	}
	
	/**
	 * Apply all conditions to a particular datum/cell/entry. Conditions not applicable are ignored. Makes for easy multithreading 
	 * since this can be done as the result set is being built.
	 * 
	 * @param fieldname	Column name of the datum.
	 * @param value		Value of the datum.
	 * @return		True if the condition holds or is not applicable, false otherwise.
	 */
	public boolean applyConditions(String fieldname, String value)
	{
		HashMap<String,String> field = new HashMap<String,String>();
		field.put(fieldname, value);

		//we are looking for a reason to discard, which will be for relevant conditions, a failure
		boolean retval = true;
		
		for(Condition c : conditions)
		{
			if(c.getLeft().equals(fieldname))
			{
				if(!c.apply(field))
				{
					retval = false;
					break;
				}
			}

		}
		
		return retval;
	}

//	/**
//	 * 
//	 * 
//	 * @param rawOutput
//	 * @return
//	 */
//	public Vector<String[]> formatOutput(Vector<Vector<String>> rawOutput)
//	{
//		//format output
//		
//		//queryOutput.size is YMAX
//		//queryOutput.get(i).size is XMAX
//		//System.out.println("Total Rows: " + queryOutput.size());
//		//System.out.println("Total Cols: " + queryOutput.get(0).size());
//		
//		//this may not be a rectangle or square, add nulls for the empty vals
//		
//		
//		Vector<String[]> retval = new Vector<String[]>();
//		
//		String[] thisRow;
//		for(int i = 0; i<rawOutput.get(0).size(); i++)
//		{
//			thisRow = new String[rawOutput.size()];
//			
//			for(int j = 0; j<rawOutput.size(); j++)
//			{
//				try
//				{
//					thisRow[j] = rawOutput.get(j).get(i);
//				}
//				catch(ArrayIndexOutOfBoundsException e)
//				{
//					thisRow[j] = "";
//				}
//			}
//			//thisRow[0] = queryOutput.get(0).get(i);
//			//thisRow[1] = queryOutput.get(1).get(i);
//			
//			//System.out.println("Adding " +thisRow[0] + " " + thisRow[1]);
//			
//			
//			retval.add(thisRow);
//		}

////		//i is y val, j is x
////		//String[] thisRow;
////		for(int i = 0; i< queryOutput.size(); i++)
////		{
////			//thisRow = new String[queryOutput.size()];
////			for(int j = 0; j<queryOutput.get(i).size(); j++)
////			{
////				//thisRow[i] = queryOutput.get(j).get(i);
////				System.out.println("i: " + i + ", j: " + j + " => " + queryOutput.get(i).get(j));
////			}
////			//System.out.println("Adding row len: " + thisRow.length);
////			//retval.add(thisRow);
////			
////		}
//		
//		return retval;
//	}
	
	
	public static void main(String[] args) 
	{
		QueryResults s = new QueryResults();
		
		s.addColumn("name");
		s.addColumn("age");
		s.addColumn("color");
		
		Vector<String[]> queryResults = new Vector<String[]>();
		queryResults.add(new String[]{"john","12","blue"});
		queryResults.add(new String[]{"larry","15","blue"});
		queryResults.add(new String[]{"joe","16","blue"});
		queryResults.add(new String[]{"steve","22","red"});
		queryResults.add(new String[]{"bill","25","red"});
		queryResults.add(new String[]{"kim","26","red"});
		queryResults.add(new String[]{"alex","42","green"});
		queryResults.add(new String[]{"nick","45","green"});
		queryResults.add(new String[]{"con","46","green"});
		queryResults.add(new String[]{"bart","52","blue"});
		queryResults.add(new String[]{"paul","55","blue"});
		queryResults.add(new String[]{"don","56","blue"});
		queryResults.add(new String[]{"dan","62","red"});
		queryResults.add(new String[]{"kate","65","red"});
		queryResults.add(new String[]{"liz","66","red"});
		queryResults.add(new String[]{"rob","62","gray"});
		queryResults.add(new String[]{"chad","65","gray"});
		queryResults.add(new String[]{"allie","66","gray"});
		queryResults.add(new String[]{"johnny  internet","66","gray"});
		
		s.setResults(queryResults);
		
		String c1 = "color == red";
		String c2 = "age >= 55";
		String c3 = "name !~ ....";
		String c4 = "name == johnny  internet";
		
		//String c4 = "name !~ ^da.*";
		s.addCondition(c1);
		s.addCondition(c2);
		s.addCondition(c3);
		s.addCondition(c4);
		
		//s.applyConditions();
		
		s.printResults();
		
//		s.removeCondition(c1);
//		s.removeCondition(c2);
//		s.removeCondition(c3);
//		s.removeCondition(c4);

		
	}

}

/**
 * A conditional constraint on the output of a query.
 * 
 * @author Jason Diamond
 *
 */
class Condition
{
	private String left;
	private String right;
	private String operator;
	private Vector<String> operations;
	
	
	/**
	 * Build a condition from a string that describes a condition.
	 * 
	 * @param conditionString		String describing a condition, in the form of "(left) (op) (right)".
	 * @throws InvalidConditionException	If the condition is malformed or otherwise invalid.
	 */
	public Condition(String conditionString) throws InvalidConditionException
	{
		//examples
		//startdate >= 101001
		//name == fartface
		//name =~ /bobby*/
		//fieldname operator value
		
		left = "";
		right = "";
		
		operations = new Vector<String>();
		operations.add("==");
		operations.add("!=");
		operations.add("<=");
		operations.add(">=");
		operations.add(">");
		operations.add("<");
		operations.add("=~");
		operations.add("!~");
		
		operations.add("=}"); // a =} b --> a is before b
		operations.add("{=");
		
		
		//String[] fields = conditionString.split("\\s+");
		
		try
		{
			for(String op : operations)
			{
				if(conditionString.contains(op))
				{
					operator = op;
					
					//left = fields[0].trim();
					left = conditionString.substring(0, conditionString.indexOf(op) - 1);
					right = conditionString.substring(conditionString.indexOf(op) + op.length()+1);
					
					//System.out.println(right);
					
					break;
				}
			}
			
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			throw new InvalidConditionException("Invalid Condition via ArrayIndex Exception: {" + left + "} {" + operator + "} {" + right + "}");
		}
		catch (StringIndexOutOfBoundsException e)
		{
			throw new InvalidConditionException("Invalid Condition via StringIndex Exception: {" + left + "} {" + operator + "} {" + right + "}");
		}

		
		//if(!operations.contains(operator))
		if( left ==null || right  == null || operator == null)
		{
			throw new InvalidConditionException("Invalid Condition: {" + left + "} {" + operator + "} {" + right + "}");
		}
	}
	
	
	/**
	 * Accessor for the fieldname of the condition.
	 * 
	 * @return	The fieldname of the condition.
	 */
	public String getLeft()
	{
		return left;
	}
	
	/**
	 * Accessor for the value of the condition.
	 * 
	 * @return	 The value of the condition.
	 */
	public String getRight()
	{
		return right;
	}
	
	/**
	 * Apply the condition to a fieldname -> value pairing. 
	 * 
	 * @param fieldname		The fieldname of the pairing.
	 * @param value		The value of the pairing.
	 * 
	 * @return	True if the condition holds or is not applicable, false otherwise.
	 */
	public boolean apply(String fieldname, String value)
	{
		HashMap<String,String> field = new HashMap<String,String>();
		field.put(fieldname, value);
		
		return apply(field);
	}
	
	/**
	 * Apply the condition to a map of fieldname -> value pairings. 
	 * 
	 * @param line	 A map of fieldname -> value pairings, possibly representing a row in a database. Also possibly representing a single fieldname -> value pairing. 
	 * @return		True if the condition holds or is not applicable, false otherwise.
	 */
	public boolean apply(HashMap<String,String> line)
	{
		
		
		boolean retval = false;
		
		//line is field -> value
				
		if(line.containsKey(left))
		{
			//operator validity already checked
			
			//operator massive if test
			//or evaluate the string somehow
			
			try
			{
				
				if(
						(operator.equals("==") && line.get(left).equals(right)) 
					)
				{
					//System.out.println("equals op");
					//System.out.println("TEST: " + line.get(left) + " " + operator + " " + right);
					//System.out.println("MATCH: " + left + " " + operator + " " + right);
					retval = true;

				}
				else if(operator.equals("!=") && !(line.get(left)).equals(right))
				{
					retval = true;
				}
				else if(operator.equals(">=") && Double.parseDouble(line.get(left)) >= Double.parseDouble(right))
				{
					//System.out.println("MATCH: " + left + " " + operator + " " + right);
					retval = true;
				}
				else if(operator.equals("<=") && Double.parseDouble(line.get(left)) <= Double.parseDouble(right))
				{
					retval = true;
				}
				else if(operator.equals("<") && Double.parseDouble(line.get(left)) < Double.parseDouble(right))
				{
					retval = true;
				}
				else if(operator.equals(">") && Double.parseDouble(line.get(left)) > Double.parseDouble(right))
				{
					retval = true;
				}
				else if(operator.equals("=~") && Pattern.matches(right, line.get(left)))
				{
					retval = true;
				}
				else if(operator.equals("!~") && !Pattern.matches(right, line.get(left)))
				{
					retval = true;
				}
				else if(operator.equals("=}") )
				{
					DateParser d = new DateParser();
					if(
							d.convertMSAccessDateToGregorian(line.get(left)).after(d.convertSQLDateToGregorian(right)) ||
							d.convertMSAccessDateToGregorian(line.get(left)).equals(d.convertSQLDateToGregorian(right))
						)
					{
						retval = true;
					}
				}
				else if(operator.equals("{="))
				{
					DateParser d = new DateParser();
					if(
							d.convertMSAccessDateToGregorian(line.get(left)).before(d.convertSQLDateToGregorian(right)) ||
							d.convertMSAccessDateToGregorian(line.get(left)).equals(d.convertSQLDateToGregorian(right))
							)
					{
						retval = true;
					}
				}
						
			}
			catch(NumberFormatException e)
			{
				//throw new InvalidConditionException("Error converting " + line.get(left) + " to double");
				retval = false;
			}
		}
		else
		{
//			for(String key : line.keySet())
//			{
//				//System.out.println(key + ": " + line.get(key));
//			}
			
			//throw new InvalidConditionException(left + " is not a valid field");
			retval = false;
		}
		
		return retval;
	}
	
	/** 
	 * Obtain a string representation of the condition. 
	 * 
	 * @return	A string representing the condition.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return left + " " + operator + " " + right;
	}
}