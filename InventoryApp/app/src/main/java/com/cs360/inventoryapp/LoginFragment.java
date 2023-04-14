package com.cs360.inventoryapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginFragment extends Fragment {
    private EditText mEditTextUsername;
    private EditText mEditTextPassword;
    private Button mButtonSubmit;
    private Button mButtonCreateUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mEditTextUsername = view.findViewById(R.id.edittext_login_username);
        mEditTextPassword = view.findViewById(R.id.edittext_login_password);
        mButtonSubmit = view.findViewById(R.id.button_login_submit);
        mButtonCreateUser = view.findViewById(R.id.button_create_account_submit);

        mButtonSubmit.setOnClickListener(v -> {
            final String username = String.valueOf(mEditTextUsername.getText());
            final String password = String.valueOf(mEditTextPassword.getText());

            ItemDatabase db = new ItemDatabase(getContext());

            User user = db.getUser(username);

            if (user != null) {
                String hashedPassword = user.getHashedPassword();

                if (AuthenicationService.verifyPassword(password, hashedPassword)) {
                    SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);
                    editor.apply();

                    String toastMessage = "Welcome " + username;
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getContext(), toastMessage, duration);
                    toast.show();

                    Navigation.findNavController(view).navigate(R.id.list_fragment);
                }
                else {
                    String toastMessage = "Invalid login credentials";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getContext(), toastMessage, duration);
                    toast.show();
                }
            }
            else {
                String toastMessage = "No user found";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getContext(), toastMessage, duration);
                toast.show();
            }
        });

        mButtonCreateUser.setOnClickListener(v -> {
            final String username = String.valueOf(mEditTextUsername.getText());
            final String password = String.valueOf(mEditTextPassword.getText());

            if (!username.isEmpty() && !password.isEmpty()) {
                if (AuthenicationService.isUsernameAvailable(getContext(), username)) {
                    ItemDatabase db = new ItemDatabase(getContext());

                    long id = db.addUser(username, password);
                    db.close();

                    SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);
                    editor.apply();

                    String toastMessage = "Username created with id " + id;
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getContext(), toastMessage, duration);
                    toast.show();

                    Navigation.findNavController(view).navigate(R.id.list_fragment);
                }
                else {
                    String toastMessage = "Username already exists";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getContext(), toastMessage, duration);
                    toast.show();
                }
            }
            else {
                String toastMessage = "Please enter a valid username and password";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getContext(), toastMessage, duration);
                toast.show();
            }
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