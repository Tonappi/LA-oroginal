package app.ikeda.tonappi.original

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import app.ikeda.tonappi.original.databinding.ActivityMainBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

//DataPickerDialog.OnDateSetListenerを追加
class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var alarmManager: AlarmManager

    //レンズとケースの登録時、種類を保持できる変数を宣言
    private var kindOfLens: Int = -1
    private var kindOfCase: Int = -1

    //SharedPreferences の変数を宣言
    private lateinit var prefType: SharedPreferences
    private lateinit var prefStrDate: SharedPreferences
    private lateinit var prefIntDate: SharedPreferences
    private lateinit var prefCountDown: SharedPreferences

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
        prefCountDown = getSharedPreferences("カウントダウン表示", MODE_PRIVATE)


        //レンズの使用開始日を表示
        val lensStartdate = prefStrDate.getString("LENS_yyyy/mm/dd", "開始日")
        binding.lensPeriodView.text = lensStartdate

        //ケースの使用開始日を表示
        val caseStartdate = prefStrDate.getString("CASE_yyyy/mm/dd", "開始日")
        binding.casePeriodView.text = caseStartdate

        //カウントダウン日数を計算して、取得、円グラフ表示
        setDayleftview()
        val lensPeriod: String? = prefCountDown.getString("LENS_COUNT","××")
        val casePeriod: String? = prefCountDown.getString("CASE_COUNT","××")
        //レンズカウントダウンを表示
        binding.lensDayleftView.text = "$lensPeriod 日"
        //ケースカウントダウンを表示
        binding.caseDaysleftView.text = "$casePeriod 日"
        //値がないとき、PieChartを非表示にする
        if(lensStartdate == "開始日"){
            binding.lensPieChart.isVisible = false
            binding.lensDayleftView.text = "××日"
        }else{

        }
        if(caseStartdate == "開始日"){
            binding.casePieChart.isVisible = false
            binding.caseDaysleftView.text = "××日"
        }else{

        }


        //レンズ登録ボタンを押したとき
        binding.lensAddButton.setOnClickListener {
            val lensList = arrayOf("ソフト(2週間)", "ハード(1ヶ月)")
            MaterialAlertDialogBuilder(this)
                .setTitle("レンズの種類")
                .setSingleChoiceItems(lensList, 0) { dialog, which ->
                    //選択されたラジオボタンの
                     kindOfLens = which
                    if (BuildConfig.DEBUG){
                        Log.d("レンズの種類選択時",kindOfLens.toString())
                    }
                }
                .setPositiveButton("はい") { dialog, _ ->
                    //レンズの種類を保存する
                    val editor = prefType.edit()
                    editor.putInt("LENS_TYPE", kindOfLens)
                    editor.apply()
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
                    kindOfCase = which
                    if (BuildConfig.DEBUG) {
                        Log.d("ケースの種類", kindOfCase.toString())
                    }
                }
                .setPositiveButton("はい") { dialog, _ ->
                    //レンズの種類を保存する
                    val editor = prefType.edit()
                    editor.putInt("CASE_TYPE", kindOfCase)
                    editor.apply()
                }
                .setNegativeButton("キャンセル", null)
                .show()

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

    companion object {
        const val LENS_REQUEST_CODE_KEY = "LENS_REQUEST_CODE"
        //const val LENS_REQUEST_CODE = 1
        const val LENS_ALARM_LOG = "LENS_ALARM_LOG"
        //const val CASE_REQUEST_CODE_KEY = "CASE_REQUEST_CODE"
        //const val CASE_REQUEST_CODE = 1
        const val CASE_ALARM_LOG = "CASE_ALARM_LOG"
    }

    //DatePickerDialogを呼び出すメソッド
    override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int){
        //使用開始日を表示形式(yyyy/mm/dd)で取得
        val StartDate: String = getString(R.string.stringformat, year, monthOfYear + 1, dayOfMonth)
        if (BuildConfig.DEBUG){
            Log.d("開始日",StartDate.toString())
        }
        
        //カウントダウン日数を計算して、取得
        setDayleftviewpicker(year,monthOfYear,dayOfMonth,year,monthOfYear,dayOfMonth)
        val lensPeriod: String? = prefCountDown.getString("LENS_COUNT","××")
        val casePeriod: String? = prefCountDown.getString("CASE_COUNT","××")

        //保持された id で処理を分岐
        when(clickedButtonId) {
            R.id.lens_day_button -> {
                //開始日の表示
                binding.lensPeriodView.text = StartDate.toString()
                //カウントダウンを表示
                binding.lensDayleftView.text = "$lensPeriod 日"
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
                //開始日の表示
                binding.casePeriodView.text = StartDate.toString()
                //カウントダウンを表示
                binding.caseDaysleftView.text = "$casePeriod 日"
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

    //終了日と現在の日時のミリ秒差を日数差に変換するメソッド
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

    //レンズ通知を作成するメソッド
    fun lensstartAlarm(endCalendar:Calendar) {
        //アラームがトリガーされたときに開始するペンディングインテント
        val pendingLensIntent = PendingIntent.getBroadcast(
            this@MainActivity,
            //requestCodeの値で、ペンディングイベントを識別する
            1,
            //明示的なブロードキャスト
            Intent(this, AlarmBroadcastReceiver::class.java).putExtra(
                LENS_REQUEST_CODE_KEY, 1
            ),
            PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE
        )
        // AlarmManagerをインスタンス化する。
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, endCalendar.timeInMillis, pendingLensIntent)
        if(BuildConfig.DEBUG){
            Log.d(LENS_ALARM_LOG, "alarmManager.set()")
            Log.d("LENS_ALARM_LOG_引数", endCalendar.toString())
        }

    }

    //ケース通知を作成するメソッド
    fun casestartAlarm(endCalendar:Calendar) {
        //アラームがトリガーされたときに開始するペンディングインテント
        val pendingCaseIntent = PendingIntent.getBroadcast(
            this@MainActivity,
            //requestCodeの値で、ペンディングイベントを識別する
            2,
            //明示的なブロードキャスト
            Intent(this, AlarmBroadcastReceiver::class.java).putExtra(
                LENS_REQUEST_CODE_KEY, 2
            ),
            PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE
        )
        // AlarmManagerをインスタンス化する。
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, endCalendar.timeInMillis, pendingCaseIntent)
        if(BuildConfig.DEBUG){
            Log.d(CASE_ALARM_LOG, "alarmManager.set()")
            Log.d("CASE_ALARM_LOG_引数", endCalendar.toString())
        }

    }

    //データが保存されているときカウントダウン日数を計算して保存する、円グラフ表示、通知を呼び出すメソッド(アプリの起動時)
    fun setDayleftview() {
        //レンズ使用開始日の取得
        val lensStartYear: Int = prefIntDate.getInt("LENS_START_YEAR",-1)
        var lensStartMonth: Int = prefIntDate.getInt("LENS_START_MONTH",-1)
        val lensStartDay: Int = prefIntDate.getInt("LENS_START_DAY",-1)
        if(BuildConfig.DEBUG) {
            Log.d("レンズの開始年",lensStartYear.toString())
            Log.d("レンズの開始月",lensStartMonth.toString())
            Log.d("レンズの開始日",lensStartDay.toString())
        }

        //ケース使用開始日の取得
        val caseStartYear: Int = prefIntDate.getInt("CASE_START_YEAR",-1)
        var caseStartMonth: Int = prefIntDate.getInt("CASE_START_MONTH",-1)
        val caseStartDay: Int = prefIntDate.getInt("CASE_START_DAY",-1)
        if(BuildConfig.DEBUG){
            Log.d("ケースの開始年",caseStartYear.toString())
            Log.d("ケースの開始月",caseStartMonth.toString())
            Log.d("ケースの開始日",caseStartDay.toString())
        }

        //レンズの種類を読み取って、日付の計算をする
        val lensType = prefType.getInt("LENS_TYPE", -1)
        Log.d(LENS_ALARM_LOG,lensType.toString())
        when(lensType) {
            0 -> {
                //レンズ開始日からレンズ終了日を求める
                val lensendCalendar = Calendar.getInstance()
                lensStartMonth = lensStartMonth - 1
                lensendCalendar.set(lensStartYear, lensStartMonth, lensStartDay)
                if (BuildConfig.DEBUG){
                    Log.d("ソフトレンズの終了日",lensendCalendar.toString())
                }
                lensendCalendar.add(Calendar.DATE,14)
                //カウントダウン日数を求めるメソッド呼び出し
                val lensCountdown: Int= changeMillistoDay(lensendCalendar)
                if (BuildConfig.DEBUG){
                    Log.d("ソフトレンズカウントダウン",lensCountdown.toString())
                }
                //戻り値にカウントダウン日数を返す
                val editorCount = prefCountDown.edit()
                editorCount .putString("LENS_COUNT",lensCountdown.toString())
                editorCount.apply()
                //通知
                lensstartAlarm(lensendCalendar)
                if (BuildConfig.DEBUG){
                    Log.d("LENS_ALARM_LOG","AlarmSet(lens)")
                }
                //円グラフ作成、表示のメソッド
                setLensPieChart(periodOflens = 14f, daysLeftlens = lensCountdown.toFloat())

            }

            1 -> {
                //レンズ使用開始日から終了日を求める
                val lensendCalendar = Calendar.getInstance()
                lensStartMonth = lensStartMonth - 1
                lensendCalendar.set(lensStartYear, lensStartMonth, lensStartDay)
                if (BuildConfig.DEBUG){
                    Log.d("ハードレンズの終了日",lensendCalendar.toString())
                }
                lensendCalendar.add(Calendar.MONTH,1)
                //カウントダウン日数を求めるメソッド呼び出し
                val lensCountdown: Int= changeMillistoDay(lensendCalendar)
                if (BuildConfig.DEBUG){
                    Log.d("ハードレンズカウントダウン",lensCountdown.toString())
                }
                //カウントダウン日数を保存
                val editorCount = prefCountDown.edit()
                editorCount .putString("LENS_COUNT",lensCountdown.toString())
                editorCount.apply()
                //通知
                lensstartAlarm(lensendCalendar)
                if (BuildConfig.DEBUG){
                    Log.d("LENS_ALARM_LOG","AlarmSet(lens)")
                }
                //円グラフ作成、表示のメソッド
                setLensPieChart(periodOflens = 31f, daysLeftlens = lensCountdown.toFloat())
            }

            else ->{
                if(lensStartYear ==-1 && lensStartMonth ==-1 && lensStartDay ==-1){

                }else{
                    //レンズ開始日からレンズ終了日を求める
                    val lensendCalendar = Calendar.getInstance()
                    lensStartMonth = lensStartMonth - 1
                    lensendCalendar.set(lensStartYear, lensStartMonth, lensStartDay)
                    if (BuildConfig.DEBUG){
                        Log.d("ソフトレンズの終了日",lensendCalendar.toString())
                    }
                    lensendCalendar.add(Calendar.DATE,14)
                    //カウントダウン日数を求めるメソッド呼び出し
                    val lensCountdown: Int= changeMillistoDay(lensendCalendar)
                    if (BuildConfig.DEBUG){
                        Log.d("ソフトレンズカウントダウン",lensCountdown.toString())
                    }
                    //戻り値にカウントダウン日数を返す
                    val editorCount = prefCountDown.edit()
                    editorCount .putString("LENS_COUNT",lensCountdown.toString())
                    editorCount.apply()
                    //通知
                    lensstartAlarm(lensendCalendar)
                    if (BuildConfig.DEBUG){
                        Log.d("LENS_ALARM_LOG","AlarmSet(lens)")
                    }

                    //円グラフ作成、表示のメソッド
                    setLensPieChart(periodOflens = 14f, daysLeftlens = lensCountdown.toFloat())

                }

            }

        }

        //ケースの種類を読み取って、日付の計算をする
        val caseType = prefType.getInt("CASE_TYPE", -1)
        if (BuildConfig.DEBUG){
            Log.d(CASE_ALARM_LOG,caseType.toString())
        }
        when(caseType) {
            0 -> {
                //ケース使用開始日から終了日を求める(2ヶ月足す)
                val caseendCalendar = Calendar.getInstance()
                caseStartMonth = caseStartMonth - 1
                caseendCalendar.set(caseStartYear, caseStartMonth, caseStartDay)
                if (BuildConfig.DEBUG){
                    Log.d("ソフト用ケースの終了日",caseendCalendar.toString())
                }
                caseendCalendar.add(Calendar.MONTH,2)
                //カウントダウン日数を求めるメソッド呼び出し
                val caseCountdown: Int= changeMillistoDay(caseendCalendar)
                if (BuildConfig.DEBUG){
                    Log.d("ソフト用ケースカウントダウン",caseCountdown.toString())
                }
                //カウントダウン日数を保存
                val editorCount = prefCountDown.edit()
                editorCount .putString("CASE_COUNT",caseCountdown.toString())
                editorCount.apply()
                //通知
                casestartAlarm(caseendCalendar)
                if (BuildConfig.DEBUG){
                    Log.d("CASE_ALARM_LOG","AlarmSet(case)")
                }
                //円グラフ作成、表示のメソッド
                setCasePieChart(periodOfcase = 62f, daysLeftcase = caseCountdown.toFloat())
            }

            1 -> {
                //ケース使用開始日から終了日を求める(6ヶ月足す)
                val caseendCalendar = Calendar.getInstance()
                caseStartMonth = caseStartMonth - 1
                caseendCalendar.set(caseStartYear, caseStartMonth, caseStartDay)
                if (BuildConfig.DEBUG) {
                    Log.d("ハード用ケースの終了日", caseendCalendar.toString())
                }
                caseendCalendar.add(Calendar.MONTH, 6)
                //カウントダウン日数を求めるメソッド呼び出し
                val caseCountdown: Int = changeMillistoDay(caseendCalendar)
                if (BuildConfig.DEBUG) {
                    Log.d("ハード用ケースカウントダウン", caseCountdown.toString())
                }
                //カウントダウン日数を保存
                val editorCount = prefCountDown.edit()
                editorCount.putString("CASE_COUNT", caseCountdown.toString())
                editorCount.apply()
                //通知
                casestartAlarm(caseendCalendar)
                if (BuildConfig.DEBUG) {
                    Log.d("CASE_ALARM_LOG", "AlarmSet(case)")
                }
                //円グラフ作成、表示のメソッド
                setCasePieChart(periodOfcase = 184f, daysLeftcase = caseCountdown.toFloat())


            }

            else->{
                if(caseStartYear ==-1 && caseStartMonth ==-1 && caseStartDay ==-1){

                }else {
                    //ケース使用開始日から終了日を求める(2ヶ月足す)
                    val caseendCalendar = Calendar.getInstance()
                    caseStartMonth = caseStartMonth - 1
                    caseendCalendar.set(caseStartYear, caseStartMonth, caseStartDay)
                    if (BuildConfig.DEBUG){
                        Log.d("ソフト用ケースの終了日", caseendCalendar.toString())
                    }
                    caseendCalendar.add(Calendar.MONTH, 2)
                    //カウントダウン日数を求めるメソッド呼び出し
                    val caseCountdown: Int = changeMillistoDay(caseendCalendar)
                    if (BuildConfig.DEBUG){
                        Log.d("ソフト用ケースカウントダウン", caseCountdown.toString())
                    }
                    //カウントダウン日数を保存
                    val editorCount = prefCountDown.edit()
                    editorCount.putString("CASE_COUNT", caseCountdown.toString())
                    editorCount.apply()
                    //通知
                    casestartAlarm(caseendCalendar)
                    if (BuildConfig.DEBUG){
                        Log.d("CASE_ALARM_LOG","AlarmSet(case)")
                    }
                    //円グラフ作成、表示のメソッド
                    setCasePieChart(periodOfcase = 62f, daysLeftcase = caseCountdown.toFloat())
                }
            }
        }
    }

    //Datepickerで選んだあと、レンズの残り日数を計算するメソッド
    fun setDayleftviewpicker(lensStartYear: Int, lensStartMonth: Int, lensStartDay: Int, caseStartYear: Int, caseStartMonth: Int, caseStartDay: Int) {

        //レンズの種類を読み取って、日付の計算をする
        val lensType = prefType.getInt("LENS_TYPE", -1)
        if (BuildConfig.DEBUG){
            Log.d(LENS_ALARM_LOG,lensType.toString())
        }
        when(lensType) {
            0 -> {
                //レンズ開始日からレンズ終了日を求める
                val lensendCalendar = Calendar.getInstance()
                //lensStartMonth = lensStartMonth - 1
                lensendCalendar.set(lensStartYear, lensStartMonth, lensStartDay)
                if (BuildConfig.DEBUG){
                    Log.d("ソフトレンズの終了日",lensendCalendar.toString())
                }
                lensendCalendar.add(Calendar.DATE,14)
                //カウントダウン日数を求めるメソッド呼び出し
                val lensCountdown: Int= changeMillistoDay(lensendCalendar)
                if (BuildConfig.DEBUG){
                    Log.d("ソフトレンズカウントダウン",lensCountdown.toString())
                }
                //戻り値にカウントダウン日数を返す
                val editorCount = prefCountDown.edit()
                editorCount .putString("LENS_COUNT",lensCountdown.toString())
                editorCount.apply()
                //通知
                lensstartAlarm(lensendCalendar)
                if (BuildConfig.DEBUG){
                    Log.d("LENS_ALARM_LOG","AlarmSet(lens)")
                }
                //円グラフ作成、表示のメソッド
                setLensPieChart(periodOflens = 14f, daysLeftlens = lensCountdown.toFloat())
            }

            1 -> {
                //レンズ使用開始日から終了日を求める
                val lensendCalendar = Calendar.getInstance()
                //lensStartMonth = lensStartMonth - 1
                lensendCalendar.set(lensStartYear, lensStartMonth, lensStartDay)
                if (BuildConfig.DEBUG){
                    Log.d("ハードレンズの終了日",lensendCalendar.toString())
                }
                lensendCalendar.add(Calendar.MONTH,1)
                //カウントダウン日数を求めるメソッド呼び出し
                val lensCountdown: Int= changeMillistoDay(lensendCalendar)
                if (BuildConfig.DEBUG){
                    Log.d("ハードレンズカウントダウン",lensCountdown.toString())
                }
                //カウントダウン日数を保存
                val editorCount = prefCountDown.edit()
                editorCount .putString("LENS_COUNT",lensCountdown.toString())
                editorCount.apply()
                //通知
                lensstartAlarm(lensendCalendar)
                if (BuildConfig.DEBUG){
                    Log.d("LENS_ALARM_LOG","AlarmSet(lens)")
                }
                //円グラフ作成、表示のメソッド
                setLensPieChart(periodOflens = 31f, daysLeftlens = lensCountdown.toFloat())
            }

            else->{
                //レンズ開始日からレンズ終了日を求める
                val lensendCalendar = Calendar.getInstance()
                //lensStartMonth = lensStartMonth - 1
                lensendCalendar.set(lensStartYear, lensStartMonth, lensStartDay)
                if (BuildConfig.DEBUG){
                    Log.d("ソフトレンズの終了日",lensendCalendar.toString())
                }
                lensendCalendar.add(Calendar.DATE,14)
                //カウントダウン日数を求めるメソッド呼び出し
                val lensCountdown: Int= changeMillistoDay(lensendCalendar)
                if (BuildConfig.DEBUG){
                    Log.d("ソフトレンズカウントダウン",lensCountdown.toString())
                }
                //戻り値にカウントダウン日数を返す
                val editorCount = prefCountDown.edit()
                editorCount .putString("LENS_COUNT",lensCountdown.toString())
                editorCount.apply()
                //通知
                lensstartAlarm(lensendCalendar)
                if (BuildConfig.DEBUG){
                    Log.d("LENS_ALARM_LOG","AlarmSet(lens)")
                }
                //円グラフ作成、表示のメソッド
                setLensPieChart(periodOflens = 14f, daysLeftlens = lensCountdown.toFloat())
            }

        }

        //ケースの種類を読み取って、日付の計算をする
        val caseType = prefType.getInt("CASE_TYPE", -1)
        if (BuildConfig.DEBUG){
            Log.d(CASE_ALARM_LOG,caseType.toString())
        }

        when(caseType) {
            0 -> {
                //ケース使用開始日から終了日を求める(2ヶ月足す)
                val caseendCalendar = Calendar.getInstance()
                //caseStartMonth = caseStartMonth - 1
                caseendCalendar.set(caseStartYear, caseStartMonth, caseStartDay)
                if (BuildConfig.DEBUG){
                    Log.d("ソフト用ケースの終了日",caseendCalendar.toString())
                }
                caseendCalendar.add(Calendar.MONTH,2)
                //カウントダウン日数を求めるメソッド呼び出し
                val caseCountdown: Int= changeMillistoDay(caseendCalendar)
                if (BuildConfig.DEBUG){
                    Log.d("ソフト用ケースカウントダウン",caseCountdown.toString())
                }
                //カウントダウン日数を保存
                val editorCount = prefCountDown.edit()
                editorCount .putString("CASE_COUNT",caseCountdown.toString())
                editorCount.apply()
                //通知
                casestartAlarm(caseendCalendar)
                if (BuildConfig.DEBUG){
                    Log.d("CASE_ALARM_LOG","AlarmSet(case)")
                }
                //円グラフ作成、表示のメソッド
                setCasePieChart(periodOfcase = 62f, daysLeftcase = caseCountdown.toFloat())
            }

            1 -> {
                //ケース使用開始日から終了日を求める(6ヶ月足す)
                val caseendCalendar = Calendar.getInstance()
                //caseStartMonth = caseStartMonth - 1
                caseendCalendar.set(caseStartYear, caseStartMonth, caseStartDay)
                if (BuildConfig.DEBUG){
                    Log.d("ハード用ケースの終了日",caseendCalendar.toString())
                }
                caseendCalendar.add(Calendar.MONTH,6)
                //カウントダウン日数を求めるメソッド呼び出し
                val caseCountdown: Int= changeMillistoDay(caseendCalendar)
                if (BuildConfig.DEBUG){
                    Log.d("ハード用ケースカウントダウン",caseCountdown.toString())
                }
                //カウントダウン日数を保存
                val editorCount = prefCountDown.edit()
                editorCount .putString("CASE_COUNT",caseCountdown.toString())
                editorCount.apply()
                //通知
                casestartAlarm(caseendCalendar)
                if (BuildConfig.DEBUG){
                    Log.d("CASE_ALARM_LOG","AlarmSet(case)")
                }
                //円グラフ作成、表示のメソッド
                setCasePieChart(periodOfcase = 184f, daysLeftcase = caseCountdown.toFloat())
            }

            else ->{
                //ケース使用開始日から終了日を求める(2ヶ月足す)
                val caseendCalendar = Calendar.getInstance()
                //caseStartMonth = caseStartMonth - 1
                caseendCalendar.set(caseStartYear, caseStartMonth, caseStartDay)
                if (BuildConfig.DEBUG){
                    Log.d("ソフト用ケースの終了日",caseendCalendar.toString())
                }
                caseendCalendar.add(Calendar.MONTH,2)
                //カウントダウン日数を求めるメソッド呼び出し
                val caseCountdown: Int= changeMillistoDay(caseendCalendar)
                if (BuildConfig.DEBUG){
                    Log.d("ソフト用ケースカウントダウン",caseCountdown.toString())
                }
                //カウントダウン日数を保存
                val editorCount = prefCountDown.edit()
                editorCount .putString("CASE_COUNT",caseCountdown.toString())
                editorCount.apply()
                //通知
                casestartAlarm(caseendCalendar)
                if (BuildConfig.DEBUG){
                    Log.d("CASE_ALARM_LOG","AlarmSet(case)")
                }
                //円グラフ作成、表示のメソッド
                setCasePieChart(periodOfcase = 62f, daysLeftcase = caseCountdown.toFloat())
            }
        }
    }

    //レンズの円グラフを表示するメソッド
    fun setLensPieChart(periodOflens: Float, daysLeftlens: Float) {
        //表示用サンプルデータの作成
        val dimensions = listOf<String>("", "")
        val values = listOf<Float>(periodOflens-daysLeftlens, daysLeftlens)
        //Entryにデータ格納
        var entryList = mutableListOf<PieEntry>()
        for (i in values.indices) {
            entryList.add(
                PieEntry(values[i], dimensions[i])
            )
        }

        //PieDataSetにデータ格納
        val pieDataSet = PieDataSet(entryList,"candle")
        //DataSetのフォーマット指定
        pieDataSet.colors = listOf(Color.rgb(1,135,134),Color.rgb(167,255,235))
        pieDataSet.setDrawValues(false)

        //PieDataにPieDataSetを格納
        val pieData = PieData(pieDataSet)
        //PieChartにPieData格納
        var pieChart = this.findViewById<PieChart>(R.id.lens_pieChart)
        pieChart.data = pieData
        //Chartのフォーマット指定
        pieChart.legend.isEnabled = false
        pieChart.holeRadius = 80f
        pieChart.description.isEnabled = false
        //PieChart更新
        pieChart.invalidate()
    }

    //ケースの円グラフを表示するメソッド
    fun setCasePieChart( periodOfcase: Float, daysLeftcase: Float){
        //表示用サンプルデータの作成
        val dimensions = listOf<String>("", "")
        val values = listOf<Float>(periodOfcase-daysLeftcase, daysLeftcase)
        //Entryにデータ格納
        var entryList = mutableListOf<PieEntry>()
        for (i in values.indices) {
            entryList.add(
                PieEntry(values[i], dimensions[i])
            )
        }

        //PieDataSetにデータ格納
        val pieDataSet = PieDataSet(entryList,"candle")
        //DataSetのフォーマット指定
        pieDataSet.colors = listOf(Color.rgb(1,135,134),Color.rgb(167,255,235))
        pieDataSet.setDrawValues(false)


        //PieDataにPieDataSetを格納
        val pieData = PieData(pieDataSet)
        //PieChartにPieData格納
        var pieChart = this.findViewById<PieChart>(R.id.case_pieChart)
        pieChart.data = pieData
        //Chartのフォーマット指定
        pieChart.legend.isEnabled = false
        pieChart.holeRadius = 80f
        pieChart.description.isEnabled = false
        //PieChart更新
        pieChart.invalidate()
    }
}







