package com.example.meinesignalverarbeitung;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.LiveFolders;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity{

    //Declare variables
    int testvariable;
    static String pathSave = "";
    static String audioName="";
    int MaximalAmplitude = 0;
    Boolean PauseInAktiv = true;
    Button btnRecord, btnStopRecord, btnPlay, btnStop, btnPause, btnAllRecords;
    TextView InformationArea;
    MediaRecorder mediaRecorder = new MediaRecorder();
    MediaPlayer mediaPlayer = new MediaPlayer();
    final int REQUEST_PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Request Runtime permission
        if (!checkPermissionFromDevice())
            requestPermission();

        //Init Elements
        btnPause = (Button) findViewById(R.id.btnPause);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnRecord = (Button) findViewById(R.id.btnStartRecord);
        btnStopRecord = (Button) findViewById(R.id.btnStopRecord);
        InformationArea = (TextView) findViewById(R.id.InformationArea);
        btnAllRecords = (Button) findViewById(R.id.btnAllRecords);

        //Default
        btnPause.setEnabled(false);
        btnStop.setEnabled(false);
        btnPlay.setEnabled(false);
        btnStopRecord.setEnabled(false);
        btnRecord.setEnabled(true);

        //New Window for listing all previous Records
        btnAllRecords.setOnClickListener(new View.OnClickListener() {
            Intent allaudiorecords = new Intent(MainActivity.this, AllAudioRecords.class);
            @Override
            public void onClick(View view) {
                //Starts new Activity
                startActivity(allaudiorecords);
                Toast.makeText(MainActivity.this, "Open new Window..."
                        , Toast.LENGTH_SHORT).show();
            }
        });

        //Aufnehmen starten
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermissionFromDevice()) {
                    audioName =  UUID.randomUUID().toString() + "_audio_record.3gp";
                    pathSave = createFile(audioName);                                               //erzeugt neuen definierten Pfad

                    PauseInAktiv = true;                                                            //Boolean, damit bei Button Pause die Audio bei play wieder normal fortgesetzt wird
                    setupMediaRecorder();
                    try {
                        mediaPlayer.reset();                                                        //Reset damit die Audio wieder abgespielt werden kann
                        InformationArea.setText(pathSave);                                          //zeigt uns den Pfad in der App an
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //Enable/Disable Buttons
                    btnRecord.setEnabled(false);
                    btnStopRecord.setEnabled(true);
                    btnPlay.setEnabled(false);
                    btnStop.setEnabled(false);
                    //Toast
                    Toast.makeText(MainActivity.this, "Recording..."
                            , Toast.LENGTH_SHORT).show();
                } else {
                    requestPermission();
                }
            }
        });

        //Aufnahme abbrechen
        btnStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                btnStopRecord.setEnabled(false);
                btnPlay.setEnabled(true);
                btnRecord.setEnabled(true);
                btnStop.setEnabled(false);
            }
        });

        //Aufnahme abspielen
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPlay.setEnabled(false);
                btnStop.setEnabled(true);
                btnStopRecord.setEnabled(false);
                btnRecord.setEnabled(false);
                btnPause.setEnabled(true);
                //Initialsiert mit der Audio
                if (PauseInAktiv) {
                    try {
                        mediaPlayer.setDataSource(pathSave);
                        mediaPlayer.prepare();
                        //mediaPlayer.setVolume((float) 0.9, (float) 0.9);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //Spielt Audio ab
                mediaPlayer.start();
                Toast.makeText(MainActivity.this, "Playing..."
                        , Toast.LENGTH_SHORT).show();
            }
        });

        //Abspielen der Aufnahme pausieren
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRecord.setEnabled(true);
                btnStopRecord.setEnabled(false);
                btnStop.setEnabled(true);
                btnPlay.setEnabled(true);

                mediaPlayer.pause();
                PauseInAktiv = false;                                                               //Boolean auf false setzen, wenn der user auf play drückt fängt er dann da an wo er aufgehört hatte
                Toast.makeText(MainActivity.this, "Pause..."
                        , Toast.LENGTH_SHORT).show();
            }
        });

        //Abspielen der Aufnahme stoppen
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Enable/Disable Buttons
                btnStopRecord.setEnabled(true);
                btnRecord.setEnabled(true);
                btnStop.setEnabled(false);
                btnPlay.setEnabled(true);
                PauseInAktiv = true;
                //Reset mediaplayer
                if (mediaPlayer != null) {
                    mediaPlayer.reset();                                                           //Reset Option, damit man dannach wieder abspielen kann
                }
                //Toast
                Toast.makeText(MainActivity.this, "Stop..."
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String createFile(String filename) {
        String AbsoluteFilePath;
        final File file = new File(getExternalFilesDir(null), filename);
        AbsoluteFilePath = file.getAbsolutePath();
        return  AbsoluteFilePath;                                                                   //Übergibt AbsolutFilePath an PathSaves
    }

    //Schnittstelle der Audioübertragung einstellen
    private void setupMediaRecorder() {
        mediaRecorder.setAudioChannels(1);
        mediaRecorder.setAudioSamplingRate(44100);
        mediaRecorder.setAudioEncodingBitRate(64000);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(pathSave);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
    }

    //Permissions
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }
}
