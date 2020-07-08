package com.kevin.viewtype.decoration.sample

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kevin.delegationadapter.DelegationAdapter
import com.kevin.viewtype.decoration.ViewTypeDividerDecoration

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val delegationAdapter = DelegationAdapter()
        delegationAdapter.addDelegate(CompanyAdapterDelegate())
        recyclerView.adapter = delegationAdapter

        // 设置头部条目上方空白间距
        val spaceDecoration = ViewTypeDividerDecoration.Builder()
            .defaultSpace(3, 45, 45)
            .defaultColor(Color.BLUE)
            .build()
        recyclerView.addItemDecoration(spaceDecoration)

        val companies: MutableList<String?> = ArrayList()
        companies.add("🇨🇳 Baidu")
        companies.add("🇨🇳 Alibaba")
        companies.add("🇨🇳 Tencent")
        companies.add("🇺🇸 Google")
        companies.add("🇺🇸 Facebook")
        companies.add("🇺🇸 Microsoft")
        delegationAdapter.setDataItems(companies)
    }
}
