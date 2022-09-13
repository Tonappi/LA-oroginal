package app.ikeda.tonappi.original

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [DatePicker.newInstance] factory method to
 * create an instance of this fragment.
 */
class DatePicker : DialogFragment(), DatePickerDialog.OnDateSetListener {
    
    override fun onCreateDialog(savedInstanceState: Bundle?):Dialog {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        //ActivityにDatePickerDialog.OnDateSetListenerがあればActivityに設定されている
        //onDateSet()を呼び出す
        return DatePickerDialog(
            this.context as Context,
            activity as MainActivity?,
            year,
            month,
            day)
        }

    override fun onDateSet(p0: android.widget.DatePicker, year: Int,
                           monthOfYear: Int, dayOfMonth: Int) {

    }
}





