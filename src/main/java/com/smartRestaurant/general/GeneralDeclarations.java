package com.smartRestaurant.general;

public interface GeneralDeclarations {
	// Max guests number that a user can book for.
	final int MAXGUESTS = 16;

	// Min guests number that a user can book for.
	final int MINGUESTS = 2;

	// Array that stores number of seats for a table.
	// example:
	// table with 8 seats.
	// table with 6 seats.
	// table with 4 seats.
	// table with 2 seats.
	final int[] SEATS = { 8, 6, 4, 2 };
}
