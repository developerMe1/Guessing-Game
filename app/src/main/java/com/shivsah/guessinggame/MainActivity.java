package com.shivsah.guessinggame;

import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    Random rand = new Random();
    String finalSourceCode;
    ArrayList<String> celebrityUrls = new ArrayList<>();
    ArrayList<String> celebrityNames = new ArrayList<>();
    ImageView celebrityImage;
    TextView result,timeLeft,score;
    Button ans1 ,ans2,ans3,ans4,start;
     boolean isActive = false, isTimerOn =false, isGameOn = false;
    CountDownTimer countDownTimer;
    int correctAns,totalQuestion = 1,correctQuestion =0;
    
    public void start(View view){
        if(!isActive) {
            isActive = true;
            isGameOn = true;
            start.setText(R.string.stop);
            result.setText(R.string.message);
            correctAns = generateQuestion();
        }else{

            endGame();
        }
    }

    public void submit(View view){
        if(isGameOn) {
            totalQuestion++;
            Button ansButton = (Button) view;
            int subAns = celebrityNames.indexOf(ansButton.getText().toString());
            if (subAns != correctAns) {
                result.setText(R.string.wrong);

            } else {
                correctQuestion++;
                result.setText(R.string.correct);
            }
            correctAns = generateQuestion();
        }
    }


    public /*static*/ class SourceCode extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            StringBuilder code = new StringBuilder();
            URL url;
            HttpsURLConnection httpsURLConnection;
            try {
                url = new URL(strings[0]);
                httpsURLConnection = (HttpsURLConnection) url.openConnection();
                InputStream inputStream = httpsURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();
                while (data != -1) {
                    char current = (char) data;
                    code.append(current);
                    data = inputStreamReader.read();

                }

                return code.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public/* static*/ class PictureDownload extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap;
            URL url;
            HttpsURLConnection httpsURLConnection;
            try {
                url = new URL(strings[0]);
                httpsURLConnection = (HttpsURLConnection) url.openConnection();
                InputStream in = httpsURLConnection.getInputStream();
                httpsURLConnection.connect();
                bitmap = BitmapFactory.decodeStream(in);
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }




    @SuppressLint("SetTextI18n")
    private int generateQuestion(){
        score.setText(correctQuestion + " / " + totalQuestion);
        //timeLeft.setText("20");
        if(isTimerOn){
            countDownTimer.cancel();
            isTimerOn = false;
        }
        countDown();
        //Question generator
        int indexUrlLen = celebrityUrls.size();
        int picNum = rand.nextInt(indexUrlLen);
        picGenerator(picNum);
        
        //answer generator
        String currAns = celebrityNames.get(picNum);
        int[] error = new int[3];
        
        for(int i = 0;i<3;i++){
        error[i] = rand.nextInt();
            while(error[i] == picNum){
            error[i] = rand.nextInt();
            }
        }

        int currOption = rand.nextInt(4);
        
        switch (currOption){
            case 0:
                ans1.setText(currAns);
                ans2.setText(celebrityNames.get(error[1]));
                ans3.setText(celebrityNames.get(error[0]));
                ans4.setText(celebrityNames.get(error[2]));
                break;
            case 1:
                ans1.setText(celebrityNames.get(error[0]));
                ans2.setText(currAns);
                ans3.setText(celebrityNames.get(error[1]));
                ans4.setText(celebrityNames.get(error[2]));
                break;
            case 2:
                ans1.setText(celebrityNames.get(error[2]));
                ans2.setText(celebrityNames.get(error[1]));
                ans3.setText(currAns);
                ans4.setText(celebrityNames.get(error[0]));
                break;
            case 3:
                ans1.setText(celebrityNames.get(error[2]));
                ans2.setText(celebrityNames.get(error[0]));
                ans3.setText(celebrityNames.get(error[1]));
                ans4.setText(currAns);
                break;
        }

        return picNum;
    }

    private void countDown() {
            if (!isTimerOn) {
                isTimerOn = true;
                countDownTimer = new CountDownTimer(16000, 1000 + 100) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timeLeft.setText(Integer.toString((int) millisUntilFinished / 1000));
                    }

                    @Override
                    public void onFinish() {
                        endGame();

                    }
                }.start();
            }
        }
    @SuppressLint("SetTextI18n")
    private void endGame(){
        result.setText("Congrats! you got " + correctQuestion + " answer correct out of "+(totalQuestion)+" Questions");
        correctQuestion =0;
        totalQuestion =0;
        countDownTimer.cancel();
        isActive = false;
        isGameOn =false;
        score.setText("00 / 00");
        start.setText(R.string.start);
        ans1.setText(R.string.initialAns);
        ans2.setText(R.string.initialAns);
        ans3.setText(R.string.initialAns);
        ans4.setText(R.string.initialAns);
        timeLeft.setText(R.string.initialAns);
        celebrityImage.setImageResource(R.drawable.ic_launcher_foreground);
        


    }

   

    public String sourceCodeGenerator() {
        SourceCode mySourceCode = new SourceCode();
        try {
            return mySourceCode.execute("https://www.imdb.com/list/ls052283250/").get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void nameGenerator() {
        Pattern pattern = Pattern.compile("<img alt=\"(.*?)\"");
        Matcher matcher = pattern.matcher(finalSourceCode);

        while (matcher.find()) {
            String group = matcher.group(1);
            celebrityNames.add(group);
        }

        Pattern p = Pattern.compile("src=\"(.*?)\"");
        Matcher m = p.matcher(finalSourceCode);
        while (m.find()) {
            String group = m.group(1);
            celebrityUrls.add(group);
        }
    }
    public void picGenerator(int picNum) {
        Bitmap bitmap;
        PictureDownload pictureDownload = new PictureDownload();
        try {
            bitmap = pictureDownload.execute(celebrityUrls.get(picNum)).get();
            celebrityImage.setImageBitmap(bitmap);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        celebrityImage = findViewById(R.id.celebrityImage);
        ans1 = findViewById(R.id.ans1);
        ans2 = findViewById(R.id.ans2);
        ans3 = findViewById(R.id.ans3);
        ans4 = findViewById(R.id.ans4);
        score = findViewById(R.id.score);
        timeLeft = findViewById(R.id.timeLeft);
        result = findViewById(R.id.result);
        start = findViewById(R.id.start);
        String generatedSourceCode = sourceCodeGenerator();
        String[] filteredSource = generatedSourceCode.split(getString(R.string.upperWasteFilter));
        String[] finalFilter = filteredSource[1].split(getString(R.string.lowerWasteFilter));
        finalSourceCode = finalFilter[0];
        nameGenerator();
        
    }
}
