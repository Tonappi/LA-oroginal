package app.ikeda.tonappi.original

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.hardware.camera2.params.LensShadingMap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
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
    private lateinit var prefType: SharedPreferences
    private lateinit var prefStrDate: SharedPreferences
    private lateinit var prefIntDate: SharedPreferences

    //クリックされたボタンの id を保持できる変数を追加
    @IdRes
    private var clickedButtonId: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        //SharedPreferences を初期化
        prefType = getSharedPreferences("種類保存", MODE_PRIVATE)
        prefStrDate = getSharedPreferences("使用開始日_str", MODE_PRIVATE)
        prefIntDate = getSharedPreferences("使用開始_int型", MODE_PRIVATE)


        //レンズの使用開始日を表示
        val lensStartdate = prefStrDate.getString("LENS_yyyy/mm/dd", "開始日登録をしてください")
        binding.lensPeriodView.text = lensStartdate

        //ケースの使用開始日を表示
        val caseStartdate = prefStrDate.getString("CASE_yyyy/mm/dd", "開始日登録をしてください")
        binding.casePeriodView.text = caseStartdate

        //レンズ使用開始日の取得
        val lensStartYear: Int = prefIntDate.getInt("LENS_START_YEAR",-1)
        var lensStartMonth: Int = prefIntDate.getInt("LENS_START_MONTH",-1)
        val lensStartDay: Int = prefIntDate.getInt("LENS_START_DAY",-1)
        Log.d("レンズの開始年",lensStartYear.toString())
        Log.d("レンズの開始月",lensStartMonth.toString())
        Log.d("レンズの開始日",lensStartDay.toString()) //ここまでOK

        //ケース使用開始日の取得
        val caseStartYear: Int = prefIntDate.getInt("CASE_START_YEAR",-1)
        var caseStartMonth: Int = prefIntDate.getInt("CASE_START_MONTH",-1)
        val caseStartDay: Int = prefIntDate.getInt("CASE_START_DAY",-1)
        Log.d("レンズの開始年",caseStartYear.toString())
        Log.d("レンズの開始月",caseStartMonth.toString())
        Log.d("レンズの開始日",caseStartdate.toString()) //ここまでOK


        //レンズ登録ボタンを押したとき
        binding.lensAddButton.setOnClickListener {
            val lensList = arrayOf("ソフト(2週間)", "ハード(1ヶ月)")
            MaterialAlertDialogBuilder(this)
                .setTitle("レンズの種類")
                .setSingleChoiceItems(lensList, 0) { dialog, which ->
                    //選択されたラジオボタンの
                    var kindOfLens: Int = which
                    Log.d("レンズの種類選択時",kindOfLens.toString())
                    //レンズの種類を保存する
                    val editor = prefType.edit()
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
                    val editor = prefType.edit()
                    editor.putInt("CASE_TYPE", kindOfCase)
                    editor.apply()

                }
                .setPositiveButton("はい") { dialog, which ->

                }
                .setNegativeButton("キャンセル", null)
                .show()
        }

        //レンズの種類を読み取って、日付の計算をする
        val LensType = prefType.getInt("LENS_TYPE", -1)
        Log.d("レンズの種類呼び出し",LensType.toString())
        when(LensType) {
            0 -> {
                //レンズ開始日からレンズ終了日を求める
                val lensendCalendar = Calendar.getInstance()
                lensStartMonth = lensStartMonth - 1
                lensendCalendar.set(lensStartYear, lensStartMonth, lensStartDay)
                lensendCalendar.add(Calendar.DATE,14)
                Log.d("ソフトレンズの終了日",lensendCalendar.toString())
                //カウントダウン日数を求めるメソッド呼び出し
                val lensCountdown: Int= changeMillistoDay(lensendCalendar)
                Log.d("ソフトレンズカウントダウン",lensCountdown.toString())
                //カウントダウンを表示
                binding.lensDayleftView.text = "$lensCountdown 日"

            }
            1 -> {
                //レンズ使用開始日から終了日を求める
                val lensendCalendar = Calendar.getInstance()
                lensStartMonth = lensStartMonth - 1
                lensendCalendar.set(lensStartYear, lensStartMonth, lensStartDay)
                Log.d("ハードレンズの終了日",lensendCalendar.toString())
                lensendCalendar.add(Calendar.MONTH,1)
                //カウントダウン日数を求めるメソッド呼び出し
                val lensCountdown: Int= changeMillistoDay(lensendCalendar)
                Log.d("ハードレンズカウントダウン",lensCountdown.toString())
                //カウントダウンを表示
                binding.lensDayleftView.text = "$lensCountdown 日"

            }
        }

        //ケースの種類を読み取って、日付の計算をする
        val CaseType = prefType.getInt("CASE_TYPE", -1)
        Log.d("ケースの種類呼び出し",CaseType.toString())
            when(CaseType) {
                0 -> {
                    //ケース使用開始日から終了日を求める(2ヶ月足す)
                    val caseendCalendar = Calendar.getInstance()
                    caseStartMonth = caseStartMonth - 1
                    caseendCalendar.set(caseStartYear, caseStartMonth, caseStartDay)
                    Log.d("ソフト用ケースの終了日",caseendCalendar.toString())
                    caseendCalendar.add(Calendar.MONTH,2)
                    //カウントダウン日数を求めるメソッド呼び出し
                    val caseCountdown: Int= changeMillistoDay(caseendCalendar)
                    Log.d("ソフト用ケースカウントダウン",caseCountdown.toString())
                    //カウントダウンを表示
                    binding.caseDaysleftView.text = "$caseCountdown 日"

                }
                1 -> {
                    //ケース使用開始日から終了日を求める(6ヶ月足す)
                    val caseendCalendar = Calendar.getInstance()
                    caseStartMonth = caseStartMonth - 1
                    caseendCalendar.set(caseStartYear, caseStartMonth, caseStartDay)
                    Log.d("ハード用ケースの終了日",caseendCalendar.toString())
                    caseendCalendar.add(Calendar.MONTH,6)
                    //カウントダウン日数を求めるメソッド呼び出し
                    val caseCountdown: Int= changeMillistoDay(caseendCalendar)
                    Log.d("ハード用ケースカウントダウン",caseCountdown.toString())
                    //カウントダウンを表示
                    binding.caseDaysleftView.text = "$caseCountdown 日"

                }
            }

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
        //使用開始日を表示形式(yyyy/mm/dd)で取得
        val StartDate: String = getString(R.string.stringformat, year, monthOfYear + 1, dayOfMonth)
        Log.d("開始日",StartDate.toString())

        //保持された id で処理を分岐
        when(clickedButtonId) {
            R.id.lens_day_button -> {
                binding.lensPeriodView.text = StartDate.toString()
                //レンズ開始日をString型で保存
                val editorStr = prefStrDate.edit()
                editorStr.putString("LENS_yyyy/mm/dd", StartDate)
                editorStr.apply()
                //レンズ開始日の年、月、日をInt型で保存
                val editorInt = prefIntDate.edit()
                editorInt.putInt("LENS_START_YEAR",year)
                editorInt.putInt("LENS_START_MONTH",monthOfYear+1)
                editorInt.putInt("LENS_START_DAY",dayOfMonth)
                editorInt.apply()

            }

            R.id.case_day_button -> {
                binding.casePeriodView.text = StartDate.toString()
                //ケース開始日をString型で保存
                val editorStr = prefStrDate.edit()
                editorStr.putString("CASE_yyyy/mm/dd", StartDate)
                editorStr.apply()
                //ケース開始日の年、月、日をInt型で保存
                val editorInt = prefIntDate.edit()
                editorInt.putInt("CASE_START_YEAR",year)
                editorInt.putInt("CASE_START_MONTH",monthOfYear+1)
                editorInt.putInt("CASE_START_DAY",dayOfMonth)
                editorInt.apply()
            }
            else -> {}
        }

    }

    private fun showDatePickerDialog() {
        val newFragment = DatePicker()
        newFragment.show(supportFragmentManager, "datePicker")
    }

    //カウントダウンのメソッド
    //レンズ・ケース使用開始日を取得してカレンダークラスに変換するメソッド
    fun lensstartdaytoCalender(StartYear:Int, StartMonth: Int, StartDay: Int): Calendar {

        Log.d("開始年",StartYear.toString())
        Log.d("開始月",StartMonth.toString())
        Log.d("開始日",StartDay.toString()) //ここまでOK

        //レンズ使用開始日をCalenderクラスで扱えるように変換する
        val StartCalendar = Calendar.getInstance()
        var StartMonth = StartMonth - 1
        StartCalendar.set(StartYear, StartMonth, StartDay)
        Log.d("レンズの使用開始日付",StartCalendar.toString())

        return StartCalendar

    }

    //終了日と現在の日時のミリ秒差を日数差に変換する
    fun changeMillistoDay(endcalendar:Calendar): Int {
        //終了日をミリ秒に換算する
        val TimeMillis = endcalendar.timeInMillis
        // 現在時刻のミリ秒
        val currentTimeMillis = System.currentTimeMillis()
        Log.d("現在時刻のミリ秒",currentTimeMillis.toString())
        //終了日と現在時刻のミリ秒差
        val differentMillis = TimeMillis - currentTimeMillis
        //終了日と現在の日数差
        val differentDays = differentMillis/(1000*60*60*24)
        //日数差を戻り値にする
        return differentDays.toInt()
    }


}






