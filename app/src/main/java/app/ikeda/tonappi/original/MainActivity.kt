package app.ikeda.tonappi.original

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.ikeda.tonappi.original.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

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

    }
}