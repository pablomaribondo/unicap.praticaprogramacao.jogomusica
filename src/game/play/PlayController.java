package game.play;

import game.menu.Menu;
import game.menu.MenuController;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.animation.Transition;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PlayController implements Initializable {

    @FXML
    private ImageView img_hint;
    @FXML
    private ImageView img_character;
    @FXML
    private ImageView img_volume;
    @FXML
    private ImageView img_spot1;
    @FXML
    private ImageView img_spot2;
    @FXML
    private ImageView img_spot3;
    @FXML
    private ImageView img_spot4;
    @FXML
    private Label description;
    @FXML
    private Label lbl_spot1;
    @FXML
    private Label lbl_spot2;
    @FXML
    private Label lbl_spot3;
    @FXML
    private Label lbl_spot4;
    @FXML
    private Label lbl_questionNumber;
    @FXML
    private Label lbl_points;
    @FXML
    private Button btn_playMusic;
    @FXML
    private Button btn_stopMusic;
    @FXML
    private Button btn_showHint;
    @FXML
    private Button btn_confirmQuestion;
    @FXML
    private Button btn_nextQuestion;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Pane finishPane;
    @FXML
    private TextField nameInput;
    @FXML
    private Label finishPoints;
    @FXML
    private Button btn_menu;

    private static final int TOTALQUESTIONS = 6;
    private int questionNumber;
    private int totalPoints;
    private int questionPoints;
    private boolean hintShown;
    private boolean arrived;
    public static ArrayList<MusicGenre> genreList = new ArrayList();
    private ArrayList<ImageView> alternatives = new ArrayList();
    private ImageView characterTarget;
    private ImageView characterSource;
    private ImageView correctAnswerSpot;
    private MusicGenre correctAnswerMusicGenre;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Deixa as imagens arredondadas
        roundImg();

        // Inicializa
        description.setWrapText(true);
        hintShown = false;
        arrived = false;
        btn_stopMusic.setOnMouseClicked(null);
        btn_nextQuestion.setVisible(false);
        btn_confirmQuestion.setVisible(false);
        finishPane.setVisible(false);
        nameInput.setAlignment(Pos.CENTER);
        questionNumber = 1;
        totalPoints = 0;
        questionPoints = 100;
        lbl_questionNumber.setText("Questão: " + String.format("%02d", questionNumber));
        lbl_points.setText("Pontos: " + String.format("%04d", totalPoints));

        Collections.shuffle(genreList);

        // Inicializa a personagem
        img_character.setImage(new Image("/game/assets/images/mainCharacter.png"));
        img_character.setX(genreList.get(1).getSpotX() + 40);
        img_character.setY(genreList.get(1).getSpotY() - 50);

        // Carrega as alternativas
        loadAlternatives();

        volumeSlider.setValue(Menu.player.getVolume() * 100);
        if (Menu.player.getVolume() == 0) {
            img_volume.setImage(new Image("/game/assets/images/soundOffWhite.png"));
        }
        volumeSlider.valueProperty().addListener((Observable observable) -> {
            Menu.player.setVolume(volumeSlider.getValue() / 100);
            if (Menu.player.getVolume() == 0) {
                img_volume.setImage(new Image("/game/assets/images/soundOffWhite.png"));
            } else {
                img_volume.setImage(new Image("/game/assets/images/soundOnWhite.png"));
            }
        });

    }

    public void roundImg() {
        // set a clip to apply rounded border to the original image.
        Rectangle clip = new Rectangle(img_hint.getFitWidth(), img_hint.getFitHeight());
        clip.setArcWidth(40);
        clip.setArcHeight(40);
        img_hint.setClip(clip);

        // snapshot the rounded image.
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage image = img_hint.snapshot(parameters, null);

        // remove the rounding clip so that our effect can show through.
        img_hint.setClip(null);

        // store the rounded image in the imageView.
        img_hint.setImage(image);
    }

    public static void animateText(Label text, String string) {
        String content = string;
        final Animation animation;
        animation = new Transition() {
            {
                setCycleDuration(Duration.millis(2000));
            }

            @Override
            protected void interpolate(double frac) {
                final int length = content.length();
                final int n = Math.round(length * (float) frac);
                text.setText(content.substring(0, n));
            }
        };
        animation.play();
    }

    public static void setTimeout(Runnable runnable, int delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (InterruptedException exception) {
                System.err.println(exception.getMessage());
            }
        }).start();
    }

    public void loadAlternatives() {
        alternatives.clear();
        alternatives.add(img_spot1);
        alternatives.add(img_spot2);
        alternatives.add(img_spot3);
        alternatives.add(img_spot4);
        Collections.shuffle(alternatives);
        Collections.shuffle(genreList);
        img_spot1.setX(genreList.get(0).getSpotX());
        img_spot1.setY(genreList.get(0).getSpotY());
        img_spot1.setId(genreList.get(0).getName());
        lbl_spot1.setTranslateX(img_spot1.getX() - 53);
        lbl_spot1.setTranslateY(img_spot1.getY() - 16);
        lbl_spot1.setText(genreList.get(0).getName());
        img_spot2.setX(genreList.get(1).getSpotX());
        img_spot2.setY(genreList.get(1).getSpotY());
        img_spot2.setId(genreList.get(1).getName());
        lbl_spot2.setTranslateX(img_spot2.getX() - 53);
        lbl_spot2.setTranslateY(img_spot2.getY() - 16);
        lbl_spot2.setText(genreList.get(1).getName());
        img_spot3.setX(genreList.get(2).getSpotX());
        img_spot3.setY(genreList.get(2).getSpotY());
        img_spot3.setId(genreList.get(2).getName());
        lbl_spot3.setTranslateX(img_spot3.getX() - 53);
        lbl_spot3.setTranslateY(img_spot3.getY() - 16);
        lbl_spot3.setText(genreList.get(2).getName());
        img_spot4.setX(genreList.get(3).getSpotX());
        img_spot4.setY(genreList.get(3).getSpotY());
        img_spot4.setId(genreList.get(3).getName());
        lbl_spot4.setTranslateX(img_spot4.getX() - 53);
        lbl_spot4.setTranslateY(img_spot4.getY() - 16);
        lbl_spot4.setText(genreList.get(3).getName());
        correctAnswerMusicGenre = genreList.get(0);
//        System.out.println(correctAnswerMusicGenre.getName());
        for (ImageView alternative : alternatives) {
            if (alternative.getId().equalsIgnoreCase(correctAnswerMusicGenre.getName())) {
                correctAnswerSpot = alternative;
                break;
            }
        }
    }

    @FXML
    private void moveCharacter(MouseEvent event) throws InterruptedException {
        arrived = false;
        characterSource = characterTarget;
        characterTarget = (ImageView) event.getTarget();
        Path path = new Path();
        path.getElements().add(new MoveTo(img_character.getX(), img_character.getY()));
        path.getElements().add(new LineTo(event.getX() + 40, event.getY() - 50));
        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(2000));
        pathTransition.setPath(path);
        pathTransition.setNode(img_character);
        pathTransition.play();
        img_character.setX(event.getX() + 40);
        img_character.setY(event.getY() - 50);
        alternatives.forEach((alternative) -> {
            alternative.setOnMouseClicked(null);
        });
        alternatives.remove(characterTarget);
        setTimeout(() -> {
            alternatives.forEach((alternative) -> {
                alternative.setOnMouseClicked((MouseEvent e) -> {
                    try {
                        moveCharacter(e);
                    } catch (InterruptedException exception) {
                        System.err.println(exception.getMessage());
                    }
                });
            });
            alternatives.add(characterTarget);
            arrived = true;
        }, 2000);
        if (characterSource != null) {
            characterSource.setImage(new Image("/game/assets/images/mapPin.png"));
        }
        characterTarget.setImage(new Image("/game/assets/images/mapPinChosen.png"));
        btn_confirmQuestion.setVisible(true);
    }

    @FXML
    private void showPicture(MouseEvent event) {
        if (!hintShown) {
            img_hint.setImage(new Image(correctAnswerMusicGenre.getImgUrl()));
            roundImg();
            hintShown = true;
        }
    }

    @FXML
    private void playMusic(MouseEvent event) {
        Menu.media = new Media(this.getClass().getResource(correctAnswerMusicGenre.getRandomSoundUrl()).toExternalForm());
        Menu.player = new MediaPlayer(Menu.media);
        Menu.player.setVolume(volumeSlider.getValue() / 100);
        Menu.player.play();

        btn_playMusic.setOnMouseClicked(null);
        btn_stopMusic.setOnMouseClicked((MouseEvent e) -> {
            stopMusic(e);
        });
    }

    @FXML
    private void stopMusic(MouseEvent event) {
        Menu.player.stop();

        btn_playMusic.setOnMouseClicked((MouseEvent e) -> {
            playMusic(e);
        });
        btn_stopMusic.setOnMouseClicked(null);
    }

    @FXML
    private void nextQuestion(MouseEvent event) {
        Menu.player.stop();
        hintShown = false;
        arrived = false;
        questionPoints = 100;
        lbl_questionNumber.setText("Questão: " + String.format("%02d", ++questionNumber));
        img_hint.setImage(new Image("/game/assets/images/questionmark.png"));
        roundImg();
        btn_showHint.setOnMouseClicked((MouseEvent e) -> {
            showPicture(e);
        });
        btn_playMusic.setOnMouseClicked((MouseEvent e) -> {
            playMusic(e);
        });
        loadAlternatives();
        alternatives.forEach((alternative) -> {
            alternative.setOnMouseClicked((MouseEvent e) -> {
                try {
                    moveCharacter(e);
                } catch (InterruptedException exception) {
                    System.err.println(exception.getMessage());
                }
            });
            alternative.setImage(new Image("/game/assets/images/mapPin.png"));
        });
        description.setText("");
        btn_nextQuestion.setVisible(false);
    }

    @FXML
    private void confirmQuestion(MouseEvent event) {
        if (arrived) {
            if (!characterTarget.equals(correctAnswerSpot)) {
                characterTarget.setImage(new Image("/game/assets/images/mapPinDisabled.png"));
                characterTarget.setOnMouseClicked(null);
                animateText(description, "Você errou! Tente novamente.");
//                alternatives.remove(characterTarget);
                characterTarget = null;
                questionPoints -= 25;
            } else {
                alternatives.remove(characterTarget);
                alternatives.forEach((alternative) -> {
                    alternative.setOnMouseClicked(null);
                    alternative.setImage(new Image("/game/assets/images/mapPinDisabled.png"));
                });
                alternatives.add(characterTarget);
                animateText(description, correctAnswerMusicGenre.getDescription());
                img_character.setImage(new Image(correctAnswerMusicGenre.getOutfitUrl()));
                btn_confirmQuestion.setVisible(false);
                btn_showHint.setOnMouseClicked(null);
                if (hintShown) {
                    totalPoints += questionPoints - 15;
                } else {
                    totalPoints += questionPoints;
                }
                lbl_points.setText("Pontos: " + String.format("%04d", totalPoints));
                if (questionNumber != TOTALQUESTIONS) {
                    setTimeout(() -> {
                        btn_nextQuestion.setVisible(true);
                    }, 1000);
                }
                if (questionNumber == TOTALQUESTIONS) {
                    finishPane.setVisible(true);
                    finishPoints.setText("Pontuação total: " + String.format("%04d", totalPoints));
                    btn_playMusic.setOnMouseClicked(null);
                    btn_stopMusic.setOnMouseClicked(null);
                    btn_showHint.setOnMouseClicked(null);
                    btn_confirmQuestion.setOnMouseClicked(null);
                    btn_nextQuestion.setOnMouseClicked(null);
                    btn_confirmQuestion.setVisible(false);
                    btn_nextQuestion.setVisible(false);
                    btn_menu.setOnMouseClicked(null);
                }
            }
        }
    }

    @FXML
    private void menu(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/game/menu/Menu.fxml"));

        Scene scene = new Scene(root);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Menu");

        Menu.player.stop();

        Menu.media = new Media(this.getClass().getResource("/game/assets/sounds/latinfide.mp3").toExternalForm());
        Menu.player = new MediaPlayer(Menu.media);
        Menu.player.setVolume(0.8);
        Menu.player.setOnEndOfMedia(() -> {
            Menu.player.seek(Duration.ZERO);
        });
        Menu.player.play();

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void submitAndMenu(MouseEvent event) throws IOException {
        MenuController.players.add(new Player(nameInput.getText(), totalPoints));
        menu(event);
    }

}
