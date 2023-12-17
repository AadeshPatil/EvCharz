package com.project.evcharz.Pages;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.project.evcharz.R;

import java.util.Random;
public class EntertainmentActivity extends AppCompatActivity implements View.OnClickListener{
    private final Button[][] buttons = new Button[3][3];
    private boolean player1Turn = true;
    private int roundCount;


    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_entertenment);

        // Initialize buttons and set onClickListener for each button
        buttons[0][0] = findViewById(R.id.button1);
        buttons[0][1] = findViewById(R.id.button2);
        buttons[0][2] = findViewById(R.id.button3);
        buttons[1][0] = findViewById(R.id.button4);
        buttons[1][1] = findViewById(R.id.button5);
        buttons[1][2] = findViewById(R.id.button6);
        buttons[2][0] = findViewById(R.id.button7);
        buttons[2][1] = findViewById(R.id.button8);
        buttons[2][2] = findViewById(R.id.button9);

        // Set onClickListener for each button
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setOnClickListener(this);
            }
        }

        ImageButton backBtn = findViewById(R.id.btn_back_id_game);
        backBtn.setOnClickListener(v -> finish());
    }

    @Override
    public void onClick(View view) {
        if (!((Button) view).getText().toString().equals("")) {
            return;
        }

        // Player's turn
        if (player1Turn) {
            ((Button) view).setText("X");
            roundCount++;

            if (checkForWin()) {
                showAlert("You wins!");
            } else if (roundCount == 9) {
                showAlert("DRAW !!");
            } else {
                player1Turn = false;
                Handler handler = new Handler();
                handler.postDelayed(this::computerTurn, 500);
            }
        }
    }

    private void computerTurn() {
        Random rand = new Random();
        int row, col;

        // Simulate computer's turn by choosing a random empty cell
        do {
            row = rand.nextInt(3);
            col = rand.nextInt(3);
        } while (!buttons[row][col].getText().toString().equals(""));

        buttons[row][col].setText("O");
        roundCount++;

        if (checkForWin()) {
            showAlert("Computer wins!");
        } else if (roundCount == 9) {
            showAlert("DRAW !!");
        } else {
            player1Turn = true;
        }
    }


    private boolean checkForWin() {
            String[][] field = new String[3][3];

            // Retrieve text from buttons and assign to the field array
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    field[i][j] = buttons[i][j].getText().toString();
                }
            }

            // Check rows, columns, and diagonals for a win
            for (int i = 0; i < 3; i++) {
                if (field[i][0].equals(field[i][1])
                        && field[i][0].equals(field[i][2])
                        && !field[i][0].equals("")) {
                    return true;
                }
                if (field[0][i].equals(field[1][i])
                        && field[0][i].equals(field[2][i])
                        && !field[0][i].equals("")) {
                    return true;
                }
            }

            return field[0][0].equals(field[1][1])
                    && field[0][0].equals(field[2][2])
                    && !field[0][0].equals("")
                    || field[0][2].equals(field[1][1])
                    && field[0][2].equals(field[2][0])
                    && !field[0][2].equals("");
        }

        private void resetBoard() {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    buttons[i][j].setText("");
                }
            }
            roundCount = 0;
            player1Turn = true;
        }

        public void showAlert(String msg){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("")
                    .setMessage(msg)
                    .setPositiveButton("OK", (dialog, id) -> {
                        resetBoard();
                        dialog.dismiss();
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }
    }
