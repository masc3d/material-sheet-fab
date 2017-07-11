package sx.android

import android.view.inputmethod.InputMethodManager

// InputMethodManager extensions

fun InputMethodManager.hideSoftInput() {
    this.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
}