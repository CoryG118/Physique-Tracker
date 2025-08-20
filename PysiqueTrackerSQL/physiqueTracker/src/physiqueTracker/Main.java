/* Owner: Cory Graham
 * 
 * Purpose: Java application to log body measurements in a SQLite Database using CRUD methods.
 * 
 * */


package physiqueTracker;

import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.InputMismatchException;


public class Main {
	
	// DB name to be reused
	static String dbName = null;
	
	// reusable asset
	static String lines = "=================================================================================";
	
	// Menu to give user options
	public static void menu() {
		
		System.out.println("");
		System.out.println("Please make a selection:");
		System.out.println("");
		System.out.println("1. Select database");
		System.out.println("2. Read entries");
		System.out.println("3. Log new entry");
		System.out.println("4. Update entry");
		System.out.println("5. Delete entry");
		System.out.println("6. Delete whole table");
		//System.out.println("7. Display metrics");
		System.out.println("9. Exit");
		System.out.println("");
	}
	
	public static void dbConnection(String name) {
		// Establishes connection to SQL database and sets values.
				Connection conn = null;
				Statement stmt = null;
				
				try {
					Class.forName("org.sqlite.JDBC");
					conn = DriverManager.getConnection("jdbc:sqlite:"+ dbName);
					
					stmt = conn.createStatement();
					String sql = "CREATE TABLE IF NOT EXISTS PHYSIQUE_TRACKER(" + 
									"DATE TEXT PRIMARY KEY NOT NULL," +
									"WEIGHT FLOAT NOT NULL," +
									"CHEST FLOAT NOT NULL," +
									"L_ARM FLOAT NOT NULL," +
									"R_ARM FLOAT NOT NULL," +
									"NAVEL FLOAT NOT NULL," +
									"WAIST FLOAT NOT NULL," +
									"L_LEG FLOAT NOT NULL," +
									"R_LEG FLOAT NOT NULL)";
					
					
					stmt.executeUpdate(sql);
					stmt.close();
					conn.close();
					
					
					System.out.println("Opened database successfully");
					System.out.println("");
		
				} 
				catch(Exception e) {
					System.err.println(e.getClass().getName() + ": " + e.getMessage() );
					System.exit(0);
					
				}	
	}
	
