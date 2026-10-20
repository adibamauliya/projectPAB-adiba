package com.example.project_pab2

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : Activity() {

    private val purple = Color.rgb(112, 76, 182)
    private val darkPurple = Color.rgb(65, 43, 112)
    private val lavender = Color.rgb(241, 235, 255)
    private val backgroundColor = Color.rgb(248, 247, 252)
    private val textColor = Color.rgb(43, 39, 57)
    private val mutedColor = Color.rgb(120, 115, 135)
    private val green = Color.rgb(50, 155, 112)
    private val orange = Color.rgb(235, 148, 65)

    private lateinit var root: LinearLayout
    private lateinit var content: LinearLayout
    private lateinit var bottomNav: LinearLayout

    private val tasks = mutableListOf<StudyTask>()
    private val notes = mutableListOf<StudyNote>()

    private var currentTab = "Home"

    data class StudyTask(
        val id: Long,
        var title: String,
        var subject: String,
        var deadline: String,
        var priority: String,
        var done: Boolean
    )

    data class StudyNote(
        val id: Long,
        var title: String,
        var subject: String,
        var body: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = darkPurple
        window.navigationBarColor = Color.rgb(255, 255, 255)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        loadData()
        buildLayout()
        showTab("Home")
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun rounded(
        color: Int,
        radius: Int = 18,
        strokeColor: Int = Color.TRANSPARENT
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
            if (strokeColor != Color.TRANSPARENT) {
                setStroke(dp(1), strokeColor)
            }
        }
    }

    private fun text(
        value: String,
        size: Float = 14f,
        color: Int = textColor,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER_VERTICAL
            includeFontPadding = true
        }
    }

    private fun button(
        label: String,
        color: Int = purple,
        textColorButton: Int = Color.WHITE
    ): TextView {
        return TextView(this).apply {
            text = label
            textSize = 14f
            setTextColor(textColorButton)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(dp(16), dp(12), dp(16), dp(12))
            background = rounded(color, 14)
            isClickable = true
            isFocusable = true
        }
    }

    private fun buildLayout() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(backgroundColor)
        }

        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(16), dp(20), dp(20))
        }

        val scroll = ScrollView(this).apply {
            isFillViewport = true
            addView(content)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
            )
        }

        bottomNav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(5), dp(8), dp(5), dp(8))
            setBackgroundColor(Color.WHITE)
            elevation = dp(12).toFloat()
        }

        root.addView(scroll)
        root.addView(bottomNav)

        setContentView(root)
        buildBottomNav()
    }

    private fun buildBottomNav() {
        bottomNav.removeAllViews()

        val tabs = listOf(
            "Home" to "⌂",
            "Tugas" to "✓",
            "Catatan" to "▤",
            "Progress" to "◔"
        )

        tabs.forEach { (name, symbol) ->
            val selected = currentTab == name

            val item = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(dp(5), dp(7), dp(5), dp(7))
                background = if (selected) rounded(lavender, 14)
                else rounded(Color.WHITE, 14)
            }

            item.addView(text(
                symbol, 23f,
                if (selected) purple else mutedColor, true
            ).apply { gravity = Gravity.CENTER })

            item.addView(text(
                name, 11f,
                if (selected) purple else mutedColor, selected
            ).apply { gravity = Gravity.CENTER })

            item.layoutParams = LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
            ).apply {
                setMargins(dp(3), 0, dp(3), 0)
            }

            item.setOnClickListener { showTab(name) }
            bottomNav.addView(item)
        }
    }

    private fun showTab(tab: String) {
        currentTab = tab
        buildBottomNav()
        content.removeAllViews()

        when (tab) {
            "Home" -> showHome()
            "Tugas" -> showTasks()
            "Catatan" -> showNotes()
            "Progress" -> showProgress()
        }
    }

    private fun addGap(height: Int = 14) {
        content.addView(View(this), LinearLayout.LayoutParams(
            1, dp(height)
        ))
    }

    private fun addText(
        value: String,
        size: Float = 14f,
        color: Int = textColor,
        bold: Boolean = false
    ) {
        content.addView(text(value, size, color, bold))
    }

    private fun addHeading(value: String, subtitle: String = "") {
        addText(value, 26f, darkPurple, true)
        if (subtitle.isNotEmpty()) {
            addGap(4)
            addText(subtitle, 13f, mutedColor)
        }
        addGap(18)
    }

    private fun card(
        color: Int = Color.WHITE,
        padding: Int = 16
    ): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(padding), dp(padding), dp(padding), dp(padding))
            background = rounded(color, 20)
            elevation = dp(2).toFloat()
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(12)
            }
        }
    }

    private fun addCard(view: View) {
        content.addView(view)
    }

    private fun showHome() {
        addText("STUDYMATE  ✦", 13f, purple, true)
        addGap(12)
        addText("Halo, Pejuang Kuliah! 👋", 25f, darkPurple, true)
        addGap(5)
        addText("Sedikit demi sedikit, tugas selesai.", 14f, mutedColor)
        addGap(20)

        val hero = card(purple, 20)
        hero.addView(text("YOUR STUDY JOURNEY", 11f, lavender, true))
        hero.addView(text(
            "Belajar hari ini,\nbangga nanti. ✨",
            25f, Color.WHITE, true
        ).apply {
            setPadding(0, dp(12), 0, dp(12))
        })
        hero.addView(text(
            "Buat target kecil dan konsisten setiap hari.",
            13f, Color.WHITE
        ))

        val heroButton = button("＋  Tambah Tugas", Color.WHITE, purple)
        heroButton.setOnClickListener { showAddTaskDialog() }
        hero.addView(heroButton, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = dp(18)
        })
        addCard(hero)

        val completed = tasks.count { it.done }
        val pending = tasks.count { !it.done }
        val total = tasks.size
        val percent = if (total == 0) 0 else completed * 100 / total

        val stats = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        stats.addView(statCard("📚", "$pending", "Belum selesai"),
            LinearLayout.LayoutParams(0, dp(105), 1f).apply {
                rightMargin = dp(6)
            })
        stats.addView(statCard("🎯", "$percent%", "Progres tugas"),
            LinearLayout.LayoutParams(0, dp(105), 1f).apply {
                leftMargin = dp(6)
            })
        addCard(stats)

        addGap(8)
        addText("Tugas Terdekat", 19f, darkPurple, true)
        addGap(10)

        val upcoming = tasks.filter { !it.done }.take(3)

        if (upcoming.isEmpty()) {
            addCard(emptyCard(
                "🎉 Belum ada tugas!",
                "Tambahkan tugas kuliah agar belajarmu lebih teratur."
            ))
        } else {
            upcoming.forEach { addCard(taskCard(it)) }
        }

        val notesCard = card(lavender, 16)
        notesCard.addView(text("💡  Tips produktif", 16f, darkPurple, true))
        notesCard.addView(text(
            "Coba belajar 25 menit, lalu istirahat 5 menit. " +
                    "Jauhkan distraksi dan fokus pada satu tugas.",
            13f, textColor
        ).apply {
            setPadding(0, dp(8), 0, 0)
        })
        addCard(notesCard)

        val notesButton = button("Buka Catatan Kuliah", lavender, purple)
        notesButton.setOnClickListener { showTab("Catatan") }
        content.addView(notesButton)
    }

    private fun statCard(
        emoji: String,
        number: String,
        label: String
    ): View {
        val box = card(Color.WHITE, 12)
        box.gravity = Gravity.CENTER
        box.addView(text(emoji, 19f).apply { gravity = Gravity.CENTER })
        box.addView(text(number, 22f, darkPurple, true).apply {
            gravity = Gravity.CENTER
        })
        box.addView(text(label, 11f, mutedColor).apply {
            gravity = Gravity.CENTER
        })
        return box
    }

    private fun emptyCard(title: String, subtitle: String): View {
        val box = card(Color.WHITE, 20)
        box.gravity = Gravity.CENTER
        box.addView(text(title, 17f, darkPurple, true).apply {
            gravity = Gravity.CENTER
        })
        box.addView(text(subtitle, 13f, mutedColor).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, 0)
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        })
        return box
    }

    private fun showTasks() {
        addHeading("Daftar Tugas 📝", "Atur deadline dan selesaikan satu per satu.")

        val add = button("＋  Tambah Tugas Baru")
        add.setOnClickListener { showAddTaskDialog() }
        content.addView(add)
        addGap(18)

        if (tasks.isEmpty()) {
            addCard(emptyCard(
                "Belum ada tugas",
                "Tekan tombol Tambah Tugas Baru untuk mulai."
            ))
        } else {
            tasks.sortedBy { it.done }.forEach { task ->
                addCard(taskCard(task))
            }
        }
    }

    private fun taskCard(task: StudyTask): View {
        val box = card(Color.WHITE, 15)

        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val check = CheckBox(this).apply {
            isChecked = task.done
            buttonTintList = android.content.res.ColorStateList.valueOf(
                if (task.done) green else purple
            )
            setOnCheckedChangeListener { _, checked ->
                task.done = checked
                saveData()
                showTab(currentTab)
            }
        }

        val titleColumn = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        titleColumn.addView(text(
            task.title, 15f,
            if (task.done) mutedColor else darkPurple, true
        ))
        titleColumn.addView(text(
            task.subject.ifBlank { "Umum" },
            12f, mutedColor
        ))

        top.addView(check)
        top.addView(titleColumn, LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
        ))
        box.addView(top)

        box.addView(text(
            "📅  Deadline: ${task.deadline.ifBlank { "Belum ditentukan" }}",
            12f, mutedColor
        ).apply {
            setPadding(dp(5), dp(8), 0, dp(8))
        })

        val priorityColor = when (task.priority) {
            "Tinggi" -> Color.rgb(202, 74, 88)
            "Sedang" -> orange
            else -> green
        }

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        row.addView(text(
            if (task.done) "✓ Selesai" else "• ${task.priority}",
            12f, if (task.done) green else priorityColor, true
        ), LinearLayout.LayoutParams(0, dp(35), 1f))

        val delete = button("Hapus", Color.rgb(255, 237, 240),
            Color.rgb(190, 62, 80))
        delete.setPadding(dp(12), dp(8), dp(12), dp(8))
        delete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Hapus tugas?")
                .setMessage("Tugas \"${task.title}\" akan dihapus.")
                .setNegativeButton("Batal", null)
                .setPositiveButton("Hapus") { _, _ ->
                    tasks.removeAll { it.id == task.id }
                    saveData()
                    showTab(currentTab)
                }
                .show()
        }
        row.addView(delete)
        box.addView(row)

        return box
    }

    private fun showAddTaskDialog() {
        val titleInput = EditText(this).apply {
            hint = "Contoh: Mengerjakan tugas PAB"
            textSize = 14f
        }
        val subjectInput = EditText(this).apply {
            hint = "Mata kuliah (contoh: Pemrograman Android)"
            textSize = 14f
        }
        val deadlineInput = EditText(this).apply {
            hint = "Deadline (contoh: 25/10/2026)"
            textSize = 14f
        }

        val prioritySpinner = Spinner(this)
        val priorities = listOf("Rendah", "Sedang", "Tinggi")
        prioritySpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            priorities
        )
        prioritySpinner.setSelection(1)

        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(8), dp(22), dp(4))
        }

        fun addField(label: String, view: View) {
            form.addView(text(label, 13f, darkPurple, true))
            form.addView(view, LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ))
            form.addView(View(this@MainActivity), LinearLayout.LayoutParams(
                1, dp(8)
            ))
        }

        addField("Nama tugas *", titleInput)
        addField("Mata kuliah", subjectInput)
        addField("Deadline", deadlineInput)
        addField("Prioritas", prioritySpinner)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Tambah Tugas 📚")
            .setView(form)
            .setNegativeButton("Batal", null)
            .setPositiveButton("Simpan", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val title = titleInput.text.toString().trim()

                if (title.isEmpty()) {
                    titleInput.error = "Nama tugas wajib diisi"
                    return@setOnClickListener
                }

                tasks.add(StudyTask(
                    System.currentTimeMillis(),
                    title,
                    subjectInput.text.toString().trim(),
                    deadlineInput.text.toString().trim(),
                    prioritySpinner.selectedItem.toString(),
                    false
                ))

                saveData()
                dialog.dismiss()
                showTab(currentTab)
            }
        }

        dialog.show()
    }

    private fun showNotes() {
        addHeading("Catatan Kuliah 📖", "Simpan materi dan ide pentingmu.")

        val add = button("＋  Tulis Catatan")
        add.setOnClickListener { showAddNoteDialog() }
        content.addView(add)
        addGap(18)

        if (notes.isEmpty()) {
            addCard(emptyCard(
                "Ruang ide kamu masih kosong",
                "Buat catatan pertamamu supaya materi mudah dicari."
            ))
        } else {
            notes.reversed().forEach { note ->
                val box = card(Color.WHITE, 17)
                box.addView(text("📒  ${note.subject.ifBlank { "Umum" }}",
                    12f, purple, true))
                box.addView(text(note.title, 18f, darkPurple, true).apply {
                    setPadding(0, dp(8), 0, dp(6))
                })
                box.addView(text(
                    note.body, 13f, mutedColor
                ).apply {
                    maxLines = 5
                    ellipsize = android.text.TextUtils.TruncateAt.END
                })

                val actions = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.END
                }

                val edit = button("Edit", lavender, purple)
                edit.setPadding(dp(14), dp(8), dp(14), dp(8))
                edit.setOnClickListener { showAddNoteDialog(note) }

                val delete = button("Hapus",
                    Color.rgb(255, 237, 240),
                    Color.rgb(190, 62, 80))
                delete.setPadding(dp(14), dp(8), dp(14), dp(8))
                delete.setOnClickListener {
                    AlertDialog.Builder(this)
                        .setTitle("Hapus catatan?")
                        .setNegativeButton("Batal", null)
                        .setPositiveButton("Hapus") { _, _ ->
                            notes.removeAll { it.id == note.id }
                            saveData()
                            showTab("Catatan")
                        }.show()
                }

                actions.addView(edit)
                actions.addView(View(this).apply {
                    layoutParams = LinearLayout.LayoutParams(dp(8), 1)
                })
                actions.addView(delete)

                box.addView(actions, LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(12)
                })
                addCard(box)
            }
        }
    }

    private fun showAddNoteDialog(existing: StudyNote? = null) {
        val titleInput = EditText(this).apply {
            hint = "Judul catatan"
            setText(existing?.title ?: "")
            textSize = 14f
        }
        val subjectInput = EditText(this).apply {
            hint = "Mata kuliah"
            setText(existing?.subject ?: "")
            textSize = 14f
        }
        val bodyInput = EditText(this).apply {
            hint = "Tulis materi, rangkuman, atau ide di sini..."
            setText(existing?.body ?: "")
            textSize = 14f
            minLines = 4
            gravity = Gravity.TOP
            inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                    android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        }

        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(4), dp(22), dp(4))
        }

        form.addView(titleInput)
        form.addView(subjectInput)
        form.addView(bodyInput)

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (existing == null) "Catatan Baru ✍️" else "Edit Catatan")
            .setView(form)
            .setNegativeButton("Batal", null)
            .setPositiveButton("Simpan", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val title = titleInput.text.toString().trim()
                if (title.isEmpty()) {
                    titleInput.error = "Judul catatan wajib diisi"
                    return@setOnClickListener
                }

                if (existing == null) {
                    notes.add(StudyNote(
                        System.currentTimeMillis(),
                        title,
                        subjectInput.text.toString().trim(),
                        bodyInput.text.toString()
                    ))
                } else {
                    existing.title = title
                    existing.subject = subjectInput.text.toString().trim()
                    existing.body = bodyInput.text.toString()
                }

                saveData()
                dialog.dismiss()
                showTab("Catatan")
            }
        }

        dialog.show()
    }

    private fun showProgress() {
        addHeading("Progress Belajar 🎯", "Lihat seberapa jauh kamu berkembang.")

        val completed = tasks.count { it.done }
        val total = tasks.size
        val remaining = total - completed
        val percent = if (total == 0) 0 else completed * 100 / total

        val hero = card(purple, 22)
        hero.gravity = Gravity.CENTER
        hero.addView(text("TOTAL PENYELESAIAN", 12f, lavender, true).apply {
            gravity = Gravity.CENTER
        })
        hero.addView(text("$percent%", 48f, Color.WHITE, true).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(10), 0, dp(10))
        })
        hero.addView(text("$completed dari $total tugas selesai",
            14f, Color.WHITE).apply {
            gravity = Gravity.CENTER
        })

        val progressBar = ProgressBar(
            this, null, android.R.attr.progressBarStyleHorizontal
        ).apply {
            max = 100
            progress = percent
            progressTintList =
                android.content.res.ColorStateList.valueOf(Color.WHITE)
            progressBackgroundTintList =
                android.content.res.ColorStateList.valueOf(
                    Color.rgb(171, 145, 221)
                )
        }

        hero.addView(progressBar, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(10)
        ).apply {
            topMargin = dp(18)
        })

        addCard(hero)

        addCard(statCard("✅", "$completed", "Selesai"))
        addCard(statCard("📌", "$remaining", "Perlu dikerjakan"))
        addCard(statCard("📚", "${notes.size}", "Catatan tersimpan"))

        addGap(8)
        addText("Tetap Semangat! 💜", 20f, darkPurple, true)
        addGap(8)
        addCard(emptyCard(
            if (total == 0) "Mulai dari satu tugas kecil"
            else if (percent == 100) "Keren! Semua tugas selesai!"
            else "Kamu sedang berproses!",
            if (total == 0)
                "Tambahkan tugas pertama untuk mulai melacak progres."
            else
                "Terus konsisten. Satu langkah kecil tetap berarti."
        ))

        val viewTasks = button("Lihat Daftar Tugas")
        viewTasks.setOnClickListener { showTab("Tugas") }
        content.addView(viewTasks)
    }

    private fun saveData() {
        val taskArray = JSONArray()
        tasks.forEach { task ->
            taskArray.put(JSONObject().apply {
                put("id", task.id)
                put("title", task.title)
                put("subject", task.subject)
                put("deadline", task.deadline)
                put("priority", task.priority)
                put("done", task.done)
            })
        }

        val noteArray = JSONArray()
        notes.forEach { note ->
            noteArray.put(JSONObject().apply {
                put("id", note.id)
                put("title", note.title)
                put("subject", note.subject)
                put("body", note.body)
            })
        }

        getSharedPreferences("studymate_data", Context.MODE_PRIVATE)
            .edit()
            .putString("tasks", taskArray.toString())
            .putString("notes", noteArray.toString())
            .apply()
    }

    private fun loadData() {
        val prefs = getSharedPreferences(
            "studymate_data", Context.MODE_PRIVATE
        )

        try {
            val taskArray = JSONArray(prefs.getString("tasks", "[]") ?: "[]")
            for (i in 0 until taskArray.length()) {
                val obj = taskArray.getJSONObject(i)
                tasks.add(StudyTask(
                    obj.optLong("id"),
                    obj.optString("title"),
                    obj.optString("subject"),
                    obj.optString("deadline"),
                    obj.optString("priority", "Sedang"),
                    obj.optBoolean("done", false)
                ))
            }

            val noteArray = JSONArray(prefs.getString("notes", "[]") ?: "[]")
            for (i in 0 until noteArray.length()) {
                val obj = noteArray.getJSONObject(i)
                notes.add(StudyNote(
                    obj.optLong("id"),
                    obj.optString("title"),
                    obj.optString("subject"),
                    obj.optString("body")
                ))
            }
        } catch (e: Exception) {
            tasks.clear()
            notes.clear()
        }
    }
}
