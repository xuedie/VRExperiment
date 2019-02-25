package ca.yorku.cse.mack.fittstouch;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

@SuppressWarnings("unused")
public class FittsTouchSetup extends Activity
{
    /*
     * The following arrays are used for the spinners in the setup dialog. The first entry is the
     * default value. It will be replaced from the app's shared preferences if a corresponding entry
     * was saved in a previous invocation.
     */
    String[] participantCode = {"P99", "P01", "P02", "P03", "P04", "P05", "P06", "P07", "P08",
            "P09", "P10", "P11", "P12", "P13", "P14", "P15", "P16", "P17", "P18", "P19", "P20",
            "P21", "P22", "P23", "P24", "P25", "P26", "P27", "P28", "P29", "P30"};
    String[] sessionCode = {"S99", "S01", "S02", "S03", "S04", "S05", "S06", "S07", "S08", "S09",
            "S10", "S11", "S12", "S13", "S14", "S15", "S16", "S17", "S18", "S19", "S20", "S21",
            "S22", "S23", "S24", "S25"};
    String[] blockCode = {"(auto)"}; // effectively disable spinner for block code
    String[] groupCode = {"G99", "G01", "G02", "G03", "G04", "G05", "G06", "G07", "G08", "G09",
            "G10", "G11", "G12", "G13", "G14", "G15", "G16", "G17", "G18", "G19", "G20", "G21",
            "G22", "G23", "G24", "G25"};
    String[] conditionCode = {"C99", "C01", "C02", "C03", "C04", "C05", "C06", "C07", "C08",
            "C09", "C10", "C11", "C12", "C13", "C14", "C15", "C16", "C17", "C18", "C19", "C20",
            "C21", "C22", "C23", "C24", "C25"};
    String moveMode = "Tap";
    String[] numberOfSessions = {"4", "1", "2", "3", "5", "6", "7", "8", "9", "10", "11", "12", "13",
            "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27",
            "28", "29", "30"};
    String[] numberOfTasks = {"4", "2", "6", "8", "10"};
    String[] numberOfTargetColumns = {"4", "1", "2", "3"};
    String[] numberOfTargetRows = {"3", "1", "2", "4"};
    String[] tapCombination = {
            "1:Right Index, Left Index, Both Thumbs",
            "2:Left Index, Both Thumbs, Right Index",
            "3:Both Thumbs, Right Index, Left Index"};
    String[] zoomCombination = {
            "1:Index+Middle, Index+Thumb, Both Thumbs, Both Index",
            "2:Index+Thumb, Index+Middle, Both Index, Both Thumbs",
            "3:Both Thumbs, Both Index, Index+Middle, Index+Thumb ",
            "4:Both Index, Both Thumbs, Index+Thumb, Index+Middle"};
    String[] amplitudes = {"120, 240, 480", "120, 240, 480", "250, 500", "500"};
    String[] widths = {"60, 100", "60, 100", "30, 60, 120", "100"};
    boolean vibrotactileFeedback = false;
    boolean auditoryFeedback = true;
    int screenOrientation;
    SharedPreferences sp;
    SharedPreferences.Editor spe;
    private Spinner spinParticipant, spinSession, spinGroup, spinCondition;
    private RadioButton radioButtonMode1D, radioButtonModeZoom, radioButtonModeScroll;
    private Spinner spinNumTrials, spinNumTasks;
    private Spinner spinNumTargetColumn, spinNumTargetRow;
    private CheckBox checkVibrotactileFeedback;
    private CheckBox checkAuditoryFeedback;
    private TableRow rowTasks, rowTrialText, rowTrial;
    private ArrayAdapter<CharSequence> adapterCT,adapterCZ;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(decorView.SYSTEM_UI_FLAG_IMMERSIVE | decorView.SYSTEM_UI_FLAG_FULLSCREEN
                | decorView.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        /*
         * Initialize reference to shared preferences. NOTE: The values saved are the default
         * values.
         */
        sp = this.getPreferences(MODE_PRIVATE);

        /*
         * Overwrite 1st entry from shared preferences, if corresponding value exits.
         */
        participantCode[0] = sp.getString("participantCode", participantCode[0]);
        sessionCode[0] = sp.getString("sessionCode", sessionCode[0]);
        // block code initialized in main activity (based on existing filenames)
        groupCode[0] = sp.getString("groupCode", groupCode[0]);
        conditionCode[0] = sp.getString("conditionCode", conditionCode[0]);
        //dimensionMode[0] = sp.getString("dimensionMode", dimensionMode[0]);
        moveMode = sp.getString("moveMode", moveMode);
        numberOfSessions[0] = sp.getString("numberOfSessions", numberOfSessions[0]);
        numberOfTargetColumns[0] = sp.getString("numberOfTargetColumns", numberOfTargetColumns[0]);
        numberOfTargetRows[0] = sp.getString("numberOfTargetRows", numberOfTargetRows[0]);
        amplitudes[0] = sp.getString("amplitudes", amplitudes[0]);
        widths[0] = sp.getString("widths", widths[0]);
        vibrotactileFeedback = sp.getBoolean("vibrotactileFeedback", false);
        auditoryFeedback = sp.getBoolean("auditoryFeedback", true);

        // get references to widget elements
        spinParticipant = (Spinner)findViewById(R.id.paramPart);
        spinSession = (Spinner)findViewById(R.id.paramSess);
        Spinner spinBlock = (Spinner)findViewById(R.id.paramBlock);
        spinGroup = (Spinner)findViewById(R.id.paramGroup);
        spinCondition = (Spinner)findViewById(R.id.paramCondition);
        //spinMode = (Spinner) findViewById(R.id.paramMode);
        radioButtonMode1D = (RadioButton)findViewById(R.id.paramMode1D);
        radioButtonModeZoom = (RadioButton)findViewById(R.id.paraModeZoom);
        radioButtonModeScroll = (RadioButton)findViewById(R.id.paraModeScroll);
        spinNumTrials = (Spinner)findViewById(R.id.paramTrials);
        spinNumTasks = (Spinner)findViewById(R.id.paramTasks);
        spinNumTargetColumn = (Spinner)findViewById(R.id.paramTargetColumn);
        spinNumTargetRow = (Spinner)findViewById(R.id.paramTargetRow);
        checkVibrotactileFeedback = (CheckBox)findViewById(R.id.paramVibrotactileFeedback);
        checkAuditoryFeedback = (CheckBox)findViewById(R.id.paramAuditoryFeedback);
        rowTasks = (TableRow)findViewById(R.id.rowTaskNum);
        rowTrialText = (TableRow)findViewById(R.id.rowTargetNumText);
        rowTrial = (TableRow)findViewById(R.id.rowTargetNum);

        // initialise spinner adapters
        ArrayAdapter<CharSequence> adapterPC = new ArrayAdapter<CharSequence>(this, R.layout
                .spinnerstyle,
                participantCode);
        spinParticipant.setAdapter(adapterPC);

        ArrayAdapter<CharSequence> adapterSS = new ArrayAdapter<CharSequence>(this, R.layout
                .spinnerstyle, sessionCode);
        spinSession.setAdapter(adapterSS);

        ArrayAdapter<CharSequence> adapterB = new ArrayAdapter<CharSequence>(this, R.layout
                .spinnerstyle, blockCode);
        spinBlock.setAdapter(adapterB);

        ArrayAdapter<CharSequence> adapterG = new ArrayAdapter<CharSequence>(this, R.layout
                .spinnerstyle, groupCode);
        spinGroup.setAdapter(adapterG);

        adapterCT = new ArrayAdapter<CharSequence>(this, R.layout
                .spinnerstyle, tapCombination);
        adapterCZ = new ArrayAdapter<CharSequence>(this, R.layout
                .spinnerstyle, zoomCombination);
        spinCondition.setAdapter(adapterCT);

        //ArrayAdapter<CharSequence> adapterM = new ArrayAdapter<CharSequence>(this, R.layout
        //       .spinnerstyle, dimensionMode);
        //spinMode.setAdapter(adapterM);

        ArrayAdapter<CharSequence> adapterTR = new ArrayAdapter<CharSequence>(this, R.layout
                .spinnerstyle,
                numberOfSessions);
        spinNumTrials.setAdapter(adapterTR);

        ArrayAdapter<CharSequence> adapterT = new ArrayAdapter<CharSequence>(this, R.layout
                .spinnerstyle,
                numberOfTasks);
        spinNumTasks.setAdapter(adapterT);

        ArrayAdapter<CharSequence> adapterTC = new ArrayAdapter<CharSequence>(this, R.layout
                .spinnerstyle,
                numberOfTargetColumns);
        spinNumTargetColumn.setAdapter(adapterTC);

        ArrayAdapter<CharSequence> adapterTRS = new ArrayAdapter<CharSequence>(this, R.layout
                .spinnerstyle,
                numberOfTargetRows);
        spinNumTargetRow.setAdapter(adapterTRS);

        if (moveMode.equals("Tap")) {
            radioButtonMode1D.toggle();
            rowTasks.setVisibility(View.GONE);
        }
        else {
            radioButtonModeZoom.toggle();
            rowTrialText.setVisibility(View.GONE);
            rowTrial.setVisibility(View.GONE);
        }

        checkVibrotactileFeedback.setChecked(vibrotactileFeedback);
        checkAuditoryFeedback.setChecked(auditoryFeedback);

        /*
         * Determine if the device is naturally portrait or landscape. This is passed on to the
         * activity in the variable screenOrientation.
         */
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        /*
         * Get the default screen orientation. Change, if necessary (see below).
         */
        screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        switch (display.getRotation())
        {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                if (width > height)
                    screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            case Surface.ROTATION_270:
            case Surface.ROTATION_90:
                if (width < height)
                    screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
        }
    }

