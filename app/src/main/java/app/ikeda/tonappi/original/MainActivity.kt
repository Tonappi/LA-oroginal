package app.ikeda.tonappi.original

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import app.ikeda.tonappi.original.databinding.ActivityMainBinding
import java.util.*

//DataPickerDialog.OnDateSetListenerを追加
class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        /*/レンズ登録ボタンを押したとき
        binding.lensAddButton.setOnClickListener {
            val lensList = arrayOf("ソフト(2週間)", "ハード(1ヶ月)")
            MaterialAlertDialogBuilder(this)
            .setTitle("レンズの種類")
            .setSingleChoiceItems(lensList, 0) { dialog, which ->
                //選択されたラジオボタンの値を取得する
                val kindOfLens: String= "ソフト(2週間)"
            }
            .setPositiveButton("はい") { dialog, which ->
                //選択されたラジオボタンによって、変数casePeriodの値を変える
                if ( kindOfLens = "ソフト(2週間)"){
                    val LensPeriod :Int = 13
                }else{
                    val LensPeriod :Int = 30
            }
            .setNegativeButton("キャンセル", null)
            .show() }

        //ケース登録ボタンを押したとき
        binding.caseAddButton.setOnClickListener {
            val caseList = arrayOf("ソフト用(2ヶ月)", "ハード用(6ヶ月)")
            MaterialAlertDialogBuilder(this)
                .setTitle("ケースの種類")
                .setSingleChoiceItems(caseList, 0) { dialog, which ->
                    //選択されたラジオボタンによって、変数casePeriodの値を変える
                    if ( kindOfLens = "ソフト(2週間)"){
                        val LensPeriod :Int = 13
                    }else{
                        val LensPeriod :Int = 30}
                }
                .setPositiveButton("はい") {dialog, which ->
                    // 値を保存する
                    val kindsOfCase:String =
                }
                .setNegativeButton("キャンセル", null)
                .show() }*/



        //レンズ交換日を取得
        val lensEndyear = 2022
        var lensEndmonth = 9
        val lensEnddate = 15

        /*/ケース交換日を取得
        val caseEndyear =
        val caseEndmonth
        val caseEnddate*/

        //現在の日時を取得
        val rightNow = Calendar.getInstance()
        Log.d("現在の日時", rightNow.toString())
        lensEndmonth = lensEndmonth-1
        rightNow.set(lensEndyear, lensEndmonth, lensEnddate);
        // 1970/1/1 から設定した rightNow のミリ秒
        val timeMillis1: Long = rightNow.getTimeInMillis()
        // 現在時刻のミリ秒
        val currentTimeMillis = System.currentTimeMillis()

        //レンズ交換までの日数を計算
        // 差分のミリ秒
        var diff = timeMillis1 - currentTimeMillis
        // ミリ秒から秒へ変換
        diff = diff / 1000
        // minutes
        diff = diff / 60
        // hour
        diff = diff / 60
        // day
        diff = diff / 24
        //レンズ交換までの日数を表示
         val lensCountdown: String = "$diff 日"
        //ケース交換までの日数を計算
        binding.lensDayleftView.text = lensCountdown


    }

    //DatePickerDialogを呼び出すメソッド
    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        //レンズの使用開始日を表示
            val lensStartDate: String = getString(R.string.lensstringformat, year, monthOfYear+1, dayOfMonth)
            binding.lensPeriodView.text = "$lensStartDate~"
        //日付を取得

    }

    private fun showDatePickerDialog(v: View) {
        val newFragment = DatePicker()
        newFragment.show(supportFragmentManager, "datePicker")
    }

    fun onDataset(view: View) {}

}


