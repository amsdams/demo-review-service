package com.amsdams.demoreviewservice;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewEntry {
	private String username;

	/**
	 * The date that the review was written.
	 */
	private Date date;

	/**
	 * The textual review content.
	 */
	private String review;
}
