package ne.iot.adaptedvoice;

import static ne.iot.adaptedvoice.Commands.air_power_off;
import static ne.iot.adaptedvoice.Commands.air_power_on;
import static ne.iot.adaptedvoice.Commands.curtain_down;
import static ne.iot.adaptedvoice.Commands.curtain_stop;
import static ne.iot.adaptedvoice.Commands.curtain_up;
import static ne.iot.adaptedvoice.Commands.fingerPrintAfterLock;
import static ne.iot.adaptedvoice.Commands.light_off;
import static ne.iot.adaptedvoice.Commands.light_on;
import static ne.iot.adaptedvoice.Commands.tv_power;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity2 extends Activity implements
        RecognitionListener {

    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private TextView returnedText;
    private TextView returnedError;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private boolean mainKeyWord, keyWord = false;

    private Button button;
    private void resetSpeechRecognizer() {

        if(speech != null)
            speech.destroy();
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        if(SpeechRecognizer.isRecognitionAvailable(this))
            speech.setRecognitionListener(this);
        else
            finish();
    }

    private void setRecogniserIntent() {

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "tk");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // UI initialisation
        returnedText = findViewById(R.id.textView1);
        returnedError = findViewById(R.id.errorView1);
        progressBar =  findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.INVISIBLE);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UrlActivity.class));
            }
        });


        // start speech recogniser
        resetSpeechRecognizer();

        // start progress bar
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);

        // check for permission
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }

        setRecogniserIntent();
        speech.startListening(recognizerIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                speech.startListening(recognizerIntent);
            } else {
                Toast.makeText(MainActivity2.this, "Permission Denied!", Toast
                        .LENGTH_SHORT).show();
                finish();
            }
        }
    }


    @Override
    public void onResume() {
        Log.i(LOG_TAG, "resume");
        super.onResume();
        resetSpeechRecognizer();
        speech.startListening(recognizerIntent);
    }

    @Override
    protected void onPause() {
        Log.i(LOG_TAG, "pause");
        super.onPause();
        speech.stopListening();
    }

    @Override
    protected void onStop() {
        Log.i(LOG_TAG, "stop");
        super.onStop();
        if (speech != null) {
            speech.destroy();
        }
    }


    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        progressBar.setIndeterminate(true);
        speech.stopListening();
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";