	public static void logNewEntry(String dbName, Scanner scnr) {
		
		String url = "jdbc:sqlite:" + dbName;
		
		String insertSql = "INSERT INTO PHYSIQUE_TRACKER (DATE, WEIGHT, CHEST, L_ARM, R_ARM, NAVEL, WAIST, L_LEG, R_LEG)" +
							"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try( Connection conn = DriverManager.getConnection(url);
			PreparedStatement pstmt = conn.prepareStatement(insertSql);){
			
			// Automatically use today's dtae
			String date = java.time.LocalDate.now().toString();
			
			System.out.println("Logging measuerments for today's date: " + date);
			
			scnr.nextLine();
			
			// Get user input
			System.out.print("Weight: ");
			float weight = scnr.nextFloat();
			
			System.out.print("Chest: ");
			float chest = scnr.nextFloat();
			
			System.out.print("Left Arm: ");
			float lArm = scnr.nextFloat();
			
			System.out.print("Right Arm: ");
			float rArm = scnr.nextFloat();
			
			System.out.print("Navel: ");
			float navel = scnr.nextFloat();
			
			System.out.print("Waist: ");
			float waist = scnr.nextFloat();
			
			System.out.print("Left Leg: ");
			float lLeg = scnr.nextFloat();
			
			System.out.print("Right Leg: ");
			float rLeg = scnr.nextFloat();
			
			// Clear scanner
			scnr.nextLine();
			
			// Bind and insert
			pstmt.setString(1, date);
			pstmt.setFloat(2, weight);
			pstmt.setFloat(3, chest);
			pstmt.setFloat(4, lArm);
			pstmt.setFloat(5, rArm);
			pstmt.setFloat(6, navel);
			pstmt.setFloat(7, waist);
			pstmt.setFloat(8, lLeg);
			pstmt.setFloat(9,rLeg);
			
			
			pstmt.executeUpdate();
			System.out.println("Entry logged successfully.");
				
			
			
		} catch(SQLException e) {
			
		}catch(InputMismatchException e) {
			
		}
		
	}
	
	
	// Read method to view existing inputs
	public static void readEntries(String dbName) {
		
		// Establishes which db we will access using the variable name url
		String url = "jdbc:sqlite:" + dbName;
		
		String query = "SELECT * FROM PHYSIQUE_TRACKER ORDER BY DATE DESC"; // Most recent first
		
		try ( Connection conn = DriverManager.getConnection(url);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query)){
			
			System.out.println(lines);
			System.out.println("DATE\t\tWEIGHT\tCHEST\tL_ARM\tR_ARM\tNAVEL\tWAIST\tL_LEG\tR_LEG");
			System.out.println(lines);
			
			boolean hasData = false;
			
			while (rs.next()) {
				hasData = true;
				
				String date = rs.getString("DATE");
				float weight = rs.getFloat("WEIGHT");
				float chest = rs.getFloat("CHEST");
				float lArm = rs.getFloat("L_ARM");
				float rArm = rs.getFloat("R_ARM");
				float navel = rs.getFloat("NAVEL");
				float waist = rs.getFloat("WAIST");
				float lLeg = rs.getFloat("L_LEG");
				float rLeg = rs.getFloat("R_LEG");
				
				System.out.printf("%s\t%.1f\t%.1f\t%.1f\t%.1f\t%.1f\t%.1f\t%.1f\t%.1f\n",
						date, weight, chest, lArm, rArm, navel, waist, lLeg, rLeg);
			}
			
			if (!hasData) {
				System.out.println("No entries found.");
				pause();
			}
			
			System.out.println(lines);
			pause();
			
			
		} catch(SQLException e) {
			System.out.println("Error reading from database: " + e.getMessage());
			pause();
		}
		
		
	}
	
	
	public static void updateEntry(String dbName, Scanner scnr) {
		
		// Establishes which db we will access using the variable name url
		String url = "jdbc:sqlite:" + dbName;
		
		System.out.println("Enter the DATE of the entry to update (YYYY-MM-DD): ");
		String date = scnr.nextLine();
		
		String query = "SELECT * FROM PHYSIQUE_TRACKER WHERE DATE = ?"; 
		String updateQuery = "UPDATE PHYSIQUE_TRACKER SET WEIGHT=?, CHEST=?, L_ARM=?, R_ARM=?, NAVEL=?, WAIST=?, L_LEG=?, R_LEG=? WHERE DATE=?";
		
		try(Connection conn = DriverManager.getConnection(url);
			PreparedStatement checkStmt = conn.prepareStatement(query);
			PreparedStatement updateStmt = conn.prepareStatement(updateQuery)){
			
			// Check if record exists
			checkStmt.setString(1, date);
			try(ResultSet rs = checkStmt.executeQuery()){
				if (!rs.next()) {
					System.out.println("No entry found for that date");
					pause();
					return;
				}
			}
			
			
			// Log new measurements
			System.out.print("Enter new weight: ");
			float weight = scnr.nextFloat();
			
			System.out.print("Enter new chest measurement: ");
			float chest = scnr.nextFloat();
			
			System.out.print("Enter new left arm measurment: ");
			float lArm = scnr.nextFloat();
			
			System.out.print("Enter new right arm measurement: ");
			float rArm = scnr.nextFloat();
			
			System.out.print("Enter new navel measurement: ");
			float navel = scnr.nextFloat();
			
			System.out.print("Enter new waist measurement: ");
			float waist = scnr.nextFloat();
			
			System.out.print("Enter new left leg measuremnt: ");
			float lLeg = scnr.nextFloat();
			
			System.out.print("Enter newe right leg measurement: ");
			float rLeg = scnr.nextFloat();
			
			// Clear scanner
			scnr.nextLine();
			
			// Push updated measurements
			
			updateStmt.setFloat(1, weight);
			updateStmt.setFloat(2, chest);
			updateStmt.setFloat(3, lArm);
			updateStmt.setFloat(4, rArm);
			updateStmt.setFloat(5, navel);
			updateStmt.setFloat(6, waist);
			updateStmt.setFloat(7, lLeg);
			updateStmt.setFloat(8, rLeg);
			updateStmt.setString(9, date);
			
			int rowsAffected = updateStmt.executeUpdate();
			
			if (rowsAffected > 1) {
				System.out.print("Update successful!");
				pause();
			}
			
			
		}
		catch (SQLException e) {
			System.out.println("Error updating entry: "+ e.getMessage());
			pause();
		}
	}
	
	
	public static void deleteEntry(String dbName, Scanner scnr) {
		
		// Establish which database will be accessed
		String url = "jdbc:sqlite:" + dbName;
		
		
		// Specify which date is to be deleted
		System.out.println("Enter date to be deleted (YYYY-MM-DD): ");
		String date = scnr.nextLine();
		
		String query = "DELETE FROM PHYSIQUE_TRACKER WHERE DATE = ?";
		
		try( Connection conn = DriverManager.getConnection(url);
			PreparedStatement pstmt = conn.prepareStatement(query)){
			
			pstmt.setString(1, date);
			
			int rowsDeleted = pstmt.executeUpdate();
			if (rowsDeleted > 0) {
				System.out.println("Entry deleted successfully.");
				pause();
			}
			else {
				System.out.println("No entry found for that date.");
				pause();
			}
			
		}
		catch(SQLException e) {
			System.out.print("Error deleting entry: " + e.getMessage());
			pause();
		}
	}
	
	
	// Delete whole table
	public static void deleteTable(String dbName, Scanner scnr) {
		
		String url = "jdbc:sqlite:" + dbName;
		
		String query = "DELETE FROM PHYSIQUE_TRACKER";
		
		try(Connection conn = DriverManager.getConnection(url);
			PreparedStatement pstmt = conn.prepareStatement(query)){
			
			int rowsDeleted = pstmt.executeUpdate();
			System.out.println("Deleted "+ rowsDeleted + " rows from PHYSIQUE_TRACKER." );
			
			pause();
			
		}
		catch(SQLException e) {
			System.out.println("Error deleting table: " + e.getMessage());
		}
		
	}
	
	
	
	// Pause method to control flow 
	public static void pause() {
		try{
			Thread.sleep(2000);   // Wait 2 seconds
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	// Check if database is open
	public static boolean dbAvailable() {
		if (dbName == null) {
			
			System.out.println("Please enter in database name in option 1.");
			return false;
		}
		
		return true;
	}
	
	
	
	
	public static void main(String[] args) {
		int input = -1;
		String check;
		String name = null;
		
		
		Scanner scnr = new Scanner(System.in);
		
		
		System.out.println("Hello! Welcome to the Physique Tracker.\n");
		System.out.println(lines);
		
		pause();
		
		
		while (input != 9) {
			menu();
			
			try {
				System.out.print("Selection: ");
				input = scnr.nextInt();
				scnr.nextLine();
				System.out.println("");
				
				
				
				// Select database
				if (input == 1) { 
					
					
					System.out.println("Enter your name: ");
					name = scnr.nextLine();
					name = name.toUpperCase();
					dbName = name + ".db";
					dbConnection(dbName);
					
					pause();
				}
				
				
				
				
				// Read entries
				else if (input == 2){  
					
					if (dbAvailable()) {
						
						readEntries(dbName);
						pause();
					}
					else {
						pause();
					}
					
				}
				
				
				
				
				// Log new entry
				else if (input == 3){  
					
					//Check to make sure db is open
					if (dbAvailable()) {
						
						logNewEntry(dbName, scnr);
						
					}	
					else {
						pause();
					}
				}
				
				
				
				
				// Update entry
				else if (input == 4){  
					
					if (dbAvailable()) {
						
						updateEntry(dbName, scnr);
						
					}
					else {
						pause();
					}
				}
				
				
				
				
				// Delete entry
				else if (input == 5){  
					
					if (dbAvailable()) {
						
						deleteEntry(dbName, scnr);
						
					}
					else {
						pause();
					}
				}
				
				
				
				// Delete table
				else if (input == 6){  
					
					if (dbAvailable()) {
						
						
						
						try {
							System.out.print("Are you sure? (Y/N)");
							check = scnr.next();
							check = check.toUpperCase();
							
							if (check.equals("Y")) {
								deleteTable(dbName, scnr);
								
							}
							else {
								System.out.println("Returning to menu");
								pause();
							}
						}
						catch(InputMismatchException e) {
							
						}
						
						
					}
					else {
						pause();
					}
				}
				
				
				
				
				
				// Exit
				else if (input == 9){ 
					break;
				}
				
				
				
				
				else{
					
					
					System.out.println("Not a valid option");
					System.out.println("");
					pause();
					
				}
				
			}
			catch(InputMismatchException e) { // Makes sure that input is exclusively int for menu control
				scnr.nextLine();  // clear the scanner
				System.out.println("");
				System.out.println("Invalid input. Please enter an intger.");
				System.out.println("");
			}
		}
		
		// Close scanner
		scnr.close();
		
		// closing message
		System.out.println("");
		System.out.println("Goodbye!");
		
	}

}
