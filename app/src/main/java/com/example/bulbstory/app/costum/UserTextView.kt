package com.example.bulbstory.app.costum

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.bulbstory.app.R

class UserTextView : AppCompatEditText {
    private lateinit var passwordIconDrawable: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        passwordIconDrawable =
            ContextCompat.getDrawable(context, R.drawable.icon_person) as Drawable
        inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        compoundDrawablePadding = 16

        setHint(R.string.hintUser)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setAutofillHints(AUTOFILL_HINT_NAME)
        }

        setDrawable(passwordIconDrawable)
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Password validation
                // Display error automatically if the password doesn't meet certain criteria
                if (!s.isNullOrEmpty() && s.length < 3)
                    error = context.getString(R.string.errorUser)
            }
        })
    }

    private fun setDrawable(
        start: Drawable? = null,
        top: Drawable? = null,
        end: Drawable? = null,
        bottom: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom)
    }
}