package com.example.meinesignalverarbeitung;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AllAudioRecords extends MainActivity {

        //Declare Variables
        Button btnBackToMainPage, btnPreviousRecord;
        TextView tvTest;
        File[] AudioRecordsPath;
        int index = 0;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.allaudiorecords);

            //Get All Audio Records from directory
            AudioRecordsPath = getAudioRecords();

            //Init Variables
            btnBackToMainPage = (Button)findViewById(R.id.btnBackToMainPage);
            btnPreviousRecord = (Button)findViewById(R.id.btnPreviousRecord);
            tvTest = (TextView) findViewById(R.id.tvZumTesten);


            //Back to Mainpage Button
            btnBackToMainPage.setOnClickListener(new View.OnClickListener() {
                Intent backToMainPage = new Intent(AllAudioRecords.this, MainActivity.class);
                @Override
                public void onClick(View view) {
                startActivity(backToMainPage);
                }
            });

            //Button showing the previous Record
            btnPreviousRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (index < AudioRecordsPath.length) {                                           //Überprüfung ob alle Audiosignale gespeichert wurden
                        tvTest.setText(AudioRecordsPath[index].getName());
                        //Reset Mediaplayer
                        if (mediaPlayer != null) {
                            mediaPlayer.reset();                                                     //Reset Option, damit man dannach wieder abspielen kann
                        }
                        //Audio abspielen
                        try {
                            mediaPlayer.setDataSource("/storage/emulated/0/Android/data/" +
                                    "com.example.meinesignalverarbeitung/files/" + AudioRecordsPath[index].getName());
                            mediaPlayer.prepare();
                            //mediaPlayer.setVolume((float) 0.9, (float) 0.9);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else btnPreviousRecord.setEnabled(false);
                    //Spielt Audio ab
                    mediaPlayer.start();
                    index++;
                }

            });
}


    private File[] getAudioRecords() {
            File AudioFiles = new File("/storage/emulated/0/Android/data/com.example.meinesignalverarbeitung/files/");
            File[] AudioRecordsPathToReturn = AudioFiles.listFiles();                                  //Speicher alle Audio Signale in einem File ab

            return AudioRecordsPathToReturn;
    }
}
//https://www.heise.de/tipps-tricks/Android-ListView-fuer-Einsteiger-4264160.html#listview_androidstudio