package com.company;

import io.validly.Notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static io.validly.NoteAllValidator.valid;

public class Validator {
	//	public void Validate(String inputType, List<String> args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
//		Method method = Validator.getClass().getDeclaredMethod(inputType.substring(1));
//		method.invoke(args);
//	}
	public List<String> ReportFinishedMap(ArrayList<String> arrayList) {
		return null;
	}

	public List<String> LeaveGame(ArrayList<String> arrayList) {
		return null;
	}

	public List<String> LostGame(ArrayList<String> arrayList) {
		return null;
	}

	public List<String> SendPlayerCount(ArrayList<String> arrayList) {
		return null;
	}

	public List<String> Register(ArrayList<String> args) {
		Notification notification = validateUsername(args.get(0));
		valid(args.get(1), "errors", notification).mustNotBeBlank("Password is blank")
				.lengthMustBeWithin(8, 50, "Password must be within 8 - 50 characters!")
				.must(containDigits().and(containLetters()), "Password must contain at least 1 digit and 1 letter!");
		return notification.getMessages().get("errors");
	}

	public List<String> Connect(ArrayList<String> args) {
		Notification notification = validateUsername(args.get(1));
		valid(args.get(0), "errors", notification).mustNotBeBlank("Password is blank");
		return notification.getMessages().get("errors");
	}

	private Notification validateUsername(String username) {
		Notification notification = new Notification();
		valid(username, "errors", notification)
				.mustNotBeBlank("Name is blank!")
				.lengthMustBeWithin(3, 25, "Name must be withing 3 - 25 characters!")
				.must(containsAllowedSymbols(), "Username isn't alphanumeric with dot and underscore!");
		return notification;
	}

	private Predicate<String> containDigits() {
		return s -> s.matches(".*\\d.*");
	}

	private Predicate<String> containsAllowedSymbols() {
		return s -> s.matches("^[a-zA-Z0-9 ._]+$");
	}

	private Predicate<String> containLetters() {
		return s -> s.matches(".*[a-zA-Z]+.*");
	}

	private Predicate<String> notContain(CharSequence... value) {
		return s -> !Arrays.asList(value).contains(s);
	}
}
