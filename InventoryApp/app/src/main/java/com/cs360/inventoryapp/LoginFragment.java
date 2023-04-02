package com.cs360.inventoryapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class LoginFragment extends Fragment {
    private EditText mEditTextUsername;
    private EditText mEditTextPassword;

    private Button mButtonSubmit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mEditTextUsername = view.findViewById(R.id.edittext_login_username);
        mEditTextPassword = view.findViewById(R.id.edittext_login_password);
        mButtonSubmit = view.findViewById(R.id.button_login_submit);

        mButtonSubmit.setOnClickListener(v -> {
            String username = String.valueOf(mEditTextUsername.getText());
            String password = String.valueOf(mEditTextPassword.getText());

            // FIXME: Check username and password

            Navigation.findNavController(view).navigate(R.id.list_fragment);
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mButtonSubmit.setOnClickListener(null);

        mEditTextPassword = null;
        mEditTextUsername = null;
        mButtonSubmit = null;
    }
}