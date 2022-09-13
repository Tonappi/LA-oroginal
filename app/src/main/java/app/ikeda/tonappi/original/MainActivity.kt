package app.ikeda.tonappi.original

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import app.ikeda.tonappi.original.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

//DataPickerDialog.OnDateSetListenerを追加
class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        //レンズ登録ボタンを押したとき
        binding.lensAddButton.setOnClickListener {
            MaterialAlertDialogBuilder(this)
            .setTitle("レンズの種類")
            .setSingleChoiceItems(arrayOf("ソフト(2週間)", "ハード(1ヶ月)"), 0) { dialog, which ->

            }
            .setPositiveButton("はい") {dialog, which ->

            }
            .setNegativeButton("キャンセル", null)
            .show() }

        //ケース登録ボタンを押したとき
        binding.caseAddButton.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("ケースの種類")
                .setSingleChoiceItems(arrayOf("ソフト用(2ヶ月)", "ハード用(6ヶ月)"), 0) { dialog, which ->

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

    fun showDatePickerDialog(v: View) {
        val newFragment = DatePicker()
        newFragment.show(supportFragmentManager, "datePicker")
    }


}