    /**
     * Called when the "OK" button is pressed.
     */
    public void clickOK(View view)
    {
        // get user's choices
        String part = participantCode[spinParticipant.getSelectedItemPosition()];
        String sess = sessionCode[spinSession.getSelectedItemPosition()];
        // String block = blockCode[spinBlock.getSelectedItemPosition()];
        String group = groupCode[spinGroup.getSelectedItemPosition()];
        String mode = moveMode;
        int condition = spinCondition.getSelectedItemPosition();
        int numTrials = Integer.parseInt(numberOfSessions[spinNumTrials.getSelectedItemPosition()]);
        int numTargetColumns = Integer.parseInt(numberOfTargetColumns[spinNumTargetColumn
                .getSelectedItemPosition()]);
        int numTargetRows = Integer.parseInt(numberOfTargetRows[spinNumTargetRow
                .getSelectedItemPosition()]);
        int numberTasks = Integer.parseInt(numberOfTasks[spinNumTasks.getSelectedItemPosition()]);
        boolean vibrotactileFeedback = checkVibrotactileFeedback.isChecked();
        boolean auditoryFeedback = checkAuditoryFeedback.isChecked();

        /*
         * NOTE: Activities are created by calling zero-parameter constructors. It is not possible
         * to directly pass arguments to Activities. Instead, primitive values can be "bundled" and
         * passed.
         */
        Bundle b = new Bundle();
        b.putString("participantCode", part);
        b.putString("sessionCode", sess);
        // b.putString("blockCode", block);
        b.putString("groupCode", group);
        b.putInt("conditionCode", condition);
        b.putString("mode", mode);
        b.putInt("numberOfTrials", numTrials);
        b.putInt("numberOfTasks", numberTasks);
        b.putInt("numberOfTargetColumns", numTargetColumns);
        b.putInt("numberOfTargetRows", numTargetRows);
        b.putBoolean("vibrotactileFeedback", vibrotactileFeedback);
        b.putBoolean("auditoryFeedback", auditoryFeedback);
        b.putInt("screenOrientation", screenOrientation);

        // start experiment activity
        Intent i;
        if (moveMode.equals("Tap")) {
            i = new Intent(getApplicationContext(), FittsTouchActivity.class);
        } else if (moveMode.equals("Zoom")){
            i = new Intent(getApplicationContext(), ZoomActivity.class);
        } else if (moveMode.equals("Scroll")){
            i = new Intent(getApplicationContext(), ScrollActivity.class);
        } else {
            i = new Intent(getApplicationContext(), CameraActivity.class);
        }
        i.putExtras(b);
        startActivity(i);
        finish();
    }

