package com.example.nakhlefransson.tipcalcapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.concurrent.TimeUnit;


public class TipCalcActivity extends Activity implements Chronometer.OnChronometerTickListener{
    //Konstanter som används när du sparar och återställa//
    //Sparar informationen om nån stänger ner appen och kommer tillbaka till den//
    private static final String TOTAL_BILL = "TOTAL_BILL";
    private static final String CURRENT_TIP = "CURRENT_TIP";
    private static final String BILL_WITHOUT_TIP = "BILL_WITHOUT_TIP";

    private double billBeforeTip;//ANVÄNDARENS NOTAN INNAN DRICKSEN
    private double tipAmount; //  NOTAN
    private double finalBill; //NOTAN INKLUSIVE DRICKSEN


    //SPARAR OCH LÄGGER TILL NYA VALUES//
    //ET STÅR FÖR EDIT TEXT//
    EditText billBeforeTipET;
    EditText tipAmountET;
    EditText finalBillET;

    private int[] checklistValues = new int[12];

    SeekBar tipSeekBar;

    //DEKLARERAR CHECKBOXARNA//
    CheckBox friendlyCheckBox;
    CheckBox specialsCheckBox;
    CheckBox opinionCheckBox;

    //DEKLARERAR RADIOBUTTONS//
    RadioButton availableBadRadio;
    RadioButton availableOkRadio;
    RadioButton availableGoodRadio;

    //DEKLARERAR
    Spinner problemsSpinner;

    Button startChronometerButton;
    Button pauseChronometerButton;
    Button resetChronometerButton;

    Chronometer timeWaitingChronometer;

    long secondsYouWaited = 0;

