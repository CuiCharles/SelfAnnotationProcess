package com.teld.selfannotationprocess;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.annotation.Route;


@Route(url = "/MainActivity")
public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}
}