package com.db.parse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class App {

	private List<String> result = new ArrayList<String>();

	private String dbName = "";

	static String newPath = "D:/Pavlo/my files/db tables/2.txt";

	static String oldPath = "D:/Pavlo/my files/db tables/1.txt";

	public App() {
	}

	public App(String dbName) {
		this.dbName = dbName;
	}
	
	public App(String newPath, String oldPath) {
		this.newPath = newPath;
		this.oldPath = oldPath;
	}

	/* main method of class App
	 * contain the main logic of program.
	 * */
	public void main() throws IOException {

		FileInputStream inputNew = new FileInputStream(newPath);

		FileInputStream inputOld = new FileInputStream(oldPath);
		
		StringWriter stringNew = new StringWriter();

		while (inputNew.available() > 0) {
			stringNew.write(inputNew.read());
		}
		
		StringWriter stringOld = new StringWriter();

		while (inputOld.available() > 0) {
			stringOld.write(inputOld.read());
		}

		List[] listField1 = parseTable(stringNew.toString());

		List[] listField2 = parseTable(stringOld.toString());

//		for (Field f : (List<Field>)listField1[0]) {
//			System.out.println(f);
//		}
//		for (Field f : (List<Field>)listField2[0]) {
//			System.out.println(f);
//		}
//		for (Constraint c : (List<Constraint>)listField1[1]) {
//			System.out.println(c);
//		}
//		for (Constraint c : (List<Constraint>)listField2[1]) {
//			System.out.println(c);
//		}

		compareFieldLists(listField1[0], listField2[0]);
		
		compareConstLists(listField1[1], listField2[1]);

		for (String s : result) {
		    System.out.println(s);
		}

	}
	
	public void main(String oldTable, String newTable) throws IOException {
		List[] listField1 = parseTable(newTable);

		List[] listField2 = parseTable(oldTable);
		
		compareFieldLists(listField1[0], listField2[0]);
		
		compareConstLists(listField1[1], listField2[1]);

		for (String s : result) {
		    System.out.println(s);
		}
	}

	/* method for parse table fields from some file
	 * return - List of Fields elements
	 * parameter - InputStream object
	 * */
	public List[] parseTable(String string) throws IOException {
		List[] lists = new List[2];
		
		List<Field> listField = new ArrayList<Field>();
		
		List<Constraint> listConst = new ArrayList<Constraint>();

//		StringWriter string = new StringWriter();
//
//		while (input.available() > 0) {
//			string.write(input.read());
//		}
		
		String startString = string.replaceAll("--.+", "\n");
		
		/* split our String by new line symbol \n */
		String[] arr = startString.split(",[\\r\\n]+|\\);|[\\r\\n]+\\)|,\\s+\\n");
		
		StringTokenizer tokenizer = new StringTokenizer(arr[0]);
		
		String firstToken = tokenizer.nextToken("(");
		String arrTemp = "";
		arrTemp += tokenizer.nextToken() ;
		while(tokenizer.hasMoreTokens()) {
			arrTemp += "(" + tokenizer.nextToken();
		}
		arr[0] = arrTemp;
		
		tokenizer = new StringTokenizer(firstToken);
		while(!(tokenizer.nextToken().equals("TABLE")));
		String lastToken = tokenizer.nextToken();
		if ((this.dbName == null)||("".equals(this.dbName))) {
			this.dbName = lastToken;
		} else if (!(this.dbName.equals(lastToken))) {
			try {
				this.dbName = lastToken;
				throw new Exception();
			} catch (Exception e) { 
				System.out.println("Different tables!!!" + this.dbName);
			}
		}
		List<List<String>> listArr = new ArrayList<List<String>>();

		/* split our array by any count of spaces */
		for (String str : arr) {
			List<String> tempList = new ArrayList<String>();
			String[] temp = str.split("\\s+|,\\s+");
			for (String tempString : temp) {
				if ((tempString != null)&&(tempString != "")) {
					tempList.add(tempString);
				}
			}
			if (!(tempList.isEmpty())){
				listArr.add(tempList);
			}
		}
		
//		for(List<String> list : listArr) {
//			System.out.println(list);
//		}

		/* convert our List of List<String> into Lists of Fields and Constraints*/
		for (List<String> list : listArr) {
			int i = 0;
			if (list.size()<=1) {
				continue;
			}
			if ((list.contains("WITH"))||(list.contains("TRIGGER"))) {break;}
			if (!(list.contains("CONSTRAINT"))) {
				/*Field!*/
				Field field = new Field();
				/*Name*/
				while ((i < list.size()) && (list.get(i).isEmpty())) {
					i++;
				}
				if (i >= list.size()) {
					continue;
				}
				field.setName(list.get(i));
				/*NOT NULL*/
				if ((list.contains("NOT")) || (list.contains("NULL"))) {
					field.setNn(" NOT NULL ");
					list.remove("NOT");
					list.remove("NULL");
				}
				/*DEFAULT*/
				if (list.contains("DEFAULT")) {
					field.setDef(" DEFAULT ");
					int k = list.indexOf("DEFAULT");
					list.remove(k);
					while (k <= list.size()-1) {
						field.setDef(field.getDef() + list.get(k) + " ");
						list.remove(k);
					}
				}
				/*TYPE*/
				int m = list.indexOf(field.getName());
				while (m < list.size() - 1) {
					list.remove(m);
					field.setType(field.getType() + list.get(m) + " ");
				}
				listField.add(field);
			} else {
				/*Constraint!!!*/
				Constraint constraint = new Constraint();
				i=list.indexOf("CONSTRAINT")+1;
				/*NAME*/
				constraint.setName(list.get(i));
				/*TYPE*/
				String type = list.get(++i);
				switch(type) {
				case "PRIMARY":
					constraint.setType(type + " " + list.get(++i));
					constraint.setField("(" + list.get(++i).replaceAll("\\(|\\s+|\\)", ""));
					while((i < list.size()-1)&&(!(list.get(++i).equals("MATCH")))) {
						String constField = list.get(i).replaceAll("\\)|\\s+", "");
						if (constField.length()!=0) {
							constraint.setField(constraint.getField() + ", " + constField);
						}
					}
					constraint.setField(constraint.getField() + ")");
					break;
				case "FOREIGN":
					constraint.setType(type + " " + list.get(++i));
					constraint.setField("(" + list.get(++i).replaceAll("\\(|\\s+|\\)", ""));
					while(!(list.get(++i).equals("REFERENCES"))) {
						constraint.setField(constraint.getField() + ", " + list.get(i));
					}
					if (!(constraint.getField().endsWith(")"))) {
						constraint.setField(constraint.getField() + ")");
					}
					constraint.setRef(list.get(i) + " " + list.get(++i));
					while ((i < list.size()-1)&&(!(list.get(++i).equals("MATCH")))) {
						constraint.setRef(constraint.getRef() + (constraint.getRef().contains("(") ? ", " : "") + list.get(i));
					}
					if (!(constraint.getRef().endsWith(")"))) {
						constraint.setRef(constraint.getRef() + ")");
					}
					break;
				case "UNIQUE":
					constraint.setType(type);
					constraint.setField("(" + list.get(++i).replaceAll("\\(|\\s+", ""));
					while ((i < list.size()-1)&&(!(list.get(++i).equals("MATCH")))) {
						String constField = list.get(i).replaceAll("\\)|\\s+", "");
						if (constField.length()!=0) {
							constraint.setField(constraint.getField() + ", " + constField);
						}
					}
					constraint.setField(constraint.getField() + ")");
					break;
				default: 
					continue;
				}
				listConst.add(constraint);
			}
		}
		/*Field List*/
		lists[0] = listField;
		/*Constraint List*/
		lists[1] = listConst;
		return lists;
	}

	/* method that compare Lists of Fields
	 * and write into result (List<String>) queries
	 * return - void
	 * parameter - List<Field> List<Field>
	 * */
	public void compareFieldLists(List<Field> listField1, List<Field> listField2) {
		for (int i = 0; i < listField1.size(); i++) {
			for (int j = 0; j < listField2.size(); j++) {
				if (listField1.get(i).getName().equals(listField2.get(j).getName())) {
					listField1.get(i).setFlag(true);
					listField2.get(j).setFlag(true);
					
					String type1 = listField1.get(i).getType().trim();
					String type2 = listField2.get(j).getType().trim();
					if (!(type1.equals(type2))) {
						this.result.add("ALTER TABLE " + this.dbName + " ALTER COLUMN "
								+ listField1.get(i).getName() + " TYPE "
								+ listField1.get(i).getType() + ";");
					}
					
					String nn1 = listField1.get(i).getNn().trim();
					String nn2 = listField2.get(j).getNn().trim();
					if (!(nn1.equals(nn2))) {
						if (!(("").equals(listField1.get(i).getNn()))) {
							this.result.add("ALTER TABLE " + this.dbName + " ALTER COLUMN "
									+ listField1.get(i).getName() + " SET "
									+ listField1.get(i).getNn() + ";");
						} else {
							this.result.add("ALTER TABLE " + this.dbName + " ALTER COLUMN "
									+ listField1.get(i).getName() + " DROP "
									+ listField2.get(j).getNn() + ";");
						}
					}
					
					String def1 = listField1.get(i).getDef().trim();
					String def2 = listField2.get(j).getDef().trim();
					if (!(def1.equals(def2))) {
						if (!(("").equals(listField1.get(i).getDef()))) {
							this.result.add("ALTER TABLE " + this.dbName + " ALTER COLUMN "
									+ listField1.get(i).getName() + " SET "
									+ listField1.get(i).getDef() + ";");
						} else {
							this.result.add("ALTER TABLE " + this.dbName
									+ " ALTER COLUMN "
									+ listField1.get(i).getName()
									+ " DROP DEFAULT;");
						}
					}
				}
			}
			if (listField1.get(i).isFlag() == false) {
				this.result.add("ALTER TABLE " + this.dbName + " ADD "
						+ listField1.get(i).getName() + " "
						+ listField1.get(i).getType() + " "
						+ listField1.get(i).getNn() + " "
						+ listField1.get(i).getDef() + ";");
			}
		}
		for (Field field : listField2) {
			if (field.isFlag() == false) {
				this.result.add("ALTER TABLE " + this.dbName + " DROP " + field.getName() + ";");
			}
		}
	}
	
	/* method that compare Lists of Constraints
	 * and write into result (List<String>) queries
	 * return - void
	 * parameter - List<Field> List<Field>
	 * */
	public void compareConstLists(List<Constraint> listConst1, List<Constraint> listConst2) {
		for(int i = 0; i < listConst1.size(); i++) {
			for(int j = 0; j < listConst2.size(); j++) {
				String fieldName1 = listConst1.get(i).getField().trim();
				String fieldName2 = listConst2.get(j).getField().trim();
				if (fieldName1.equals(fieldName2)) {
					String type1 = listConst1.get(i).getType().trim();
					String type2 = listConst2.get(j).getType().trim();
					if (type1.equals(type2)) {
						String ref1 = listConst1.get(i).getRef().replaceAll(" ", "");
						String ref2 = listConst2.get(j).getRef().replaceAll(" ", "");
						if (ref1.equals(ref2)) {
							listConst1.get(i).setFlag(true);
							listConst2.get(j).setFlag(true);
						}
					}
				}
			}
			if (listConst1.get(i).isFlag()==false) {
				this.result.add("ALTER TABLE " + this.dbName + " ADD CONSTRAINT "
						+ listConst1.get(i).getName() + " "
						+ listConst1.get(i).getType() + " "
						+ listConst1.get(i).getField() + " "
						+ listConst1.get(i).getRef() + ";");
			}
		}
		for (Constraint constr : listConst2) {
			if (constr.isFlag() == false) {
				this.result.add("ALTER TABLE " + this.dbName + " DROP CONSTRAINT " + constr.getName() + ";");
			}
		}
	}

	public List<String> getResult() {
		return result;
	}

	public void setResult(List<String> result) {
		this.result = result;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getPath1() {
		return newPath;
	}

	public void setPath1(String path1) {
		this.newPath = path1;
	}

	public String getPath2() {
		return oldPath;
	}

	public void setPath2(String path2) {
		this.oldPath = path2;
	}

}