    TextView timeWaitingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_calc);

        //KOLLAR OM APPEN HAR PRECIS STARTAT ELLER STARTATS OM OCH SÄTTER DE URSPRUNGLIGA VÄRERNA//
        if (savedInstanceState == null) { //OM DEN PRECIS HAR STARTAT//
            billBeforeTip = 0.0;//SÄTTER URSPRUNGLIGA VÄRDERNA//
            tipAmount = .15;
            finalBill = 0.0;
            //ELLER HÄMTA SÄTT DE INFORMATIONEN SOM HAR SPARATS//

        } else {
            billBeforeTip = savedInstanceState.getDouble(BILL_WITHOUT_TIP);
            tipAmount = savedInstanceState.getDouble(CURRENT_TIP);
            finalBill = savedInstanceState.getDouble(TOTAL_BILL);
        }

        //INTIELLRAR TEXT BOXARNA//
        billBeforeTipET = (EditText) findViewById(R.id.billEditText);
        tipAmountET = (EditText) findViewById(R.id.tipEditText);
        finalBillET = (EditText) findViewById(R.id.finalBillEditText);

        //INITIALISERA EN SEEKBAR OCH LÄGGER TILL EN CHANGELISTERNER//
        tipSeekBar = (SeekBar) findViewById(R.id.changeTipSeekBar);
        tipSeekBar.setOnSeekBarChangeListener(tipSeekBarListener);

        //LÄGGER TILL EN CHANGELISTERNER FÖR NÄR NOTAN INNAN DRICKSEN ÄR ÄNDRAD//
        billBeforeTipET.addTextChangedListener(billBeforeTipListener);

        //INITIALISERA CHEKBOXARNA//
        friendlyCheckBox = (CheckBox) findViewById(R.id.friendlyCheckBox);
        specialsCheckBox = (CheckBox) findViewById(R.id.specialsCheckBox);
        opinionCheckBox = (CheckBox) findViewById(R.id.opinionCheckBox);

        setUpIntroCheckBoxes(); //

        //INITIALISERA RADIOBUTTONS//
        availableBadRadio = (RadioButton) findViewById(R.id.badRadioButton);//
        availableOkRadio = (RadioButton) findViewById(R.id.okRadioButton);//
        availableGoodRadio = (RadioButton) findViewById(R.id.goodRadioButton); //

        //LÄGGER CHANGELISTERNER TILL RADIOBUTTONS//
        addChangeListernerToRadios();

        //INITIALISERA SPINNERN//
        problemsSpinner = (Spinner) findViewById(R.id.problemsSpinner);

        addItemSelectedListernerToSpinner();

        //INITIALISERA KNAPPARNA//
        startChronometerButton = (Button) findViewById(R.id.startChronometerButton);
        pauseChronometerButton = (Button) findViewById(R.id.pauseChronometerButton);
        resetChronometerButton = (Button) findViewById(R.id.resetChronometerButton);

        //SÄTTER ONCLICKLISTERNERS FÖR KNAPPARNA//
        setButtonOnClickListerners();

        //INITIALISERA TIDTAGNINGEN//
        timeWaitingChronometer = (Chronometer) findViewById(R.id.timeWatingChronometer);
        timeWaitingChronometer.setOnChronometerTickListener(this);
        //TEXT VIEW FÖR TIDTAGNINGEN//
        timeWaitingTextView = (TextView) findViewById(R.id.timeWatingTextView);
    }

    //KALLAS NÄR NOTAN INNAN DRICKSEN ÄNDRAS//
    private TextWatcher billBeforeTipListener = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                //ÄNDRAR BELLOPET INNAN DRICKSEN FÖR DEN NYA INMATNINGEN//
                billBeforeTip = Double.parseDouble(s.toString());
            } catch (NumberFormatException e) {
                billBeforeTip = 0.0;
            }
            updateTipAndFinalBill();//UPPDATERAR//
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    //UPPDATERAR TIPAMOUNT OCH LÄGGER TILL DRICKSEN//
    private void updateTipAndFinalBill() {
        //HÄMTAR TIPaMOUNT
        double tipAmount = Double.parseDouble(tipAmountET.getText().toString());
        //HÄMTAR NOTAN INKLUSIVE DRICKSEN
        double finalBill = billBeforeTip + (billBeforeTip * tipAmount);
        //SÄTTER HELA FAKTURAN INKLUSIVE DRICKSEN OCH KONVERTERAR TILL 2 DECIMALER//
        finalBillET.setText(String.format("%.02f", finalBill));
    }

    //UPPDATERAR OM MAN TILL EXEMPEL LÄMNAR APPEN OCH KOMMER TILLBAKA TILL DEN//
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble(TOTAL_BILL, finalBill);
        outState.putDouble(CURRENT_TIP, tipAmount);
        outState.putDouble(BILL_WITHOUT_TIP, billBeforeTip);
    }

    //SEEKBAR GJORD FÖR ATT GÖRA EN ANPASSNINGSBAR DRICKS//
    private SeekBar.OnSeekBarChangeListener tipSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

            //HÄMTAR DEN INSTÄLLDA VÄRDET TILL SEEKBARN//
            tipAmount = (tipSeekBar.getProgress()) * .01;

            //SÄTTER TIPAMMOUNT MED VÄRDET TILL SEEKBAR//
            tipAmountET.setText(String.format("%.02f", tipAmount));

            //UPPDATERAR ALLA EDITTEXT//
            updateTipAndFinalBill();
        };

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private void setUpIntroCheckBoxes() {
        //LÄGGER CHANGELISTERNER TILL FRIENDLYCHECKBOXEN//
        friendlyCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //SÄTTER DEN RÄTTA VÄRDET FÖR VARJE POST FÖR SERVERARENS CHECKBOX LISTASN//
                checklistValues[0] = (friendlyCheckBox.isChecked()) ? 4 : 0;

                //KALKULERAR DRICKSEN MED HJÄLP AV CHECKLISTAN//
                setTipFromWaitressChecklist();
                //UPPDATERAR ALLA ANDRA EDITTEXTER //
                updateTipAndFinalBill();
            }
        });

        //LÄGGER CHANGELISTERNER TILL SPECIAL CHECKBOXEN//
        specialsCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //SÄTTER DEN RÄTTA VÄRDET FÖR VARJE POST FÖR SERVERARENS CHECKBOX LISTAN//
                checklistValues[1] = (specialsCheckBox.isChecked()) ? 1 : 0;

                //KALKULERAR DRICKSEN MED HJÄLP AV CHECKLISTAN//
                setTipFromWaitressChecklist();
                //UPPDATERAR ALLA ANDRA EDITTEXTER //
                updateTipAndFinalBill();
            }
        });

        opinionCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //SÄTTER DEN RÄTTA VÄRDET FÖR VARJE POST FÖR SERVERARENS CHECKBOX LISTASN//
                checklistValues[2] = (opinionCheckBox.isChecked()) ? 2 : 0;

                //KALKULERAR DRICKSEN MED HJÄLP AV CHECKLISTAN//
                setTipFromWaitressChecklist();
                //UPPDATERAR ALLA ANDRA EDITTEXTER //
                updateTipAndFinalBill();
            }
        });
    }

    //KALKULERAR DRICKSEN MED HJÄLP AV CHECKLISTANS ALTERNATIV//
    private void setTipFromWaitressChecklist() {

        int checklistTotal = 0;

        //GÅR IGENOM ALLA CHECKLISTA VÄRDERNA FÖR ATT RÄKNA UT TOTALSUMMA BEROENDE PÅ SERVITÖRENS PRESTANDA//
        for (int item : checklistValues) {

            checklistTotal += item;
        }

        tipAmountET.setText(String.format("%.02f", checklistTotal * .01));
    }


    private void addChangeListernerToRadios() {

        availableBadRadio.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener() {
            @Override
           public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checklistValues[3] = (availableBadRadio.isChecked()) ? -1 : 0;

                setTipFromWaitressChecklist();

                updateTipAndFinalBill();
            }
        });

        availableBadRadio.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checklistValues[4] = (availableOkRadio.isChecked()) ? 2 : 0;

                setTipFromWaitressChecklist();

                updateTipAndFinalBill();
            }
        });

        availableBadRadio.setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checklistValues[5] = (availableGoodRadio.isChecked()) ? 4 : 0;

                setTipFromWaitressChecklist();

                updateTipAndFinalBill();
            }
        });
    }

    private void addItemSelectedListernerToSpinner() {

        problemsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                checklistValues[6] = (String.valueOf(problemsSpinner.getSelectedItem()).equals("Bad")) ? -1 : 0;
                checklistValues[7] = (String.valueOf(problemsSpinner.getSelectedItem()).equals("Ok")) ? 3 : 0;
                checklistValues[8] = (String.valueOf(problemsSpinner.getSelectedItem()).equals("Good")) ? 6 : 0;

                setTipFromWaitressChecklist();
                updateTipAndFinalBill();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });
    }

    private void setButtonOnClickListerners() {

        startChronometerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                int stoppedMilliseconds = 0;

                String chronoText = timeWaitingChronometer.getText().toString();
                String array[] = chronoText.split(":");

                if (array.length == 2) {
                    //räkna sekunderna//
                    stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 1000 +
                            (Integer.parseInt(array[1]) * 1000);
                } else if (array.length == 3) {
                    //räkna minuterna//
                    stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 60 * 1000 +
                            Integer.parseInt(array[1]) * 60 * 1000
                            + Integer.parseInt(array[2]) * 1000;
                }

                timeWaitingChronometer.setBase(SystemClock.elapsedRealtime() - stoppedMilliseconds);

                //SÄTTER DE SEKUNDERNA SOM JAG HAR VÄNTAT PÅ SERVITRISEN ATT KOMMA TILL MITT BORD OCH HJÄLPA MIG//
                secondsYouWaited = Long.parseLong(array[1]);

                //uppdaterar//
                //updateTipBasedOnTimeWaited(secondsYouWaited);
                //STARTA CHRONROMETERN//
                timeWaitingChronometer.start();
            }
        });
        // NÄR MAN KLICKAR PÅ PAUSE KNAPPEN KÖRS DETTA//
        pauseChronometerButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                //DÅ STOPPAS CHRONOMETERN//
                timeWaitingChronometer.stop();

            }
        });

        // NÄR MAN KLICKAR PÅ RESET KNAPPEN KÖRS DETTA//
        resetChronometerButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                //DÅ NOLLAS CHRONOMETERN//
                timeWaitingChronometer.setBase(SystemClock.elapsedRealtime());

                secondsYouWaited = 0;

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tip_calc, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public void onChronometerTick(Chronometer chronometer) {
        //Hämta hur många sek som har gått

        if(TimeUnit.MILLISECONDS.toSeconds(SystemClock.elapsedRealtime()-chronometer.getBase()) == 20){

            double currentTip = Double.parseDouble(tipAmountET.getText().toString());
            double newTip = currentTip - 0.01;
            tipAmountET.setText(String.valueOf(newTip));

            String bill = billBeforeTipET.getText().toString();
            finalBillET.setText(String.valueOf(Double.parseDouble(bill) - newTip));
        }
       }
    }

