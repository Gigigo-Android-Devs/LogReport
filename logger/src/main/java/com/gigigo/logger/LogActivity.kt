package com.gigigo.logger

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_logger_log.clearFab
import kotlinx.android.synthetic.main.activity_logger_log.logList
import java.io.BufferedReader
import java.io.InputStreamReader

class LogActivity : AppCompatActivity() {
    private var observableLog: Observable<LogMessage>? = null
    private var observableClear: Observable<LogMessage>? = null
    private val logAdapter = LogAdapter()
    private val compositeDisposable = CompositeDisposable()
    private var menuFilterSpinner: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logger_log)

        val pid = android.os.Process.myPid()
        val commandLog = "logcat --pid $pid -v time -d"
        val commandClear = "logcat -c"

        observableLog = Observable.create {
            execLine(commandLog, it)
        }

        observableClear = Observable.create {
            execLine(commandClear, it)
        }

        logList.layoutManager = LinearLayoutManager(this)
        logList.adapter = logAdapter

        clearFab.setOnClickListener {

            logAdapter.clear()
            observableClear?.let { observable ->
                val disposable = observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
                compositeDisposable.add(disposable)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.log_menu, menu)
        menuFilterSpinner = menu?.findItem(R.id.menu_filter_spinner)
        setupSpinner(menuFilterSpinner)
        return true
    }

    override fun onResume() {
        super.onResume()

        observableLog?.let { observable ->
            val disposable = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { buffer ->
                    logAdapter.add(buffer.message)
                }
            compositeDisposable.add(disposable)
        }
    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }

    private fun setupSpinner(item: MenuItem?) {
        item?.let {
            val view = it.actionView
            when (view) {
                is Spinner -> {
                    view.adapter = ArrayAdapter.createFromResource(
                        this,
                        R.array.filter_spinner_data,
                        android.R.layout.simple_spinner_dropdown_item
                    )
                    view.onItemSelectedListener = object : OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            logAdapter.filterBy(position)
                        }
                    }
                }
            }
        }
    }

    private fun execLine(command: String, emitter: ObservableEmitter<LogMessage>) {
        var bufferedReader: BufferedReader? = null
        try {
            val process = Runtime.getRuntime().exec(command)
            bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
            var line = bufferedReader.readLine()
            while (line != null) {
                emitter.onNext(LogMessage(line.messageType(), line))
                line = bufferedReader.readLine()
            }
        } catch (e: Throwable) {
            emitter.onError(e)
        } finally {
            bufferedReader?.close()
        }

        emitter.onComplete()
    }
}