//        returnedText.setText(text);
        ///////////////////////////////////
        Log.e("COMMAND", "MATCHES => " + matches);


        String[] namesArr = matches.toArray(new String[matches.size()]);

        for (int i = 0; i <= namesArr.length-1; i++) {
            if ("аха".equals(namesArr[i]) || "ахал".equals(namesArr[i]) || "ха-ха".equals(namesArr[i]) || "ха-ха-ха".equals(namesArr[i]) || "а ха".equals(namesArr[i]) || "охо".equals(namesArr[i]) || "ага".equals(namesArr[i]) || "ах".equals(namesArr[i])) {
                keyWord = true;
                Toast.makeText(getApplicationContext(), "Ahal", Toast.LENGTH_SHORT).show();
                break;
            } else if ("сравнить ок".equals(namesArr[i]) || "сранный явка".equals(namesArr[i]) || "Сравни ярка".equals(namesArr[i]) || "сранный йог".equals(namesArr[i]) || "сранный Йорк".equals(namesArr[i]) || "шрамы Йорк".equals(namesArr[i]) || "страны ок".equals(namesArr[i]) || "вчера не як".equals(namesArr[i]) || "терраны яркость".equals(namesArr[i]) || "шрамы явка".equals(namesArr[i]) || "шрамы Яг".equals(namesArr[i]) || "тираны як".equals(namesArr[i]) || "вчера на як".equals(namesArr[i]) || "сраный я".equals(namesArr[i]) || "сранный ок".equals(namesArr[i]) || "сраный ак".equals(namesArr[i]) ||"сраный ок".equals(namesArr[i]) ||"сранный як".equals(namesArr[i]) || "сразу як".equals(namesArr[i]) || "срань як".equals(namesArr[i]) || "сраная".equals(namesArr[i]) || "сранный Яг".equals(namesArr[i]) || "сранный я".equals(namesArr[i])) {
//                if (keyWord) {
                    Log.e("COMMAND", "LIGHT ON");
                    light_on(getApplicationContext());
                    Toast.makeText(getApplicationContext(), "Çyrany ýak", Toast.LENGTH_SHORT).show();
                    keyWord = false;
//                }
                break;
            } else if ("sravni.ru".equals(namesArr[i]) || "сраное вчера".equals(namesArr[i]) || "сраных вечер".equals(namesArr[i]) || "сранный крючок".equals(namesArr[i]) || "вчерашний вечер".equals(namesArr[i]) || "сраный зачет".equals(namesArr[i]) || "сраный дача".equals(namesArr[i]) || "страны кучер".equals(namesArr[i]) || "сраный десерт".equals(namesArr[i]) || "срань вечер".equals(namesArr[i]) || "сразу черный".equals(namesArr[i]) || "страны учет".equals(namesArr[i]) || "сраный picture".equals(namesArr[i]) || "странный вечер".equals(namesArr[i]) || "сранный вечер".equals(namesArr[i]) || "вчера на вечер".equals(namesArr[i]) || "сранный couture".equals(namesArr[i]) || "сранный ветер".equals(namesArr[i]) || "сравнить вечер".equals(namesArr[i]) || "вчера не вечер".equals(namesArr[i]) || "шрамы вечер".equals(namesArr[i]) || "страны вечер".equals(namesArr[i]) || "вчера вечер".equals(namesArr[i]) || "сраный surf".equals(namesArr[i]) || "сравнить".equals(namesArr[i]) || "страны".equals(namesArr[i])) {
//                if (keyWord) {
                    Log.e("COMMAND", "LIGHT OFF");
                    light_off(getApplicationContext());
                    Toast.makeText(getApplicationContext(), "Çyrany öçür", Toast.LENGTH_SHORT).show();
                    keyWord = false;
//                }
                break;
            } else if ("кондиционер очень".equals(namesArr[i]) || "канцлер ашан".equals(namesArr[i]) || "консоли 1".equals(namesArr[i]) || "кондиционер 1".equals(namesArr[i]) || "кондиционер як".equals(namesArr[i]) || "кондиционер йорк".equals(namesArr[i]) || "канцлер от цель".equals(namesArr[i]) || "концерт цель".equals(namesArr[i]) || "кондиционер от цен".equals(namesArr[i]) || "канцлер отель".equals(namesArr[i]) || "контейнер отцу".equals(namesArr[i]) || "канцлер отце".equals(namesArr[i]) || "контейнер отца".equals(namesArr[i]) || "канцлер отца".equals(namesArr[i]) || "кондиционера цены".equals(namesArr[i]) || "кондиционер а центр".equals(namesArr[i]) || "кондиционера центр".equals(namesArr[i]) || "кондиционеров Сургут".equals(namesArr[i]) || "кондиционер Абсолют".equals(namesArr[i]) || "кондиционер отцов".equals(namesArr[i]) || "кондиционер отца".equals(namesArr[i]) || "кондиционер овца".equals(namesArr[i]) || "кондиционер отцу".equals(namesArr[i]) || "кондиционер отцом".equals(namesArr[i]) || "кондиционер Ассоль".equals(namesArr[i]) || "кондиционер Opel".equals(namesArr[i]) || "кондиционер artel".equals(namesArr[i]) || "кондиционер отшил".equals(namesArr[i]) || "кондиционер отель".equals(namesArr[i]) || "кондиционер оцелот".equals(namesArr[i]) || "кондиционер отшила".equals(namesArr[i]) || "кондиционер отзывы".equals(namesArr[i]) || "кондиционер отсюда".equals(namesArr[i])) {
//                if (keyWord) {
                    Log.e("COMMAND", "CONDITIONER ON");
                    air_power_on(getApplicationContext());
                    Toast.makeText(getApplicationContext(), "Kondisioner açyl", Toast.LENGTH_SHORT).show();
                    keyWord = false;
//                }
            } else if ("кондиционер я понял".equals(namesArr[i]) || "кондиционеры афон".equals(namesArr[i]) || "канцлер я фон".equals(namesArr[i]) || "контролер я пол".equals(namesArr[i]) || "концерты apple".equals(namesArr[i]) || "канцлер я пол".equals(namesArr[i]) || "концерт я пол".equals(namesArr[i]) || "кондиционер я был".equals(namesArr[i]) || "кондиционер был".equals(namesArr[i]) || "кондиционер Apple".equals(namesArr[i]) ||"кондиционер я полу".equals(namesArr[i]) || "кондиционер я пол".equals(namesArr[i]) || "кондиционер apple".equals(namesArr[i]) || "кондиционеры apple".equals(namesArr[i]) || "кондиционер я поля".equals(namesArr[i]) || "кондиционер опа".equals(namesArr[i]) || "кондиционеры опа".equals(namesArr[i]) || "кондиционер афон".equals(namesArr[i])) {
//                if (keyWord) {
                    Log.e("COMMAND", "CONDITIONER OFF");
                    air_power_off(getApplicationContext());
                    Toast.makeText(getApplicationContext(), "Kondisioner ýapyl", Toast.LENGTH_SHORT).show();
                    keyWord = false;
//                }
                break;
            } else if ("кататься".equals(namesArr[i]) || "операция".equals(namesArr[i]) || "копицентр".equals(namesArr[i]) || "кафе Ассоль".equals(namesArr[i]) || "кафе WhatsApp".equals(namesArr[i]) || "кафе Асыл".equals(namesArr[i]) || "а пацаны".equals(namesArr[i]) || "каприз отцу".equals(namesArr[i]) || "apple отцом".equals(namesArr[i]) || "opel центр".equals(namesArr[i]) || "кафе отце".equals(namesArr[i]) || "кафе от цен".equals(namesArr[i]) || "копы отце".equals(namesArr[i]) || "копы отца".equals(namesArr[i]) || "копы от цен".equals(namesArr[i]) || "гатыа сан".equals(namesArr[i]) || "кафе ацил".equals(namesArr[i]) || "кафе акцент".equals(namesArr[i]) || "кафе Accent".equals(namesArr[i]) || "кафе оценка".equals(namesArr[i]) || "кафе акцентр".equals(namesArr[i]) || "папа сын".equals(namesArr[i]) || "по пацан".equals(namesArr[i]) || "копаться".equals(namesArr[i]) || "папе отшил".equals(namesArr[i]) || "папе ацил".equals(namesArr[i]) || "кафе Асель".equals(namesArr[i]) || "кафе отцом".equals(namesArr[i]) || "папе отель".equals(namesArr[i]) || "кафе отель".equals(namesArr[i]) || "opel".equals(namesArr[i])) {
//                if (keyWord) {
                    Log.e("COMMAND", "DOOR OPEN");
                    fingerPrintAfterLock(getApplicationContext());
                    Toast.makeText(getApplicationContext(), "Gapy açyl", Toast.LENGTH_SHORT).show();
                    keyWord = false;
//                }
                break;
            } else if ("телевизор отсылка".equals(namesArr[i]) || "телевизор асан".equals(namesArr[i]) || "телевизор отчет".equals(namesArr[i]) || "телевизор 1".equals(namesArr[i]) || "телевизор а".equals(namesArr[i]) || "телевизор очень".equals(namesArr[i]) || "телевизора центр".equals(namesArr[i]) || "телевизор accent".equals(namesArr[i]) || "телевизор Samsung".equals(namesArr[i]) || "телевизор Thomson".equals(namesArr[i]) || "телевизор Томсон".equals(namesArr[i]) || "телевизор отель".equals(namesArr[i]) || "телевизор ацилок".equals(namesArr[i]) || "телевизор Accent".equals(namesArr[i]) || "телевизор artel".equals(namesArr[i]) ||"телевизор отцу".equals(namesArr[i]) || "телевизор отцом".equals(namesArr[i]) || "телевизор Ассоль".equals(namesArr[i]) || "телевизор акцент".equals(namesArr[i]) || "телевизор осел".equals(namesArr[i])) {
//                if (keyWord) {
                    Log.e("COMMAND", "TV ON");
                    tv_power(getApplicationContext());
                    Toast.makeText(getApplicationContext(), "Telewizor açyl", Toast.LENGTH_SHORT).show();
                    keyWord = false;
//                }
                break;
            } else if ("телевизор Apple".equals(namesArr[i]) || "телевизор я понял".equals(namesArr[i]) || "телевизор я пон".equals(namesArr[i]) || "телевизор я пол".equals(namesArr[i]) || "телевизоры оптом".equals(namesArr[i]) || "телевизор Йорк".equals(namesArr[i]) || "телевизоры Apple".equals(namesArr[i]) || "телевизоры apple".equals(namesArr[i]) || "телевизор apple".equals(namesArr[i]) || "телевизор я поля".equals(namesArr[i]) || "телевизоры опа".equals(namesArr[i]) || "телевизор опа".equals(namesArr[i]) || "телевизоры афон".equals(namesArr[i])) {
//                if (keyWord) {
                    Log.e("COMMAND", "TV OFF");
                    tv_power(getApplicationContext());
                    Toast.makeText(getApplicationContext(), "Telewizor ýapyl", Toast.LENGTH_SHORT).show();
                    keyWord = false;
//                }
                break;
            } else if ("жалюз Осин".equals(namesArr[i]) || "жаль Уотсон".equals(namesArr[i]) || "Жаль очень".equals(namesArr[i]) || "шале-отель".equals(namesArr[i]) || "жалюзи открыл".equals(namesArr[i]) || "жаль у вас".equals(namesArr[i]) || "залез отце".equals(namesArr[i]) || "жаль у сапсан".equals(namesArr[i]) || "жаль у отца".equals(namesArr[i]) || "жаль усачев".equals(namesArr[i]) || "жаль уотсон".equals(namesArr[i]) || "зовет отцом".equals(namesArr[i]) || "занят отцом".equals(namesArr[i]) || "жалюзи ацил".equals(namesArr[i]) || "жалюз ацил".equals(namesArr[i]) || "жалюзи отшил".equals(namesArr[i]) || "жалюзи отель".equals(namesArr[i]) || "жалюзи Аксон".equals(namesArr[i]) || "жалюз Accent".equals(namesArr[i]) || "жалюзи отцом".equals(namesArr[i]) || "жалюз отшил".equals(namesArr[i])) {
//                if (keyWord) {
                    Log.e("COMMAND", "CURTAIN OPEN");

                    Toast.makeText(getApplicationContext(), "Žalýuz açyl", Toast.LENGTH_SHORT).show();
                    curtain_up(getApplicationContext());
                    try {
                        Thread.sleep(3000);
                        curtain_stop(getApplicationContext());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    keyWord = false;
//                }
                break;
            } else if ("жаль я понял".equals(namesArr[i]) || "жалюзи пон".equals(namesArr[i]) || "жалюзи опан".equals(namesArr[i]) || "жаль а я пон".equals(namesArr[i]) || "жалюзи я пон".equals(namesArr[i]) || "жалюзи опал".equals(namesArr[i]) || "жалюзи apple".equals(namesArr[i]) || "жалюзи афон".equals(namesArr[i]) || "жалюзи а по".equals(namesArr[i]) || "салют я пол".equals(namesArr[i]) || "жалюзи а пол".equals(namesArr[i]) || "жалюзи Apple".equals(namesArr[i]) || "жалюзи пол".equals(namesArr[i]) || "жалюзи АПЛ".equals(namesArr[i]) || "жалюз Apple".equals(namesArr[i]) || "жалюзи Якутск".equals(namesArr[i])) {
//                if (keyWord) {
                    Log.e("COMMAND", "CURTAIN CLOSE");

                    Toast.makeText(getApplicationContext(), "Žalýuz ýapyl", Toast.LENGTH_SHORT).show();
                    curtain_down(getApplicationContext());
                    try {
                        Thread.sleep(3000);
                        curtain_stop(getApplicationContext());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    keyWord = false;
//                }
                break;
            }

            // akylly oy
            else if ("андрей".equals(namesArr[i]) || "а колей".equals(namesArr[i]) || "олень".equals(namesArr[i]) || "акула".equals(namesArr[i]) || "оконный".equals(namesArr[i]) || "a hyundai".equals(namesArr[i]) || "а когда".equals(namesArr[i]) || "оля".equals(namesArr[i]) || "акулы".equals(namesArr[i])) {
//                if (mainKeyWord) {
                    Log.e("COMMAND", "MAIN KEY");
                    Toast.makeText(getApplicationContext(), "Akylly öý", Toast.LENGTH_SHORT).show();
                    mainKeyWord = true;
//                }
                break;
            }
        }

        ///////////////////////////////////
        speech.startListening(recognizerIntent);
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.i(LOG_TAG, "FAILED " + errorMessage);
//        returnedError.setText(errorMessage);

        // rest voice recogniser
        resetSpeechRecognizer();
        speech.startListening(recognizerIntent);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        //Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);
    }

    public String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
}