package com.rexitagator.rexitagator;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    public EditText txtTexto;
    public TextView lblTextoFinal;
    public CardView cardViewResultado;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        txtTexto = (EditText)findViewById(R.id.txtTexto);
        lblTextoFinal = (TextView)findViewById(R.id.lblTextoFinal);
        cardViewResultado = (CardView)findViewById(R.id.card_view2);
    }

    public void onClickRexigator(View view)
    {

        lblTextoFinal.setText(geraTexto((txtTexto.getText().toString())));
    }

    public void copiarTexto (View view)
    {

        copiar();

    }

    public void compartilharTexto (View view)
    {

        share();

    }

    private void share()
    {

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, lblTextoFinal.getText().toString());
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.compartilheAgora)));

    }

    private String geraTexto (String texto)
    {

        String []textos = texto.split(" ");
        String textoFinal = "";
        for (int i = 0; i < textos.length; i++)
        {
            textos[i] = textos[i].trim();
            if (textos[i].length() > 1 || textos[i].equalsIgnoreCase("é"))
            textos[i] = "#"+(textos[i].substring(0,1).toUpperCase() + textos[i].substring(1));
            textoFinal += textos[i] + " ";
        }

        if (textoFinal.length() <= 1)
            textoFinal = getString(R.string.texto_vazio);

        Animation animacao = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        cardViewResultado.startAnimation(animacao);
        cardViewResultado.setVisibility(View.VISIBLE);

        return  textoFinal;

    }

    public void copiar()
    {

        android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                MainActivity.this.getSystemService(MainActivity.this.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData
                .newPlainText(
                        "Copiado", lblTextoFinal.getText().toString());
        clipboard.setPrimaryClip(clip);

    }

    public void falarTexto (View v)
    {

        promptSpeechInput();

    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.digaAlgo));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.naoEntendi),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtTexto.setText(result.get(0));
                    lblTextoFinal.setText(geraTexto((txtTexto.getText().toString())));
                }
                break;
            }

        }
    }

}
