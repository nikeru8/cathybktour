package com.daniel.cathybktour.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.daniel.cathybktour.view.main.MainActivityViewModel

class ViewModelFactory : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        when {

            modelClass.isAssignableFrom(MainActivityViewModel::class.java) -> {

                return MainActivityViewModel() as T

            }


            else -> throw IllegalArgumentException("Unknown ViewModel class")

        }

    }

}