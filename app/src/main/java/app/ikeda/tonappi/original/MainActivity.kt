package app.ikeda.tonappi.original

import android.app.DatePickerDialog
import android.app.PendingIntent.getActivity
import android.content.DialogInterface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import app.ikeda.tonappi.original.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.LocalDateTime
import java.util.*

//DataPickerDialog.OnDateSetListenerを追加
class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        //レンズ登録ボタンを押したとき
        binding.lensAddButton.setOnClickListener {
            val lensList = arrayOf("ソフト(2週間)", "ハード(1ヶ月)")
            MaterialAlertDialogBuilder(this)
            .setTitle("レンズの種類")
            .setSingleChoiceItems(lensList, 0) { dialog, which ->

            }
            .setPositiveButton("はい",) { dialog, which ->


            }
            .setNegativeButton("キャンセル", null)
            .show() }

        //ケース登録ボタンを押したとき
        binding.caseAddButton.setOnClickListener {
            val caseList = arrayOf("ソフト用(2ヶ月)", "ハード用(6ヶ月)")
            MaterialAlertDialogBuilder(this)
                .setTitle("ケースの種類")
                .setSingleChoiceItems(caseList, 0) { dialog, which ->

                }
                .setPositiveButton("はい") {dialog, which ->


                }
                .setNegativeButton("キャンセル", null)
                .show() }

    }



    //DatePickerDialogを呼び出すメソッド
    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val lensStartDate: String = getString(R.string.lensstringformat, year, monthOfYear+1, dayOfMonth)
        binding.lensPeriodView.text = "$lensStartDate~"

    }

    private fun showDatePickerDialog(v: View) {
        val newFragment = DatePicker()
        newFragment.show(supportFragmentManager, "datePicker")
    }

}


