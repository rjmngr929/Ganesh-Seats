package com.my.raido.Validations

import android.content.Context
import com.my.ganeshseats.R

class EmptyTextRule(context: Context,
    override val errorMessage: String =  context.getString(R.string.emptyTextField)
): ValidationRule(predicate = {it.isNullOrEmpty() })
