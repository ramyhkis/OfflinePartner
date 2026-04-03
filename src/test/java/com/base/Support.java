package com.base;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Support {

	public String generateEmail() {
		LocalDateTime now = LocalDateTime.now();

		// Format date as yyyyMMddHHmmss
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		String timestamp = now.format(formatter); // <-- this defines timestamp

		// Generate random number
		int randomNum = (int)(Math.random() * 1000);

		// Build professional email
		String email = "user" + timestamp + randomNum + "@gmail.com";
		System.out.println(email); // e.g., user20251030143025123@gmail.com
		return email;
	}


	public String generate_mobileNumber() {

		Random random = new Random();

		// First digit should not be 0
		int firstDigit = random.nextInt(9) + 1; // 1 to 9

		// Remaining 9 digits
		long remainingDigits = (long)(random.nextDouble() * 1_000_000_000L);

		// Combine to make 10-digit number
		String mobileNumber = String.valueOf(firstDigit) + String.format("%09d", remainingDigits);

		System.out.println(mobileNumber); // e.g., 9123456789


		return mobileNumber;
	}


	// Method to generate a random vehicle number like GJ23EFCD49
	public String generateVehicleNumber() {
		Random random = new Random();

		// Fixed state code (can change as needed)
		String stateCode = "GJ";

		// Generate 2 random digits for RTO code (10–99)
		int rtoCode = random.nextInt(90) + 10;

		// Generate 4 random uppercase letters (A–Z)
		StringBuilder letters = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			char letter = (char) ('A' + random.nextInt(26));
			letters.append(letter);
		}

		// Generate 2 random digits at the end (10–99)
		int lastDigits = random.nextInt(90) + 10;

		// Combine everything to form vehicle number
		String vehicleNumber = stateCode + rtoCode + letters + lastDigits;

		return vehicleNumber;
	}


	// Method to generate a random 6-letter name
	public String generateFirstName() {
		Random random = new Random();
		String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

		StringBuilder name = new StringBuilder();

		// Generate 6 random letters
		for (int i = 0; i < 6; i++) {
			char letter = alphabet.charAt(random.nextInt(alphabet.length()));
			name.append(letter);
		}

		// Capitalize the first letter, make rest lowercase (for a natural name look)
		String finalName = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

		return finalName;
	}


	// 🔹 Separate method to generate a random 5-letter last name
	public String generateLastName() {
		Random random = new Random();
		String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		StringBuilder lastName = new StringBuilder();

		for (int i = 0; i < 5; i++) {
			char letter = alphabet.charAt(random.nextInt(alphabet.length()));
			lastName.append(letter);
		}

		// Capitalize first letter for a natural look
		return lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
	}

}
