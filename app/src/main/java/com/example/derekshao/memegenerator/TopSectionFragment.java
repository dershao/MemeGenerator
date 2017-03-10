package com.example.derekshao.memegenerator;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;


public class TopSectionFragment extends Fragment {

    private EditText topTextInput;
    private EditText bottomTextInput;

    TopSectionListener activityCommander;

    private static String TAG = "derekishere";

    //create interface to ensure MainActivity contains createMeme method
    public interface TopSectionListener {
        void createMeme(String top, String bottom);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;

        if (context instanceof Activity) {
            activity = (Activity) context;

            try {
                activityCommander = (TopSectionListener) activity;
            }
            catch (ClassCastException e) {
                throw new ClassCastException(activity.toString());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.top_section_fragment, container, false);
        Log.v(TAG, "Are you here?");
        topTextInput = (EditText)view.findViewById(R.id.topTextInput);
        bottomTextInput = (EditText)view.findViewById(R.id.bottomTextInput);
        final Button button = (Button) view.findViewById(R.id.memeButton);

        //listen for button clicked to use createMeme method
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.v(TAG, "Did I reach you?");
                        buttonClicked(view);
                    }
                }
        );

        return view;
    }

    //uses override method createMeme in MainActivity
    public void buttonClicked(View view) {
        Log.v(TAG, "yes");
        Log.v(TAG, topTextInput.getText().toString());
        Log.v(TAG, bottomTextInput.getText().toString());
        activityCommander.createMeme(topTextInput.getText().toString(), bottomTextInput.getText().toString());
    }
}
