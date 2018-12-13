package com.example.tohyama.base_podcast

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button_tcfm = findViewById<Button>(R.id.button_tcfm)
        val button_rebuild = findViewById<Button>(R.id.button_rebuild)
        val button_misreading_chat = findViewById<Button>(R.id.button_misreading_chat)
        val button_bi_news = findViewById<Button>(R.id.button_bi_news)

        button_tcfm.setOnClickListener {
            val intent = Intent(this, Episode_list::class.java)
            intent.putExtra("RSS_URL", "https://feeds.turingcomplete.fm/tcfm")
            startActivity(intent)
        }

        button_rebuild.setOnClickListener {
            val intent = Intent(this, Episode_list::class.java)
            intent.putExtra("RSS_URL", "http://feeds.rebuild.fm/rebuildfm")
            startActivity(intent)
        }

        button_misreading_chat.setOnClickListener {
            val intent = Intent(this, Episode_list::class.java)
            intent.putExtra("RSS_URL", "https://misreading.chat/category/episodes/feed")
            startActivity(intent)
        }

        button_bi_news.setOnClickListener {
            val intent = Intent(this, Episode_list::class.java)
            intent.putExtra("RSS_URL", "http://bilingualnews.libsyn.com/rss")
            startActivity(intent)
        }
    }
}
