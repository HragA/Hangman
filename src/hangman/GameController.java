package hangman;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class GameController {

	private final ExecutorService executorService;
	private final Game game;
	private int numwrong = 0;

	public GameController(Game game) {
		this.game = game;
		executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				return thread;
			}
		});
	}

	public static GameController getInstance() {
		return null;
	}

	@FXML
	private VBox board ;
	@FXML
	private Label statusLabel ;
	@FXML
	private Label enterALetterLabel ;
	@FXML
	private Label correctGuessLabel;
	@FXML
	private Label incorrectGuessLabel;
	@FXML
	private TextField textField ;
	@FXML
	private TextField correctGuessField ;
	@FXML
	private TextField incorrectGuessField ;

    public void initialize() throws IOException {
		System.out.println("in initialize");
		drawHangman(numwrong);
		addTextBoxListener();
		setUpStatusLabelBindings();
		prepAnswerFields();
	}

	private void addTextBoxListener() {
		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
				if(newValue.length() > 0 && newValue.matches("[a-zA-Z]+")) {
					int tempIndex = -1;
					int numChar = 0;
					System.out.print(newValue);
					for (int i = 0; i < game.getAnsArray().length; i++) {
						if (newValue.equals(game.getAnsArray()[i])) {
							numChar++;
						}
					}
					for (int i = 0; i < numChar-1; i++) {
						tempIndex = getValidIndex(game.getAnsArray(), newValue);
						game.makeMove(newValue);
						if (tempIndex != -1 && (game.getGameStatus() == Game.GameStatus.GOOD_GUESS || game.getGameStatus() == Game.GameStatus.WON)) {
							String tempField1 = correctGuessField.getText();
							int tempInt = tempIndex * 2;
							if (tempInt == 0) {
								String tempField2 = newValue + tempField1.substring(1);
								correctGuessField.setText(tempField2);
							} else {
								String tempField2 = tempField1.substring(0, tempInt) + newValue + tempField1.substring(tempInt + 1);
								correctGuessField.setText(tempField2);
							}
						}
					}
					tempIndex = getValidIndex(game.getAnsArray(), newValue);
					game.makeMove(newValue);
					if (tempIndex != -1 && (game.getGameStatus() == Game.GameStatus.GOOD_GUESS || game.getGameStatus() == Game.GameStatus.WON)) {
						String tempField1 = correctGuessField.getText();
						int tempInt = tempIndex * 2;
						if (tempInt == 0) {
							String tempField2 = newValue + tempField1.substring(1);
							correctGuessField.setText(tempField2);
						} else {
							String tempField2 = tempField1.substring(0, tempInt) + newValue + tempField1.substring(tempInt + 1);
							correctGuessField.setText(tempField2);
						}
					}
					else if (game.getGameStatus() == Game.GameStatus.BAD_GUESS || game.getGameStatus() == Game.GameStatus.GAME_OVER) {
						String tempField = incorrectGuessField.getText();
						numwrong++;
						drawHangman(numwrong);
						if(numwrong <= 9) {
							if (tempField.equals("")) {
								tempField = newValue;
								incorrectGuessField.setText(tempField);
							}
							else {
								tempField = tempField + " " + newValue;
								incorrectGuessField.setText(tempField);
							}
						}
					}
					textField.clear();
				}
				else{
					textField.clear();
				}
			}
		});
	}

	private int getValidIndex(String[] array, String input) {
		int index = -1;
		for(int i = 0; i < array.length; i++) {
			if(array[i].equalsIgnoreCase(input)) {
				index = i;
				break;
			}
		}
		return index;
	}

	private void setUpStatusLabelBindings() {

		System.out.println("in setUpStatusLabelBindings");
		statusLabel.textProperty().bind(Bindings.format("%s", game.gameStatusProperty()));
		enterALetterLabel.textProperty().bind(Bindings.format("%s", "Enter a letter:"));
		correctGuessLabel.textProperty().bind(Bindings.format("%s", "Correct:"));
		incorrectGuessLabel.textProperty().bind(Bindings.format("%s", "Incorrect:"));
		/*	Bindings.when(
					game.currentPlayerProperty().isNotNull()
			).then(
				Bindings.format("To play: %s", game.currentPlayerProperty())
			).otherwise(
				""
			)
		);
		*/
	}

	private void prepAnswerFields() {
		// Disable user interaction with these text fields
		correctGuessField.setDisable(true);
		incorrectGuessField.setDisable(true);
		// Customize text fields
		correctGuessField.getStyleClass().add("custom");
		incorrectGuessField.getStyleClass().add("custom");
		// Initialize Correct Guess fields
		initAnswerFields();
	}

	private void initAnswerFields() {
    	correctGuessField.setText("");
		incorrectGuessField.setText("");
		for (int i = 0; i < game.getAnswer().length(); i++) {
			if (i == game.getAnswer().length() -1) {
				correctGuessField.setText(correctGuessField.getText() + "_");
			}
			else {
				correctGuessField.setText(correctGuessField.getText() + "_ ");
			}
		}
	}

	private void drawHangman(int wrong) {
    	
    	// Hanging Bar
		Line hangingSupport = new Line(20.0f, 470.0f, 200.0f, 470.0f);
		hangingSupport.setManaged(false);
		hangingSupport.setStrokeWidth(5);
		hangingSupport.setStroke(Color.BLACK);

		Line hangingPole = new Line(100.0f, 470.0f, 100.0f, 50.0f);
		hangingPole.setManaged(false);
		hangingPole.setStrokeWidth(5);
		hangingPole.setStroke(Color.BLACK);

		Line hangingRod = new Line(100.0f, 50.0f, 250.0f, 50.0f);
		hangingRod.setManaged(false);
		hangingRod.setStrokeWidth(5);
		hangingRod.setStroke(Color.BLACK);

		Line hangingRope = new Line(250.0f, 50.0f, 250.0f, 150.0f);
		hangingRope.setManaged(false);
		hangingRope.setStrokeWidth(5);
		hangingRope.setStroke(Color.BROWN);

    	// Person
		Circle head = new Circle(250.0f, 200.0f, 0);
		head.setManaged(false);
		head.setRadius(50);
		head.setStroke(Color.BLACK);
		head.setFill(null);
		head.setStrokeWidth(5);

		Circle leftEye = new Circle(270.0f, 190.0f, 50);
		leftEye.setManaged(false);
		leftEye.setRadius(5);
		leftEye.setStroke(Color.BLUE);

		Circle rightEye = new Circle(230.0f, 190.0f, 50);
		rightEye.setManaged(false);
		rightEye.setRadius(5);
		rightEye.setStroke(Color.BLUE);


		Arc arc = new Arc(235.0f, 230.0f, 35.0f, 25.0f, 1.0f , 5.0f);
		arc.setManaged(false);
		arc.setType(ArcType.ROUND);

		Line BodyLine = new Line(250.0f, 250.0f, 250.0f, 350.0f);
		BodyLine.setManaged(false);
		BodyLine.setStrokeWidth(5);
		BodyLine.setStroke(Color.BLACK);

		Line LeftArm = new Line(250.0f, 280.0f, 200.0f, 320.0f);
		LeftArm.setManaged(false);
		LeftArm.setStrokeWidth(5);
		LeftArm.setStroke(Color.BLACK);

		Line RightArm = new Line(250.0f, 280.f, 300.0f, 320.0f);
		RightArm.setManaged(false);
		RightArm.setStrokeWidth(5);
		RightArm.setStroke(Color.BLACK);

		Line LeftLeg = new Line(250.0f, 350.0f, 200.0f, 400.0f);
		LeftLeg.setManaged(false);
		LeftLeg.setStrokeWidth(5);
		LeftLeg.setStroke(Color.BLACK);

		Line RightLeg = new Line(250.0f, 350.0f, 300.0f, 400.0f);
		RightLeg.setManaged(false);
		RightLeg.setStrokeWidth(5);
		RightLeg.setStroke(Color.BLACK);

		if(wrong == 0) {
			board.getChildren().add(hangingSupport);
			board.getChildren().add(hangingPole);
			board.getChildren().add(hangingRod);
			board.getChildren().add(hangingRope);
		}

		if(wrong == 1) {
			board.getChildren().add(head);
		}

		if(wrong == 2) {
			board.getChildren().add(BodyLine);
		}

		if(wrong == 3) {
			board.getChildren().add(LeftLeg);
		}

		if(wrong == 4) {
			board.getChildren().add(RightLeg);
		}

		if(wrong == 5) {
			board.getChildren().add(RightArm);
		}

		if(wrong == 6) {
			board.getChildren().add(LeftArm);
		}

		if(wrong == 7) {
			board.getChildren().add(leftEye);
		}

		if(wrong == 8) {
			board.getChildren().add(rightEye);
		}

		if(wrong == 9) {
			board.getChildren().add(arc);
		}

	}
		
	@FXML 
	private void newHangman() {
		game.reset();
		board.getChildren().clear();
		numwrong = 0;
		drawHangman(numwrong);
		initAnswerFields();
	}

	@FXML
	private void quit() {
		board.getScene().getWindow().hide();
	}

}