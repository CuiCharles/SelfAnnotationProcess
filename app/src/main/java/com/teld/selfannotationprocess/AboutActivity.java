package com.teld.selfannotationprocess;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.annotation.Route;


@Route(url = "/")
public class AboutActivity extends Activity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
}