    // called when the "Save" button is pressed
    public void clickSave(View view)
    {
        spe = sp.edit();
        spe.putString("participantCode", participantCode[spinParticipant.getSelectedItemPosition()]);
        spe.putString("sessionCode", sessionCode[spinSession.getSelectedItemPosition()]);
        spe.putString("groupCode", groupCode[spinGroup.getSelectedItemPosition()]);
        spe.putString("conditionCode", conditionCode[spinCondition.getSelectedItemPosition()]);
        spe.putString("moveMode", moveMode);
        spe.putString("numberOfTrials", numberOfSessions[spinNumTrials.getSelectedItemPosition()]);
        spe.putString("numberOfTargetColumns", numberOfTargetColumns[spinNumTargetColumn.getSelectedItemPosition()]);
        spe.putString("numberOfTargetRows", numberOfTargetRows[spinNumTargetRow.getSelectedItemPosition()]);
        spe.putBoolean("vibrotactileFeedback", checkVibrotactileFeedback.isChecked());
        spe.putBoolean("auditoryFeedback", checkAuditoryFeedback.isChecked());
        spe.apply();
        Toast.makeText(this, "Preferences saved!", Toast.LENGTH_SHORT).show();
    }

    // called when the "Exit" button is pressed
    public void clickExit(View view)
    {
        super.onDestroy(); // cleanup
        this.finish(); // terminate
    }

    // called when either the 1D or 2D radio button is clicked
    public void radioButtonClick(View v)
    {
        if (v == radioButtonMode1D) {
            moveMode = "Tap";
            spinCondition.setAdapter(adapterCT);
            rowTasks.setVisibility(View.GONE);
            rowTrialText.setVisibility(View.VISIBLE);
            rowTrial.setVisibility(View.VISIBLE);
        }
        else if (v == radioButtonModeZoom){
            moveMode = "Zoom";
            spinCondition.setAdapter(adapterCZ);
            rowTasks.setVisibility(View.VISIBLE);
            rowTrialText.setVisibility(View.GONE);
            rowTrial.setVisibility(View.GONE);
        }
        else if (v == radioButtonModeScroll){
            moveMode = "Scroll";
            rowTasks.setVisibility(View.GONE);
            rowTrialText.setVisibility(View.VISIBLE);
            rowTrial.setVisibility(View.VISIBLE);
        }
        else {
            moveMode = "Camera";
            rowTasks.setVisibility(View.GONE);
            rowTrialText.setVisibility(View.VISIBLE);
            rowTrial.setVisibility(View.VISIBLE);
        }
    }
}
