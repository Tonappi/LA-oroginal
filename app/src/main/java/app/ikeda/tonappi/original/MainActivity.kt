package app.ikeda.tonappi.original

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import app.ikeda.tonappi.original.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

//DataPickerDialog.OnDateSetListenerを追加
class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivityMainBinding

    //SharedPreferences の変数を宣言
    private lateinit var prefLens: SharedPreferences
    private lateinit var prefCase: SharedPreferences

    //クリックされたボタンの id を保持できる変数を追加
    @IdRes
    private var clickedButtonId: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        //SharedPreferences を初期化
        prefLens = getSharedPreferences("レンズの種類保存", Context.MODE_PRIVATE)

        //レンズ登録ボタンを押したとき
        var lensperiod: Int = 0
        binding.lensAddButton.setOnClickListener {
            val lensList = arrayOf("ソフト(2週間)", "ハード(1ヶ月)")
            MaterialAlertDialogBuilder(this)
                .setTitle("レンズの種類")
                .setSingleChoiceItems(lensList, 0) { dialog, which ->
                    //選択されたラジオボタンの
                    var kindOfLens: Int = which
                    Log.d("レンズの種類",kindOfLens.toString())
                    //レンズの種類を保存する
                    val editor = prefLens.edit()
                    editor.putInt("LENS_TYPE", kindOfLens)
                    editor.apply()
                }
                .setPositiveButton("はい") { dialog, which ->

                }
                .setNegativeButton("キャンセル", null)
                .show()
                }


        //ケース登録ボタンを押したとき
        binding.caseAddButton.setOnClickListener {
            val caseList = arrayOf("ソフト用(2ヶ月)", "ハード用(6ヶ月)")
            MaterialAlertDialogBuilder(this)
                .setTitle("ケースの種類")
                .setSingleChoiceItems(caseList, 0) { dialog, which ->
                    //選択されたラジオボタンの
                    var kindOfCase: Int = which
                    Log.d("ケースの種類",kindOfCase.toString())
                    //レンズの種類を保存する
                    val editor = prefCase.edit()
                    editor.putInt("CASE_TYPE", kindOfCase)
                    editor.apply()

                }
                .setPositiveButton("はい") { dialog, which ->

                }
                .setNegativeButton("キャンセル", null)
                .show()
        }


        //レンズ交換日を取得
        val lensEndyear: Int = 2022
        var lensEndmonth: Int = 9
        val lensEnddate: Int = 17

        /*/ケース交換日を取得
        val caseEndyear =
        val caseEndmonth =
        val caseEnddate =*/

        //目標日の取得
        val Goaldate = Calendar.getInstance()
        Log.d("現在の日時", Goaldate.toString())
        lensEndmonth = lensEndmonth - 1
        Goaldate.set(lensEndyear, lensEndmonth, lensEnddate);
        // 1970/1/1 から設定した rightNow のミリ秒
        val GoaltimeMillis: Long = Goaldate.getTimeInMillis()
        // 現在時刻のミリ秒
        val currentTimeMillis = System.currentTimeMillis()

        //レンズ交換までの日数を計算
        // 差分のミリ秒
        var different = GoaltimeMillis - currentTimeMillis
        // ミリ秒から秒へ変換
        different = different / 1000
        // minutes
        different = different / 60
        // hour
        different = different / 60
        // day
        different = different / 24
        //レンズ交換までの日数を表示
        val lensCountdown: String = "$different 日"
        //ケース交換までの日数を計算
        binding.lensDayleftView.text = lensCountdown

        //レンズの日付登録ボタンクリック時
        binding.lensDayButton.setOnClickListener {
            //id を更新
            clickedButtonId = it.id
            showDatePickerDialog()
            Log.d("開始日", "レンズ")


        }

        //ケースの日付登録ボタンクリック時
        binding.caseDayButton.setOnClickListener {
            //id を更新
            clickedButtonId = it.id
            showDatePickerDialog()
            Log.d("開始日", "ケース")

        }

    }

    //DatePickerDialogを呼び出すメソッド
    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int){
        //使用開始日を文字列で取得
        val StartDate: String = getString(R.string.stringformat, year, monthOfYear + 1, dayOfMonth)
        Log.d("開始日",StartDate.toString())

        //保持された id で処理を分岐
        when(clickedButtonId) {
            R.id.lens_day_button -> {
                binding.lensPeriodView.text = StartDate.toString()
                // 保存も書く
            }
            
            R.id.case_day_button -> {
                binding.casePeriodView.text = StartDate.toString()
                // 保存も書く
            }
            else -> {}
        }

    }

    private fun showDatePickerDialog() {
        val newFragment = DatePicker()
        newFragment.show(supportFragmentManager, "datePicker")
    }

}






